import { createContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axiosConfig from "../util/axiosConfig"; // твой axios с токеном

export const AppContext = createContext();

export const AppContextProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token && !user) {
            // Получаем данные текущего пользователя с backend
            axiosConfig.get("/profile")
                .then(res => setUser(res.data))
                .catch(() => {
                    localStorage.removeItem("token");
                    setUser(null);
                    navigate("/login");
                });
        }
    }, []);

    const logout = () => {
        localStorage.removeItem("token");
        setUser(null);
        navigate("/login");
    };

    return (
        <AppContext.Provider value={{ user, setUser, logout }}>
            {children}
        </AppContext.Provider>
    );
};
