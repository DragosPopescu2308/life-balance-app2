import React, { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Sidebar from '../components/Sidebar'
import Topbar from '../components/Topbar'
import ToastContainer from '../components/ToastContainer'
import { useAuth } from '../lib/auth'
import { API_BASE } from '../api/client'
import Modal from '../components/Modal'

function initials(nameOrEmail) {
  const s = (nameOrEmail || '').trim()
  if (!s) return '?'
  const parts = s.split(/\s+/).slice(0, 2)
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[1][0]).toUpperCase()
}

export default function AppShell({ title, right, children }) {
  const { user } = useAuth()
  const nav = useNavigate()
  const [open, setOpen] = useState(false)
  const [avatarOpen, setAvatarOpen] = useState(false)

  const displayName = useMemo(() => user?.fullName || user?.email || 'User', [user])

  const avatarSrc = useMemo(() => {
    return user?.avatarUrl ? `${API_BASE}${user.avatarUrl}?t=${Date.now()}` : null
  }, [user?.avatarUrl])

  return (
    <div className="min-h-screen flex bg-slate-900">
      <Sidebar />

      <div className="flex-1">
        <Topbar>
          <div className="flex items-center gap-3">
            {right}

            <div className="relative">
              <button
                className="flex items-center gap-2 bg-slate-800 px-3 py-2 rounded hover:bg-slate-700 border border-slate-700/60"
                onClick={() => setOpen(v => !v)}
              >
                <div className="w-8 h-8 rounded-full bg-slate-900 overflow-hidden flex items-center justify-center font-bold text-xs border border-slate-700">
                  {avatarSrc ? (
                    <img
                      src={avatarSrc}
                      alt="avatar"
                      className="w-full h-full object-cover cursor-pointer"
                      onClick={(e) => { e.stopPropagation(); setAvatarOpen(true) }}
                      onError={(e) => { e.currentTarget.style.display = 'none' }}
                    />
                  ) : (
                    initials(displayName)
                  )}
                </div>

                <div className="hidden md:block">{displayName}</div>
              </button>

              {open && (
                <div className="absolute right-0 mt-2 w-48 bg-slate-900 border border-slate-700 rounded shadow-lg overflow-hidden">
                  <button
                    className="w-full text-left px-4 py-2 hover:bg-slate-800"
                    onClick={() => {
                      setOpen(false)
                      nav('/app/profile')
                    }}
                  >
                    Profile
                  </button>
                </div>
              )}
            </div>
          </div>
        </Topbar>

        <main className="p-6">
          {title && <h1 className="text-3xl font-bold mb-6">{title}</h1>}
          {children}
        </main>
      </div>

      <ToastContainer />

      <Modal open={avatarOpen} title="Profile photo" onClose={() => setAvatarOpen(false)}>
        {avatarSrc ? (
          <img
            src={avatarSrc}
            alt="avatar large"
            className="w-full max-h-[70vh] object-contain rounded bg-black"
          />
        ) : (
          <div className="text-slate-300">No avatar</div>
        )}
      </Modal>
    </div>
  )
}
