import React, { useMemo, useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { incomes, categories } from '../../api/endpoints'
import { toast } from '../../lib/toast'
import IncomeAttachments from '../../components/IncomeAttachments'

export default function IncomesTab({ month }) {
  const qc = useQueryClient()

  const { data = [], isLoading } = useQuery(['incomes', month], () => incomes.list(month))
  const { data: cats = [] } = useQuery(['categories', 'INCOME'], () => categories.byType('INCOME'))

  const [form, setForm] = useState(() => ({
    title: '',
    amount: '',
    dateReceived: new Date().toISOString().slice(0, 10),
    notes: '',
    categoryId: ''
  }))

  const catOptions = useMemo(() => cats ?? [], [cats])

  async function handleCreate(e) {
    e.preventDefault()  // nu lasam browser sa faca refresh la submit

    if (!form.categoryId) {
      toast('Choose a category (required).', 'error')
      return
    }

    try {
      await incomes.create({
        title: form.title,
        amount: form.amount,
        dateReceived: form.dateReceived,
        notes: form.notes,
        categoryId: Number(form.categoryId)
      })
      toast('Income added.', 'success')
      setForm(f => ({ ...f, title: '', amount: '', notes: '' }))
      qc.invalidateQueries(['incomes', month])
      qc.invalidateQueries(['dashboard', month])
    } catch (err) {
      toast(err.message || 'Create failed', 'error')
    }
  }

  async function handleDelete(id) {
    const ok = window.confirm('Delete this income?')
    if (!ok) return

    try {
      await incomes.delete(id)
      toast('Deleted.', 'success')
      qc.invalidateQueries(['incomes', month])
      qc.invalidateQueries(['dashboard', month])
    } catch (err) {
      toast(err.message || 'Delete failed', 'error')
    }
  }

  return (
    <div className="space-y-4">
      <form onSubmit={handleCreate} className="bg-slate-900/40 rounded p-4 space-y-3">
        <div className="font-bold">Add Income</div>

        <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
          <input
            className="bg-slate-900 p-2 rounded"
            placeholder="Title"
            value={form.title}
            onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
            required
          />
          <input
            className="bg-slate-900 p-2 rounded"
            placeholder="Amount"
            value={form.amount}
            onChange={e => setForm(f => ({ ...f, amount: e.target.value }))}
            required
          />
          <input
            className="bg-slate-900 p-2 rounded"
            type="date"
            value={form.dateReceived}
            onChange={e => setForm(f => ({ ...f, dateReceived: e.target.value }))}
            required
          />
          <select
            className="bg-slate-900 p-2 rounded"
            value={form.categoryId}
            onChange={e => setForm(f => ({ ...f, categoryId: e.target.value }))}
            required
          >
            <option value="">Select category</option>
            {catOptions.map(c => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>

          <button className="bg-green-600 px-3 py-2 rounded font-semibold">Add</button>
        </div>

        <textarea
          className="bg-slate-900 p-2 rounded w-full"
          placeholder="Notes"
          value={form.notes}
          onChange={e => setForm(f => ({ ...f, notes: e.target.value }))}
          rows={2}
        />
      </form>

      {isLoading && <div>Loading...</div>}
      {!isLoading && data.length === 0 && <div className="p-4">No incomes</div>}

      <ul className="space-y-3">
        {data.map(e => (
          <li key={e.id} className="p-3 bg-slate-800 rounded">
            <div className="flex justify-between items-center">
              <div>
                <div className="font-bold">{e.title}</div>
                <div className="text-sm text-slate-400">{e.dateReceived} • {e.notes || '-'}</div>
              </div>

              <div className="flex items-center gap-2">
                <div className="px-2 py-1 bg-slate-900 rounded">{e.amount}</div>
                <button className="px-3 py-1 rounded bg-red-600" onClick={() => handleDelete(e.id)}>
                  Delete
                </button>
              </div>
            </div>


            <IncomeAttachments incomeId={e.id} />
          </li>
        ))}
      </ul>
    </div>
  )
}
