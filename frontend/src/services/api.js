const API_URL = (import.meta.env.VITE_API_URL || "http://localhost:8080/api").replace(/\/$/, "");
const SESSION_KEY = "shopease_auth";

export class ApiError extends Error {
  constructor(message, status = 0, details = null) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.details = details;
  }
}

export function readSession() {
  try {
    return JSON.parse(localStorage.getItem(SESSION_KEY)) || null;
  } catch {
    localStorage.removeItem(SESSION_KEY);
    return null;
  }
}

export function emitAppChange() {
  window.dispatchEvent(new Event("shopease:change"));
}

export async function api(path, options = {}) {
  const { auth = true, timeout = 10000, body, headers, ...fetchOptions } = options;
  const controller = new AbortController();
  const timer = window.setTimeout(() => controller.abort(), timeout);
  const session = readSession();

  try {
    const response = await fetch(`${API_URL}${path}`, {
      ...fetchOptions,
      signal: controller.signal,
      headers: {
        Accept: "application/json",
        ...(body !== undefined ? { "Content-Type": "application/json" } : {}),
        ...(auth && session?.accessToken
          ? { Authorization: `Bearer ${session.accessToken}` }
          : {}),
        ...headers,
      },
      body: body === undefined ? undefined : JSON.stringify(body),
    });

    const payload = response.status === 204 ? null : await response.json().catch(() => null);
    if (!response.ok) {
      if (response.status === 401 && auth) {
        localStorage.removeItem(SESSION_KEY);
        localStorage.removeItem("token");
        emitAppChange();
      }
      throw new ApiError(
        payload?.message || `Request failed (${response.status})`,
        response.status,
        payload?.fieldErrors || payload?.details || null,
      );
    }
    return payload?.data ?? payload;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    if (error.name === "AbortError") {
      throw new ApiError("The server took too long to respond. Please try again.");
    }
    throw new ApiError("Cannot reach ShopEase right now. Check that the backend is running.");
  } finally {
    window.clearTimeout(timer);
  }
}

export { API_URL, SESSION_KEY };
