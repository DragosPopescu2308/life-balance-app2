import React from 'react'
import AppShell from '../layout/AppShell'
import { dashboard } from '../api/endpoints'
import { useQuery } from '@tanstack/react-query'
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip } from 'recharts'
import FinanceChart from '../components/FinanceChart'

export default function DashboardPage() {
  const [month, setMonth] = React.useState(() => {
    const now = new Date()
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  })

  const { data, isLoading } = useQuery(['dashboard', month], () => dashboard.monthly(month))

  const sortedCats = React.useMemo(() => {
    const arr = [...(data?.topExpenseCategories || [])]
    arr.sort((a, b) => Number(b.total) - Number(a.total))
    return arr
  }, [data])

  const pieData = sortedCats.map(c => ({
    name: c.categoryName,
    value: Number(c.total)
  }))

  return (
    <AppShell
      title="Dashboard"
      right={
        <input
          type="month"
          value={month}
          onChange={e => setMonth(e.target.value)}
          className="bg-slate-800 p-2 rounded"
        />
      }
    >
      {/* TOP CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard label="Income" value={data?.totalIncome} loading={isLoading} color="text-green-400" />
        <StatCard label="Expenses" value={data?.totalExpense} loading={isLoading} color="text-red-400" />
        <StatCard label="Savings" value={data?.totalSavings} loading={isLoading} color="text-blue-400" />
        <StatCard label="Net Balance" value={data?.net} loading={isLoading} color="text-yellow-300" />
      </div>

      {/* BAR CHART */}
      <FinanceChart
        income={data?.totalIncome || 0}
        expenses={data?.totalExpense || 0}
        savings={data?.totalSavings || 0}
      />

      {/* PIE CHART */}
      <div className="bg-slate-800 rounded p-4 mt-6">
        <h4 className="font-bold mb-2">Expense distribution</h4>
        {pieData.length > 0 && (
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={pieData} dataKey="value" nameKey="name" innerRadius={50} outerRadius={90}>
                {pieData.map((_, idx) => (
                  <Cell key={idx} fill={['#f97316','#fb923c','#f59e0b','#f43f5e','#60a5fa','#22c55e'][idx%6]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        )}
      </div>
    </AppShell>
  )
}

function StatCard({ label, value, loading, color }) {
  return (
    <div className="bg-slate-800 rounded p-4">
      {label}
      <div className={`text-2xl font-bold ${color}`}>
        {loading ? '...' : value ?? 0}
      </div>
    </div>
  )
}
