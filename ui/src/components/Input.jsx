import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";

export default function Input({ label, value, onChange, placeholder, type }) {
    const [showPassword, setShowPassword] = useState(false);
    const toggleShowPassword = () => setShowPassword(!showPassword);

    return (
        <div className="mb-4">
            <label className="text-[13px] text-slate-800 block mb-1">
                {label}
            </label>
            <div className="relative">
                <input
                    type={type === "password" ? (showPassword ? "text" : "password") : type}
                    value={value}
                    onChange={onChange}
                    placeholder={placeholder}
                    className="w-full bg-transparent outline-none border border-gray-300 rounded-md py-2 px-3 pr-10
                     text-gray-700 leading-tight focus:outline-none focus:border-blue-500"
                />
                {type === "password" && (
                    <span
                        className="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer"
                        onClick={toggleShowPassword}
                    >
            {showPassword ? (
                <EyeOff size={20} className="text-primary" />
            ) : (
                <Eye size={20} className="text-primary" />
            )}
          </span>
                )}
            </div>
        </div>
    );
}
