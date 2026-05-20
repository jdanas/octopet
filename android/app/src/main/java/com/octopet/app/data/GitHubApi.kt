package com.octopet.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal const val TAG = "OctoPetAPI"
val client = OkHttpClient()
private val JSON = "application/json".toMediaType()

// Uses `viewer` so no username is needed — works with both PAT and OAuth tokens
private val QUERY = """
    query(${"$"}from: DateTime!, ${"$"}to: DateTime!) {
      viewer {
        name
        login
        createdAt
        contributionsCollection(from: ${"$"}from, to: ${"$"}to) {
          totalCommitContributions
          totalPullRequestContributions
          totalIssueContributions
          restrictedContributionsCount
          contributionCalendar {
            totalContributions
            weeks {
              contributionDays {
                contributionCount
                date
              }
            }
          }
        }
      }
    }
""".trimIndent()

data class GitHubData(
    val username: String,
    val displayName: String,
    val joinedAt: String,
    val totalContributions: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val todayCount: Int,
    val thisWeekCount: Int,
    val thisMonthCount: Int,
    val commits: Int,
    val prs: Int,
    val issues: Int,
    val grid: List<List<Int>>,
    val recentActivity: List<ActivityItem> = emptyList(),
)

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

suspend fun fetchGitHubData(token: String): ApiResult<GitHubData> =
    withContext(Dispatchers.IO) {
        try {
            // Rolling 12-month window — matches GitHub profile page
            val toDate   = LocalDate.now()
            val fromDate = toDate.minusDays(364)
            val iso      = DateTimeFormatter.ISO_LOCAL_DATE

            val variables = JSONObject()
                .put("from", "${fromDate.format(iso)}T00:00:00Z")
                .put("to",   "${toDate.format(iso)}T23:59:59Z")

            val body = JSONObject()
                .put("query", QUERY)
                .put("variables", variables)
                .toString()
                .toRequestBody(JSON)

            val request = Request.Builder()
                .url("https://api.github.com/graphql")
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext ApiResult.Error("Empty response")

            if (!response.isSuccessful) return@withContext ApiResult.Error("HTTP ${response.code}")

            val root = JSONObject(responseBody)
            if (root.has("errors")) {
                val msg = root.getJSONArray("errors").getJSONObject(0).optString("message", "API error")
                return@withContext ApiResult.Error(msg)
            }

            val user       = root.getJSONObject("data").getJSONObject("viewer")
            val collection = user.getJSONObject("contributionsCollection")
            val calendar   = collection.getJSONObject("contributionCalendar")
            val weeksArray = calendar.getJSONArray("weeks")

            // Flat day map (date → count) for streak/weekly calculations
            val dayMap = mutableMapOf<String, Int>()
            for (w in 0 until weeksArray.length()) {
                val days = weeksArray.getJSONObject(w).getJSONArray("contributionDays")
                for (d in 0 until days.length()) {
                    val day = days.getJSONObject(d)
                    dayMap[day.getString("date")] = day.getInt("contributionCount")
                }
            }

            val today = LocalDate.now()
            val fmt   = DateTimeFormatter.ISO_LOCAL_DATE

            // Longest streak
            var longestStreak = 0
            var tempStreak    = 0
            for (dateStr in dayMap.keys.sorted()) {
                if ((dayMap[dateStr] ?: 0) > 0) tempStreak++ else tempStreak = 0
                if (tempStreak > longestStreak) longestStreak = tempStreak
            }
            // Current streak (walk back from today)
            var currentStreak = 0
            var checkDate = today
            while (true) {
                val count = dayMap[checkDate.format(fmt)] ?: 0
                if (count > 0) { currentStreak++; checkDate = checkDate.minusDays(1) } else break
            }

            val todayCount    = dayMap[today.format(fmt)] ?: 0
            val thisWeekCount = (0..6).sumOf { dayMap[today.minusDays(it.toLong()).format(fmt)] ?: 0 }
            val thisMonthCount = (0..29).sumOf { dayMap[today.minusDays(it.toLong()).format(fmt)] ?: 0 }

            val grid = mutableListOf<List<Int>>()
            for (w in 0 until weeksArray.length()) {
                val days = weeksArray.getJSONObject(w).getJSONArray("contributionDays")
                grid.add((0 until days.length()).map { days.getJSONObject(it).getInt("contributionCount") })
            }

            val joinedAt = runCatching {
                val d = LocalDate.parse(user.getString("createdAt").take(10), fmt)
                d.format(DateTimeFormatter.ofPattern("MMM yyyy"))
            }.getOrDefault("")

            val loginName = user.getString("login")

            Log.d(TAG, "viewer=$loginName total=${calendar.getInt("totalContributions")} restricted=${collection.getInt("restrictedContributionsCount")} weeks=${weeksArray.length()}")

            val activity = fetchRecentActivity(loginName, token)

            ApiResult.Success(
                GitHubData(
                    username           = loginName,
                    displayName        = user.optString("name").ifBlank { loginName },
                    joinedAt           = joinedAt,
                    totalContributions = calendar.getInt("totalContributions"),
                    currentStreak      = currentStreak,
                    longestStreak      = longestStreak,
                    todayCount         = todayCount,
                    thisWeekCount      = thisWeekCount,
                    thisMonthCount     = thisMonthCount,
                    commits            = collection.getInt("totalCommitContributions"),
                    prs                = collection.getInt("totalPullRequestContributions"),
                    issues             = collection.getInt("totalIssueContributions"),
                    grid               = grid,
                    recentActivity     = activity,
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

// ── Recent activity via REST Events API ─────────────────────────────────────

internal fun fetchRecentActivity(username: String, token: String): List<ActivityItem> {
    return try {
        val request = Request.Builder()
            .url("https://api.github.com/users/$username/events?per_page=30")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/vnd.github+json")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return emptyList()
        if (!response.isSuccessful) return emptyList()

        val events = org.json.JSONArray(body)
        val items  = mutableListOf<ActivityItem>()
        val now    = System.currentTimeMillis()

        for (i in 0 until events.length()) {
            if (items.size >= 5) break
            val event   = events.getJSONObject(i)
            val type    = event.optString("type")
            val repo    = event.optJSONObject("repo")?.optString("name")?.substringAfter("/") ?: continue
            val timeAgo = relativeTime(event.optString("created_at"), now)
            val payload = event.optJSONObject("payload") ?: continue

            when (type) {
                "PushEvent" -> {
                    val msg = payload.optJSONArray("commits")
                        ?.optJSONObject(0)?.optString("message")
                        ?.lines()?.first()?.take(60) ?: "Pushed commits"
                    items.add(ActivityItem(ActivityKind.COMMIT, repo, msg, timeAgo))
                }
                "PullRequestEvent" -> {
                    val title = payload.optJSONObject("pull_request")?.optString("title")?.take(60) ?: "Pull request"
                    items.add(ActivityItem(ActivityKind.PR, repo, title, timeAgo))
                }
                "IssuesEvent" -> {
                    val title = payload.optJSONObject("issue")?.optString("title")?.take(60) ?: "Issue"
                    items.add(ActivityItem(ActivityKind.ISSUE, repo, title, timeAgo))
                }
            }
        }
        items
    } catch (e: Exception) { emptyList() }
}

private fun relativeTime(iso: String, nowMs: Long): String {
    return try {
        val diffMs  = nowMs - java.time.Instant.parse(iso).toEpochMilli()
        val minutes = diffMs / 60_000
        when {
            minutes < 60   -> "${minutes}m"
            minutes < 1440 -> "${minutes / 60}h"
            else           -> "${minutes / 1440}d"
        }
    } catch (e: Exception) { "?" }
}
