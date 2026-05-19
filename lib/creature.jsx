// Octo-pet creature component.
// Evolves through 5 stages based on total contributions.
// Expresses mood based on streak status.

const CreatureStages = [
  { name: 'egg',       min: 0,    emoji: 'Egg' },
  { name: 'sprout',    min: 10,   emoji: 'Sprout' },
  { name: 'hatchling', min: 50,   emoji: 'Hatchling' },
  { name: 'fledgling', min: 200,  emoji: 'Fledgling' },
  { name: 'elder',     min: 500,  emoji: 'Elder' },
];

function getCreatureStage(contributions) {
  let stage = CreatureStages[0];
  for (const s of CreatureStages) if (contributions >= s.min) stage = s;
  return stage;
}

// Moods: happy (committed today), neutral (yesterday), hungry (2+ days), sleeping (night)
function Creature({ stage = 'hatchling', mood = 'happy', size = 180, animated = true }) {
  const wobbleStyle = animated ? {
    animation: 'creatureWobble 3s ease-in-out infinite',
  } : {};

  const colors = {
    body: '#6b9e4f',
    bodyDark: '#4a7a36',
    belly: '#c8e0a8',
    eye: '#1a1a1a',
    eyeShine: '#ffffff',
    cheek: '#f5a9a0',
    shell: '#8b6f47',
    shellDark: '#6b5437',
  };

  // Eyes react to mood
  const eyes = {
    happy:    { shape: 'happy' },
    neutral:  { shape: 'open' },
    hungry:   { shape: 'sad' },
    sleeping: { shape: 'closed' },
    excited:  { shape: 'star' },
  }[mood] || { shape: 'open' };

  const renderEye = (cx, cy, r = 4) => {
    if (eyes.shape === 'happy')   return <path d={`M${cx - r} ${cy} Q${cx} ${cy - r * 1.2} ${cx + r} ${cy}`} stroke={colors.eye} strokeWidth="2.2" fill="none" strokeLinecap="round"/>;
    if (eyes.shape === 'closed')  return <path d={`M${cx - r} ${cy} Q${cx} ${cy + r * 0.8} ${cx + r} ${cy}`} stroke={colors.eye} strokeWidth="2.2" fill="none" strokeLinecap="round"/>;
    if (eyes.shape === 'sad')     return <path d={`M${cx - r} ${cy + r * 0.5} Q${cx} ${cy - r * 0.3} ${cx + r} ${cy + r * 0.5}`} stroke={colors.eye} strokeWidth="2.2" fill="none" strokeLinecap="round"/>;
    if (eyes.shape === 'star')    return (
      <g>
        <circle cx={cx} cy={cy} r={r} fill={colors.eye}/>
        <circle cx={cx + r * 0.4} cy={cy - r * 0.4} r={r * 0.5} fill={colors.eyeShine}/>
      </g>
    );
    return (
      <g>
        <circle cx={cx} cy={cy} r={r} fill={colors.eye}/>
        <circle cx={cx + r * 0.35} cy={cy - r * 0.35} r={r * 0.4} fill={colors.eyeShine}/>
      </g>
    );
  };

  const renderBody = () => {
    if (stage === 'egg') {
      return (
        <g>
          <ellipse cx="100" cy="110" rx="55" ry="68" fill={colors.shell}/>
          <ellipse cx="100" cy="110" rx="55" ry="68" fill="url(#eggShine)" opacity="0.5"/>
          {/* cracks */}
          <path d="M75 90 L85 100 L80 110 L92 120" stroke={colors.shellDark} strokeWidth="1.5" fill="none" strokeLinecap="round"/>
          <path d="M120 85 L115 95 L125 105" stroke={colors.shellDark} strokeWidth="1.5" fill="none" strokeLinecap="round"/>
          {/* spots */}
          <circle cx="85" cy="130" r="4" fill={colors.shellDark} opacity="0.4"/>
          <circle cx="115" cy="140" r="3" fill={colors.shellDark} opacity="0.4"/>
          <circle cx="105" cy="85" r="2.5" fill={colors.shellDark} opacity="0.4"/>
        </g>
      );
    }

    if (stage === 'sprout') {
      return (
        <g>
          {/* soil mound */}
          <ellipse cx="100" cy="165" rx="50" ry="12" fill="#6b5437" opacity="0.4"/>
          {/* stem */}
          <rect x="96" y="120" width="8" height="50" rx="4" fill={colors.bodyDark}/>
          {/* little body */}
          <ellipse cx="100" cy="115" rx="28" ry="26" fill={colors.body}/>
          <ellipse cx="100" cy="120" rx="20" ry="15" fill={colors.belly}/>
          {/* leaves */}
          <ellipse cx="75" cy="110" rx="18" ry="9" fill={colors.body} transform="rotate(-30 75 110)"/>
          <ellipse cx="125" cy="110" rx="18" ry="9" fill={colors.body} transform="rotate(30 125 110)"/>
          {/* eyes */}
          {renderEye(91, 110, 3)}
          {renderEye(109, 110, 3)}
          {/* mouth */}
          {mood === 'happy' && <path d="M95 118 Q100 122 105 118" stroke={colors.eye} strokeWidth="1.5" fill="none" strokeLinecap="round"/>}
        </g>
      );
    }

    if (stage === 'hatchling') {
      return (
        <g>
          {/* shadow */}
          <ellipse cx="100" cy="170" rx="40" ry="6" fill="#000" opacity="0.1"/>
          {/* body blob */}
          <path d="M50 110 Q50 60 100 60 Q150 60 150 110 Q150 160 100 165 Q50 160 50 110 Z" fill={colors.body}/>
          {/* belly */}
          <ellipse cx="100" cy="125" rx="32" ry="28" fill={colors.belly}/>
          {/* tentacles */}
          <ellipse cx="65" cy="155" rx="10" ry="14" fill={colors.body}/>
          <ellipse cx="85" cy="162" rx="10" ry="14" fill={colors.body}/>
          <ellipse cx="115" cy="162" rx="10" ry="14" fill={colors.body}/>
          <ellipse cx="135" cy="155" rx="10" ry="14" fill={colors.body}/>
          {/* eyes */}
          {renderEye(85, 100, 5)}
          {renderEye(115, 100, 5)}
          {/* cheeks */}
          {mood === 'happy' && (
            <g>
              <circle cx="75" cy="115" r="5" fill={colors.cheek} opacity="0.6"/>
              <circle cx="125" cy="115" r="5" fill={colors.cheek} opacity="0.6"/>
            </g>
          )}
          {/* mouth */}
          {mood === 'happy' && <path d="M92 118 Q100 126 108 118" stroke={colors.eye} strokeWidth="2" fill="none" strokeLinecap="round"/>}
          {mood === 'neutral' && <line x1="94" y1="120" x2="106" y2="120" stroke={colors.eye} strokeWidth="2" strokeLinecap="round"/>}
          {mood === 'hungry' && <path d="M92 122 Q100 116 108 122" stroke={colors.eye} strokeWidth="2" fill="none" strokeLinecap="round"/>}
          {mood === 'sleeping' && (
            <g>
              <path d="M92 120 Q100 124 108 120" stroke={colors.eye} strokeWidth="2" fill="none" strokeLinecap="round"/>
              <text x="145" y="75" fontFamily="system-ui" fontSize="16" fill={colors.eye} opacity="0.5">z</text>
              <text x="155" y="65" fontFamily="system-ui" fontSize="20" fill={colors.eye} opacity="0.5">Z</text>
            </g>
          )}
        </g>
      );
    }

    if (stage === 'fledgling') {
      return (
        <g>
          <ellipse cx="100" cy="175" rx="45" ry="6" fill="#000" opacity="0.1"/>
          {/* bigger body */}
          <path d="M40 105 Q40 50 100 50 Q160 50 160 105 Q160 165 100 170 Q40 165 40 105 Z" fill={colors.body}/>
          {/* belly */}
          <ellipse cx="100" cy="125" rx="38" ry="32" fill={colors.belly}/>
          {/* tentacles with suckers */}
          {[55, 78, 100, 122, 145].map((x, i) => (
            <g key={i}>
              <ellipse cx={x} cy="165" rx="10" ry="16" fill={colors.body}/>
              <circle cx={x} cy="172" r="2" fill={colors.bodyDark}/>
            </g>
          ))}
          {/* little crown / leaves on top */}
          <path d="M85 55 L90 42 L95 55 Z" fill={colors.bodyDark}/>
          <path d="M100 52 L105 38 L110 52 Z" fill={colors.bodyDark}/>
          <path d="M115 55 L110 42 L105 55 Z" fill={colors.bodyDark}/>
          {/* eyes - bigger */}
          {renderEye(82, 98, 6)}
          {renderEye(118, 98, 6)}
          {/* cheeks */}
          {(mood === 'happy' || mood === 'excited') && (
            <g>
              <circle cx="70" cy="115" r="6" fill={colors.cheek} opacity="0.7"/>
              <circle cx="130" cy="115" r="6" fill={colors.cheek} opacity="0.7"/>
            </g>
          )}
          {/* mouth */}
          {mood === 'happy' && <path d="M90 120 Q100 130 110 120" stroke={colors.eye} strokeWidth="2.2" fill="none" strokeLinecap="round"/>}
          {mood === 'excited' && <ellipse cx="100" cy="122" rx="6" ry="5" fill={colors.eye}/>}
          {mood === 'neutral' && <line x1="92" y1="122" x2="108" y2="122" stroke={colors.eye} strokeWidth="2.2" strokeLinecap="round"/>}
          {mood === 'hungry' && <path d="M90 125 Q100 118 110 125" stroke={colors.eye} strokeWidth="2.2" fill="none" strokeLinecap="round"/>}
        </g>
      );
    }

    // elder
    return (
      <g>
        <ellipse cx="100" cy="178" rx="50" ry="6" fill="#000" opacity="0.12"/>
        {/* body */}
        <path d="M35 100 Q35 42 100 42 Q165 42 165 100 Q165 170 100 175 Q35 170 35 100 Z" fill={colors.body}/>
        {/* darker back */}
        <path d="M35 100 Q35 42 100 42 Q165 42 165 100 Q165 110 100 108 Q35 110 35 100 Z" fill={colors.bodyDark} opacity="0.3"/>
        {/* belly */}
        <ellipse cx="100" cy="130" rx="42" ry="34" fill={colors.belly}/>
        {/* belly stripes */}
        <path d="M75 120 Q100 125 125 120" stroke={colors.body} strokeWidth="1.5" fill="none" opacity="0.3"/>
        <path d="M72 135 Q100 140 128 135" stroke={colors.body} strokeWidth="1.5" fill="none" opacity="0.3"/>
        {/* tentacles */}
        {[48, 72, 100, 128, 152].map((x, i) => (
          <g key={i}>
            <ellipse cx={x} cy="170" rx="11" ry="18" fill={colors.body}/>
            <circle cx={x} cy="178" r="2.5" fill={colors.bodyDark}/>
            <circle cx={x} cy="170" r="1.5" fill={colors.bodyDark} opacity="0.5"/>
          </g>
        ))}
        {/* crown */}
        <path d="M75 48 L80 30 L88 45 L100 28 L112 45 L120 30 L125 48 Z" fill="#d4a84a"/>
        <circle cx="100" cy="35" r="3" fill="#f0d878"/>
        <circle cx="85" cy="42" r="2" fill="#f0d878"/>
        <circle cx="115" cy="42" r="2" fill="#f0d878"/>
        {/* eyes - wise */}
        {renderEye(80, 100, 6)}
        {renderEye(120, 100, 6)}
        {/* wise beard */}
        <path d="M95 135 Q100 150 105 135" stroke={colors.eye} strokeWidth="1" fill="none" opacity="0.3"/>
        {/* cheeks */}
        <circle cx="66" cy="118" r="7" fill={colors.cheek} opacity="0.7"/>
        <circle cx="134" cy="118" r="7" fill={colors.cheek} opacity="0.7"/>
        {/* mouth */}
        {mood === 'happy' && <path d="M88 122 Q100 132 112 122" stroke={colors.eye} strokeWidth="2.5" fill="none" strokeLinecap="round"/>}
        {mood !== 'happy' && <path d="M90 124 Q100 128 110 124" stroke={colors.eye} strokeWidth="2.2" fill="none" strokeLinecap="round"/>}
      </g>
    );
  };

  return (
    <div style={{ width: size, height: size, ...wobbleStyle }}>
      <svg viewBox="0 0 200 200" width={size} height={size}>
        <defs>
          <radialGradient id="eggShine" cx="0.35" cy="0.3" r="0.6">
            <stop offset="0" stopColor="#fff" stopOpacity="0.6"/>
            <stop offset="1" stopColor="#fff" stopOpacity="0"/>
          </radialGradient>
        </defs>
        {renderBody()}
      </svg>
    </div>
  );
}

Object.assign(window, { Creature, CreatureStages, getCreatureStage });
