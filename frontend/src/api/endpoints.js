// src/api/endpoints.js
import { apiGet, apiPost, apiPut, apiDelete, apiPostMultipart, API_BASE } from './client';

export const auth = {
  me: () => apiGet('/auth/me'),
  login: (payload) => apiPost('/auth/login', payload),
  register: (payload) => apiPost('/auth/register', payload),
  logout: () => apiPost('/auth/logout', {})
};

export const categories = {
  all: (type) => apiGet(type ? `/categories?type=${encodeURIComponent(type)}` : '/categories'),
  byType: (type) => apiGet(`/categories?type=${encodeURIComponent(type)}`),
  create: (c) => apiPost('/categories', c),
  update: (id, c) => apiPut(`/categories/${id}`, c),
  delete: (id) => apiDelete(`/categories/${id}`)
};

export const incomes = {
  list: (month) => apiGet(month ? `/incomes?month=${encodeURIComponent(month)}` : '/incomes'),
  get: (id) => apiGet(`/incomes/${id}`),
  create: (payload) => apiPost('/incomes', payload),
  update: (id, payload) => apiPut(`/incomes/${id}`, payload),
  delete: (id) => apiDelete(`/incomes/${id}`)
};

export const expenses = {
  list: (month) => apiGet(month ? `/expenses?month=${encodeURIComponent(month)}` : '/expenses'),
  get: (id) => apiGet(`/expenses/${id}`),
  create: (payload) => apiPost('/expenses', payload),
  update: (id, payload) => apiPut(`/expenses/${id}`, payload),
  delete: (id) => apiDelete(`/expenses/${id}`)
};

export const dashboard = {
  monthly: (month) => apiGet(month ? `/dashboard?month=${encodeURIComponent(month)}` : '/dashboard')
};

export const receipts = {
  list: (expenseId) => apiGet(`/expenses/${expenseId}/receipts`),
  upload: (expenseId, file) => {
    const fd = new FormData();
    fd.append('file', file);
    return apiPostMultipart(`/expenses/${expenseId}/receipts`, fd);
  },
  delete: (receiptId) => apiDelete(`/receipts/${receiptId}`),
};

export const profile = {
  me: () => apiGet('/profile/me'),
  update: (payload) => apiPut('/profile', payload),

  uploadAvatar: (file) => {
    const fd = new FormData()
    fd.append('file', file)
    return apiPostMultipart('/profile/avatar', fd)
  },

  // ✅ FIX: asta lipsea
  deleteAvatar: () => apiDelete('/profile/avatar'),
};

export const incomeAttachments = {
  list: (incomeId) => apiGet(`/incomes/${incomeId}/attachments`),
  upload: (incomeId, file) => {
    const fd = new FormData()
    fd.append('file', file)
    return apiPostMultipart(`/incomes/${incomeId}/attachments`, fd)
  },
  delete: (attachmentId) => apiDelete(`/income-attachments/${attachmentId}`),
};

export { API_BASE };


export const savings = {
  settings: () => apiGet('/savings/settings'),
  updateSettings: (payload) => apiPut('/savings/settings', payload),
  monthly: (month) => apiGet(`/savings/monthly?month=${month}`)
}
