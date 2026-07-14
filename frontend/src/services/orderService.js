import { api } from "./api";

export function placeOrder(order) {
  return api("/orders", { method: "POST", body: order });
}

export async function listOrders(page = 0, size = 10) {
  return api(`/orders?page=${page}&size=${size}`);
}

export function getOrder(orderNumber) {
  return api(`/orders/${orderNumber}`);
}

export function createPaymentIntent(orderId, amount, currency = "INR") {
  return api(`/payments/intent/${orderId}`, { method: "POST", body: { amount, currency } });
}

export function confirmPayment(providerPaymentId) {
  return api("/payments/confirm", { method: "POST", body: { providerPaymentId } });
}

export function getPaymentConfig() {
  return api("/payments/config");
}
