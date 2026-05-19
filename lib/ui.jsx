// Shared UI primitives + tokens for the tracker app.

const TOKENS = {
  paper: '#faf7f2',
  paperDeep: '#f2ede3',
  paperLine: '#ebe4d5',
  ink: '#1a1a1a',
  inkSoft: '#4a4a4a',
  inkMuted: '#7a7a7a',
  inkFaint: '#b5b0a3',
  // accents (shared chroma, varying hue)
  moss: 'oklch(0.62 0.12 140)',       // creature green
  mossDeep: 'oklch(0.48 0.12 140)',
  mossSoft: 'oklch(0.88 0.05 140)',
  coral: 'oklch(0.68 0.14 35)',        // streak flame
  coralSoft: 'oklch(0.92 0.05 35)',
  lavender: 'oklch(0.68 0.11 295)',    // achievements
  lavenderSoft: 'oklch(0.94 0.04 295)',
  butter: 'oklch(0.85 0.1 85)',        // goals / sun
  butterSoft: 'oklch(0.96 0.04 85)',
  // contrib heatmap (moss scale)
  heat0: '#ebe4d5',
  heat1: 'oklch(0.85 0.06 140)',
  heat2: 'oklch(0.72 0.1 140)',
  heat3: 'oklch(0.58 0.12 140)',
  heat4: 'oklch(0.42 0.13 140)',
  font: '"Space Grotesk", -apple-system, system-ui, sans-serif',
  mono: '"JetBrains Mono", ui-monospace, Menlo, monospace',
};

