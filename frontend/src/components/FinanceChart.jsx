import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts'

export default function FinanceChart({ income, expenses, savings }) {
  const data = [{
    name: 'This Month',
    Income: income,
    Expenses: expenses,
    Savings: savings
  }]

  return (
    <div className="bg-slate-800 p-4 rounded">
      <div className="font-bold mb-2">Money Flow</div>
      <ResponsiveContainer width="100%" height={250}>
        <BarChart data={data}>
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="Income" fill="#22c55e" />
          <Bar dataKey="Expenses" fill="#ef4444" />
          <Bar dataKey="Savings" fill="#3b82f6" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
