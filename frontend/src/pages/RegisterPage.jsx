import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'

export default function RegisterPage(){
  const { register } = useAuth();
  const nav = useNavigate();
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [about, setAbout] = useState('');


  async function handle(e){
    e.preventDefault();
    setError(null); setLoading(true);
    try{
      await register({ fullName, email, password, about });
      nav('/login');
    }catch(err){
      setError(err.message || 'Register failed');
    }finally{ setLoading(false); }
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <form className="bg-slate-800 p-8 rounded-lg w-full max-w-md" onSubmit={handle}>
        <h2 className="text-xl font-bold mb-4">Register</h2>
        {error && <div className="bg-red-600 p-2 rounded mb-2">{error}</div>}
        <label className="block mb-2">Full name<input required className="w-full p-2 rounded bg-slate-900" value={fullName} onChange={e=>setFullName(e.target.value)} /></label>
        <label className="block mb-2">Email<input required className="w-full p-2 rounded bg-slate-900" value={email} onChange={e=>setEmail(e.target.value)} /></label>
        <label className="block mb-2">Password<input type="password" required className="w-full p-2 rounded bg-slate-900" value={password} onChange={e=>setPassword(e.target.value)} /></label>
        <label className="block mb-2">
          About (optional)
          <textarea
            className="w-full p-2 rounded bg-slate-900"
            rows={4}
            value={about}
            onChange={e => setAbout(e.target.value)}
            placeholder="A short bio..."
          />
        </label>
        <div className="flex gap-2 mt-4">
          <button className="btn-primary p-2 rounded bg-green-600" disabled={loading}>{loading? '...' : 'Register'}</button>
          <button type="button" className="btn-ghost p-2 rounded bg-transparent" onClick={() => nav('/login')}>Back</button>
        </div>
      </form>
    </div>
  )
}
