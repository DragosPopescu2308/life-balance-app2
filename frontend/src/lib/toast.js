const listeners = new Set()

export function subscribe(fn) { listeners.add(fn); return () => listeners.delete(fn) }
export function toast(message, type = 'info') {
  for (const fn of listeners) fn({ message, type })
}
