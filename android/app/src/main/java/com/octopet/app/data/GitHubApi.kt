package com.octopet.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val client = OkHttpClient()
private val JSON = "application/json".toMediaType()

private val QUERY = """
    query(${"$"}login: String!) {
      user(login: ${"$"}login) {
        name
        login
        createdAt
        contributionsCollection {
          totalCommitContributions
          totalPullRequestContributions
          totalIssueContributions
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
)

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

suspend fun fetchGitHubData(username: String, token: String): ApiResult<GitHubData> =
    withContext(Dispatchers.IO) {
        try {
            val variables = JSONObject().put("login", username)
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

            if (!response.isSuccessful) {
                return@withContext ApiResult.Error("HTTP ${response.code}")
            }

            val root = JSONObject(responseBody)
            if (root.has("errors")) {
                val msg = root.getJSONArray("errors").getJSONObject(0).optString("message", "API error")
                return@withContext ApiResult.Error(msg)
            }

            val user = root.getJSONObject("data").getJSONObject("user")
            val collection = user.getJSONObject("contributionsCollection")
            val calendar = collection.getJSONObject("contributionCalendar")
            val weeksArray = calendar.getJSONArray("weeks")

            // Build flat day list (date → count)
            val dayMap = mutableMapOf<String, Int>()
            for (w in 0 until weeksArray.length()) {
                val days = weeksArray.getJSONObject(w).getJSONArray("contributionDays")
                for (d in 0 until days.length()) {
                    val day = days.getJSONObject(d)
                    dayMap[day.getString("date")] = day.getInt("contributionCount")
                }
            }

            val today = LocalDate.now()
            val fmt = DateTimeFormatter.ISO_LOCAL_DATE

            // Streak: walk backwards from today
            var currentStreak = 0
            var longestStreak = 0
            var tempStreak = 0
            val sortedDates = dayMap.keys.sorted()
            for (dateStr in sortedDates) {
                if ((dayMap[dateStr] ?: 0) > 0) tempStreak++ else tempStreak = 0
                if (tempStreak > longestStreak) longestStreak = tempStreak
            }
            var checkDate = today
            while (true) {
                val count = dayMap[checkDate.format(fmt)] ?: 0
                if (count > 0) { currentStreak++; checkDate = checkDate.minusDays(1) } else break
            }

            val todayCount = dayMap[today.format(fmt)] ?: 0

            val thisWeekCount = (0..6).sumOf {
                dayMap[today.minusDays(it.toLong()).format(fmt)] ?: 0
            }
            val thisMonthCount = (0..29).sumOf {
                dayMap[today.minusDays(it.toLong()).format(fmt)] ?: 0
            }

            // Rebuild 53-week grid in the same shape as MockData (week columns, 7 days each)
            val grid = mutableListOf<List<Int>>()
            for (w in 0 until weeksArray.length()) {
                val days = weeksArray.getJSONObject(w).getJSONArray("contributionDays")
                val week = (0 until days.length()).map {
                    days.getJSONObject(it).getInt("contributionCount")
                }
                grid.add(week)
            }

            // Created-at → "Mar 2023"
            val createdAt = user.getString("createdAt")
            val joinedAt = runCatching {
                val d = LocalDate.parse(createdAt.take(10), fmt)
                d.format(DateTimeFormatter.ofPattern("MMM yyyy"))
            }.getOrDefault("")

            ApiResult.Success(
                GitHubData(
                    username = user.getString("login"),
                    displayName = user.optString("name").ifBlank { user.getString("login") },
                    joinedAt = joinedAt,
                    totalContributions = calendar.getInt("totalContributions"),
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    todayCount = todayCount,
                    thisWeekCount = thisWeekCount,
                    thisMonthCount = thisMonthCount,
                    commits = collection.getInt("totalCommitContributions"),
                    prs = collection.getInt("totalPullRequestContributions"),
                    issues = collection.getInt("totalIssueContributions"),
                    grid = grid,
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
