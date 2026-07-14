import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import ProductCard from "../../components/ProductCard";
import { addToCart } from "../../services/cartService";
import { getProduct, getRelatedProducts } from "../../services/productService";
import { isWishlisted, toggleWishlist } from "../../services/wishlistService";
import { formatCurrency } from "../../utils/format";

function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [related, setRelated] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saved, setSaved] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    let active = true;
    (async () => {
      setLoading(true);
      const item = await getProduct(id);
      if (!active) return;
      setProduct(item); setSaved(item ? isWishlisted(item.id) : false); setLoading(false);
      if (item) setRelated(await getRelatedProducts(item));
    })();
    return () => { active = false; };
  }, [id]);

  if (loading) return <main className="page-shell"><div className="empty-state">Loading product…</div></main>;
  if (!product) return <main className="page-shell"><div className="empty-state"><h1>Product not found</h1><Link className="button" to="/products">Back to shop</Link></div></main>;

  const add = async () => { await addToCart(product); setMessage("Added to your cart"); };
  const save = () => { const result = toggleWishlist(product); setSaved(result.added); };

  return (
    <main className="page-shell">
      <Link className="back-link" to="/products">← Back to collection</Link>
      <section className="product-detail">
        <div className="detail-image"><img src={product.image} alt={product.name} /></div>
        <div className="detail-copy"><span className="eyebrow">{product.categoryName}</span><h1>{product.name}</h1><div className="rating">★ {Number(product.rating || 4.7).toFixed(1)} <span>({product.reviewCount || 24} reviews)</span></div><p className="detail-price">{formatCurrency(product.price)}</p><p className="lead">{product.summary}</p><p>{product.description}</p><div className="stock-line"><span className={product.stock > 0 ? "in-stock" : "out-stock"}></span>{product.stock > 0 ? `${product.stock} available` : "Currently unavailable"}</div><div className="button-row"><button className="button" onClick={add} disabled={!product.stock}>Add to cart</button><button className="button ghost" onClick={save}>{saved ? "♥ Saved" : "♡ Save for later"}</button></div>{message && <p className="success-text">{message} · <Link to="/cart">View cart</Link></p>}<div className="detail-notes"><span>Free delivery over ₹2,000</span><span>14-day easy returns</span><span>Secure checkout</span></div></div>
      </section>
      {related.length > 0 && <section className="related-section"><div className="section-heading"><div><span className="eyebrow">You may also like</span><h2>From the same edit</h2></div></div><div className="catalog-grid">{related.map((item) => <ProductCard key={item.id} product={item} />)}</div></section>}
    </main>
  );
}

export default ProductDetails;
