
export const API_BASE = import.meta.env.VITE_API_BASE ?? '/api';

async function parseResponse(res) {
  const ct = res.headers.get('content-type') || '';
  if (ct.includes('application/json')) return res.json();
  return res.text();
}

function timeoutFetch(url, opts = {}, ms = 10000) {
  const controller = new AbortController();
  const id = setTimeout(() => controller.abort(), ms);
  return fetch(url, { signal: controller.signal, ...opts }).finally(() => clearTimeout(id));
}

export async function apiFetch(path, options = {}, timeoutMs = 10000) {
  const opts = {
    credentials: 'include',
    headers: {},
    ...options
  };

  try {
    const res = await timeoutFetch(`${API_BASE}${path}`, opts, timeoutMs);

    if (!res.ok) {
      const body = await parseResponse(res).catch(() => null);
      const msg =
        (body && (body.message || body.error || (typeof body === 'string' ? body : JSON.stringify(body)))) ||
        `${res.status} ${res.statusText}`;
      const err = new Error(msg);
      err.status = res.status;
      err.body = body;
      throw err;
    }

    return parseResponse(res);
  } catch (err) {
    if (err.name === 'AbortError') throw new Error('Request timed out');
    throw err;
  }
}

export function apiGet(path) {
  return apiFetch(path, { method: 'GET' });
}

export function apiPost(path, body) {
  return apiFetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
}

export function apiPut(path, body) {
  return apiFetch(path, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
}

export function apiDelete(path) {
  return apiFetch(path, { method: 'DELETE' });
}


export function apiPostMultipart(path, formData) {
  return apiFetch(path, {
    method: 'POST',
    body: formData
  });
}
