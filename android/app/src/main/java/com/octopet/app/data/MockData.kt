package com.octopet.app.data

object MockData {

    val user = User(
        username = "octocoder",
        displayName = "Riley Chen",
        joinedAt = "Mar 2023",
    )

    val stats = Stats(
        totalContributions = 342,
        currentStreak = 23,
        longestStreak = 47,
        thisWeek = 38,
        thisMonth = 127,
        commits = 284,
        prs = 31,
        issues = 27,
    )

    val goals = listOf(
        Goal("weekly_commits", "Weekly commits",  target = 40, current = 38, unit = "commits", period = "this week"),
        Goal("streak_goal",    "Streak goal",     target = 30, current = 23, unit = "days",    period = "current"),
        Goal("monthly_prs",    "Pull requests",   target = 8,  current = 5,  unit = "PRs",     period = "this month"),
        Goal("review_goal",    "Code reviews",    target = 15, current = 9,  unit = "reviews", period = "this month"),
    )

    val badges = listOf(
        Badge("first_commit",  "First Commit",       "Made your first contribution",         BadgeIcon.SEED,      earned = true,  date = "Mar 12"),
        Badge("week_streak",   "Week Warrior",        "7 days in a row",                      BadgeIcon.FLAME,     earned = true,  date = "Mar 19"),
        Badge("twenty_streak", "Consistency Champ",   "20 day streak",                        BadgeIcon.FLAME2,    earned = true,  date = "Apr 8"),
        Badge("century",       "Century Club",        "100 contributions",                    BadgeIcon.TROPHY,    earned = true,  date = "Apr 2"),
        Badge("pr_pro",        "PR Pro",              "Merged 25 pull requests",              BadgeIcon.MERGE,     earned = true,  date = "Apr 14"),
        Badge("night_owl",     "Night Owl",           "Commit after midnight",                BadgeIcon.MOON,      earned = true,  date = "Mar 28"),
        Badge("open_source",   "Open Source Hero",    "10 contributions to public repos",     BadgeIcon.HEART,     earned = true,  date = "Apr 5"),
        Badge("polyglot",      "Polyglot",            "Contribute in 5 languages",            BadgeIcon.GLOBE,     earned = false),
        Badge("marathon",      "Marathon",            "50 day streak",                        BadgeIcon.LIGHTNING, earned = false),
        Badge("early_bird",    "Early Bird",          "Commit before 6am",                    BadgeIcon.SUN,       earned = false),
        Badge("elder",         "Elder Pet",           "Hatch all 5 pet stages",               BadgeIcon.CROWN,     earned = false),
        Badge("reviewer",      "Code Reviewer",       "Review 20 PRs",                        BadgeIcon.EYE,       earned = false),
    )

    val activity = listOf(
        ActivityItem(ActivityKind.COMMIT, "mobile-app",   "fix: race condition in auth flow",   "2h"),
        ActivityItem(ActivityKind.PR,     "mobile-app",   "Add biometric login support",         "4h"),
        ActivityItem(ActivityKind.COMMIT, "design-sys",   "chore: update tokens to v2",          "6h"),
        ActivityItem(ActivityKind.ISSUE,  "api-gateway",  "Rate limiter drops requests under load", "1d"),
        ActivityItem(ActivityKind.COMMIT, "api-gateway",  "refactor: extract middleware",         "1d"),
    )

    /** 53 weeks × 7 days of seeded contribution counts (matches the JS prototype). */
    fun generateContributionGrid(): List<List<Int>> {
        var seed = 42L
        fun rand(): Double {
            seed = (seed * 9301L + 49297L) % 233280L
            return seed.toDouble() / 233280.0
        }

        val weeks = 53
        val grid = mutableListOf<List<Int>>()
        for (w in 0 until weeks) {
            val week = mutableListOf<Int>()
            for (d in 0 until 7) {
                val isWeekend = d == 0 || d == 6
                val baseChance = if (isWeekend) 0.4 else 0.75
                var count = 0
                if (rand() < baseChance) {
                    val r = rand()
                    count = when {
                        r < 0.5  -> (rand() * 3).toInt() + 1
                        r < 0.8  -> (rand() * 5).toInt() + 4
                        r < 0.95 -> (rand() * 8).toInt() + 9
                        else     -> (rand() * 10).toInt() + 17
                    }
                }
                if (w >= weeks - 2 && !isWeekend && count == 0) {
                    count = (rand() * 6).toInt() + 2
                }
                week.add(count)
            }
            grid.add(week)
        }
        grid[weeks - 1] = grid[weeks - 1].toMutableList().also { it[6] = 7 }
        return grid
    }

    fun contribLevel(count: Int): Int = when {
        count == 0  -> 0
        count <= 3  -> 1
        count <= 8  -> 2
        count <= 16 -> 3
        else        -> 4
    }
}
