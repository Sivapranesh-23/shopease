import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { login } from "../../services/authService";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const submit = async (event) => {
    event.preventDefault(); setError(""); setLoading(true);
    try {
      const session = await login(form);
      navigate(session.user?.role === "ADMIN" ? "/admin/dashboard" : location.state?.from || "/products", { replace: true });
    } catch (err) { setError(err.message); } finally { setLoading(false); }
  };
  return <main className="auth-page"><section className="auth-art"><div><span className="eyebrow light">Welcome back</span><h1>Your edit,<br />right where you left it.</h1><p>Sign in to check out, track orders, and keep favourites close.</p></div></section><section className="auth-panel"><form className="auth-form" onSubmit={submit}><Link className="brand dark" to="/">Shop<span>Ease</span></Link><div><span className="eyebrow">Account</span><h2>Sign in</h2><p>New here? <Link to="/register">Create an account</Link></p></div>{error && <div className="error-banner">{error}</div>}<label>Email address<input type="email" required autoComplete="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label><label>Password<input type="password" required autoComplete="current-password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label><button className="button full" disabled={loading}>{loading ? "Signing in…" : "Sign in"}</button><p className="form-note">Demo admin: admin@shopease.com / password123</p></form></section></main>;
}

export default Login;
