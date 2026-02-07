import React, { useMemo, useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { expenses, categories } from '../../api/endpoints'
import { toast } from '../../lib/toast'
import ExpenseReceipts from '../../components/ExpenseReceipts'

export default function ExpensesTab({ month }) {
  const qc = useQueryClient()

  const { data = [], isLoading } = useQuery(['expenses', month], () => expenses.list(month))
  const { data: cats = [] } = useQuery(['categories', 'EXPENSE'], () => categories.byType('EXPENSE'))

  const [form, setForm] = useState(() => ({
    title: '',
    amount: '',
    dateSpent: new Date().toISOString().slice(0, 10),
    notes: '',
    categoryId: ''
  }))

  const catOptions = useMemo(() => cats ?? [], [cats])

  async function handleCreate(e) {
    e.preventDefault()

    if (!form.categoryId) {
      toast('Choose a category (required).', 'error')
      return
    }

    try {
      await expenses.create({
        title: form.title,
        amount: form.amount,
        dateSpent: form.dateSpent,
        notes: form.notes,
        categoryId: Number(form.categoryId)
      })
      toast('Expense added.', 'success')
      setForm(f => ({ ...f, title: '', amount: '', notes: '' }))
      qc.invalidateQueries(['expenses', month])
      qc.invalidateQueries(['dashboard', month])
    } catch (err) {
      toast(err.message || 'Create failed', 'error')
    }
  }

  async function handleDelete(id) {
    const ok = window.confirm('Delete this expense?')
    if (!ok) return

    try {
      await expenses.delete(id)
      toast('Deleted.', 'success')
      qc.invalidateQueries(['expenses', month])
      qc.invalidateQueries(['dashboard', month])
    } catch (err) {
      toast(err.message || 'Delete failed', 'error')
    }
  }

  return (
    <div className="space-y-4">
      <form onSubmit={handleCreate} className="bg-slate-900/40 rounded p-4 space-y-3">
        <div className="font-bold">Add Expense</div>

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
            value={form.dateSpent}
            onChange={e => setForm(f => ({ ...f, dateSpent: e.target.value }))}
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
      {!isLoading && data.length === 0 && <div className="p-4">No expenses</div>}

      <ul className="space-y-3">
        {data.map(x => (
          <li key={x.id} className="p-3 bg-slate-800 rounded">
            <div className="flex justify-between items-center">
              <div>
                <div className="font-bold">{x.title}</div>
                <div className="text-sm text-slate-400">{x.dateSpent} • {x.notes || '-'}</div>
              </div>

              <div className="flex items-center gap-2">
                <div className="px-2 py-1 bg-slate-900 rounded">{x.amount}</div>
                <button className="px-3 py-1 rounded bg-red-600" onClick={() => handleDelete(x.id)}>
                  Delete
                </button>
              </div>
            </div>

            <ExpenseReceipts expenseId={x.id} />
          </li>
        ))}

      </ul>
    </div>
  )
}
