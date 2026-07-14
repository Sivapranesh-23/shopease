import { Routes, Route, useLocation, Navigate } from "react-router-dom";

import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

import Home from "./pages/user/Home";
import Login from "./pages/user/Login";
import Register from "./pages/user/Register";
import Products from "./pages/user/Products";
import ProductDetails from "./pages/user/ProductDetails";
import Cart from "./pages/user/Cart";
import Wishlist from "./pages/user/Wishlist";
import Checkout from "./pages/user/Checkout";
import Payment from "./pages/user/Payment";
import OrderHistory from "./pages/user/OrderHistory";
import Profile from "./pages/user/Profile";

import Dashboard from "./pages/admin/Dashboard";
import ProductsManage from "./pages/admin/ProductsManage";
import CategoriesManage from "./pages/admin/CategoriesManage";
import UsersManage from "./pages/admin/UsersManage";
import OrdersManage from "./pages/admin/OrdersManage";
import InventoryManage from "./pages/admin/InventoryManage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  const location = useLocation();

  const hideNavbarFooter = location.pathname.startsWith("/admin");

  return (
    <>
      {!hideNavbarFooter && <Navbar />}

      <Routes>
        <Route path="/" element={<Home />} />

        <Route path="/login" element={<Login />} />
        <Route path="/admin/login" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route path="/products" element={<Products />} />
        <Route path="/products/:id" element={<ProductDetails />} />

        <Route path="/cart" element={<Cart />} />
        <Route path="/wishlist" element={<Wishlist />} />
        <Route path="/checkout" element={<ProtectedRoute><Checkout /></ProtectedRoute>} />
        <Route path="/payment" element={<ProtectedRoute><Payment /></ProtectedRoute>} />
        <Route path="/orders" element={<ProtectedRoute><OrderHistory /></ProtectedRoute>} />
        <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />

        <Route path="/admin/dashboard" element={<ProtectedRoute admin><Dashboard /></ProtectedRoute>} />
        <Route path="/admin/products" element={<ProtectedRoute admin><ProductsManage /></ProtectedRoute>} />
        <Route path="/admin/categories" element={<ProtectedRoute admin><CategoriesManage /></ProtectedRoute>} />
        <Route path="/admin/users" element={<ProtectedRoute admin><UsersManage /></ProtectedRoute>} />
        <Route path="/admin/orders" element={<ProtectedRoute admin><OrdersManage /></ProtectedRoute>} />
        <Route path="/admin/inventory" element={<ProtectedRoute admin><InventoryManage /></ProtectedRoute>} />

        <Route path="*" element={<Navigate to="/" />} />
      </Routes>

      {!hideNavbarFooter && <Footer />}
    </>
  );
}

export default App;
