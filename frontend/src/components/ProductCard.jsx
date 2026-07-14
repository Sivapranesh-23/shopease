import { useState } from "react";
import { Link } from "react-router-dom";
import { addToCart } from "../services/cartService";
import { isWishlisted, toggleWishlist } from "../services/wishlistService";
import { formatCurrency } from "../utils/format";

function ProductCard({ product }) {
  const [saved, setSaved] = useState(() => isWishlisted(product.id));
  const [added, setAdded] = useState(false);

  const add = async () => {
    await addToCart(product);
    setAdded(true);
    window.setTimeout(() => setAdded(false), 1200);
  };

  const save = () => {
    const result = toggleWishlist(product);
    setSaved(result.added);
  };

  return (
    <article className="product-card">
      <button className={`wish-button ${saved ? "saved" : ""}`} onClick={save} aria-label="Toggle wishlist">{saved ? "♥" : "♡"}</button>
      <Link to={`/products/${product.slug || product.id}`} className="product-image-link">
        <img src={product.image || product.imageUrl} alt={product.name} loading="lazy" />
      </Link>
      <div className="product-card-body">
        <span className="eyebrow">{product.categoryName || "ShopEase edit"}</span>
        <Link to={`/products/${product.slug || product.id}`}><h3>{product.name}</h3></Link>
        <div className="product-card-row">
          <strong>{formatCurrency(product.price)}</strong>
          <span>★ {Number(product.rating || 4.7).toFixed(1)}</span>
        </div>
        <button className="button full" onClick={add}>{added ? "Added ✓" : "Add to cart"}</button>
      </div>
    </article>
  );
}

export default ProductCard;
