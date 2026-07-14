import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AdminLayout from "../../components/AdminLayout";
import { getCategoryBreakdown, getDashboardSummary, getRevenue } from "../../services/adminService";
import { formatCurrency } from "../../utils/format";

function Dashboard() {
  const [data, setData] = useState({ summary: null, revenue: [], categories: [] });
  const [error, setError] = useState("");
  useEffect(() => { Promise.all([getDashboardSummary(), getRevenue(), getCategoryBreakdown()]).then(([summary, revenue, categories]) => setData({ summary, revenue, categories })).catch((err) => setError(err.message)); }, []);
  const s = data.summary;
  const maxRevenue = Math.max(1, ...data.revenue.map((item) => Number(item.revenue)));
  return <AdminLayout title="Overview" eyebrow="Good decisions start here" actions={<Link className="button" to="/admin/products">+ Add product</Link>}>{error && <div className="error-banner">{error}</div>}{!s && !error ? <div className="admin-loading">Loading store health…</div> : s && <><section className="metric-grid"><article><span>Revenue</span><h2>{formatCurrency(s.totalRevenue)}</h2><small>All non-cancelled orders</small></article><article><span>Orders</span><h2>{s.totalOrders}</h2><small>Lifetime orders</small></article><article><span>Customers</span><h2>{s.totalUsers}</h2><small>Registered accounts</small></article><article className={s.lowStockCount ? "warning" : ""}><span>Low stock</span><h2>{s.lowStockCount}</h2><small>Below 10 units</small></article></section><section className="admin-grid"><article className="admin-card chart-card"><div className="card-heading"><div><span className="eyebrow">Performance</span><h2>Revenue by month</h2></div></div><div className="bar-chart">{data.revenue.map((item) => <div key={item.label}><span style={{ height: `${Math.max(4, Number(item.revenue) / maxRevenue * 100)}%` }} title={formatCurrency(item.revenue)}></span><small>{item.label}</small></div>)}</div></article><article className="admin-card"><div className="card-heading"><div><span className="eyebrow">Catalog mix</span><h2>Categories</h2></div><Link to="/admin/categories">Manage</Link></div><div className="breakdown-list">{data.categories.map((item) => <div key={item.category}><span>{item.category}<small>{item.unitsSold} products</small></span><div><i style={{ width: `${item.percentage}%` }}></i></div><b>{item.percentage}%</b></div>)}</div></article></section></>}</AdminLayout>;
}

export default Dashboard;
