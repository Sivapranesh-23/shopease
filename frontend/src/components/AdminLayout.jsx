import { NavLink, useNavigate } from "react-router-dom";
import { getSession, logout } from "../services/authService";

const links = [
  ["/admin/dashboard", "Overview", "⌁"],
  ["/admin/products", "Products", "◇"],
  ["/admin/categories", "Categories", "▦"],
  ["/admin/inventory", "Inventory", "▤"],
  ["/admin/orders", "Orders", "▣"],
  ["/admin/users", "Customers", "○"],
];

function AdminLayout({ title, eyebrow, actions, children }) {
  const navigate = useNavigate();
  const user = getSession()?.user;
  const signOut = async () => { await logout(); navigate("/login"); };
  return <div className="admin-shell"><aside className="admin-sidebar"><div><NavLink className="brand" to="/">Shop<span>Ease</span></NavLink><small>ADMIN CONSOLE</small></div><nav>{links.map(([to, label, icon]) => <NavLink key={to} to={to}><span>{icon}</span>{label}</NavLink>)}</nav><div className="admin-user"><div className="avatar small">{user?.name?.[0] || "A"}</div><div><b>{user?.name || "Admin"}</b><small>{user?.email}</small></div><button onClick={signOut} title="Sign out">↗</button></div></aside><main className="admin-main"><header className="admin-header"><div><span className="eyebrow">{eyebrow || "ShopEase admin"}</span><h1>{title}</h1></div><div className="admin-actions">{actions}</div></header>{children}</main></div>;
}

export default AdminLayout;
