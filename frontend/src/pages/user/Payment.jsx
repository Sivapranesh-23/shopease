import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { clearCart } from "../../services/cartService";
import { confirmPayment, createPaymentIntent, getPaymentConfig } from "../../services/orderService";
import { formatCurrency } from "../../utils/format";
import { loadStripe } from "@stripe/stripe-js";
import { Elements, CardElement, useStripe, useElements } from "@stripe/react-stripe-js";

function Payment() {
  const { state } = useLocation();
  const order = state?.order;
  const [stripeKey, setStripeKey] = useState("");
  const [stripePromise, setStripePromise] = useState(null);
  const [loadingConfig, setLoadingConfig] = useState(true);

  useEffect(() => {
    getPaymentConfig()
      .then((data) => {
        if (data?.publishableKey) {
          setStripeKey(data.publishableKey);
          setStripePromise(loadStripe(data.publishableKey));
        }
      })
      .catch((err) => console.error("Error loading payment config", err))
      .finally(() => setLoadingConfig(false));
  }, []);

  if (!order) {
    return (
      <main className="page-shell">
        <div className="empty-state">
          <h1>No payment in progress</h1>
          <p>Start from your cart when you are ready.</p>
          <Link className="button" to="/cart">View cart</Link>
        </div>
      </main>
    );
  }

  if (loadingConfig) {
    return (
      <main className="page-shell">
        <div className="empty-state">
          <p>Loading payment configuration…</p>
        </div>
      </main>
    );
  }

  return (
    <main className="page-shell">
      <header className="page-header compact">
        <span className="eyebrow">Final step</span>
        <h1>Payment</h1>
        <p>{stripeKey ? "Complete your payment securely with Stripe." : "Sandbox checkout — no real card is charged."}</p>
      </header>

      {stripePromise ? (
        <Elements stripe={stripePromise}>
          <StripePaymentForm order={order} />
        </Elements>
      ) : (
        <StubPaymentForm order={order} />
      )}
    </main>
  );
}

function StripePaymentForm({ order }) {
  const stripe = useStripe();
  const elements = useElements();
  const [cardName, setCardName] = useState("");
  const [status, setStatus] = useState("ready");
  const [error, setError] = useState("");

  const pay = async (event) => {
    event.preventDefault();
    if (!stripe || !elements) return;
    setStatus("processing");
    setError("");

    try {
      // 1. Create PaymentIntent
      const intent = await createPaymentIntent(order.id, order.grandTotal, "INR");

      // 2. Confirm card payment
      const cardElement = elements.getElement(CardElement);
      const result = await stripe.confirmCardPayment(intent.clientSecret, {
        payment_method: {
          card: cardElement,
          billing_details: {
            name: cardName,
          },
        },
      });

      if (result.error) {
        throw new Error(result.error.message);
      }

      if (result.paymentIntent.status === "succeeded") {
        // 3. Confirm on backend to finalize
        await confirmPayment(result.paymentIntent.id);
        await clearCart();
        setStatus("complete");
      } else {
        throw new Error("Payment status is: " + result.paymentIntent.status);
      }
    } catch (err) {
      setError(err.message);
      setStatus("ready");
    }
  };

  if (status === "complete") return <PaymentSuccessScreen order={order} />;

  return (
    <form className="payment-layout" onSubmit={pay}>
      <section className="form-card">
        <h2>Card Details</h2>
        {error && <div className="error-banner">{error}</div>}
        <label>
          Name on card
          <input
            required
            autoComplete="cc-name"
            value={cardName}
            onChange={(e) => setCardName(e.target.value)}
            placeholder="John Doe"
          />
        </label>
        <label style={{ marginTop: "1rem" }}>
          Card number, Expiry, and CVC
          <div className="stripe-card-wrapper" style={{ padding: "0.8rem", border: "1px solid #ccc", borderRadius: "4px", backgroundColor: "#fff" }}>
            <CardElement
              options={{
                style: {
                  base: {
                    color: "#333",
                    fontFamily: "Inter, sans-serif",
                    fontSize: "16px",
                    "::placeholder": { color: "#aab7c4" },
                  },
                  invalid: { color: "#fa755a", iconColor: "#fa755a" },
                },
              }}
            />
          </div>
        </label>
      </section>
      <PaymentSummaryPanel order={order} status={status} />
    </form>
  );
}

function StubPaymentForm({ order }) {
  const [card, setCard] = useState({ number: "", expiry: "", cvc: "", name: "" });
  const [status, setStatus] = useState("ready");
  const [error, setError] = useState("");

  const pay = async (event) => {
    event.preventDefault();
    setStatus("processing");
    setError("");

    try {
      const intent = await createPaymentIntent(order.id, order.grandTotal, "INR");
      await confirmPayment(intent.providerPaymentId);
      await clearCart();
      setStatus("complete");
    } catch (err) {
      setError(err.message);
      setStatus("ready");
    }
  };

  if (status === "complete") return <PaymentSuccessScreen order={order} />;

  return (
    <form className="payment-layout" onSubmit={pay}>
      <section className="form-card">
        <h2>Card details (Mock Mode)</h2>
        {error && <div className="error-banner">{error}</div>}
        <label>
          Name on card
          <input
            required
            autoComplete="cc-name"
            value={card.name}
            onChange={(e) => setCard({ ...card, name: e.target.value })}
          />
        </label>
        <label>
          Card number
          <input
            required
            inputMode="numeric"
            autoComplete="cc-number"
            pattern="[0-9 ]{12,23}"
            placeholder="4242 4242 4242 4242"
            value={card.number}
            onChange={(e) => setCard({ ...card, number: e.target.value.replace(/[^0-9 ]/g, "") })}
          />
        </label>
        <div className="form-grid">
          <label>
            Expiry
            <input
              required
              autoComplete="cc-exp"
              pattern="[0-9]{2}/[0-9]{2}"
              placeholder="MM/YY"
              value={card.expiry}
              onChange={(e) => setCard({ ...card, expiry: e.target.value })}
            />
          </label>
          <label>
            CVC
            <input
              required
              autoComplete="cc-csc"
              inputMode="numeric"
              pattern="[0-9]{3,4}"
              value={card.cvc}
              onChange={(e) => setCard({ ...card, cvc: e.target.value })}
            />
          </label>
        </div>
      </section>
      <PaymentSummaryPanel order={order} status={status} />
    </form>
  );
}

function PaymentSummaryPanel({ order, status }) {
  return (
    <aside className="summary-card">
      <span className="eyebrow">Order {order.orderNumber}</span>
      <div className="summary-total">
        <span>Total</span>
        <b>{formatCurrency(order.grandTotal)}</b>
      </div>
      <button className="button full" disabled={status === "processing"}>
        {status === "processing" ? "Confirming…" : `Pay ${formatCurrency(order.grandTotal)}`}
      </button>
      <p>Encrypted connection · Secured checkout</p>
    </aside>
  );
}

function PaymentSuccessScreen({ order }) {
  return (
    <main className="page-shell">
      <div className="success-card">
        <span>✓</span>
        <div className="eyebrow">Payment confirmed</div>
        <h1>Thank you. It’s all yours.</h1>
        <p>Order <b>{order.orderNumber}</b> has been placed. We’ll keep you updated as it moves.</p>
        <div className="button-row">
          <Link className="button" to="/orders">Track order</Link>
          <Link className="button ghost" to="/products">Continue shopping</Link>
        </div>
      </div>
    </main>
  );
}

export default Payment;
