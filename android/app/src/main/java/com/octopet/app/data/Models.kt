package com.octopet.app.data

// ── Pet system ───────────────────────────────────────────────

enum class PetFamily { OCTO, ROBO, SLIME }

enum class PetStage(val label: String, val minContribs: Int) {
    EGG("egg", 0),
    SPROUT("sprout", 10),
    HATCHLING("hatchling", 50),
    FLEDGLING("fledgling", 200),
    ELDER("elder", 500),
}

enum class PetMood { HAPPY, NEUTRAL, HUNGRY, SLEEPING, EXCITED }

fun stageForContribs(total: Int): PetStage =
    PetStage.entries.lastOrNull { total >= it.minContribs } ?: PetStage.EGG

val PetStage.moodMessage: Map<PetMood, String>
    get() = mapOf(
        PetMood.HAPPY    to "Just shipped some commits. I feel great!",
        PetMood.NEUTRAL  to "Hey — show me some code today?",
        PetMood.HUNGRY   to "I'm getting hungry... feed me commits!",
        PetMood.SLEEPING to "zzz... code a bit, I'll wake up.",
        PetMood.EXCITED  to "LET'S GO! You're on fire today!",
    )

// ── User & stats ─────────────────────────────────────────────

data class User(
    val username: String,
    val displayName: String,
    val joinedAt: String,
)

data class Stats(
    val totalContributions: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val thisWeek: Int,
    val thisMonth: Int,
    val commits: Int,
    val prs: Int,
    val issues: Int,
)

// ── Goals ────────────────────────────────────────────────────

data class Goal(
    val id: String,
    val name: String,
    val target: Int,
    val current: Int,
    val unit: String,
    val period: String,
)

// ── Badges ───────────────────────────────────────────────────

enum class BadgeIcon {
    SEED, FLAME, FLAME2, TROPHY, MERGE, MOON, GLOBE,
    LIGHTNING, SUN, CROWN, HEART, EYE
}

data class Badge(
    val id: String,
    val name: String,
    val desc: String,
    val icon: BadgeIcon,
    val earned: Boolean,
    val date: String? = null,
)

// ── Activity feed ────────────────────────────────────────────

enum class ActivityKind { COMMIT, PR, ISSUE }

data class ActivityItem(
    val kind: ActivityKind,
    val repo: String,
    val message: String,
    val timeAgo: String,
)

// ── App state ────────────────────────────────────────────────

data class AppState(
    val family: PetFamily = PetFamily.OCTO,
    val stage: PetStage = PetStage.FLEDGLING,
    val mood: PetMood = PetMood.HAPPY,
    val stats: Stats = MockData.stats,
    val onboarded: Boolean = false,
)
