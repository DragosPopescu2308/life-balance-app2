import React, { useState } from 'react'
import AppShell from '../layout/AppShell'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { categories } from '../api/endpoints'
import { toast } from '../lib/toast'

export default function CategoriesPage() {
  const qc = useQueryClient()

  const { data = [], isLoading } = useQuery(['categories'], () => categories.all())

  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null) // category object or null
  const [name, setName] = useState('')
  const [type, setType] = useState('EXPENSE')

  function startAdd() {
    setEditing(null)
    setName('')
    setType('EXPENSE')
    setOpen(true)
  }

  function startEdit(cat) {
    setEditing(cat)
    setName(cat.name)
    setType(cat.type)
    setOpen(true)
  }

  async function submit(e) {
    e.preventDefault()
    try {
      if (editing) {
        await categories.update(editing.id, { name, type })
        toast('Category updated.', 'success')
      } else {
        await categories.create({ name, type })
        toast('Category created.', 'success')
      }
      setOpen(false)
      qc.invalidateQueries(['categories'])
    } catch (err) {
      toast(err.message || 'Save failed', 'error')
    }
  }

  async function remove(cat) {
    const ok = window.confirm(`Delete category "${cat.name}"?`)
    if (!ok) return

    try {
      await categories.delete(cat.id)
      toast('Category deleted.', 'success')
      qc.invalidateQueries(['categories'])
    } catch (err) {
      toast(err.message || 'Delete failed', 'error')
    }
  }

  return (
    <AppShell
      title="Categories"
      right={
        <button className="bg-green-600 px-4 py-2 rounded font-semibold" onClick={startAdd}>
          + Add
        </button>
      }
    >
      <div className="bg-slate-800 rounded">
        <div className="p-4 border-b border-slate-700 flex justify-between items-center">
          <div className="font-bold">Your categories</div>
          <div className="text-slate-300">{data.length} items</div>
        </div>

        {isLoading && <div className="p-4">Loading...</div>}
        {!isLoading && data.length === 0 && <div className="p-4">No categories</div>}

        <ul>
          {data.map(c => (
            <li key={c.id} className="p-4 border-t border-slate-700 flex justify-between items-center">
              <div>
                <div className="font-semibold">{c.name}</div>
                <div className="text-sm text-slate-400">{c.type}</div>
              </div>

              <div className="flex gap-2">
                <button className="px-4 py-2 rounded bg-slate-700" onClick={() => startEdit(c)}>
                  Edit
                </button>
                <button className="px-4 py-2 rounded bg-red-600" onClick={() => remove(c)}>
                  Delete
                </button>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {open && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="absolute inset-0 bg-black/50" onClick={() => setOpen(false)} />
          <div className="relative z-10 w-full max-w-lg bg-slate-900 rounded p-5">
            <div className="flex justify-between items-center mb-4">
              <div className="font-bold text-lg">{editing ? 'Edit Category' : 'Add Category'}</div>
              <button className="px-3 py-1 rounded bg-slate-800" onClick={() => setOpen(false)}>
                Close
              </button>
            </div>

            <form onSubmit={submit} className="space-y-3">
              <input
                className="w-full bg-slate-800 p-2 rounded"
                placeholder="Name"
                value={name}
                onChange={e => setName(e.target.value)}
                required
              />

              <select className="w-full bg-slate-800 p-2 rounded" value={type} onChange={e => setType(e.target.value)}>
                <option value="EXPENSE">EXPENSE</option>
                <option value="INCOME">INCOME</option>
              </select>

              <button className="w-full bg-green-600 p-2 rounded font-semibold">
                {editing ? 'Save changes' : 'Create'}
              </button>
            </form>
          </div>
        </div>
      )}
    </AppShell>
  )
}
