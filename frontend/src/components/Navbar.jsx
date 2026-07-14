import { useEffect, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { getSession, logout } from "../services/authService";
import { getLocalCartCount } from "../services/cartService";
import { getWishlist } from "../services/wishlistService";

function Navbar() {
  const navigate = useNavigate();
  const snapshot = () => ({ session: getSession(), cart: getLocalCartCount(), wishlist: getWishlist().length });
  const [state, setState] = useState(snapshot);

  useEffect(() => {
    const update = () => setState(snapshot());
    window.addEventListener("shopease:change", update);
    window.addEventListener("storage", update);
    return () => {
      window.removeEventListener("shopease:change", update);
      window.removeEventListener("storage", update);
    };
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };

  return (
    <nav className="navbar">
      <Link to="/" className="brand">Shop<span>Ease</span></Link>
      <div className="nav-links">
        <NavLink to="/">Home</NavLink>
        <NavLink to="/products">Shop</NavLink>
        <NavLink to="/wishlist">Wishlist <small>{state.wishlist}</small></NavLink>
        <NavLink to="/cart">Cart <small>{state.cart}</small></NavLink>
        {state.session ? (
          <>
            {state.session.user?.role === "ADMIN" && <Link to="/admin/dashboard">Admin</Link>}
            <NavLink to="/profile">{state.session.user?.name?.split(" ")[0] || "Account"}</NavLink>
            <button className="link-button" onClick={handleLogout}>Logout</button>
          </>
        ) : <NavLink to="/login">Sign in</NavLink>}
      </div>
    </nav>
  );
}

export default Navbar;
