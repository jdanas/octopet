// Additional Octo-pet families: Robo-pet and Pixel Slime.
// Same API as Creature: { stage, mood, size, animated }.

// ─────────────────────────────────────────────────────────────
// RoboPet — circuit / mech themed, evolves with more hardware
// ─────────────────────────────────────────────────────────────
function RoboPet({ stage = 'hatchling', mood = 'happy', size = 180, animated = true }) {
  const wobble = animated ? { animation: 'creatureWobble 3.4s ease-in-out infinite' } : {};

  const c = {
    body: '#c9d2dc',
    bodyDark: '#8997a7',
    panel: '#e8edf2',
    screen: '#1a1a1a',
    screenLit: 'oklch(0.78 0.18 165)',  // mint green LED
    accent: 'oklch(0.62 0.17 35)',       // warning orange
    accentSoft: 'oklch(0.92 0.06 35)',
    bolt: '#5a6878',
  };

  // Mood -> screen face
  const renderScreen = (cx, cy, w, h) => {
    const lit = c.screenLit;
    return (
      <g>
        <rect x={cx - w/2} y={cy - h/2} width={w} height={h} rx={3} fill={c.screen}/>
        {mood === 'happy' && (
          <g stroke={lit} strokeWidth="2" fill="none" strokeLinecap="round">
            <path d={`M${cx - w*0.25} ${cy - h*0.1} L${cx - w*0.1} ${cy + h*0.15} L${cx + w*0.25} ${cy - h*0.2}`}/>
          </g>
        )}
        {mood === 'neutral' && (
          <g fill={lit}>
            <rect x={cx - w*0.3} y={cy - 1.5} width={w*0.2} height={3} rx={1}/>
            <rect x={cx + w*0.1} y={cy - 1.5} width={w*0.2} height={3} rx={1}/>
          </g>
        )}
        {mood === 'hungry' && (
          <g stroke={c.accent} strokeWidth="2" fill="none" strokeLinecap="round">
            <line x1={cx - w*0.25} y1={cy - h*0.2} x2={cx - w*0.05} y2={cy + h*0.2}/>
            <line x1={cx - w*0.05} y1={cy - h*0.2} x2={cx - w*0.25} y2={cy + h*0.2}/>
            <line x1={cx + w*0.05} y1={cy - h*0.2} x2={cx + w*0.25} y2={cy + h*0.2}/>
            <line x1={cx + w*0.25} y1={cy - h*0.2} x2={cx + w*0.05} y2={cy + h*0.2}/>
          </g>
        )}
        {mood === 'sleeping' && (
          <g stroke={lit} strokeWidth="2" fill="none" strokeLinecap="round" opacity="0.6">
            <path d={`M${cx - w*0.25} ${cy} L${cx - w*0.1} ${cy} M${cx + w*0.1} ${cy} L${cx + w*0.25} ${cy}`}/>
          </g>
        )}
        {mood === 'excited' && (
          <g fill={lit}>
            <circle cx={cx - w*0.2} cy={cy} r="3"/>
            <circle cx={cx + w*0.2} cy={cy} r="3"/>
            <rect x={cx - 4} y={cy + h*0.15} width={8} height={2} fill={c.accent}/>
          </g>
        )}
        {/* scanline */}
        <rect x={cx - w/2} y={cy - h/2} width={w} height={h} rx={3} fill="url(#scanlineRobo)" opacity="0.2"/>
      </g>
    );
  };

  const bolt = (cx, cy, r = 2) => (
    <g>
      <circle cx={cx} cy={cy} r={r} fill={c.bolt}/>
      <line x1={cx - r*0.6} y1={cy} x2={cx + r*0.6} y2={cy} stroke={c.panel} strokeWidth="0.8"/>
    </g>
  );

  const renderBody = () => {
    if (stage === 'egg') {
      // unhatched capsule
      return (
        <g>
          <rect x="65" y="55" width="70" height="110" rx="35" fill={c.bodyDark}/>
          <rect x="70" y="60" width="60" height="100" rx="30" fill={c.body}/>
          {/* status light */}
          <circle cx="100" cy="80" r="5" fill={c.accent}/>
          <circle cx="100" cy="80" r="2" fill="#fff" opacity="0.7"/>
          {/* seam */}
          <line x1="70" y1="110" x2="130" y2="110" stroke={c.bodyDark} strokeWidth="1.5"/>
          <text x="100" y="138" textAnchor="middle" fontFamily="JetBrains Mono, monospace" fontSize="10" fill={c.bodyDark} fontWeight="600">v0.1</text>
        </g>
      );
    }

    if (stage === 'sprout') {
      // tiny cube with antenna
      return (
        <g>
          {/* antenna */}
          <line x1="100" y1="65" x2="100" y2="50" stroke={c.bodyDark} strokeWidth="2"/>
          <circle cx="100" cy="48" r="4" fill={c.accent}/>
          {/* cube body */}
          <rect x="72" y="70" width="56" height="56" rx="6" fill={c.bodyDark}/>
          <rect x="74" y="72" width="52" height="52" rx="5" fill={c.body}/>
          {/* screen */}
          {renderScreen(100, 97, 36, 22)}
          {/* bolts */}
          {bolt(80, 78)}{bolt(120, 78)}{bolt(80, 118)}{bolt(120, 118)}
          {/* base */}
          <rect x="84" y="126" width="32" height="8" rx="2" fill={c.bodyDark}/>
          <ellipse cx="100" cy="138" rx="22" ry="3" fill="#000" opacity="0.1"/>
        </g>
      );
    }

    if (stage === 'hatchling') {
      // small bot with arms
      return (
        <g>
          <ellipse cx="100" cy="172" rx="40" ry="5" fill="#000" opacity="0.1"/>
          {/* antenna */}
          <line x1="100" y1="60" x2="100" y2="45" stroke={c.bodyDark} strokeWidth="2"/>
          <circle cx="100" cy="42" r="5" fill={c.accent}/>
          <circle cx="100" cy="42" r="2" fill="#fff" opacity="0.6"/>
          {/* head */}
          <rect x="65" y="65" width="70" height="60" rx="10" fill={c.bodyDark}/>
          <rect x="67" y="67" width="66" height="56" rx="9" fill={c.body}/>
          {renderScreen(100, 95, 50, 32)}
          {bolt(75, 75)}{bolt(125, 75)}
          {/* arms */}
          <rect x="50" y="110" width="14" height="36" rx="6" fill={c.bodyDark}/>
          <rect x="52" y="112" width="10" height="32" rx="5" fill={c.body}/>
          <circle cx="57" cy="146" r="7" fill={c.bodyDark}/>
          <rect x="136" y="110" width="14" height="36" rx="6" fill={c.bodyDark}/>
          <rect x="138" y="112" width="10" height="32" rx="5" fill={c.body}/>
          <circle cx="143" cy="146" r="7" fill={c.bodyDark}/>
          {/* body */}
          <rect x="78" y="128" width="44" height="38" rx="6" fill={c.bodyDark}/>
          <rect x="80" y="130" width="40" height="34" rx="5" fill={c.body}/>
          {/* chest light */}
          <circle cx="100" cy="146" r="4" fill={mood === 'hungry' ? c.accent : c.screenLit}/>
        </g>
      );
    }

    if (stage === 'fledgling') {
      return (
        <g>
          <ellipse cx="100" cy="178" rx="48" ry="5" fill="#000" opacity="0.12"/>
          {/* twin antenna */}
          <line x1="88" y1="55" x2="84" y2="38" stroke={c.bodyDark} strokeWidth="2"/>
          <circle cx="83" cy="36" r="4" fill={c.accent}/>
          <line x1="112" y1="55" x2="116" y2="38" stroke={c.bodyDark} strokeWidth="2"/>
          <circle cx="117" cy="36" r="4" fill={c.screenLit}/>
          {/* head */}
          <rect x="58" y="58" width="84" height="64" rx="14" fill={c.bodyDark}/>
          <rect x="60" y="60" width="80" height="60" rx="13" fill={c.body}/>
          {renderScreen(100, 90, 60, 36)}
          {bolt(70, 70)}{bolt(130, 70)}{bolt(70, 110)}{bolt(130, 110)}
          {/* speaker dots */}
          <g fill={c.bodyDark} opacity="0.5">
            <circle cx="74" cy="115" r="1.5"/><circle cx="80" cy="115" r="1.5"/><circle cx="86" cy="115" r="1.5"/>
            <circle cx="114" cy="115" r="1.5"/><circle cx="120" cy="115" r="1.5"/><circle cx="126" cy="115" r="1.5"/>
          </g>
          {/* arms */}
          <rect x="40" y="118" width="16" height="44" rx="7" fill={c.bodyDark}/>
          <rect x="42" y="120" width="12" height="40" rx="6" fill={c.body}/>
          <circle cx="48" cy="162" r="9" fill={c.bodyDark}/>
          <circle cx="48" cy="162" r="6" fill={c.body}/>
          <rect x="144" y="118" width="16" height="44" rx="7" fill={c.bodyDark}/>
          <rect x="146" y="120" width="12" height="40" rx="6" fill={c.body}/>
          <circle cx="152" cy="162" r="9" fill={c.bodyDark}/>
          <circle cx="152" cy="162" r="6" fill={c.body}/>
          {/* body */}
          <rect x="68" y="125" width="64" height="50" rx="10" fill={c.bodyDark}/>
          <rect x="70" y="127" width="60" height="46" rx="9" fill={c.body}/>
          {/* chest panel */}
          <rect x="82" y="140" width="36" height="22" rx="4" fill={c.panel}/>
          <rect x="86" y="146" width="10" height="3" rx="1" fill={c.screenLit}/>
          <rect x="86" y="151" width="14" height="3" rx="1" fill={c.bodyDark} opacity="0.4"/>
          <rect x="86" y="156" width="8" height="3" rx="1" fill={c.bodyDark} opacity="0.4"/>
          <circle cx="110" cy="151" r="4" fill={mood === 'hungry' ? c.accent : c.screenLit}/>
        </g>
      );
    }

    // elder — full mech
    return (
      <g>
        <ellipse cx="100" cy="183" rx="55" ry="6" fill="#000" opacity="0.14"/>
        {/* halo / data ring */}
        <ellipse cx="100" cy="38" rx="22" ry="4" fill="none" stroke={c.screenLit} strokeWidth="2" opacity="0.7"/>
        <ellipse cx="100" cy="38" rx="22" ry="4" fill="none" stroke={c.accent} strokeWidth="1" opacity="0.5"/>
        {/* twin antenna */}
        <line x1="84" y1="55" x2="78" y2="38" stroke={c.bodyDark} strokeWidth="2.5"/>
        <circle cx="77" cy="36" r="5" fill={c.accent}/>
        <line x1="116" y1="55" x2="122" y2="38" stroke={c.bodyDark} strokeWidth="2.5"/>
        <circle cx="123" cy="36" r="5" fill={c.screenLit}/>
        {/* head */}
        <rect x="52" y="52" width="96" height="70" rx="16" fill={c.bodyDark}/>
        <rect x="54" y="54" width="92" height="66" rx="15" fill={c.body}/>
        {renderScreen(100, 88, 72, 42)}
        {bolt(64, 64, 2.5)}{bolt(136, 64, 2.5)}{bolt(64, 112, 2.5)}{bolt(136, 112, 2.5)}
        {/* circuit traces */}
        <g stroke={c.screenLit} strokeWidth="1" fill="none" opacity="0.4">
          <path d="M58 70 L62 70 L62 76"/>
          <path d="M142 70 L138 70 L138 76"/>
        </g>
        {/* arms with shoulder pauldrons */}
        <circle cx="42" cy="126" r="12" fill={c.bodyDark}/>
        <circle cx="42" cy="126" r="8" fill={c.body}/>
        <rect x="36" y="130" width="18" height="40" rx="8" fill={c.bodyDark}/>
        <rect x="38" y="132" width="14" height="36" rx="7" fill={c.body}/>
        <circle cx="45" cy="172" r="10" fill={c.bodyDark}/>
        <circle cx="45" cy="172" r="6" fill={c.body}/>
        <circle cx="158" cy="126" r="12" fill={c.bodyDark}/>
        <circle cx="158" cy="126" r="8" fill={c.body}/>
        <rect x="146" y="130" width="18" height="40" rx="8" fill={c.bodyDark}/>
        <rect x="148" y="132" width="14" height="36" rx="7" fill={c.body}/>
        <circle cx="155" cy="172" r="10" fill={c.bodyDark}/>
        <circle cx="155" cy="172" r="6" fill={c.body}/>
        {/* body */}
        <rect x="62" y="124" width="76" height="56" rx="12" fill={c.bodyDark}/>
        <rect x="64" y="126" width="72" height="52" rx="11" fill={c.body}/>
        {/* core */}
        <circle cx="100" cy="152" r="14" fill={c.bodyDark}/>
        <circle cx="100" cy="152" r="10" fill={c.screenLit} opacity="0.9"/>
        <circle cx="100" cy="152" r="5" fill="#fff" opacity="0.6"/>
        {/* side vents */}
        <g fill={c.bodyDark} opacity="0.6">
          <rect x="70" y="140" width="14" height="2"/><rect x="70" y="146" width="14" height="2"/><rect x="70" y="152" width="14" height="2"/>
          <rect x="116" y="140" width="14" height="2"/><rect x="116" y="146" width="14" height="2"/><rect x="116" y="152" width="14" height="2"/>
        </g>
      </g>
    );
  };

  return (
    <div style={{ width: size, height: size, ...wobble }}>
      <svg viewBox="0 0 200 200" width={size} height={size}>
        <defs>
          <linearGradient id="scanlineRobo" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#fff" stopOpacity="0"/>
            <stop offset="50%" stopColor="#fff" stopOpacity="0.3"/>
            <stop offset="100%" stopColor="#fff" stopOpacity="0"/>
          </linearGradient>
        </defs>
        {renderBody()}
      </svg>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// PixelSlime — 8-bit blocky retro creature
// Drawn as a grid of cells with a chunky pixel feel.
// ─────────────────────────────────────────────────────────────
function PixelSlime({ stage = 'hatchling', mood = 'happy', size = 180, animated = true }) {
  const wobble = animated ? { animation: 'creatureWobble 2.6s steps(2) infinite' } : {};

  const p = {
    body: 'oklch(0.72 0.16 285)',     // purple
    bodyDark: 'oklch(0.52 0.18 285)',
    highlight: 'oklch(0.92 0.06 285)',
    eye: '#1a1a1a',
    eyeShine: '#fff',
    crown: '#f0c649',
    crownDark: '#b58a26',
    accent: 'oklch(0.7 0.16 25)',
  };

  // 12x12 grid, each cell is `unit` pixels
  const unit = 200 / 16; // 12.5 — gives padding
  const cell = (x, y, fill, w = 1, h = 1) => (
    <rect x={2 + x * unit} y={2 + y * unit} width={unit * w + 0.5} height={unit * h + 0.5} fill={fill} shapeRendering="crispEdges"/>
  );

  // Mood eyes drawn as little 2x2 or 3x3 pixel clusters
  const renderEyes = (lx, rx, y) => {
    if (mood === 'happy') {
      return [
        cell(lx, y, p.eye), cell(lx + 1, y, p.eye),
        cell(rx, y, p.eye), cell(rx + 1, y, p.eye),
        cell(lx, y - 1, p.eye, 0.6, 0.6), cell(rx + 1, y - 1, p.eye, 0.6, 0.6),
      ];
    }
    if (mood === 'sleeping') {
      return [
        cell(lx, y + 0.4, p.eye, 2, 0.4),
        cell(rx, y + 0.4, p.eye, 2, 0.4),
      ];
    }
    if (mood === 'hungry') {
      return [
        cell(lx, y, p.eye), cell(lx + 1, y, p.eye),
        cell(rx, y, p.eye), cell(rx + 1, y, p.eye),
        cell(lx - 0.5, y + 1, p.bodyDark, 0.6, 0.6),
        cell(rx + 1.5, y + 1, p.bodyDark, 0.6, 0.6),
      ];
    }
    if (mood === 'excited') {
      return [
        // sparkle stars
        cell(lx + 0.5, y - 0.5, p.eyeShine, 0.6, 0.6),
        cell(lx, y, p.eye), cell(lx + 1, y, p.eye),
        cell(lx, y + 1, p.eye), cell(lx + 1, y + 1, p.eye),
        cell(rx + 1.5, y - 0.5, p.eyeShine, 0.6, 0.6),
        cell(rx, y, p.eye), cell(rx + 1, y, p.eye),
        cell(rx, y + 1, p.eye), cell(rx + 1, y + 1, p.eye),
      ];
    }
    // neutral default
    return [
      cell(lx, y, p.eye), cell(lx + 1, y, p.eye),
      cell(lx, y + 1, p.eye), cell(lx + 1, y + 1, p.eye),
      cell(rx, y, p.eye), cell(rx + 1, y, p.eye),
      cell(rx, y + 1, p.eye), cell(rx + 1, y + 1, p.eye),
    ];
  };

  const renderMouth = (cx, cy) => {
    if (mood === 'happy')   return [cell(cx - 1, cy, p.eye), cell(cx, cy + 0.5, p.eye), cell(cx + 1, cy, p.eye)];
    if (mood === 'excited') return [cell(cx - 1, cy, p.eye), cell(cx, cy, p.eye, 2, 1), cell(cx - 1, cy + 1, p.eye, 4, 0.4)];
    if (mood === 'hungry')  return [cell(cx - 1, cy + 0.5, p.eye), cell(cx, cy, p.eye), cell(cx + 1, cy + 0.5, p.eye)];
    if (mood === 'sleeping')return [cell(cx - 0.5, cy + 0.5, p.eye, 2, 0.5)];
    return [cell(cx - 1, cy + 0.5, p.eye, 3, 0.5)];
  };

  const renderBody = () => {
    if (stage === 'egg') {
      // pixel egg
      const cells = [];
      // top row narrower
      cells.push(cell(5, 1, p.bodyDark, 4, 1));
      cells.push(cell(4, 2, p.bodyDark, 6, 1));
      cells.push(cell(3, 3, p.bodyDark, 8, 1));
      // body
      for (let y = 4; y <= 11; y++) cells.push(cell(2, y, p.bodyDark, 10, 1));
      // bottom rounding
      cells.push(cell(3, 12, p.bodyDark, 8, 1));
      // inner
      cells.push(cell(5, 2, p.body, 4, 1));
      cells.push(cell(4, 3, p.body, 6, 1));
      for (let y = 4; y <= 11; y++) cells.push(cell(3, y, p.body, 8, 1));
      cells.push(cell(4, 12, p.body, 6, 1));
      // highlight
      cells.push(cell(4, 3, p.highlight));
      cells.push(cell(5, 3, p.highlight));
      cells.push(cell(3, 4, p.highlight));
      cells.push(cell(3, 5, p.highlight));
      // crack
      cells.push(cell(7, 6, p.bodyDark));
      cells.push(cell(8, 7, p.bodyDark));
      cells.push(cell(7, 8, p.bodyDark));
      cells.push(cell(8, 9, p.bodyDark));
      return cells;
    }

    if (stage === 'sprout') {
      const cells = [];
      // small dome
      cells.push(cell(6, 5, p.bodyDark, 2, 1));
      cells.push(cell(5, 6, p.bodyDark, 4, 1));
      cells.push(cell(4, 7, p.bodyDark, 6, 1));
      cells.push(cell(4, 8, p.bodyDark, 6, 1));
      cells.push(cell(4, 9, p.bodyDark, 6, 1));
      cells.push(cell(5, 10, p.bodyDark, 4, 1));
      // inner
      cells.push(cell(6, 6, p.body, 2, 1));
      cells.push(cell(5, 7, p.body, 4, 1));
      cells.push(cell(5, 8, p.body, 4, 1));
      cells.push(cell(6, 9, p.body, 2, 1));
      // highlight
      cells.push(cell(5, 6, p.highlight));
      cells.push(cell(4, 7, p.highlight));
      // ground shadow
      cells.push(cell(4, 11, p.bodyDark, 6, 0.3));
      // eyes + mouth
      cells.push(...renderEyes(5.5, 8, 7));
      cells.push(...renderMouth(7, 9));
      return cells;
    }

    if (stage === 'hatchling') {
      const cells = [];
      // dome shape
      cells.push(cell(5, 3, p.bodyDark, 4, 1));
      cells.push(cell(4, 4, p.bodyDark, 6, 1));
      cells.push(cell(3, 5, p.bodyDark, 8, 1));
      for (let y = 6; y <= 10; y++) cells.push(cell(2, y, p.bodyDark, 10, 1));
      cells.push(cell(2, 11, p.bodyDark, 10, 0.5));
      // inner
      cells.push(cell(5, 4, p.body, 4, 1));
      cells.push(cell(4, 5, p.body, 6, 1));
      for (let y = 6; y <= 10; y++) cells.push(cell(3, y, p.body, 8, 1));
      // highlight
      cells.push(cell(4, 4, p.highlight));
      cells.push(cell(5, 4, p.highlight));
      cells.push(cell(3, 5, p.highlight));
      // shadow
      cells.push(cell(2, 11.5, '#000', 10, 0.25));
      // eyes + mouth
      cells.push(...renderEyes(4, 8, 7));
      cells.push(...renderMouth(7, 9));
      return cells;
    }

    if (stage === 'fledgling') {
      const cells = [];
      // little crown spikes
      cells.push(cell(5, 1, p.crown));
      cells.push(cell(7, 1, p.crown));
      cells.push(cell(9, 1, p.crown));
      // taller dome
      cells.push(cell(5, 2, p.bodyDark, 4, 1));
      cells.push(cell(4, 3, p.bodyDark, 6, 1));
      cells.push(cell(3, 4, p.bodyDark, 8, 1));
      cells.push(cell(2, 5, p.bodyDark, 10, 1));
      for (let y = 6; y <= 11; y++) cells.push(cell(1, y, p.bodyDark, 12, 1));
      cells.push(cell(2, 12, p.bodyDark, 10, 0.5));
      // inner
      cells.push(cell(5, 3, p.body, 4, 1));
      cells.push(cell(4, 4, p.body, 6, 1));
      cells.push(cell(3, 5, p.body, 8, 1));
      for (let y = 6; y <= 11; y++) cells.push(cell(2, y, p.body, 10, 1));
      // highlight
      cells.push(cell(4, 3, p.highlight));
      cells.push(cell(5, 3, p.highlight));
      cells.push(cell(3, 4, p.highlight));
      cells.push(cell(2, 5, p.highlight));
      cells.push(cell(2, 6, p.highlight));
      // belly stripe
      cells.push(cell(4, 9, p.highlight, 6, 0.4));
      // shadow
      cells.push(cell(1, 12.5, '#000', 12, 0.25));
      // eyes + mouth bigger
      cells.push(...renderEyes(4, 8, 7));
      cells.push(...renderMouth(7, 9));
      return cells;
    }

    // elder — biggest slime + crown + horns
    const cells = [];
    // crown
    cells.push(cell(4, 0, p.crownDark, 8, 0.5));
    cells.push(cell(4, 0.5, p.crown));
    cells.push(cell(6, 0, p.crown, 0.5, 1));
    cells.push(cell(7, 0, p.crown));
    cells.push(cell(8, 0, p.crown));
    cells.push(cell(11, 0.5, p.crown));
    cells.push(cell(4, 1, p.crown, 8, 1));
    cells.push(cell(6, 1.5, p.bodyDark));
    cells.push(cell(9, 1.5, p.bodyDark));
    cells.push(cell(7, 1.5, '#fff'));
    cells.push(cell(8, 1.5, '#fff'));
    // body
    cells.push(cell(5, 2.5, p.bodyDark, 6, 1));
    cells.push(cell(3, 3.5, p.bodyDark, 10, 1));
    cells.push(cell(2, 4.5, p.bodyDark, 12, 1));
    for (let y = 5.5; y <= 11.5; y++) cells.push(cell(1, y, p.bodyDark, 13, 1));
    cells.push(cell(2, 12.5, p.bodyDark, 11, 0.5));
    // inner
    cells.push(cell(5, 3, p.body, 6, 1));
    cells.push(cell(3, 4, p.body, 10, 1));
    cells.push(cell(2, 5, p.body, 12, 1));
    for (let y = 6; y <= 11; y++) cells.push(cell(2, y, p.body, 11, 1));
    // highlight
    cells.push(cell(4, 4, p.highlight));
    cells.push(cell(3, 5, p.highlight));
    cells.push(cell(2, 6, p.highlight));
    cells.push(cell(2, 7, p.highlight));
    // belly stripes
    cells.push(cell(3, 9, p.highlight, 9, 0.3));
    cells.push(cell(4, 10, p.highlight, 7, 0.3));
    // gem on forehead
    cells.push(cell(7, 5, p.accent, 1, 1));
    cells.push(cell(7, 5, '#fff', 0.4, 0.4));
    // eyes + mouth
    cells.push(...renderEyes(4, 8, 7));
    cells.push(...renderMouth(7, 9));
    // shadow
    cells.push(cell(1, 13, '#000', 13, 0.3));
    return cells;
  };

  const cells = renderBody();
  return (
    <div style={{ width: size, height: size, ...wobble, imageRendering: 'pixelated' }}>
      <svg viewBox="0 0 200 200" width={size} height={size} shapeRendering="crispEdges">
        {cells.map((el, i) => React.cloneElement(el, { key: i }))}
      </svg>
    </div>
  );
}

// Convenience: render any pet family by name
function PetByFamily({ family = 'octo', stage, mood, size, animated }) {
  if (family === 'robo')  return <RoboPet stage={stage} mood={mood} size={size} animated={animated}/>;
  if (family === 'slime') return <PixelSlime stage={stage} mood={mood} size={size} animated={animated}/>;
  return <Creature stage={stage} mood={mood} size={size} animated={animated}/>;
}

const PetFamilies = [
  { id: 'octo',  name: 'Octo-pet',    desc: 'Soft moss creature' },
  { id: 'robo',  name: 'Robo-pet',    desc: 'Circuit companion' },
  { id: 'slime', name: 'Pixel Slime', desc: '8-bit retro buddy' },
];

Object.assign(window, { RoboPet, PixelSlime, PetByFamily, PetFamilies });
