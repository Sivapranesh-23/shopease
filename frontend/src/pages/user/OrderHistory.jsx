import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { listOrders } from "../../services/orderService";
import { formatCurrency, formatDate } from "../../utils/format";

function OrderHistory() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  useEffect(() => { listOrders().then((page) => setOrders(page.content || [])).catch((err) => setError(err.message)).finally(() => setLoading(false)); }, []);
  return <main className="page-shell"><header className="page-header compact"><span className="eyebrow">Your account</span><h1>Orders</h1><p>Everything you’ve chosen, in one place.</p></header>{loading ? <div className="empty-state">Loading orders…</div> : error ? <div className="error-banner">{error}</div> : orders.length ? <div className="order-list">{orders.map((order) => <article className="order-card" key={order.id}><header><div><span className="eyebrow">{order.orderNumber}</span><h2>{formatDate(order.createdAt)}</h2></div><span className={`status ${order.status.toLowerCase()}`}>{order.status}</span></header><div className="order-items">{order.items.map((item) => <div key={item.id}><img src={item.imageUrl} alt={item.productName} /><span>{item.productName}<small>Qty {item.quantity}</small></span><b>{formatCurrency(item.unitPrice * item.quantity)}</b></div>)}</div><footer><span>Total <b>{formatCurrency(order.grandTotal)}</b></span>{order.trackingNumber ? <span>{order.shippingCarrier}: {order.trackingNumber}</span> : <span>Tracking will appear once dispatched.</span>}</footer></article>)}</div> : <div className="empty-state"><span className="empty-icon">▢</span><h2>No orders yet</h2><p>When something catches your eye, it’ll appear here after checkout.</p><Link className="button" to="/products">Start shopping</Link></div>}</main>;
}

export default OrderHistory;
