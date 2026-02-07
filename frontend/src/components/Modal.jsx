import React from 'react'

export default function Modal({ open, title, onClose, children }) {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/60" onMouseDown={onClose} />

      <div
        className="relative z-10 w-full max-w-3xl rounded-xl bg-slate-900 border border-slate-700 shadow-xl"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div className="flex justify-between items-center px-4 py-3 border-b border-slate-700">
          <h3 className="text-lg font-bold">{title}</h3>
          <button onClick={onClose} className="px-3 py-1 rounded bg-slate-800 hover:bg-slate-700">
            Close
          </button>
        </div>

        <div className="p-4 max-h-[75vh] overflow-auto">
          {children}
        </div>
      </div>
    </div>
  )
}
