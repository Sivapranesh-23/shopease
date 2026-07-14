import { api } from "./api";
import productData from "../data/ProductData";

const parsePrice = (price) => Number(String(price || 0).replace(/[^0-9.]/g, ""));

export const fallbackProducts = productData.flatMap((group) =>
  group.related.map((item) => ({
    ...item,
    price: parsePrice(item.price),
    imageUrl: item.image,
    slug: String(item.id),
    summary: `A carefully selected ${item.name.toLowerCase()} from our ${group.name} collection.`,
    description: `Designed for daily use with a premium finish, this ${item.name.toLowerCase()} balances timeless style with practical detail.`,
    categoryName: group.name,
    categoryId: group.id,
    rating: 4.7,
    reviewCount: 24,
    stock: 25,
    active: true,
  })),
);

export function normalizeProduct(product) {
  return {
    ...product,
    image: product.image || product.imageUrl,
    price: Number(product.price || 0),
    categoryName: product.categoryName || product.category?.name || "Collection",
  };
}

function fallbackPage({ q = "", category, page = 0, size = 12 } = {}) {
  const query = q.trim().toLowerCase();
  const filtered = fallbackProducts.filter((item) =>
    (!query || `${item.name} ${item.categoryName}`.toLowerCase().includes(query)) &&
    (!category || Number(category) === item.categoryId),
  );
  const start = page * size;
  return {
    content: filtered.slice(start, start + size),
    number: page,
    size,
    totalElements: filtered.length,
    totalPages: Math.max(1, Math.ceil(filtered.length / size)),
    fallback: true,
  };
}

export async function listProducts(params = {}) {
  const query = new URLSearchParams({ page: params.page || 0, size: params.size || 12, sort: params.sort || "newest" });
  try {
    const page = await api(`/products?${query}`, { auth: false });
    return { ...page, content: page.content.map(normalizeProduct) };
  } catch {
    return fallbackPage(params);
  }
}

export async function searchProducts(params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== "" && value !== null && value !== undefined) query.set(key, value);
  });
  try {
    const page = await api(`/products/search?${query}`, { auth: false });
    return { ...page, content: page.content.map(normalizeProduct) };
  } catch {
    return fallbackPage(params);
  }
}

export async function getProduct(identifier) {
  try {
    const path = /^\d+$/.test(String(identifier)) ? `/products/id/${identifier}` : `/products/${identifier}`;
    return normalizeProduct(await api(path, { auth: false }));
  } catch {
    return fallbackProducts.find((item) => String(item.id) === String(identifier) || item.slug === identifier) || null;
  }
}

export async function getRelatedProducts(product, limit = 4) {
  try {
    return (await api(`/products/${product.id}/related?limit=${limit}`, { auth: false })).map(normalizeProduct);
  } catch {
    return fallbackProducts.filter((item) => item.categoryId === product.categoryId && item.id !== product.id).slice(0, limit);
  }
}

export async function listCategories() {
  try {
    return await api("/categories", { auth: false });
  } catch {
    return productData.map((group) => ({ id: group.id, name: group.name, slug: String(group.id), imageUrl: group.image }));
  }
}
