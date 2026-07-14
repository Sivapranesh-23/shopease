import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getSession } from "../../services/authService";
import { getCart } from "../../services/cartService";
import { placeOrder } from "../../services/orderService";
import { formatCurrency } from "../../utils/format";

const initialAddress = { fullName: "", phone: "", line1: "", line2: "", city: "", postalCode: "", country: "India" };

function Checkout() {
  const navigate = useNavigate();
  const [cart, setCart] = useState({ items: [], subtotal: 0 });
  const [address, setAddress] = useState(() => ({ ...initialAddress, fullName: getSession()?.user?.name || "" }));
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  useEffect(() => { getCart().then((data) => { setCart(data); setLoading(false); }); }, []);

  const submit = async (event) => {
    event.preventDefault(); setError(""); setSubmitting(true);
    try {
      const order = await placeOrder({
        shippingAddress: address,
        payment: { paymentMethodId: "stub-checkout" },
        items: cart.items.map((item) => ({ productId: item.productId || item.id, quantity: item.quantity })),
      });
      navigate("/payment", { state: { order } });
    } catch (err) { setError(err.message); } finally { setSubmitting(false); }
  };

  if (loading) return <main className="page-shell"><div className="empty-state">Preparing checkout…</div></main>;
  if (!cart.items.length) return <main className="page-shell"><div className="empty-state"><h1>Your cart is empty</h1><Link className="button" to="/products">Return to shop</Link></div></main>;
  const shipping = cart.subtotal >= 2000 ? 0 : 149;

  return <main className="page-shell"><header className="page-header compact"><span className="eyebrow">Secure checkout</span><h1>Where should we send it?</h1><p>Your details are used only to fulfil this order.</p></header><form className="checkout-layout" onSubmit={submit}><section className="form-card"><h2>Delivery details</h2>{error && <div className="error-banner">{error}</div>}<div className="form-grid"><label className="wide">Full name<input required value={address.fullName} onChange={(e) => setAddress({ ...address, fullName: e.target.value })} /></label><label>Phone number<input required pattern="[0-9+ -]{8,18}" value={address.phone} onChange={(e) => setAddress({ ...address, phone: e.target.value })} /></label><label>Postal code<input required value={address.postalCode} onChange={(e) => setAddress({ ...address, postalCode: e.target.value })} /></label><label className="wide">Address line 1<input required value={address.line1} onChange={(e) => setAddress({ ...address, line1: e.target.value })} /></label><label className="wide">Address line 2 <small>Optional</small><input value={address.line2} onChange={(e) => setAddress({ ...address, line2: e.target.value })} /></label><label>City<input required value={address.city} onChange={(e) => setAddress({ ...address, city: e.target.value })} /></label><label>Country<input required value={address.country} onChange={(e) => setAddress({ ...address, country: e.target.value })} /></label></div><div className="secure-note">▣ Secure payment · Your order is only placed after you continue.</div></section><aside className="summary-card"><span className="eyebrow">Order summary</span>{cart.items.map((item) => <div key={item.productId || item.id}><span>{item.name || item.productName} × {item.quantity}</span><b>{formatCurrency((item.price || item.unitPrice) * item.quantity)}</b></div>)}<hr /><div><span>Subtotal</span><b>{formatCurrency(cart.subtotal)}</b></div><div><span>Delivery</span><b>{shipping ? formatCurrency(shipping) : "Free"}</b></div><div className="summary-total"><span>Due today</span><b>{formatCurrency(cart.subtotal + shipping)}</b></div><button className="button full" disabled={submitting}>{submitting ? "Placing order…" : "Continue to payment"}</button></aside></form></main>;
}

export default Checkout;
