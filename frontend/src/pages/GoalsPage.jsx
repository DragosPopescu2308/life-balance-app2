import React, { useState } from 'react'
import AppShell from '../layout/AppShell'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { goals } from '../api/endpoints'
import { toast } from '../lib/toast'

export default function GoalsPage(){

const qc = useQueryClient()

const { data: goalsList = [], isLoading } = useQuery(
['goals'],
goals.list
)

const [form,setForm] = useState({
title:'',
targetAmount:'',
deadline:'',
allocationPercent:''
})

const [editing,setEditing] = useState(null)

async function createGoal(e){
e.preventDefault()

try{

await goals.create({
title:form.title,
targetAmount:Number(form.targetAmount),
deadline:form.deadline || null,
allocationPercent:Number(form.allocationPercent)
})

toast('Goal created','success')

setForm({
title:'',
targetAmount:'',
deadline:'',
allocationPercent:''
})

qc.invalidateQueries(['goals'])

}catch(err){
toast(err.message || 'Create failed','error')
}
}

async function updateGoal(){

try{

await goals.update(editing.id,{
title:editing.title,
targetAmount:Number(editing.targetAmount),
deadline:editing.deadline,
allocationPercent:Number(editing.allocationPercent),
active:true
})

toast('Goal updated','success')

setEditing(null)

qc.invalidateQueries(['goals'])

}catch(err){
toast(err.message,'error')
}
}

async function deleteGoal(id){

if(!window.confirm('Delete this goal?')) return

try{

await goals.delete(id)

toast('Goal deleted','success')

qc.invalidateQueries(['goals'])

}catch(err){
toast(err.message,'error')
}
}

return(

  <AppShell title="Goals">

{/* CREATE GOAL */}

   <form
    onSubmit={createGoal}
    className="bg-slate-800 p-4 rounded mb-6 space-y-3"
   >

```
<div className="font-bold text-lg">Create Goal</div>

<div className="grid grid-cols-1 md:grid-cols-5 gap-3">

 <input
  placeholder="Goal title"
  className="bg-slate-900 p-2 rounded"
  value={form.title}
  onChange={e=>setForm({...form,title:e.target.value})}
  required
 />

 <input
  type="number"
  placeholder="Target amount"
  className="bg-slate-900 p-2 rounded"
  value={form.targetAmount}
  onChange={e=>setForm({...form,targetAmount:e.target.value})}
  required
 />

 <input
  type="date"
  className="bg-slate-900 p-2 rounded"
  value={form.deadline}
  onChange={e=>setForm({...form,deadline:e.target.value})}
 />

 <input
  type="number"
  placeholder="% of savings"
  className="bg-slate-900 p-2 rounded"
  value={form.allocationPercent}
  onChange={e=>setForm({...form,allocationPercent:e.target.value})}
  required
 />

 <button className="bg-green-600 rounded font-semibold">
  Create
 </button>

</div>
```

   </form>

{isLoading && <div>Loading...</div>}

{/* GOALS LIST */}

   <div className="space-y-4">

```
{goalsList.map(g=>{

 const progress = Math.round(g.progress ?? 0)

 return(

  <div key={g.id} className="bg-slate-800 p-5 rounded">

   <div className="flex justify-between items-start">

    <div className="space-y-1">

     <div className="text-lg font-bold">
      {g.title}
     </div>

     <div className="text-sm text-slate-400">
      Target: ${g.targetAmount}
     </div>

     <div className="text-sm text-slate-400">
      Saved: ${g.savedAmount ?? 0}
     </div>

     <div className="text-sm text-slate-400">
      Remaining: ${g.remainingAmount}
     </div>

     <div className="text-xs text-purple-400">
      {g.allocationPercent}% of savings
     </div>

     {g.estimatedMonthly && (

      <div className="text-xs text-blue-400">
       Monthly needed: ${g.estimatedMonthly}
      </div>

     )}

    </div>

    <div className="flex gap-2">

     <button
      className="bg-yellow-600 px-3 py-1 rounded"
      onClick={()=>setEditing(g)}
     >
      Edit
     </button>

     <button
      className="bg-red-600 px-3 py-1 rounded"
      onClick={()=>deleteGoal(g.id)}
     >
      Delete
     </button>

    </div>

   </div>

   {/* PROGRESS BAR */}

   <div className="mt-4">

    <div className="h-3 bg-slate-900 rounded">

     <div
      className="h-3 bg-green-500 rounded"
      style={{width:`${progress}%`}}
     />

    </div>

    <div className="text-xs mt-1 text-slate-400">
     {progress}%
    </div>

   </div>

  </div>

 )

})}
```

   </div>

  </AppShell>

)
}
