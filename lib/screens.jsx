// Screen components for the tracker app.

function OnboardingScreen({ onComplete, family = 'octo' }) {
  const [step, setStep] = React.useState(0);
  const [username, setUsername] = React.useState('');

  const steps = [
    {
      title: 'Meet your\nOcto-pet.',
      sub: 'A little companion that grows every time you ship code.',
      creature: { stage: 'egg', mood: 'happy' },
      cta: 'Continue',
    },
    {
      title: 'Commit.\nGrow.\nRepeat.',
      sub: 'Your pet evolves through 5 stages as you hit contribution milestones.',
      creature: { stage: 'sprout', mood: 'happy' },
      cta: 'I\'m in',
    },
    {
      title: 'Connect\nGitHub',
      sub: 'We\'ll read your public contribution graph. Nothing else.',
      creature: null,
      cta: 'Hatch my pet',
      showInput: true,
    },
  ];

  const s = steps[step];
  const progress = (step + 1) / steps.length;

  return (
    <PaperBg style={{ display: 'flex', flexDirection: 'column' }}>
      <div style={{ height: 60 }}/>
      {/* progress */}
      <div style={{ padding: '0 24px', display: 'flex', gap: 6 }}>
        {steps.map((_, i) => (
          <div key={i} style={{
            flex: 1, height: 3, borderRadius: 2,
            background: i <= step ? TOKENS.ink : TOKENS.paperLine,
            transition: 'background 0.3s',
          }}/>
        ))}
      </div>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: '40px 28px 0' }}>
        {s.creature && (
          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 32 }}>
            <PetByFamily family={family} stage={s.creature.stage} mood={s.creature.mood} size={200}/>
          </div>
        )}

        {!s.creature && (
          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 32 }}>
            <div style={{
              width: 76, height: 76, borderRadius: 22,
              background: TOKENS.ink, color: TOKENS.paper,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <Icon name="github" size={44} color={TOKENS.paper}/>
            </div>
          </div>
        )}

        <h1 style={{
          fontSize: 40, fontWeight: 700, lineHeight: 1.05,
          letterSpacing: -1.2, color: TOKENS.ink,
          whiteSpace: 'pre-line', margin: 0, textAlign: 'center',
        }}>{s.title}</h1>
        <p style={{
          fontSize: 16, lineHeight: 1.5, color: TOKENS.inkMuted,
          margin: '16px 24px 0', textAlign: 'center',
        }}>{s.sub}</p>

        {s.showInput && (
          <div style={{
            marginTop: 28, padding: '14px 16px',
            background: '#fff', borderRadius: 14,
            border: `1px solid ${TOKENS.paperLine}`,
            display: 'flex', alignItems: 'center', gap: 10,
          }}>
            <span style={{ color: TOKENS.inkMuted, fontFamily: TOKENS.mono, fontSize: 15 }}>github.com/</span>
            <input value={username} onChange={e => setUsername(e.target.value)}
              placeholder="your-username" style={{
                flex: 1, border: 'none', outline: 'none', fontSize: 15,
                fontFamily: TOKENS.mono, color: TOKENS.ink, background: 'transparent',
              }}/>
          </div>
        )}

        {step === 1 && (
          <div style={{ marginTop: 32, display: 'flex', gap: 10, justifyContent: 'center', flexWrap: 'wrap' }}>
            {CreatureStages.map((st, i) => (
              <div key={st.name} style={{
                display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
                padding: '10px 12px', borderRadius: 12,
                background: i === 1 ? TOKENS.mossSoft : 'transparent',
                border: `1px solid ${i === 1 ? TOKENS.moss : TOKENS.paperLine}`,
                minWidth: 60,
              }}>
                <div style={{ fontSize: 11, fontWeight: 600, color: TOKENS.ink, textTransform: 'uppercase', letterSpacing: 0.5 }}>{st.name}</div>
                <div style={{ fontSize: 10, fontFamily: TOKENS.mono, color: TOKENS.inkMuted }}>{st.min}+</div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div style={{ padding: '24px 24px 44px' }}>
        <button
          onClick={() => step < steps.length - 1 ? setStep(step + 1) : onComplete()}
          disabled={s.showInput && !username}
          style={{
            width: '100%', height: 56, borderRadius: 28,
            background: (s.showInput && !username) ? TOKENS.inkFaint : TOKENS.ink,
            color: TOKENS.paper, border: 'none',
            fontFamily: TOKENS.font, fontSize: 17, fontWeight: 600,
            cursor: (s.showInput && !username) ? 'default' : 'pointer',
            transition: 'background 0.2s',
          }}>
          {s.cta}
        </button>
        {step === 0 && (
          <button style={{
            width: '100%', height: 44, marginTop: 8,
            background: 'transparent', border: 'none',
            color: TOKENS.inkMuted, fontFamily: TOKENS.font, fontSize: 14,
            cursor: 'pointer',
          }} onClick={() => setStep(steps.length - 1)}>
            Skip intro
          </button>
        )}
      </div>
    </PaperBg>
  );
}

function HomeScreen({ state, setState, grid }) {
  const { stage, mood, stats, family = 'octo' } = state;
  const stageInfo = CreatureStages.find(s => s.name === stage) || CreatureStages[2];
  const stageIdx = CreatureStages.findIndex(s => s.name === stage);
  const nextStage = CreatureStages[stageIdx + 1];
  const progress = nextStage
    ? (stats.totalContributions - stageInfo.min) / (nextStage.min - stageInfo.min)
    : 1;

  const feedMessages = {
    happy: "Just shipped some commits. I feel great!",
    neutral: "Hey — show me some code today?",
    hungry: "I'm getting hungry... feed me commits!",
    sleeping: "zzz... code a bit, I'll wake up.",
    excited: "LET'S GO! You're on fire today!",
  };

  // Recent 20 weeks for compact view
  const recentGrid = grid.slice(-20);

  return (
    <PaperBg style={{ overflowY: 'auto', paddingBottom: 100 }}>
      {/* Top bar */}
      <div style={{
        padding: '56px 20px 12px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      }}>
        <div>
          <div style={{ fontSize: 13, color: TOKENS.inkMuted, fontFamily: TOKENS.mono }}>@{MOCK_USER.username}</div>
          <div style={{ fontSize: 22, fontWeight: 700, color: TOKENS.ink, letterSpacing: -0.5 }}>Hey, Riley</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button style={{
            width: 40, height: 40, borderRadius: 20,
            border: `1px solid ${TOKENS.paperLine}`, background: '#fff',
            display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
          }}>
            <Icon name="bell" size={20} color={TOKENS.ink}/>
          </button>
        </div>
      </div>

      {/* Pet hero — warm gradient habitat */}
      <div style={{
        margin: '0 16px',
        borderRadius: 28,
        background: `linear-gradient(180deg, ${TOKENS.mossSoft} 0%, ${TOKENS.paper} 100%)`,
        border: `1px solid ${TOKENS.paperLine}`,
        padding: '20px 20px 24px',
        position: 'relative',
        overflow: 'hidden',
      }}>
        {/* little sparkles */}
        <div style={{ position: 'absolute', top: 16, right: 20, opacity: 0.4 }}>
          <Icon name="sparkle" size={16} color={TOKENS.mossDeep}/>
        </div>
        <div style={{ position: 'absolute', top: 36, right: 50, opacity: 0.3 }}>
          <Icon name="sparkle" size={10} color={TOKENS.mossDeep}/>
        </div>

        {/* stage badge */}
        <div style={{
          display: 'inline-flex', alignItems: 'center', gap: 6,
          padding: '4px 10px', borderRadius: 999,
          background: TOKENS.ink, color: TOKENS.paper,
          fontSize: 11, fontWeight: 600, letterSpacing: 0.8,
          textTransform: 'uppercase',
        }}>
          Stage {stageIdx + 1} · {stage}
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', margin: '8px 0' }}>
          <PetByFamily family={family} stage={stage} mood={mood} size={180}/>
        </div>

        {/* speech bubble */}
        <div style={{
          background: '#fff',
          borderRadius: 16,
          padding: '10px 14px',
          margin: '0 auto',
          maxWidth: '90%',
          border: `1px solid ${TOKENS.paperLine}`,
          fontSize: 14, color: TOKENS.ink, textAlign: 'center',
          position: 'relative',
          fontStyle: 'italic',
        }}>
          "{feedMessages[mood]}"
        </div>

        {/* evolve progress */}
        {nextStage && (
          <div style={{ marginTop: 16 }}>
            <div style={{
              display: 'flex', justifyContent: 'space-between',
              fontSize: 12, color: TOKENS.inkMuted, marginBottom: 6,
              fontFamily: TOKENS.mono,
            }}>
              <span>{stats.totalContributions} / {nextStage.min}</span>
              <span>→ {nextStage.name}</span>
            </div>
            <div style={{
              height: 8, borderRadius: 4, background: TOKENS.paperLine,
              overflow: 'hidden',
            }}>
              <div style={{
                height: '100%', width: `${progress * 100}%`,
                background: TOKENS.moss,
                borderRadius: 4,
                transition: 'width 0.6s ease',
              }}/>
            </div>
          </div>
        )}
      </div>

      {/* Stat row */}
      <div style={{ display: 'flex', gap: 10, padding: '16px 16px 0' }}>
        <Card tone="coral" style={{ flex: 1, padding: 14 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 6 }}>
            <Icon name="flame" size={16} color="oklch(0.55 0.14 35)"/>
            <div style={{ fontSize: 11, fontWeight: 600, color: TOKENS.inkSoft, textTransform: 'uppercase', letterSpacing: 0.4 }}>Streak</div>
          </div>
          <div style={{ fontSize: 26, fontWeight: 700, color: TOKENS.ink, fontFamily: TOKENS.mono }}>
            {stats.currentStreak}<span style={{ fontSize: 13, color: TOKENS.inkMuted, fontWeight: 500, marginLeft: 4 }}>days</span>
          </div>
        </Card>
        <Card style={{ flex: 1, padding: 14 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 6 }}>
            <Icon name="commit" size={16} color={TOKENS.mossDeep}/>
            <div style={{ fontSize: 11, fontWeight: 600, color: TOKENS.inkSoft, textTransform: 'uppercase', letterSpacing: 0.4 }}>This week</div>
          </div>
          <div style={{ fontSize: 26, fontWeight: 700, color: TOKENS.ink, fontFamily: TOKENS.mono }}>
            {stats.thisWeek}
          </div>
        </Card>
      </div>

      {/* Heatmap card */}
      <div style={{ padding: '12px 16px 0' }}>
        <Card>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
            <div style={{ fontSize: 15, fontWeight: 600, color: TOKENS.ink }}>Last 20 weeks</div>
            <div style={{ fontSize: 12, color: TOKENS.inkMuted, fontFamily: TOKENS.mono }}>{stats.totalContributions} total</div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'center' }}>
            <Heatmap grid={recentGrid} cell={11} gap={3}/>
          </div>
          <div style={{
            display: 'flex', alignItems: 'center', gap: 6,
            marginTop: 12, justifyContent: 'flex-end',
            fontSize: 10, color: TOKENS.inkMuted, fontFamily: TOKENS.mono,
          }}>
            <span>less</span>
            {[TOKENS.heat0, TOKENS.heat1, TOKENS.heat2, TOKENS.heat3, TOKENS.heat4].map((c, i) => (
              <div key={i} style={{ width: 9, height: 9, borderRadius: 2, background: c }}/>
            ))}
            <span>more</span>
          </div>
        </Card>
      </div>

      {/* Today's activity */}
      <div style={{ padding: '12px 16px 0' }}>
        <Card>
          <div style={{ fontSize: 15, fontWeight: 600, color: TOKENS.ink, marginBottom: 10 }}>Today's activity</div>
          {MOCK_ACTIVITY.slice(0, 3).map((a, i) => (
            <div key={i} style={{
              display: 'flex', alignItems: 'center', gap: 12,
              padding: '8px 0',
              borderTop: i > 0 ? `1px solid ${TOKENS.paperLine}` : 'none',
            }}>
              <div style={{
                width: 32, height: 32, borderRadius: 10,
                background: a.kind === 'commit' ? TOKENS.mossSoft : a.kind === 'pr' ? TOKENS.lavenderSoft : TOKENS.coralSoft,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                flexShrink: 0,
              }}>
                <Icon name={a.kind} size={16} color={a.kind === 'commit' ? TOKENS.mossDeep : a.kind === 'pr' ? 'oklch(0.48 0.11 295)' : 'oklch(0.55 0.14 35)'}/>
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 13, color: TOKENS.ink, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{a.msg}</div>
                <div style={{ fontSize: 11, color: TOKENS.inkMuted, fontFamily: TOKENS.mono }}>{a.repo} · {a.time} ago</div>
              </div>
            </div>
          ))}
        </Card>
      </div>
    </PaperBg>
  );
}

function GoalsScreen({ stats }) {
  return (
    <PaperBg style={{ overflowY: 'auto', paddingBottom: 100 }}>
      <div style={{ padding: '56px 24px 20px' }}>
        <div style={{ fontSize: 13, color: TOKENS.inkMuted, fontFamily: TOKENS.mono, marginBottom: 4 }}>Your targets</div>
        <h1 style={{ fontSize: 32, fontWeight: 700, color: TOKENS.ink, letterSpacing: -0.8, margin: 0 }}>Goals</h1>
      </div>

      {/* Weekly ring hero */}
      <div style={{ padding: '0 16px 16px' }}>
        <Card tone="butter" style={{ padding: 24, display: 'flex', alignItems: 'center', gap: 20 }}>
          <Ring value={stats.thisWeek / 40} size={96} stroke={10} color="oklch(0.55 0.14 85)" track="rgba(200,180,120,0.3)">
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: 22, fontWeight: 700, color: TOKENS.ink, fontFamily: TOKENS.mono, lineHeight: 1 }}>{stats.thisWeek}</div>
              <div style={{ fontSize: 10, color: TOKENS.inkMuted, fontFamily: TOKENS.mono }}>of 40</div>
            </div>
          </Ring>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 13, color: TOKENS.inkSoft, fontWeight: 500 }}>This week</div>
            <div style={{ fontSize: 22, fontWeight: 700, color: TOKENS.ink, letterSpacing: -0.4, marginTop: 2 }}>Nearly there</div>
            <div style={{ fontSize: 13, color: TOKENS.inkMuted, marginTop: 4 }}>2 more commits to hit your goal.</div>
          </div>
        </Card>
      </div>

      {/* Goal cards */}
      <div style={{ padding: '0 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {MOCK_GOALS.map(g => {
          const pct = Math.min(1, g.current / g.target);
          const done = g.current >= g.target;
          return (
            <Card key={g.id} style={{ padding: 16 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 10 }}>
                <div>
                  <div style={{ fontSize: 15, fontWeight: 600, color: TOKENS.ink }}>{g.name}</div>
                  <div style={{ fontSize: 12, color: TOKENS.inkMuted, fontFamily: TOKENS.mono, marginTop: 2 }}>{g.period}</div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: 18, fontWeight: 700, color: TOKENS.ink, fontFamily: TOKENS.mono }}>
                    {g.current}<span style={{ color: TOKENS.inkFaint }}>/{g.target}</span>
                  </div>
                  <div style={{ fontSize: 11, color: TOKENS.inkMuted }}>{g.unit}</div>
                </div>
              </div>
              <div style={{
                height: 8, borderRadius: 4, background: TOKENS.paperLine,
                overflow: 'hidden',
              }}>
                <div style={{
                  height: '100%', width: `${pct * 100}%`,
                  background: done ? TOKENS.moss : TOKENS.ink,
                  borderRadius: 4,
                  transition: 'width 0.6s ease',
                }}/>
              </div>
            </Card>
          );
        })}

        {/* Add new goal */}
        <button style={{
          padding: 16, borderRadius: 20,
          border: `1.5px dashed ${TOKENS.paperLine}`,
          background: 'transparent', color: TOKENS.inkMuted,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
          fontFamily: TOKENS.font, fontSize: 14, fontWeight: 500,
          cursor: 'pointer',
        }}>
          <Icon name="plus" size={18}/> Add a goal
        </button>
      </div>
    </PaperBg>
  );
}

