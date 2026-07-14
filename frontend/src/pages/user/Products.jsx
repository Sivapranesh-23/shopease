import { useEffect, useState } from "react";
import ProductCard from "../../components/ProductCard";
import { listCategories, searchProducts } from "../../services/productService";

function Products() {
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("");
  const [sort, setSort] = useState("newest");
  const [page, setPage] = useState(0);
  const [result, setResult] = useState({ content: [], totalPages: 1, totalElements: 0 });
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { listCategories().then(setCategories); }, []);
  useEffect(() => {
    let current = true;
    const timer = window.setTimeout(async () => {
      setLoading(true);
      const data = await searchProducts({ q: query, category, sort, page, size: 12 });
      if (current) { setResult(data); setLoading(false); }
    }, 250);
    return () => { current = false; window.clearTimeout(timer); };
  }, [query, category, sort, page]);

  return (
    <main className="page-shell">
      <header className="page-header"><span className="eyebrow">The full collection</span><h1>Find your next favourite.</h1><p>{result.totalElements} considered pieces for work, weekends, and everything between.</p></header>
      <div className="catalog-toolbar">
        <label className="search-field"><span>⌕</span><input value={query} onChange={(e) => { setQuery(e.target.value); setPage(0); }} placeholder="Search products" /></label>
        <select value={category} onChange={(e) => { setCategory(e.target.value); setPage(0); }}><option value="">All collections</option>{categories.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select>
        <select value={sort} onChange={(e) => setSort(e.target.value)}><option value="newest">Newest</option><option value="price-asc">Price: low to high</option><option value="price-desc">Price: high to low</option><option value="rating">Top rated</option></select>
      </div>
      {loading ? <div className="empty-state">Loading the collection…</div> : result.content.length ? <div className="catalog-grid">{result.content.map((product) => <ProductCard key={product.id} product={product} />)}</div> : <div className="empty-state"><h2>No pieces found</h2><p>Try a broader search or another collection.</p></div>}
      {result.totalPages > 1 && <div className="pagination"><button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>Previous</button><span>Page {page + 1} of {result.totalPages}</span><button disabled={page + 1 >= result.totalPages} onClick={() => setPage((p) => p + 1)}>Next</button></div>}
    </main>
  );
}

export default Products;
