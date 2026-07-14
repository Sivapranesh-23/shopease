import { api, emitAppChange, readSession, SESSION_KEY } from "./api";

function saveSession(session) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  localStorage.setItem("token", session.accessToken);
  localStorage.setItem("user", JSON.stringify(session.user));
  emitAppChange();
  return session;
}

export async function login(credentials) {
  return saveSession(await api("/auth/login", { method: "POST", auth: false, body: credentials }));
}

export async function register(details) {
  return saveSession(await api("/auth/register", { method: "POST", auth: false, body: details }));
}

export async function getProfile() {
  const user = await api("/auth/me");
  const session = readSession();
  if (session) saveSession({ ...session, user });
  return user;
}

export async function logout() {
  try {
    if (readSession()) await api("/auth/logout", { method: "POST" });
  } finally {
    clearSession();
  }
}

export function clearSession() {
  localStorage.removeItem(SESSION_KEY);
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  emitAppChange();
}

export function getSession() {
  return readSession();
}

export function isAdmin() {
  return readSession()?.user?.role === "ADMIN";
}
