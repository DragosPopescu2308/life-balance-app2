import { useQuery, useQueryClient } from '@tanstack/react-query'
import { savings } from '../api/endpoints'

export default function SavingsPanel({ month }) {
  const qc = useQueryClient()
  const { data } = useQuery(['saving-settings'], savings.settings)
  const { data: monthly = 0 } = useQuery(['saving-month', month], () => savings.monthly(month))

  if (!data) return null

  async function update(p){
    await savings.updateSettings(p)
    qc.invalidateQueries(['saving-settings'])
  }

  return (
    <div className="bg-slate-800 p-4 rounded space-y-3">
      <div className="font-bold text-lg">Auto Savings</div>

      <label className="flex items-center gap-2">
        <input
          type="checkbox"
          checked={data.active}
          onChange={e => update({ ...data, active: e.target.checked })}
        />
        Enable auto-save
      </label>

      <div>
        <div className="text-sm text-slate-400">Percentage</div>
        <input
          type="number"
          className="bg-slate-900 p-2 rounded w-24"
          value={data.percentage}
          onChange={e => update({ ...data, percentage: Number(e.target.value) })}
        /> %
      </div>

      <div className="pt-2 border-t border-slate-700">
        <div className="text-sm text-slate-400">Saved this month</div>
        <div className="text-xl font-bold text-blue-400">${monthly.toFixed(2)}</div>
      </div>
    </div>
  )
}
