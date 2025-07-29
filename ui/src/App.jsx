import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import { useContext } from "react";
import { AppContext } from "./context/AppContext";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import Home from "./pages/Home";
import Income from "./pages/Income";
import Expense from "./pages/Expense";
import Category from "./pages/Category";
import Filter from "./pages/Filter";
import Navbar from "./pages/Navbar.jsx";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function ProtectedRoute({ children }) {
    const { user } = useContext(AppContext);
    if (!user) {
        return <Navigate to="/login" replace />;
    }
    return children;
}

export default function App() {
    const location = useLocation();

    // Пути, на которых Navbar НЕ показывается
    const hideNavbarPaths = ["/login", "/signup"];
    const shouldShowNavbar = !hideNavbarPaths.includes(location.pathname);

    return (
        <>
            {shouldShowNavbar && <Navbar />}
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<SignUp />} />
                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Home />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/income"
                    element={
                        <ProtectedRoute>
                            <Income />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/expense"
                    element={
                        <ProtectedRoute>
                            <Expense />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/category"
                    element={
                        <ProtectedRoute>
                            <Category />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/filter"
                    element={
                        <ProtectedRoute>
                            <Filter />
                        </ProtectedRoute>
                    }
                />
                <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
            <ToastContainer />
        </>
    );
}
