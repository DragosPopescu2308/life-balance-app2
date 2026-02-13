import React, { useRef } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { incomeAttachments } from '../api/endpoints'
import { toast } from '../lib/toast'
import { API_BASE } from '../api/client'

function isImage(ct){ return typeof ct === 'string' && ct.startsWith('image/') }
function isPdf(ct){ return ct === 'application/pdf' }

export default function IncomeAttachments({ incomeId }) {
  const qc = useQueryClient()
  const fileRef = useRef(null)

  const { data = [], isLoading } = useQuery(
    ['income-attachments', incomeId],
    () => incomeAttachments.list(incomeId),
    { enabled: !!incomeId }
  )

  async function handleUpload(e) {
    const file = e.target.files?.[0]
    if (!file) return
    try {
      await incomeAttachments.upload(incomeId, file)
      toast('Attachment uploaded.', 'success')
      qc.invalidateQueries(['income-attachments', incomeId])
      if (fileRef.current) fileRef.current.value = ''
    } catch (err) {
      toast(err.message || 'Upload failed', 'error')
    }
  }

  async function handleDelete(id) {
    if (!window.confirm('Delete this attachment?')) return
    try {
      await incomeAttachments.delete(id)
      toast('Deleted.', 'success')
      qc.invalidateQueries(['income-attachments', incomeId])
    } catch (err) {
      toast(err.message || 'Delete failed', 'error')
    }
  }

  return (
    <div className="mt-3 border-t border-slate-700/60 pt-3">
      <div className="flex items-center justify-between gap-3">
        <div className="text-sm font-semibold text-slate-200">Attachments</div>

        <label className="inline-flex items-center gap-2 text-sm cursor-pointer">
          <input
            ref={fileRef}
            type="file"
            accept="image/*,application/pdf"
            onChange={handleUpload}
            className="hidden"
          />
          <span className="px-3 py-1 rounded bg-slate-900 hover:bg-slate-800 border border-slate-700">
            + Upload
          </span>
        </label>
      </div>

      {isLoading && <div className="text-sm text-slate-400 mt-2">Loading...</div>}
      {!isLoading && data.length === 0 && <div className="text-sm text-slate-400 mt-2">No attachments</div>}

      <div className="mt-3 grid grid-cols-1 md:grid-cols-2 gap-3">
        {data.map(a => {
          const fileUrl = `${API_BASE}/income-attachments/${a.id}/file`

          return (
            <div key={a.id} className="bg-slate-900/40 border border-slate-700/60 rounded p-3">
              <div className="flex items-start justify-between gap-3">
                <div className="min-w-0">
                  <div className="text-sm font-semibold truncate">{a.originalFilename}</div>
                  <div className="text-xs text-slate-400">{a.contentType} • {a.fileSize} bytes</div>
                </div>

                <div className="flex gap-2">
                  <a href={fileUrl} target="_blank" rel="noreferrer"
                     className="px-2 py-1 text-xs rounded bg-slate-800 hover:bg-slate-700">
                    Open
                  </a>
                  <button
                    className="px-2 py-1 text-xs rounded bg-red-600 hover:bg-red-500"
                    onClick={() => handleDelete(a.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>

              <div className="mt-3">
                {isImage(a.contentType) && (
                  <img
                    src={fileUrl}
                    alt={a.originalFilename}
                    className="w-full h-36 object-contain rounded bg-slate-950 border border-slate-700/50"
                    loading="lazy"
                  />
                )}

                {isPdf(a.contentType) && (
                  <div className="text-sm text-slate-300">
                    PDF attached.{' '}
                    <a className="underline" href={fileUrl} target="_blank" rel="noreferrer">
                      Click to open
                    </a>
                  </div>
                )}
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
