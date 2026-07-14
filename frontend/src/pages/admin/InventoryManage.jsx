import { useEffect, useMemo, useState } from "react";
import AdminLayout from "../../components/AdminLayout";
import { getAdminProducts, updateProduct } from "../../services/adminService";

function InventoryManage() {
  const [products, setProducts] = useState([]); const [query, setQuery] = useState(""); const [filter, setFilter] = useState("all"); const [error, setError] = useState(""); const [busy, setBusy] = useState(null);
  const load = () => getAdminProducts().then((page) => setProducts(page.content)).catch((err) => setError(err.message));
  useEffect(load, []);
  const visible = useMemo(() => products.filter((p) => p.name.toLowerCase().includes(query.toLowerCase()) && (filter === "all" || filter === "low" && p.stock < 10 || filter === "out" && p.stock === 0)), [products, query, filter]);
  const save = async (product, stock) => { setBusy(product.id); try { await updateProduct(product.id, { stock: Math.max(0, Number(stock)) }); await load(); } catch (err) { setError(err.message); } finally { setBusy(null); } };
  return <AdminLayout title="Inventory" eyebrow="Stock control">{error && <div className="error-banner">{error}</div>}<div className="metric-grid compact-metrics"><article><span>Total units</span><h2>{products.reduce((n, p) => n + p.stock, 0)}</h2></article><article><span>Low stock</span><h2>{products.filter((p) => p.stock > 0 && p.stock < 10).length}</h2></article><article><span>Out of stock</span><h2>{products.filter((p) => p.stock === 0).length}</h2></article></div><section className="admin-card table-card"><div className="table-tools"><label className="search-field"><span>⌕</span><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search inventory" /></label><select value={filter} onChange={(e) => setFilter(e.target.value)}><option value="all">All stock</option><option value="low">Low stock</option><option value="out">Out of stock</option></select></div><div className="table-scroll"><table><thead><tr><th>Product</th><th>SKU</th><th>Current stock</th><th>Adjust</th></tr></thead><tbody>{visible.map((p) => <InventoryRow key={p.id} product={p} busy={busy === p.id} save={save} />)}</tbody></table></div></section></AdminLayout>;
}
function InventoryRow({ product, busy, save }) {
  const [stock, setStock] = useState(product.stock);
  return <tr><td><div className="table-product"><img src={product.image} alt="" /><b>{product.name}</b></div></td><td>{product.sku}</td><td><span className={product.stock < 10 ? "stock low" : "stock"}>{product.stock}</span></td><td><div className="stock-editor"><input type="number" min="0" value={stock} onChange={(e) => setStock(e.target.value)} /><button disabled={busy || Number(stock) === product.stock} onClick={() => save(product, stock)}>{busy ? "Saving…" : "Update"}</button></div></td></tr>;
}
export default InventoryManage;
