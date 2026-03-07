import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ExpensesPage from './pages/ExpensesPage'
import IncomesPage from './pages/IncomesPage'
import CategoriesPage from './pages/CategoriesPage'
import ProfilePage from './pages/ProfilePage'
import { AuthProvider, useAuth } from './lib/auth'
import GoalsPage from './pages/GoalsPage'

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="p-6">Loading...</div>
  if (!user) return <Navigate to="/login" />
  return children
}

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route path="/app" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
        <Route path="/app/expenses" element={<ProtectedRoute><ExpensesPage /></ProtectedRoute>} />
        <Route path="/app/incomes" element={<ProtectedRoute><IncomesPage /></ProtectedRoute>} />
        <Route path="/app/categories" element={<ProtectedRoute><CategoriesPage /></ProtectedRoute>} />
        <Route path="/app/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
        <Route path="/app/goals" element={<ProtectedRoute><GoalsPage /></ProtectedRoute>} />

        <Route path="/" element={<Navigate to="/app" />} />
      </Routes>
    </AuthProvider>
  )
}
