import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";

export default function Input({ label, value, onChange, placeholder, type = "text" }) {
    const [showPassword, setShowPassword] = useState(false);
    const toggleShowPassword = () => setShowPassword((prev) => !prev);

    return (
        <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <div className="relative">
                <input
                    type={type === "password" ? (showPassword ? "text" : "password") : type}
                    value={value}
                    onChange={onChange}
                    placeholder={placeholder}
                    className="w-full border border-gray-300 rounded-md py-2 px-3 pr-10 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                {type === "password" && (
                    <button
                        type="button"
                        onClick={toggleShowPassword}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-blue-600"
                    >
                        {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                )}
            </div>
        </div>
    );
}
