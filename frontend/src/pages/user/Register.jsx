import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { register } from "../../services/authService";

function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", email: "", password: "", confirm: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const submit = async (event) => {
    event.preventDefault(); setError("");
    if (form.password !== form.confirm) return setError("Passwords do not match.");
    setLoading(true);
    try { await register({ name: form.name, email: form.email, password: form.password }); navigate("/products"); }
    catch (err) { setError(err.message); } finally { setLoading(false); }
  };
  return <main className="auth-page"><section className="auth-art register-art"><div><span className="eyebrow light">Join ShopEase</span><h1>Keep the good<br />things closer.</h1><p>Save favourites, move through checkout faster, and follow every delivery.</p></div></section><section className="auth-panel"><form className="auth-form" onSubmit={submit}><Link className="brand dark" to="/">Shop<span>Ease</span></Link><div><span className="eyebrow">Create account</span><h2>Let’s get acquainted</h2><p>Already a member? <Link to="/login">Sign in</Link></p></div>{error && <div className="error-banner">{error}</div>}<label>Full name<input required autoComplete="name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label><label>Email address<input type="email" required autoComplete="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label><label>Password<input type="password" minLength="8" required autoComplete="new-password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /><small>Use at least 8 characters.</small></label><label>Confirm password<input type="password" minLength="8" required autoComplete="new-password" value={form.confirm} onChange={(e) => setForm({ ...form, confirm: e.target.value })} /></label><button className="button full" disabled={loading}>{loading ? "Creating account…" : "Create account"}</button></form></section></main>;
}

export default Register;
