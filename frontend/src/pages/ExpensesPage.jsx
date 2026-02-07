import React from 'react'
import AppShell from '../layout/AppShell'
import ExpensesTab from './tabs/ExpensesTab'

export default function ExpensesPage(){
  const [month, setMonth] = React.useState(() => {
    const now = new Date()
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  })

  return (
    <AppShell
      title="Expenses"
      right={
        <input
          type="month"
          value={month}
          onChange={e => setMonth(e.target.value)}
          className="bg-slate-900 p-2 rounded"
        />
      }
    >
      <ExpensesTab month={month} />
    </AppShell>
  )
}
