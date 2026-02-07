import React, { useEffect, useState } from 'react'
import { subscribe } from '../lib/toast'

export default function ToastContainer(){
  const [toasts, setToasts] = useState([])
  useEffect(() => subscribe(t => {
    setToasts(s => [...s, { ...t, id: Date.now() }]);
  }), [])

  useEffect(() => {
    if (!toasts.length) return;
    const id = setTimeout(() => setToasts(s => s.slice(1)), 3500);
    return () => clearTimeout(id);
  }, [toasts])

  return (
    <div className="fixed right-4 bottom-4 flex flex-col gap-2 z-50">
      {toasts.map(t => (
        <div key={t.id} className={`p-2 rounded ${t.type==='error'?'bg-red-600':'bg-green-600'}`}>{t.message}</div>
      ))}
    </div>
  )
}
