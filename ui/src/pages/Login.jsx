import { useState, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import Input from "../components/Input";
import axiosConfig from "../util/axiosConfig";
import { API_ENDPOINTS } from "../util/apiEndpoints";
import { AppContext } from "../context/AppContext";
import { toast } from "react-toastify";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const { setUser } = useContext(AppContext);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        if (!email.trim() || !password.trim()) {
            setError("Email and password are required");
            return;
        }
        try {
            const res = await axiosConfig.post(API_ENDPOINTS.LOGIN, { email, password });
            const { token } = res.data;
            localStorage.setItem("token", token);
            setUser({ token, email });
            toast.success("Login successful");
            navigate("/dashboard");
        } catch (err) {
            const errorData = err.response?.data;
            let errorMsg = "Invalid credentials or account not activated";

            if (typeof errorData === "string") {
                errorMsg = errorData;
            } else if (errorData?.message) {
                errorMsg = errorData.message;
            } else if (typeof errorData === "object") {
                errorMsg = Object.values(errorData)[0];
            }

            setError(errorMsg);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
                <h2 className="text-2xl font-bold mb-6 text-center">Login</h2>
                <form onSubmit={handleSubmit}>
                    <Input
                        label="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="you@example.com"
                    />
                    <Input
                        label="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Your password"
                        type="password"
                    />
                    {error && <p className="text-red-600 mb-3">{error}</p>}
                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
                    >
                        Log In
                    </button>
                </form>
                <p className="mt-4 text-center">
                    Don't have an account?{" "}
                    <Link to="/signup" className="text-blue-600 hover:underline">
                        Sign Up
                    </Link>
                </p>
            </div>
        </div>
    );
}
