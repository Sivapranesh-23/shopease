import { Link } from "react-router-dom";
import { fallbackProducts } from "../../services/productService";
import ProductCard from "../../components/ProductCard";

function Home() {
  return (
    <main>
      <section className="hero">
        <div className="hero-content">
          <span className="eyebrow light">The considered collection · 2026</span>
          <h1>Everyday pieces,<br />chosen exceptionally.</h1>
          <p>Modern accessories and objects with lasting materials, clean lines, and no unnecessary noise.</p>
          <div className="button-row">
            <Link className="button" to="/products">Shop the collection</Link>
            <Link className="button secondary" to="/products?sort=price-asc">Explore essentials</Link>
          </div>
        </div>
      </section>
      <section className="section intro-grid">
        <div><span className="eyebrow">Why ShopEase</span><h2>Good design should make life feel simpler.</h2></div>
        <div className="value-grid">
          <article><b>01</b><h3>Curated, not crowded</h3><p>Fewer, better pieces selected for material, utility, and enduring style.</p></article>
          <article><b>02</b><h3>Easy by design</h3><p>Clear pricing, secure checkout, and thoughtful delivery updates.</p></article>
          <article><b>03</b><h3>Made to stay</h3><p>Products meant to earn their place in your daily rotation.</p></article>
        </div>
      </section>
      <section className="section surface">
        <div className="section-heading"><div><span className="eyebrow">This week’s edit</span><h2>Quietly excellent</h2></div><Link to="/products">View all →</Link></div>
        <div className="catalog-grid">{fallbackProducts.slice(0, 4).map((product) => <ProductCard key={product.id} product={product} />)}</div>
      </section>
    </main>
  );
}

export default Home;
