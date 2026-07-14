import { Navigate } from "react-router-dom";
import { getSession } from "../services/authService";

function ProtectedRoute({ children, admin = false }) {
  const session = getSession();

  if (!session?.accessToken) {
    return <Navigate to="/login" replace />;
  }

  if (admin && session.user?.role !== "ADMIN") {
    return <Navigate to="/products" replace />;
  }

  return children;
}

export default ProtectedRoute;
