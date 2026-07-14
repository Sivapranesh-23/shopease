import { api, emitAppChange, readSession } from "./api";
import { normalizeProduct } from "./productService";

const CART_KEY = "shopease_cart";

function readLocalCart() {
  try {
    const current = JSON.parse(localStorage.getItem(CART_KEY));
    const legacy = JSON.parse(localStorage.getItem("cart"));
    return Array.isArray(current) ? current : Array.isArray(legacy) ? legacy : [];
  } catch {
    return [];
  }
}

function saveLocalCart(items) {
  localStorage.setItem(CART_KEY, JSON.stringify(items));
  localStorage.removeItem("cart");
  emitAppChange();
  return items;
}

function localResponse(items = readLocalCart()) {
  const normalized = items.map((item) => ({ ...normalizeProduct(item), quantity: Number(item.quantity || 1) }));
  const subtotal = normalized.reduce((sum, item) => sum + item.price * item.quantity, 0);
  return { items: normalized, subtotal, totalItems: normalized.reduce((sum, item) => sum + item.quantity, 0), local: true };
}

function saveDbCart(cart) {
  const normalizedItems = (cart.items || []).map((item) => ({
    ...item,
    id: item.productId,
    productId: item.productId,
    name: item.productName,
    slug: item.productSlug,
    image: item.imageUrl,
    price: Number(item.unitPrice || 0),
    quantity: Number(item.quantity || 1),
  }));
  localStorage.setItem(CART_KEY, JSON.stringify(normalizedItems));
  localStorage.removeItem("cart");
  emitAppChange();
  return {
    ...cart,
    items: normalizedItems,
  };
}

export async function getCart() {
  if (!readSession()) return localResponse();
  try {
    const localItems = readLocalCart();
    if (localItems.length > 0) {
      // Sync guest cart items to backend database
      for (const item of localItems) {
        try {
          await api("/cart/items", { method: "POST", body: { productId: item.id || item.productId, quantity: item.quantity } });
        } catch {
          // ignore failures (e.g. if item already exists or is unavailable)
        }
      }
      // Clear local storage unsynced cart items
      localStorage.removeItem(CART_KEY);
      localStorage.removeItem("cart");
    }
    const cart = await api("/cart");
    return saveDbCart(cart);
  } catch {
    return localResponse();
  }
}

export async function addToCart(product, quantity = 1) {
  if (readSession()) {
    try {
      const cart = await api("/cart/items", { method: "POST", body: { productId: product.id, quantity } });
      return saveDbCart(cart);
    } catch {
      // Keep the storefront useful if only the frontend is running or backend errors.
    }
  }
  const items = readLocalCart();
  const existing = items.find((item) => String(item.id || item.productId) === String(product.id));
  if (existing) existing.quantity = Number(existing.quantity || 1) + quantity;
  else items.push({ ...normalizeProduct(product), quantity });
  return localResponse(saveLocalCart(items));
}

export async function updateCartItem(productId, quantity) {
  if (quantity < 1) return removeCartItem(productId);
  if (readSession()) {
    try {
      const cart = await api(`/cart/items/${productId}?quantity=${quantity}`, { method: "PUT" });
      return saveDbCart(cart);
    } catch {
      // Fall through to the local cart.
    }
  }
  const items = readLocalCart().map((item) => String(item.id || item.productId) === String(productId) ? { ...item, quantity } : item);
  return localResponse(saveLocalCart(items));
}

export async function removeCartItem(productId) {
  if (readSession()) {
    try {
      const cart = await api(`/cart/items/${productId}`, { method: "DELETE" });
      return saveDbCart(cart);
    } catch {
      // Fall through to the local cart.
    }
  }
  return localResponse(saveLocalCart(readLocalCart().filter((item) => String(item.id || item.productId) !== String(productId))));
}

export async function clearCart() {
  if (readSession()) {
    try {
      await api("/cart", { method: "DELETE" });
    } catch {
      // Ignore
    }
  }
  saveLocalCart([]);
}

export function getLocalCartCount() {
  return readLocalCart().reduce((sum, item) => sum + Number(item.quantity || 1), 0);
}