// Icon set — hand-drawn style, monoline
function Icon({ name, size = 20, color = 'currentColor', strokeWidth = 1.8 }) {
  const p = { fill: 'none', stroke: color, strokeWidth, strokeLinecap: 'round', strokeLinejoin: 'round' };
  const paths = {
    flame: <g {...p}><path d="M12 3c-1 3-4 4-4 8a4 4 0 008 0c0-2-1-3-2-4 0 2-1 3-2 3s-1-2 0-4-1-3 0-3z"/><path d="M12 3c2 3 5 4 5 9a5 5 0 01-10 0"/></g>,
    trophy: <g {...p}><path d="M6 4h12v4a6 6 0 01-12 0z"/><path d="M6 6H4a2 2 0 002 2"/><path d="M18 6h2a2 2 0 01-2 2"/><path d="M10 14h4l1 3v2H9v-2z"/><path d="M12 10v4"/></g>,
    seed: <g {...p}><path d="M12 20c0-6 3-10 7-11-1 6-3 10-7 11z"/><path d="M12 20c0-6-3-10-7-11 1 6 3 10 7 11z"/><path d="M12 20v-4"/></g>,
    merge: <g {...p}><circle cx="6" cy="6" r="2"/><circle cx="6" cy="18" r="2"/><circle cx="18" cy="15" r="2"/><path d="M6 8v8"/><path d="M6 12c0-3 3-3 6-3s6 0 6 4"/></g>,
    moon: <g {...p}><path d="M20 14a7 7 0 01-10-10 8 8 0 1010 10z"/></g>,
    sun: <g {...p}><circle cx="12" cy="12" r="3.5"/><path d="M12 3v2M12 19v2M3 12h2M19 12h2M5.6 5.6l1.4 1.4M17 17l1.4 1.4M5.6 18.4L7 17M17 7l1.4-1.4"/></g>,
    lightning: <g {...p}><path d="M13 3L5 14h6l-1 7 8-11h-6z"/></g>,
    globe: <g {...p}><circle cx="12" cy="12" r="9"/><path d="M3 12h18"/><path d="M12 3c3 3 3 15 0 18"/><path d="M12 3c-3 3-3 15 0 18"/></g>,
    crown: <g {...p}><path d="M3 18l2-10 4 4 3-6 3 6 4-4 2 10z"/><path d="M3 18h18"/></g>,
    heart: <g {...p}><path d="M12 20s-7-4-7-10a4 4 0 017-2 4 4 0 017 2c0 6-7 10-7 10z"/></g>,
    eye: <g {...p}><path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/></g>,
    flame2: <g {...p}><path d="M12 3s-5 5-5 10a5 5 0 0010 0c0-3-3-5-3-5s2-2 0-4z"/></g>,
    commit: <g {...p}><circle cx="12" cy="12" r="3.5"/><path d="M12 2v5M12 16v6"/></g>,
    pr: <g {...p}><circle cx="6" cy="5" r="2"/><circle cx="6" cy="19" r="2"/><circle cx="18" cy="19" r="2"/><path d="M6 7v10"/><path d="M18 17V9a4 4 0 00-4-4h-3"/><path d="M13 3l-2 2 2 2"/></g>,
    issue: <g {...p}><circle cx="12" cy="12" r="9"/><path d="M12 7v6M12 16v.5"/></g>,
    check: <g {...p}><path d="M4 12l5 5L20 6"/></g>,
    plus: <g {...p}><path d="M12 5v14M5 12h14"/></g>,
    chevron: <g {...p}><path d="M9 6l6 6-6 6"/></g>,
    chevronDown: <g {...p}><path d="M6 9l6 6 6-6"/></g>,
    home: <g {...p}><path d="M3 12l9-8 9 8"/><path d="M5 10v10h14V10"/></g>,
    user: <g {...p}><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3-7 8-7s8 3 8 7"/></g>,
    badge: <g {...p}><circle cx="12" cy="10" r="6"/><path d="M8 14l-2 7 6-3 6 3-2-7"/></g>,
    target: <g {...p}><circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5"/><circle cx="12" cy="12" r="1.5" fill={color}/></g>,
    github: <g fill={color}><path d="M12 2a10 10 0 00-3.2 19.5c.5.1.7-.2.7-.5v-1.8c-2.8.6-3.4-1.4-3.4-1.4-.5-1.2-1.1-1.5-1.1-1.5-.9-.6.1-.6.1-.6 1 .1 1.5 1 1.5 1 .9 1.6 2.4 1.1 3 .9.1-.7.4-1.1.7-1.4-2.2-.2-4.6-1.1-4.6-5 0-1.1.4-2 1-2.7-.1-.3-.5-1.3.1-2.7 0 0 .8-.3 2.7 1a9.4 9.4 0 015 0c1.9-1.3 2.7-1 2.7-1 .6 1.4.2 2.4.1 2.7.6.7 1 1.6 1 2.7 0 3.9-2.4 4.8-4.6 5 .4.3.7.9.7 1.9v2.8c0 .3.2.6.7.5A10 10 0 0012 2z"/></g>,
    lock: <g {...p}><rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V8a4 4 0 018 0v3"/></g>,
    sparkle: <g {...p}><path d="M12 3l2 6 6 2-6 2-2 6-2-6-6-2 6-2z"/></g>,
    calendar: <g {...p}><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/></g>,
    share: <g {...p}><circle cx="6" cy="12" r="2.5"/><circle cx="18" cy="5" r="2.5"/><circle cx="18" cy="19" r="2.5"/><path d="M8 11l8-5M8 13l8 5"/></g>,
    bell: <g {...p}><path d="M6 16V11a6 6 0 0112 0v5l2 2H4z"/><path d="M10 20a2 2 0 004 0"/></g>,
    settings: <g {...p}><circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4 12H2M22 12h-2M5.6 5.6L7 7M17 17l1.4 1.4M5.6 18.4L7 17M17 7l1.4-1.4"/></g>,
  };
  return (
    <svg width={size} height={size} viewBox="0 0 24 24">{paths[name] || null}</svg>
  );
}

// A subtle paper-texture backdrop using layered radial gradients.
function PaperBg({ children, style = {} }) {
  return (
    <div style={{
      background: TOKENS.paper,
      backgroundImage: `
        radial-gradient(ellipse at 20% 10%, rgba(200,160,80,0.06), transparent 50%),
        radial-gradient(ellipse at 80% 90%, rgba(120,150,100,0.05), transparent 50%)
      `,
      width: '100%', height: '100%',
      ...style,
    }}>{children}</div>
  );
}

// Soft card with deep paper edge
function Card({ children, style = {}, onClick, tone = 'paper' }) {
  const bg = {
    paper: '#ffffff',
    deep: TOKENS.paperDeep,
    moss: TOKENS.mossSoft,
    coral: TOKENS.coralSoft,
    lavender: TOKENS.lavenderSoft,
    butter: TOKENS.butterSoft,
  }[tone] || '#fff';
  return (
    <div onClick={onClick} style={{
      background: bg,
      borderRadius: 20,
      padding: 16,
      border: `1px solid ${TOKENS.paperLine}`,
      boxShadow: '0 1px 0 rgba(0,0,0,0.02)',
      cursor: onClick ? 'pointer' : 'default',
      ...style,
    }}>{children}</div>
  );
}