function BadgesScreen() {
  const earned = MOCK_BADGES.filter(b => b.earned);
  const locked = MOCK_BADGES.filter(b => !b.earned);

  return (
    <PaperBg style={{ overflowY: 'auto', paddingBottom: 100 }}>
      <div style={{ padding: '56px 24px 20px' }}>
        <div style={{ fontSize: 13, color: TOKENS.inkMuted, fontFamily: TOKENS.mono, marginBottom: 4 }}>
          {earned.length} of {MOCK_BADGES.length} earned
        </div>
        <h1 style={{ fontSize: 32, fontWeight: 700, color: TOKENS.ink, letterSpacing: -0.8, margin: 0 }}>Achievements</h1>
      </div>

      {/* Progress bar */}
      <div style={{ padding: '0 24px 24px' }}>
        <div style={{ height: 6, borderRadius: 3, background: TOKENS.paperLine, overflow: 'hidden' }}>
          <div style={{
            height: '100%', width: `${(earned.length / MOCK_BADGES.length) * 100}%`,
            background: `linear-gradient(90deg, ${TOKENS.moss}, ${TOKENS.lavender})`,
            borderRadius: 3,
          }}/>
        </div>
      </div>

      {/* Earned */}
      <div style={{ padding: '0 16px' }}>
        <div style={{
          fontSize: 11, fontWeight: 600, color: TOKENS.inkMuted,
          textTransform: 'uppercase', letterSpacing: 0.6,
          padding: '0 8px 10px', fontFamily: TOKENS.mono,
        }}>Earned · {earned.length}</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {earned.map(b => <BadgeCard key={b.id} badge={b} earned/>)}
        </div>

        <div style={{
          fontSize: 11, fontWeight: 600, color: TOKENS.inkMuted,
          textTransform: 'uppercase', letterSpacing: 0.6,
          padding: '24px 8px 10px', fontFamily: TOKENS.mono,
        }}>Locked · {locked.length}</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {locked.map(b => <BadgeCard key={b.id} badge={b}/>)}
        </div>
      </div>
    </PaperBg>
  );
}

