import React from 'react'
import AppShell from '../layout/AppShell'
import { dashboard } from '../api/endpoints'
import { useQuery } from '@tanstack/react-query'
import {
  ResponsiveContainer,
  PieChart, Pie, Cell, Tooltip,
  AreaChart, Area, LineChart, Line, XAxis, YAxis, Legend, CartesianGrid
} from 'recharts'
import FinanceChart from '../components/FinanceChart'

export default function DashboardPage() {
  const [month, setMonth] = React.useState(() => {
    const now = new Date()
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  })

  const { data, isLoading } = useQuery(['dashboard', month], () => dashboard.monthly(month))


  const { data: trend = [], isLoading: trendLoading } = useQuery(
    ['dashboardTrend', month],
    () => dashboard.trend({ months: 6, endMonth: month }),
    { keepPreviousData: true }
  )

  const sortedCats = React.useMemo(() => {
    const arr = [...(data?.topExpenseCategories || [])]
    arr.sort((a, b) => Number(b.total) - Number(a.total))
    return arr
  }, [data])

  const pieData = sortedCats.map(c => ({
    name: c.categoryName,
    value: Number(c.total)
  }))

  const trendChartData = React.useMemo(() => {
    return (trend || []).map(p => ({
      month: p.month, // "YYYY-MM"
      Income: Number(p.income || 0),
      Expense: Number(p.expense || 0),
      Savings: Number(p.savings || 0),
      Net: Number(p.net || 0),
    }))
  }, [trend])

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

      {/* BAR CHART (luna curentă selectată) */}
      <FinanceChart
        income={data?.totalIncome || 0}
        expenses={data?.totalExpense || 0}
        savings={data?.totalSavings || 0}
      />

      {/* TREND CHARTS */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mt-6">
        {/* 1) Income vs Expense trend */}
        <div className="bg-slate-800 rounded p-4">
          <div className="flex items-center justify-between mb-2">
            <div className="font-bold">Income vs Expense Trend</div>
            <div className="text-xs text-slate-400">Last 6 months</div>
          </div>

          {(trendLoading || trendChartData.length === 0) && (
            <div className="text-sm text-slate-400">{trendLoading ? 'Loading...' : 'No data'}</div>
          )}

          {trendChartData.length > 0 && (
            <ResponsiveContainer width="100%" height={260}>
              <AreaChart data={trendChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Area type="monotone" dataKey="Income" />
                <Area type="monotone" dataKey="Expense" />
              </AreaChart>
            </ResponsiveContainer>
          )}
        </div>

        {/* 2) Savings growth */}
        <div className="bg-slate-800 rounded p-4">
          <div className="flex items-center justify-between mb-2">
            <div className="font-bold">Savings Growth</div>
            <div className="text-xs text-slate-400">Last 6 months</div>
          </div>

          {(trendLoading || trendChartData.length === 0) && (
            <div className="text-sm text-slate-400">{trendLoading ? 'Loading...' : 'No data'}</div>
          )}

          {trendChartData.length > 0 && (
            <ResponsiveContainer width="100%" height={260}>
              <LineChart data={trendChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="Savings" />
                <Line type="monotone" dataKey="Net" />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      {/* PIE CHART */}
      <div className="bg-slate-800 rounded p-4 mt-6">
        <h4 className="font-bold mb-2">Expense distribution</h4>

        {pieData.length === 0 && <div className="text-sm text-slate-400">No categories</div>}

        {pieData.length > 0 && (
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={pieData} dataKey="value" nameKey="name" innerRadius={50} outerRadius={90}>
                {pieData.map((_, idx) => (
                  <Cell
                    key={idx}
                    fill={['#f97316','#fb923c','#f59e0b','#f43f5e','#60a5fa','#22c55e'][idx%6]}
                  />
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
