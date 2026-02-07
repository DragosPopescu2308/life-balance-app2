import React from 'react'

function Logo() {
  return (
    <div className="flex items-center gap-3 select-none">
      <div className="relative w-11 h-11 rounded-xl bg-slate-900 border border-slate-700 overflow-hidden shadow">
        {/* soft glow */}
        <div className="absolute inset-0 bg-gradient-to-br from-emerald-500/20 via-transparent to-cyan-400/15" />

        {/* animated line chart */}
        <svg
          className="absolute inset-0"
          viewBox="0 0 44 44"
          fill="none"
          aria-hidden="true"
        >
          {/* grid (subtle) */}
          <path
            d="M6 30H38M6 22H38M6 14H38"
            className="stroke-slate-700/50"
            strokeWidth="1"
          />
          <path
            d="M10 34V10M18 34V10M26 34V10M34 34V10"
            className="stroke-slate-700/30"
            strokeWidth="1"
          />

          {/* chart path */}
          <path
            d="M8 30 L15 24 L21 26 L28 18 L36 14"
            className="stroke-emerald-400"
            strokeWidth="2.6"
            strokeLinecap="round"
            strokeLinejoin="round"
            style={{
              strokeDasharray: 80,
              strokeDashoffset: 80,
              animation: 'lb_draw 2.2s ease-in-out infinite'
            }}
          />

          {/* moving dot */}
          <circle
            r="2.5"
            className="fill-emerald-300"
            style={{ animation: 'lb_dot 2.2s ease-in-out infinite' }}
          />
        </svg>

        {/* $ badge */}
        <div className="absolute right-1 top-1 w-6 h-6 rounded-full bg-slate-950/90 border border-slate-700 flex items-center justify-center">
          <span className="text-[12px] font-black text-emerald-300" style={{ animation: 'lb_pulse 1.3s ease-in-out infinite' }}>
            $
          </span>
        </div>

        {/* keyframes */}
        <style>{`
          @keyframes lb_draw {
            0%   { stroke-dashoffset: 80; opacity: .7; }
            45%  { stroke-dashoffset: 0;  opacity: 1; }
            75%  { stroke-dashoffset: 0;  opacity: 1; }
            100% { stroke-dashoffset: -80; opacity: .6; }
          }

          @keyframes lb_dot {
            0%   { transform: translate(8px, 30px); opacity: 0; }
            20%  { opacity: 1; }
            45%  { transform: translate(36px, 14px); opacity: 1; }
            75%  { transform: translate(36px, 14px); opacity: 1; }
            100% { transform: translate(36px, 14px); opacity: 0; }
          }

          @keyframes lb_pulse {
            0%,100% { transform: scale(1); }
            50%     { transform: scale(1.12); }
          }
        `}</style>
      </div>

      <div className="leading-tight">
        <div className="font-extrabold tracking-wide text-slate-100">Life Balance</div>
        <div className="text-xs text-slate-300">Time &amp; Money</div>
      </div>
    </div>
  )
}

export default function Topbar({ children }) {
  return (
    <header className="w-full bg-slate-800 px-4 py-3 flex items-center justify-between border-b border-slate-700/60">
      <Logo />
      <div>{children}</div>
    </header>
  )
}
