import React, { useEffect, useMemo, useRef, useState } from 'react'
import AppShell from '../layout/AppShell'
import { useAuth } from '../lib/auth'
import { profile as profileApi, savings as savingsApi } from '../api/endpoints'
import { toast } from '../lib/toast'
import { API_BASE } from '../api/client'
import Modal from '../components/Modal'

function initials(nameOrEmail) {
  const s = (nameOrEmail || '').trim()
  if (!s) return '?'
  const parts = s.split(/\s+/).slice(0, 2)
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[1][0]).toUpperCase()
}

export default function ProfilePage() {
  const { user, logout } = useAuth()
  const fileRef = useRef(null)

  const initial = useMemo(() => ({
    fullName: user?.fullName ?? '',
    about: user?.about ?? ''
  }), [user])

  const [isEditing, setIsEditing] = useState(false)
  const [fullName, setFullName] = useState(initial.fullName)
  const [about, setAbout] = useState(initial.about)
  const [saving, setSaving] = useState(false)
  const [avatarOpen, setAvatarOpen] = useState(false)

  // ✅ Savings settings state
  const [savingsLoading, setSavingsLoading] = useState(true)
  const [savingsSaving, setSavingsSaving] = useState(false)
  const [savingsActive, setSavingsActive] = useState(true)
  const [savingsPercentage, setSavingsPercentage] = useState(10)

  useEffect(() => {
    setFormFromUser()

  }, [user])

  function setFormFromUser() {
    setFullName(user?.fullName ?? '')
    setAbout(user?.about ?? '')
  }

  const displayName = user?.fullName || user?.email || 'User'
  const avatarSrc = user?.avatarUrl ? `${API_BASE}${user.avatarUrl}?t=${Date.now()}` : null


  useEffect(() => {
    let mounted = true

    async function loadSettings() {
      if (!user) {
        if (mounted) setSavingsLoading(false)
        return
      }

      setSavingsLoading(true)
      try {
        const s = await savingsApi.settings()
        if (!mounted) return

        setSavingsActive(Boolean(s?.active))
        setSavingsPercentage(Number(s?.percentage ?? 10))
      } catch (err) {
        if (!mounted) return
        toast(err.message || 'Could not load savings settings', 'error')
      } finally {
        if (mounted) setSavingsLoading(false)
      }
    }

    loadSettings()
    return () => { mounted = false }
  }, [user])

  async function handleSave() {
    setSaving(true)
    try {
      await profileApi.update({ fullName, about })
      toast('Profile updated', 'success')
      window.location.reload()
    } catch (err) {
      toast(err.message || 'Update failed', 'error')
    } finally {
      setSaving(false)
      setIsEditing(false)
    }
  }

  async function handleLogout() {
    try {
      await logout()
      window.location.href = '/login'
    } catch (err) {
      toast(err.message || 'Logout failed', 'error')
    }
  }

  async function handleAvatarUpload(e) {
    const file = e.target.files?.[0]
    if (!file) return
    try {
      await profileApi.uploadAvatar(file)
      toast('Avatar updated', 'success')
      if (fileRef.current) fileRef.current.value = ''
      window.location.reload()
    } catch (err) {
      toast(err.message || 'Upload failed', 'error')
    }
  }

  async function handleAvatarDelete() {
    if (!user?.avatarUrl) return
    if (!window.confirm('Delete avatar?')) return

    try {
      await profileApi.deleteAvatar()
      toast('Avatar deleted', 'success')
      window.location.reload()
    } catch (err) {
      toast(err.message || 'Delete failed', 'error')
    }
  }


  async function handleSavingsSave() {
    const pct = Number(savingsPercentage)

    if (Number.isNaN(pct)) {
      toast('Percentage must be a number.', 'error')
      return
    }
    if (pct < 0 || pct > 100) {
      toast('Percentage must be between 0 and 100.', 'error')
      return
    }

    setSavingsSaving(true)
    try {
      await savingsApi.updateSettings({
        percentage: pct,
        active: Boolean(savingsActive),
      })
      toast('Savings settings updated', 'success')
    } catch (err) {
      toast(err.message || 'Could not update savings settings', 'error')
    } finally {
      setSavingsSaving(false)
    }
  }

  return (
    <AppShell title="Profile">
      <div className="max-w-3xl space-y-4">

        {/* Avatar card */}
        <div className="bg-slate-800 rounded p-6 border border-slate-700/60">
          <div className="flex items-center gap-4">
            <button
              className="w-16 h-16 rounded-full bg-slate-900 overflow-hidden flex items-center justify-center font-bold text-xl border border-slate-700 hover:opacity-95"
              onClick={() => { if (avatarSrc) setAvatarOpen(true) }}
              title={avatarSrc ? 'Click to view larger' : ''}
              type="button"
            >
              {avatarSrc ? (
                <img src={avatarSrc} alt="avatar" className="w-full h-full object-cover" />
              ) : (
                initials(displayName)
              )}
            </button>

            <div className="flex-1">
              <div className="text-lg font-semibold">Profile photo</div>
              <div className="text-sm text-slate-400">jpg / png / webp</div>

              <div className="mt-3 flex items-center gap-2">
                <label className="inline-flex items-center gap-2 text-sm cursor-pointer">
                  <input
                    ref={fileRef}
                    type="file"
                    accept="image/*"
                    onChange={handleAvatarUpload}
                    className="hidden"
                  />
                  <span className="px-3 py-1 rounded bg-slate-700 hover:bg-slate-600">
                    Upload
                  </span>
                </label>

                <button
                  className="px-3 py-1 rounded bg-red-600 hover:bg-red-500 text-sm disabled:opacity-50"
                  onClick={handleAvatarDelete}
                  disabled={!user?.avatarUrl}
                >
                  Delete
                </button>

                {avatarSrc && (
                  <button
                    className="px-3 py-1 rounded bg-slate-700 hover:bg-slate-600 text-sm"
                    onClick={() => setAvatarOpen(true)}
                    type="button"
                  >
                    View
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* ✅ Savings settings card */}
        <div className="bg-slate-800 rounded p-6 border border-slate-700/60">
          <div className="flex items-center justify-between gap-4">
            <div>
              <div className="text-lg font-semibold">Savings settings</div>
              <div className="text-sm text-slate-400">
                Auto-save a percentage from each new income.
              </div>
            </div>

            <button
              className="px-4 py-2 rounded bg-green-600 hover:bg-green-500 disabled:opacity-60"
              onClick={handleSavingsSave}
              disabled={savingsLoading || savingsSaving}
              type="button"
            >
              {savingsSaving ? 'Saving...' : 'Save'}
            </button>
          </div>

          <hr className="border-slate-700 my-5" />

          {savingsLoading ? (
            <div className="text-sm text-slate-400">Loading...</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
              <label className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={Boolean(savingsActive)}
                  onChange={e => setSavingsActive(e.target.checked)}
                />
                <span className="text-sm text-slate-200">Active</span>
              </label>

              <label className="block">
                <div className="text-sm text-slate-300 mb-1">Percentage</div>
                <input
                  type="number"
                  min={0}
                  max={100}
                  step={0.1}
                  className="w-full p-2 rounded bg-slate-900 outline-none focus:ring-2 focus:ring-slate-600"
                  value={savingsPercentage}
                  onChange={e => setSavingsPercentage(e.target.value)}
                />
                <div className="text-xs text-slate-400 mt-1">0–100%</div>
              </label>

              <div className="text-sm text-slate-300">
                Example: income 1000 → saves <b>{(Number(savingsPercentage || 0) / 100 * 1000).toFixed(2)}</b>
              </div>
            </div>
          )}
        </div>

        {/* Profile main card */}
        <div className="bg-slate-800 rounded p-6 border border-slate-700/60">
          <div className="flex items-start justify-between gap-4">
            <div>
              <div className="text-2xl font-bold">{user?.fullName ?? 'User'}</div>
              <div className="text-slate-300">{user?.email ?? ''}</div>
            </div>

            <div className="flex gap-2">
              {!isEditing && (
                <button
                  className="px-4 py-2 rounded bg-slate-700 hover:bg-slate-600"
                  onClick={() => {
                    setFormFromUser()
                    setIsEditing(true)
                  }}
                >
                  Edit
                </button>
              )}

              {isEditing && (
                <>
                  <button
                    className="px-4 py-2 rounded bg-slate-700 hover:bg-slate-600"
                    onClick={() => {
                      setFormFromUser()
                      setIsEditing(false)
                    }}
                    disabled={saving}
                  >
                    Cancel
                  </button>
                  <button
                    className="px-4 py-2 rounded bg-green-600 hover:bg-green-500 disabled:opacity-60"
                    onClick={handleSave}
                    disabled={saving}
                  >
                    {saving ? 'Saving...' : 'Save'}
                  </button>
                </>
              )}

              <button
                className="px-4 py-2 rounded bg-red-600 hover:bg-red-500"
                onClick={handleLogout}
              >
                Logout
              </button>
            </div>
          </div>

          <hr className="border-slate-700 my-5" />

          <div className="space-y-2">
            <div className="text-lg font-semibold">About</div>

            {!isEditing && (
              <div className="text-slate-200 whitespace-pre-wrap">
                {about?.trim()
                  ? about
                  : <span className="text-slate-400">No about yet. Click Edit and add something.</span>
                }
              </div>
            )}

            {isEditing && (
              <div className="space-y-3">
                <label className="block">
                  <div className="text-sm text-slate-300 mb-1">Full name</div>
                  <input
                    className="w-full p-2 rounded bg-slate-900 outline-none focus:ring-2 focus:ring-slate-600"
                    value={fullName}
                    onChange={e => setFullName(e.target.value)}
                    maxLength={100}
                  />
                </label>

                <label className="block">
                  <div className="text-sm text-slate-300 mb-1">About</div>
                  <textarea
                    className="w-full p-2 rounded bg-slate-900 outline-none focus:ring-2 focus:ring-slate-600"
                    rows={6}
                    value={about}
                    onChange={e => setAbout(e.target.value)}
                    maxLength={2000}
                    placeholder="Write something about you..."
                  />
                  <div className="text-xs text-slate-400 mt-1">
                    {about.length}/2000
                  </div>
                </label>
              </div>
            )}
          </div>
        </div>

      </div>

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
    </AppShell>
  )
}
