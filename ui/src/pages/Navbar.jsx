import { NavLink } from "react-router-dom";
import { useContext } from "react";
import { AppContext } from "../context/AppContext";

export default function Navbar() {
    const { user, logout } = useContext(AppContext);

    return (
        <nav className="bg-gradient-to-r from-blue-700 via-indigo-700 to-purple-700 text-white p-4 shadow-md sticky top-0 z-50">
            <div className="max-w-6xl mx-auto flex justify-between items-center">
                <div className="flex space-x-6">
                    <NavLink
                        to="/dashboard"
                        className={({ isActive }) =>
                            isActive
                                ? "border-b-2 border-white font-semibold pb-1"
                                : "hover:border-b-2 hover:border-gray-300 pb-1 transition"
                        }
                    >
                        Dashboard
                    </NavLink>
                    <NavLink
                        to="/income"
                        className={({ isActive }) =>
                            isActive
                                ? "border-b-2 border-white font-semibold pb-1"
                                : "hover:border-b-2 hover:border-gray-300 pb-1 transition"
                        }
                    >
                        Income
                    </NavLink>
                    <NavLink
                        to="/expense"
                        className={({ isActive }) =>
                            isActive
                                ? "border-b-2 border-white font-semibold pb-1"
                                : "hover:border-b-2 hover:border-gray-300 pb-1 transition"
                        }
                    >
                        Expense
                    </NavLink>
                    <NavLink
                        to="/category"
                        className={({ isActive }) =>
                            isActive
                                ? "border-b-2 border-white font-semibold pb-1"
                                : "hover:border-b-2 hover:border-gray-300 pb-1 transition"
                        }
                    >
                        Category
                    </NavLink>
                    <NavLink
                        to="/filter"
                        className={({ isActive }) =>
                            isActive
                                ? "border-b-2 border-white font-semibold pb-1"
                                : "hover:border-b-2 hover:border-gray-300 pb-1 transition"
                        }
                    >
                        Filter
                    </NavLink>
                </div>

                {user ? (
                    <div className="flex items-center space-x-4">
            <span className="text-sm font-medium">
              Hello, {user.fullName ? user.fullName.split(" ")[0] : "User"}
            </span>
                        <button
                            onClick={logout}
                            className="bg-red-600 hover:bg-red-700 px-3 py-1 rounded transition"
                        >
                            Logout
                        </button>
                    </div>
                ) : null}
            </div>
        </nav>
    );
}