function BadgeCard({ badge, earned }) {
  const color = earned ? TOKENS.lavender : TOKENS.inkFaint;
  const bg = earned ? TOKENS.lavenderSoft : TOKENS.paperDeep;
  return (
    <div style={{
      background: earned ? '#fff' : TOKENS.paperDeep,
      borderRadius: 18, padding: 14,
      border: `1px solid ${TOKENS.paperLine}`,
      opacity: earned ? 1 : 0.65,
      position: 'relative',
    }}>
      <div style={{
        width: 48, height: 48, borderRadius: 14,
        background: bg, display: 'flex', alignItems: 'center', justifyContent: 'center',
        marginBottom: 10, position: 'relative',
      }}>
        <Icon name={badge.icon} size={26} color={color} strokeWidth={1.9}/>
        {!earned && (
          <div style={{
            position: 'absolute', top: -4, right: -4,
            width: 18, height: 18, borderRadius: 9,
            background: TOKENS.paperDeep, border: `1px solid ${TOKENS.paperLine}`,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <Icon name="lock" size={10} color={TOKENS.inkMuted}/>
          </div>
        )}
      </div>
      <div style={{ fontSize: 13, fontWeight: 600, color: TOKENS.ink, marginBottom: 2 }}>{badge.name}</div>
      <div style={{ fontSize: 11, color: TOKENS.inkMuted, lineHeight: 1.4 }}>{badge.desc}</div>
      {earned && badge.date && (
        <div style={{ fontSize: 10, color: TOKENS.inkMuted, fontFamily: TOKENS.mono, marginTop: 6 }}>
          {badge.date}
        </div>
      )}
    </div>
  );
}

function ProfileScreen({ state, grid }) {
  const { stage, stats, family = 'octo' } = state;
  const stageIdx = CreatureStages.findIndex(s => s.name === stage);
  const earned = MOCK_BADGES.filter(b => b.earned);

  return (
    <PaperBg style={{ overflowY: 'auto', paddingBottom: 100 }}>
      <div style={{ padding: '56px 20px 16px', display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
        <button style={{
          width: 40, height: 40, borderRadius: 20,
          border: `1px solid ${TOKENS.paperLine}`, background: '#fff',
          display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
        }}>
          <Icon name="share" size={18} color={TOKENS.ink}/>
        </button>
        <button style={{
          width: 40, height: 40, borderRadius: 20,
          border: `1px solid ${TOKENS.paperLine}`, background: '#fff',
          display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
        }}>
          <Icon name="settings" size={18} color={TOKENS.ink}/>
        </button>
      </div>

      {/* Header */}
      <div style={{ padding: '0 24px 20px', textAlign: 'center' }}>
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 8 }}>
          <PetByFamily family={family} stage={stage} mood="happy" size={120}/>
        </div>
        <div style={{ fontSize: 26, fontWeight: 700, color: TOKENS.ink, letterSpacing: -0.5 }}>
          {MOCK_USER.displayName}
        </div>
        <div style={{ fontSize: 14, color: TOKENS.inkMuted, fontFamily: TOKENS.mono }}>
          @{MOCK_USER.username}
        </div>
        <div style={{
          display: 'inline-flex', alignItems: 'center', gap: 6,
          marginTop: 12, padding: '6px 12px', borderRadius: 999,
          background: TOKENS.mossSoft, color: TOKENS.mossDeep,
          fontSize: 12, fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.6,
        }}>
          <Icon name="sparkle" size={12} color={TOKENS.mossDeep}/>
          Stage {stageIdx + 1} · {stage}
        </div>
      </div>

      {/* Big stats grid */}
      <div style={{ padding: '0 16px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <StatTile label="Total contributions" value={stats.totalContributions} tone="moss"/>
          <StatTile label="Current streak" value={`${stats.currentStreak}d`} tone="coral"/>
          <StatTile label="Longest streak" value={`${stats.longestStreak}d`} tone="butter"/>
          <StatTile label="Badges earned" value={earned.length} tone="lavender"/>
        </div>
      </div>

      {/* Breakdown */}
      <div style={{ padding: '16px' }}>
        <Card>
          <div style={{ fontSize: 15, fontWeight: 600, color: TOKENS.ink, marginBottom: 14 }}>Breakdown</div>
          <BreakdownRow icon="commit" label="Commits"     value={stats.commits} color={TOKENS.mossDeep}/>
          <BreakdownRow icon="pr"     label="Pull requests" value={stats.prs} color="oklch(0.48 0.11 295)"/>
          <BreakdownRow icon="issue"  label="Issues"      value={stats.issues} color="oklch(0.55 0.14 35)"/>
        </Card>
      </div>

      {/* Full heatmap */}
      <div style={{ padding: '0 16px 16px' }}>
        <Card>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
            <div style={{ fontSize: 15, fontWeight: 600, color: TOKENS.ink }}>12 months</div>
            <div style={{ fontSize: 12, color: TOKENS.inkMuted, fontFamily: TOKENS.mono }}>
              since {MOCK_USER.joinedAt}
            </div>
          </div>
          <Heatmap grid={grid} cell={6} gap={2}/>
        </Card>
      </div>
    </PaperBg>
  );
}

function StatTile({ label, value, tone }) {
  return (
    <Card tone={tone} style={{ padding: 14 }}>
      <div style={{ fontSize: 11, fontWeight: 600, color: TOKENS.inkSoft, textTransform: 'uppercase', letterSpacing: 0.5 }}>
        {label}
      </div>
      <div style={{ fontSize: 28, fontWeight: 700, color: TOKENS.ink, fontFamily: TOKENS.mono, marginTop: 6, letterSpacing: -0.5 }}>
        {value}
      </div>
    </Card>
  );
}

function BreakdownRow({ icon, label, value, color }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '8px 0' }}>
      <div style={{
        width: 32, height: 32, borderRadius: 10,
        background: TOKENS.paperDeep,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <Icon name={icon} size={16} color={color}/>
      </div>
      <div style={{ flex: 1, fontSize: 14, color: TOKENS.ink }}>{label}</div>
      <div style={{ fontSize: 16, fontWeight: 700, color: TOKENS.ink, fontFamily: TOKENS.mono }}>{value}</div>
    </div>
  );
}

Object.assign(window, {
  OnboardingScreen, HomeScreen, GoalsScreen, BadgesScreen, ProfileScreen,
});