// Simple progress ring
function Ring({ value = 0.5, size = 48, stroke = 5, color = TOKENS.moss, track = TOKENS.paperLine, children }) {
  const r = (size - stroke) / 2;
  const c = 2 * Math.PI * r;
  const off = c * (1 - Math.min(1, Math.max(0, value)));
  return (
    <div style={{ position: 'relative', width: size, height: size, display: 'inline-block' }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={track} strokeWidth={stroke}/>
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={color} strokeWidth={stroke}
          strokeDasharray={c} strokeDashoffset={off} strokeLinecap="round"
          style={{ transition: 'stroke-dashoffset 0.6s ease' }}/>
      </svg>
      {children && (
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          {children}
        </div>
      )}
    </div>
  );
}

// Tab bar (bottom nav)
function TabBar({ active, onChange }) {
  const tabs = [
    { id: 'home', label: 'Home', icon: 'home' },
    { id: 'goals', label: 'Goals', icon: 'target' },
    { id: 'badges', label: 'Badges', icon: 'badge' },
    { id: 'profile', label: 'Profile', icon: 'user' },
  ];
  return (
    <div style={{
      position: 'absolute', bottom: 0, left: 0, right: 0,
      paddingBottom: 34, paddingTop: 8, paddingLeft: 8, paddingRight: 8,
      background: 'rgba(250,247,242,0.94)',
      backdropFilter: 'blur(18px) saturate(160%)',
      WebkitBackdropFilter: 'blur(18px) saturate(160%)',
      borderTop: `1px solid ${TOKENS.paperLine}`,
      display: 'flex', zIndex: 50,
    }}>
      {tabs.map(t => {
        const isActive = active === t.id;
        return (
          <button key={t.id} onClick={() => onChange(t.id)} style={{
            flex: 1, border: 'none', background: 'transparent',
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
            padding: '6px 0', cursor: 'pointer',
            color: isActive ? TOKENS.ink : TOKENS.inkMuted,
            fontFamily: TOKENS.font,
          }}>
            <Icon name={t.icon} size={22} strokeWidth={isActive ? 2.2 : 1.7}/>
            <span style={{ fontSize: 10, fontWeight: isActive ? 600 : 500, letterSpacing: 0.2 }}>{t.label}</span>
          </button>
        );
      })}
    </div>
  );
}

// Contribution heatmap grid
function Heatmap({ grid, cell = 10, gap = 2.5, onCellClick }) {
  const levels = [TOKENS.heat0, TOKENS.heat1, TOKENS.heat2, TOKENS.heat3, TOKENS.heat4];
  return (
    <div style={{ display: 'flex', gap, overflowX: 'auto' }}>
      {grid.map((week, wi) => (
        <div key={wi} style={{ display: 'flex', flexDirection: 'column', gap }}>
          {week.map((count, di) => {
            const lv = contribLevel(count);
            return (
              <div key={di} onClick={() => onCellClick && onCellClick(wi, di, count)}
                title={`${count} contributions`}
                style={{
                  width: cell, height: cell, borderRadius: Math.max(1.5, cell * 0.2),
                  background: levels[lv],
                  cursor: onCellClick ? 'pointer' : 'default',
                }}/>
            );
          })}
        </div>
      ))}
    </div>
  );
}

// Style sheet to inject once
const GLOBAL_STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500;600&display=swap');
  * { box-sizing: border-box; }
  body { margin: 0; font-family: ${TOKENS.font}; -webkit-font-smoothing: antialiased; }
  @keyframes creatureWobble {
    0%, 100% { transform: translateY(0) rotate(-1deg); }
    50% { transform: translateY(-3px) rotate(1deg); }
  }
  @keyframes fadeUp {
    from { opacity: 0; transform: translateY(8px); }
    to { opacity: 1; transform: translateY(0); }
  }
  @keyframes pulse {
    0%, 100% { transform: scale(1); }
    50% { transform: scale(1.04); }
  }
  @keyframes shimmer {
    0% { background-position: -200% 0; }
    100% { background-position: 200% 0; }
  }
  .shimmer {
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
    background-size: 200% 100%;
    animation: shimmer 2s infinite;
  }
`;

Object.assign(window, { TOKENS, Icon, PaperBg, Card, Ring, TabBar, Heatmap, GLOBAL_STYLES });
