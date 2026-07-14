import { useEffect, useMemo, useState } from "react";
import AdminLayout from "../../components/AdminLayout";
import { getUsers, setUserActive } from "../../services/adminService";

function UsersManage() {
  const [users, setUsers] = useState([]); const [query, setQuery] = useState(""); const [error, setError] = useState(""); const [busy, setBusy] = useState(null);
  const load = () => getUsers().then((page) => setUsers(page.content)).catch((err) => setError(err.message));
  useEffect(load, []);
  const visible = useMemo(() => users.filter((u) => `${u.name} ${u.email}`.toLowerCase().includes(query.toLowerCase())), [users, query]);
  const toggle = async (user) => { setBusy(user.id); try { await setUserActive(user.id, !user.active); await load(); } catch (err) { setError(err.message); } finally { setBusy(null); } };
  return <AdminLayout title="Customers" eyebrow="Account management">{error && <div className="error-banner">{error}</div>}<section className="admin-card table-card"><div className="table-tools"><label className="search-field"><span>⌕</span><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search customers" /></label><span>{users.filter((u) => u.active).length} active accounts</span></div><div className="table-scroll"><table><thead><tr><th>Customer</th><th>Email</th><th>Role</th><th>Status</th><th></th></tr></thead><tbody>{visible.map((user) => <tr key={user.id}><td><div className="table-user"><div className="avatar small">{user.name[0]}</div><b>{user.name}</b></div></td><td>{user.email}</td><td>{user.role}</td><td><span className={`status ${user.active ? "active" : "inactive"}`}>{user.active ? "Active" : "Suspended"}</span></td><td className="row-actions"><button disabled={busy === user.id || user.role === "ADMIN"} onClick={() => toggle(user)}>{user.active ? "Suspend" : "Activate"}</button></td></tr>)}</tbody></table></div></section></AdminLayout>;
}
export default UsersManage;
