// Mock data for the GitHub tracker app.

// Generate 53 weeks x 7 days of contribution data (~1 year)
function generateContributionGrid() {
  // Seeded pseudo-random for stable demo
  let seed = 42;
  const rand = () => {
    seed = (seed * 9301 + 49297) % 233280;
    return seed / 233280;
  };

  const weeks = 53;
  const grid = [];
  for (let w = 0; w < weeks; w++) {
    const week = [];
    for (let d = 0; d < 7; d++) {
      // Weekends quieter, some random streaks
      const isWeekend = d === 0 || d === 6;
      const baseChance = isWeekend ? 0.4 : 0.75;
      let count = 0;
      if (rand() < baseChance) {
        const r = rand();
        if (r < 0.5) count = Math.floor(rand() * 3) + 1;       // 1-3
        else if (r < 0.8) count = Math.floor(rand() * 5) + 4;  // 4-8
        else if (r < 0.95) count = Math.floor(rand() * 8) + 9; // 9-16
        else count = Math.floor(rand() * 10) + 17;             // 17+
      }
      // Force last 14 days to be active-ish to show a nice current state
      if (w >= weeks - 2) {
        if (!isWeekend && count === 0) count = Math.floor(rand() * 6) + 2;
      }
      week.push(count);
    }
    grid.push(week);
  }
  // Make today (last cell in last partial week) definitely active
  grid[weeks - 1][6] = 7;
  return grid;
}

function contribLevel(count) {
  if (count === 0) return 0;
  if (count <= 3) return 1;
  if (count <= 8) return 2;
  if (count <= 16) return 3;
  return 4;
}

const MOCK_USER = {
  username: 'octocoder',
  displayName: 'Riley Chen',
  avatarColor: '#c8a07a',
  joinedAt: 'Mar 2023',
};

const MOCK_STATS = {
  totalContributions: 342,
  currentStreak: 23,
  longestStreak: 47,
  thisWeek: 38,
  thisMonth: 127,
  commits: 284,
  prs: 31,
  issues: 27,
};

const MOCK_BADGES = [
  { id: 'first_commit', name: 'First Commit', desc: 'Made your first contribution', icon: 'seed', earned: true, date: 'Mar 12' },
  { id: 'week_streak', name: 'Week Warrior', desc: '7 days in a row', icon: 'flame', earned: true, date: 'Mar 19' },
  { id: 'twenty_streak', name: 'Consistency Champ', desc: '20 day streak', icon: 'flame2', earned: true, date: 'Apr 8' },
  { id: 'century', name: 'Century Club', desc: '100 contributions', icon: 'trophy', earned: true, date: 'Apr 2' },
  { id: 'pr_pro', name: 'PR Pro', desc: 'Merged 25 pull requests', icon: 'merge', earned: true, date: 'Apr 14' },
  { id: 'night_owl', name: 'Night Owl', desc: 'Commit after midnight', icon: 'moon', earned: true, date: 'Mar 28' },
  { id: 'polyglot', name: 'Polyglot', desc: 'Contribute in 5 languages', icon: 'globe', earned: false },
  { id: 'marathon', name: 'Marathon', desc: '50 day streak', icon: 'lightning', earned: false },
  { id: 'early_bird', name: 'Early Bird', desc: 'Commit before 6am', icon: 'sun', earned: false },
  { id: 'elder', name: 'Elder Pet', desc: 'Hatch all 5 pet stages', icon: 'crown', earned: false },
  { id: 'open_source', name: 'Open Source Hero', desc: '10 contributions to public repos', icon: 'heart', earned: true, date: 'Apr 5' },
  { id: 'reviewer', name: 'Code Reviewer', desc: 'Review 20 PRs', icon: 'eye', earned: false },
];

const MOCK_GOALS = [
  { id: 'weekly_commits', name: 'Weekly commits', target: 40, current: 38, unit: 'commits', period: 'this week' },
  { id: 'streak_goal', name: 'Streak goal', target: 30, current: 23, unit: 'days', period: 'current' },
  { id: 'monthly_prs', name: 'Pull requests', target: 8, current: 5, unit: 'PRs', period: 'this month' },
  { id: 'review_goal', name: 'Code reviews', target: 15, current: 9, unit: 'reviews', period: 'this month' },
];

const MOCK_ACTIVITY = [
  { kind: 'commit', repo: 'mobile-app', msg: 'fix: race condition in auth flow', time: '2h' },
  { kind: 'pr',     repo: 'mobile-app', msg: 'Add biometric login support', time: '4h' },
  { kind: 'commit', repo: 'design-sys', msg: 'chore: update tokens to v2', time: '6h' },
  { kind: 'issue',  repo: 'api-gateway', msg: 'Rate limiter drops requests under load', time: '1d' },
  { kind: 'commit', repo: 'api-gateway', msg: 'refactor: extract middleware', time: '1d' },
];

Object.assign(window, {
  generateContributionGrid, contribLevel,
  MOCK_USER, MOCK_STATS, MOCK_BADGES, MOCK_GOALS, MOCK_ACTIVITY,
});
