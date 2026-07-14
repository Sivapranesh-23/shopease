import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getProfile, getSession } from "../../services/authService";

function Profile() {
  const [user, setUser] = useState(getSession()?.user || null);
  const [error, setError] = useState("");
  useEffect(() => { getProfile().then(setUser).catch((err) => setError(err.message)); }, []);
  return <main className="page-shell"><header className="page-header compact"><span className="eyebrow">Your account</span><h1>Good to see you, {user?.name?.split(" ")[0] || "there"}.</h1><p>Manage your ShopEase life from here.</p></header>{error && <div className="error-banner">{error}</div>}<div className="profile-grid"><section className="profile-card"><div className="avatar">{user?.name?.split(" ").map((part) => part[0]).slice(0, 2).join("")}</div><div><span className="eyebrow">Profile</span><h2>{user?.name}</h2><p>{user?.email}</p><span className="status active">{user?.role === "ADMIN" ? "Administrator" : "Active customer"}</span></div></section><Link className="account-tile" to="/orders"><span>▣</span><div><h3>Order history</h3><p>Track deliveries and revisit past purchases.</p></div><b>→</b></Link><Link className="account-tile" to="/wishlist"><span>♡</span><div><h3>Wishlist</h3><p>Return to the pieces you saved.</p></div><b>→</b></Link>{user?.role === "ADMIN" && <Link className="account-tile" to="/admin/dashboard"><span>⌁</span><div><h3>Admin console</h3><p>Manage catalog, customers, and orders.</p></div><b>→</b></Link>}</div></main>;
}

export default Profile;
