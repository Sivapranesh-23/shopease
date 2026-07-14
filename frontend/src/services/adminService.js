import { api } from "./api";
import { normalizeProduct } from "./productService";

export const getDashboardSummary = () => api("/admin/reports/summary");
export const getRevenue = (months = 6) => api(`/admin/reports/revenue?months=${months}`);
export const getCategoryBreakdown = () => api("/admin/reports/categories");
export async function getAdminProducts(page = 0, size = 100) {
  const result = await api(`/admin/products?page=${page}&size=${size}`);
  return { ...result, content: result.content.map(normalizeProduct) };
}
export const createProduct = (product) => api("/admin/products", { method: "POST", body: product });
export const updateProduct = (id, product) => api(`/admin/products/${id}`, { method: "PUT", body: product });
export const deleteProduct = (id) => api(`/admin/products/${id}`, { method: "DELETE" });
export const getAdminCategories = () => api("/admin/categories");
export const createCategory = (category) => api("/admin/categories", { method: "POST", body: category });
export const updateCategory = (id, category) => api(`/admin/categories/${id}`, { method: "PUT", body: category });
export const deleteCategory = (id) => api(`/admin/categories/${id}`, { method: "DELETE" });
export const getUsers = (page = 0, size = 100) => api(`/admin/users?page=${page}&size=${size}`);
export const setUserActive = (id, active) => api(`/admin/users/${id}/active`, { method: "PATCH", body: { active } });
export const getAdminOrders = (page = 0, size = 100, status = "") => api(`/admin/orders?page=${page}&size=${size}${status ? `&status=${status}` : ""}`);
export const updateOrderStatus = (id, status) => api(`/admin/orders/${id}/status`, { method: "PATCH", body: { status } });
