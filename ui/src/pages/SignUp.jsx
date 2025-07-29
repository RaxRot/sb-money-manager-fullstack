import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Input from "../components/Input";
import axiosConfig from "../util/axiosConfig";
import { API_ENDPOINTS } from "../util/apiEndpoints";
import { toast } from "react-toastify";

export default function SignUp() {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        if (!fullName.trim() || !email.trim() || !password.trim()) {
            setError("All fields are required");
            return;
        }
        try {
            const res = await axiosConfig.post(API_ENDPOINTS.REGISTER, {
                fullName,
                email,
                password,
            });
            if (res.status === 201) {
                toast.success("Registration successful! Check your email to activate your account.");
                navigate("/login");
            }
        } catch (err) {
            if (err.response?.data) {
                if (typeof err.response.data === "object") {
                    const messages = Object.values(err.response.data).join(", ");
                    setError(messages);
                } else {
                    setError(err.response.data);
                }
            } else {
                setError("Registration failed");
            }
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
                <h2 className="text-2xl font-bold mb-6 text-center">Sign Up</h2>
                <form onSubmit={handleSubmit}>
                    <Input
                        label="Full Name"
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        placeholder="Your full name"
                    />
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
                        className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
                    >
                        Sign Up
                    </button>
                </form>
                <p className="mt-4 text-center">
                    Already have an account?{" "}
                    <Link to="/login" className="text-blue-600 hover:underline">
                        Log In
                    </Link>
                </p>
            </div>
        </div>
    );
}
