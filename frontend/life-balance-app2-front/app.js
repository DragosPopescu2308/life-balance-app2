const API_BASE = "http://localhost:8080/api";

async function apiGet(path) {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status} - ${text}`);
  }
  return res.json();
}

function pretty(obj) {
  return JSON.stringify(obj, null, 2);
}

document.getElementById("btnLoadCategories").addEventListener("click", async () => {
  const out = document.getElementById("categoriesOut");
  out.textContent = "Loading...";
  try {
    const data = await apiGet("/categories");
    out.textContent = pretty(data);
  } catch (e) {
    out.textContent = e.message;
  }
});

document.getElementById("btnLoadIncomes").addEventListener("click", async () => {
  const userId = document.getElementById("incomeUserId").value || 1;
  const out = document.getElementById("incomesOut");
  out.textContent = "Loading...";
  try {
    const data = await apiGet(`/incomes?userId=${encodeURIComponent(userId)}`);
    out.textContent = pretty(data);
  } catch (e) {
    out.textContent = e.message;
  }
});

document.getElementById("btnLoadExpenses").addEventListener("click", async () => {
  const userId = document.getElementById("expenseUserId").value || 1;
  const out = document.getElementById("expensesOut");
  out.textContent = "Loading...";
  try {
    const data = await apiGet(`/expenses?userId=${encodeURIComponent(userId)}`);
    out.textContent = pretty(data);
  } catch (e) {
    out.textContent = e.message;
  }
});
