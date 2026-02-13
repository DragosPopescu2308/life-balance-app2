import React from 'react'
import { Link, useLocation } from 'react-router-dom'

function NavItem({ to, children }) {
  const loc = useLocation()
  const active = loc.pathname === to
  return (
    <Link
      to={to}
      className={`p-2 rounded hover:bg-slate-800 ${active ? 'bg-slate-800' : ''}`}
    >
      {children}
    </Link>
  )
}

export default function Sidebar() {
  return (
    <aside className="w-64 bg-slate-900 p-4 hidden md:block">
      <div className="mb-6 font-bold text-xl">Life Balance</div>
      <nav className="flex flex-col gap-2">
        <NavItem to="/app">Dashboard</NavItem>
        <NavItem to="/app/expenses">Expenses</NavItem>
        <NavItem to="/app/incomes">Incomes</NavItem>
        <NavItem to="/app/categories">Categories</NavItem>
      </nav>
    </aside>
  )
}
