import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getCart, removeCartItem, updateCartItem } from "../../services/cartService";
import { formatCurrency } from "../../utils/format";

function Cart() {
  const [cart, setCart] = useState({ items: [], subtotal: 0, totalItems: 0 });
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(null);

  useEffect(() => { getCart().then((data) => { setCart(data); setLoading(false); }); }, []);
  const change = async (item, quantity) => { setBusy(item.productId || item.id); setCart(await updateCartItem(item.productId || item.id, quantity)); setBusy(null); };
  const remove = async (item) => { setBusy(item.productId || item.id); setCart(await removeCartItem(item.productId || item.id)); setBusy(null); };
  const shipping = cart.subtotal >= 2000 ? 0 : 149;

  if (loading) return <main className="page-shell"><div className="empty-state">Loading your cart…</div></main>;
  if (!cart.items?.length) return <main className="page-shell"><div className="empty-state"><span className="empty-icon">◇</span><h1>Your cart is waiting.</h1><p>Explore the collection and add something worth keeping.</p><Link className="button" to="/products">Browse the collection</Link></div></main>;

  return (
    <main className="page-shell">
      <header className="page-header compact"><span className="eyebrow">Your selection</span><h1>Shopping cart</h1><p>{cart.totalItems || cart.items.reduce((n, item) => n + item.quantity, 0)} items</p></header>
      <div className="checkout-layout">
        <section className="cart-list">{cart.items.map((item) => <article className="cart-item" key={item.productId || item.id}><Link to={`/products/${item.slug || item.productSlug || item.id}`}><img src={item.image || item.imageUrl} alt={item.name || item.productName} /></Link><div className="cart-copy"><span className="eyebrow">{item.categoryName || "ShopEase collection"}</span><h2>{item.name || item.productName}</h2><p>{formatCurrency(item.price || item.unitPrice)}</p><div className="quantity"><button disabled={busy !== null} onClick={() => change(item, item.quantity - 1)}>−</button><span>{item.quantity}</span><button disabled={busy !== null} onClick={() => change(item, item.quantity + 1)}>+</button></div></div><div className="cart-line-total"><strong>{formatCurrency((item.price || item.unitPrice) * item.quantity)}</strong><button className="text-button" onClick={() => remove(item)}>Remove</button></div></article>)}</section>
        <aside className="summary-card"><span className="eyebrow">Order summary</span><div><span>Subtotal</span><b>{formatCurrency(cart.subtotal)}</b></div><div><span>Delivery</span><b>{shipping ? formatCurrency(shipping) : "Free"}</b></div><div className="summary-total"><span>Total</span><b>{formatCurrency(cart.subtotal + shipping)}</b></div>{shipping > 0 && <p>Add {formatCurrency(2000 - cart.subtotal)} more for free delivery.</p>}<Link className="button full" to="/checkout">Continue to checkout</Link><Link className="center-link" to="/products">Keep shopping</Link></aside>
      </div>
    </main>
  );
}

export default Cart;
