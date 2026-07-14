function Footer() {
  return (
    <>
      <style>{`
        .footer {
          background: #111827;
          color: #ffffff;
          width: 100%;
          margin-top: auto;
        }

        .footer-container {
          max-width: 1400px;
          margin: auto;
          padding: 50px 40px 30px;
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
          gap: 30px;
        }

        .footer-section h3 {
          margin-bottom: 15px;
          color: #60a5fa;
          font-size: 20px;
        }

        .footer-section p {
          line-height: 1.8;
          color: #d1d5db;
        }

        .footer-section ul {
          list-style: none;
          padding: 0;
        }

        .footer-section ul li {
          margin: 10px 0;
        }

        .footer-section ul li a {
          color: #d1d5db;
          text-decoration: none;
          transition: 0.3s;
        }

        .footer-section ul li a:hover {
          color: #60a5fa;
          padding-left: 5px;
        }

        .footer-bottom {
          border-top: 1px solid #374151;
          text-align: center;
          padding: 15px;
          color: #9ca3af;
          font-size: 14px;
        }

        @media (max-width: 768px) {
          .footer-container {
            padding: 40px 20px;
            text-align: center;
          }
        }
      `}</style>

      <footer className="footer">
        <div className="footer-container">

          <div className="footer-section">
            <h3>ShopEase</h3>
            <p>
              Your trusted online shopping destination for quality products,
              secure payments, and fast delivery.
            </p>
          </div>

          <div className="footer-section">
            <h3>Quick Links</h3>
            <ul>
              <li><a href="/">Home</a></li>
              <li><a href="/products">Products</a></li>
              <li><a href="/wishlist">Wishlist</a></li>
              <li><a href="/cart">Cart</a></li>
            </ul>
          </div>

          <div className="footer-section">
            <h3>Customer Service</h3>
            <ul>
              <li><a href="/">Contact Us</a></li>
              <li><a href="/">FAQs</a></li>
              <li><a href="/">Shipping Policy</a></li>
              <li><a href="/">Returns & Refunds</a></li>
            </ul>
          </div>

          <div className="footer-section">
            <h3>Contact</h3>
            <p>📍 Nagercoil, Tamil Nadu</p>
            <p>📞 +91 98765 43210</p>
            <p>✉ support@shopease.com</p>
          </div>

        </div>

        <div className="footer-bottom">
          <p>© 2025 ShopEase. All Rights Reserved.</p>
        </div>
      </footer>
    </>
  );
}

export default Footer;