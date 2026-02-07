import React, { createContext, useContext, useEffect, useState } from 'react'
import { auth } from '../api/endpoints'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    auth.me()
      .then(res => { if (mounted) setUser(res) })
      .catch(() => { if (mounted) setUser(null) })
      .finally(() => { if (mounted) setLoading(false) })
    return () => { mounted = false }
  }, [])

  const login = async (credentials) => {
    const res = await auth.login(credentials)
    setUser(res)
    return res
  }

  const register = async (payload) => {
    const res = await auth.register(payload)
    setUser(res)
    return res
  }

  const logout = async () => {
    await auth.logout()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() { return useContext(AuthContext) }
