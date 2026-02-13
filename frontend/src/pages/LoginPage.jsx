import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'

export default function LoginPage(){
  const { login } = useAuth();
  const nav = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  async function handle(e){
    e.preventDefault();
    setError(null); setLoading(true);
    try{
      await login({ email, password });
      nav('/app');
    }catch(err){
      setError(err.message || 'Login failed');
    }finally{ setLoading(false); }
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <form className="bg-slate-800 p-8 rounded-lg w-full max-w-md" onSubmit={handle}>
        <h2 className="text-xl font-bold mb-4">Login</h2>
        {error && <div className="bg-red-600 p-2 rounded mb-2">{error}</div>}
        <label className="block mb-2">Email<input required className="w-full p-2 rounded bg-slate-900" value={email} onChange={e=>setEmail(e.target.value)} /></label>
        <label className="block mb-2">Password<input type="password" required className="w-full p-2 rounded bg-slate-900" value={password} onChange={e=>setPassword(e.target.value)} /></label>
        <div className="flex gap-2 mt-4">
          <button className="btn-primary p-2 rounded bg-green-600" disabled={loading}>{loading? '...' : 'Login'}</button>
          <button type="button" className="btn-ghost p-2 rounded bg-transparent" onClick={() => nav('/register')}>Register</button>
        </div>
      </form>
    </div>
  )
}
