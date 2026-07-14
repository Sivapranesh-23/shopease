import { useEffect, useState } from "react";
import AdminLayout from "../../components/AdminLayout";
import { getAdminOrders, updateOrderStatus } from "../../services/adminService";
import { formatCurrency, formatDate } from "../../utils/format";

const statuses = ["PENDING", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "REFUNDED"];
function OrdersManage() {
  const [orders, setOrders] = useState([]); const [filter, setFilter] = useState(""); const [error, setError] = useState(""); const [busy, setBusy] = useState(null);
  const load = () => getAdminOrders(0, 100, filter).then((page) => setOrders(page.content)).catch((err) => setError(err.message));
  useEffect(load, [filter]);
  const change = async (id, status) => { setBusy(id); try { await updateOrderStatus(id, status); await load(); } catch (err) { setError(err.message); } finally { setBusy(null); } };
  return <AdminLayout title="Orders" eyebrow="Fulfilment" actions={<select value={filter} onChange={(e) => setFilter(e.target.value)}><option value="">All statuses</option>{statuses.map((s) => <option key={s}>{s}</option>)}</select>}>{error && <div className="error-banner">{error}</div>}<section className="admin-card table-card"><div className="card-heading"><div><span className="eyebrow">{orders.length} orders</span><h2>Order queue</h2></div></div><div className="table-scroll"><table><thead><tr><th>Order</th><th>Date</th><th>Items</th><th>Total</th><th>Status</th></tr></thead><tbody>{orders.map((order) => <tr key={order.id}><td><b>{order.orderNumber}</b></td><td>{formatDate(order.createdAt)}</td><td>{order.items?.reduce((n, item) => n + item.quantity, 0) || 0}</td><td>{formatCurrency(order.grandTotal)}</td><td><select className={`status-select ${order.status.toLowerCase()}`} disabled={busy === order.id} value={order.status} onChange={(e) => change(order.id, e.target.value)}>{statuses.map((s) => <option key={s}>{s}</option>)}</select></td></tr>)}</tbody></table></div>{!orders.length && <div className="admin-loading">No orders match this view.</div>}</section></AdminLayout>;
}
export default OrdersManage;
