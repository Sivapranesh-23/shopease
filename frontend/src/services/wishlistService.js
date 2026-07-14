import { api, emitAppChange, readSession } from "./api";
import { normalizeProduct } from "./productService";

const KEY = "shopease_wishlist";

export function getWishlist() {
  try {
    return JSON.parse(localStorage.getItem(KEY)) || [];
  } catch {
    return [];
  }
}

export function isWishlisted(id) {
  return getWishlist().some((item) => String(item.id) === String(id));
}

let syncing = false;
export async function syncWishlist() {
  if (!readSession() || syncing) return;
  syncing = true;
  try {
    const localItems = getWishlist();
    // If local storage has items, sync them to backend database
    if (localItems.length > 0) {
      for (const item of localItems) {
        try {
          await api(`/wishlist/${item.id}`, { method: "POST" });
        } catch {
          // Ignore individual failures (e.g. duplicates)
        }
      }
    }
    // Fetch fresh wishlist from database
    const dbWishlist = await api("/wishlist");
    const normalized = dbWishlist.map(normalizeProduct);
    localStorage.setItem(KEY, JSON.stringify(normalized));
    emitAppChange();
  } catch (err) {
    console.error("Failed to sync wishlist with backend", err);
  } finally {
    syncing = false;
  }
}

export async function toggleWishlist(product) {
  const items = getWishlist();
  const exists = items.some((item) => String(item.id) === String(product.id));
  
  // Optimistic UI update
  const next = exists 
    ? items.filter((item) => String(item.id) !== String(product.id)) 
    : [...items, normalizeProduct(product)];
  
  localStorage.setItem(KEY, JSON.stringify(next));
  emitAppChange();

  if (readSession()) {
    try {
      if (exists) {
        await api(`/wishlist/${product.id}`, { method: "DELETE" });
      } else {
        await api(`/wishlist/${product.id}`, { method: "POST" });
      }
      // Re-sync from DB
      await syncWishlist();
    } catch (err) {
      console.error("Failed to toggle wishlist item on backend", err);
    }
  }

  return { items: next, added: !exists };
}

// Automatically sync when state changes or session starts
window.addEventListener("shopease:change", () => {
  if (readSession()) {
    syncWishlist();
  }
});

// Initial trigger
if (readSession()) {
  setTimeout(syncWishlist, 200);
}
