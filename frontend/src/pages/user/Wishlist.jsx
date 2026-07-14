import { useState } from "react";
import { Link } from "react-router-dom";
import ProductCard from "../../components/ProductCard";
import { getWishlist } from "../../services/wishlistService";

function Wishlist() {
  const [items, setItems] = useState(getWishlist);
  const refresh = () => window.setTimeout(() => setItems(getWishlist()), 0);
  return <main className="page-shell" onClick={refresh}><header className="page-header compact"><span className="eyebrow">Saved for later</span><h1>Your wishlist</h1><p>{items.length ? `${items.length} pieces you have your eye on.` : "A quiet place for the pieces you love."}</p></header>{items.length ? <div className="catalog-grid">{items.map((item) => <ProductCard key={item.id} product={item} />)}</div> : <div className="empty-state"><span className="empty-icon">♡</span><h2>Nothing saved yet</h2><p>Tap the heart on any product to keep it here.</p><Link className="button" to="/products">Discover products</Link></div>}</main>;
}

export default Wishlist;
