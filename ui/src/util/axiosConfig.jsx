import axios from "axios";

const BASE_URL = "http://localhost:8080/api/v1.0";

const excludeEndPoints = ["/login", "/register", "/health", "/status", "/activate"];

const axiosConfig = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
    },
});

axiosConfig.interceptors.request.use(
    (config) => {
        const shouldSkipToken = excludeEndPoints.some((endpoint) =>
            config.url?.includes(endpoint)
        );

        if (!shouldSkipToken) {
            const token = localStorage.getItem("token");
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        }

        return config;
    },
    (error) => Promise.reject(error)
);

axiosConfig.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            if (error.response.status === 401) {
                window.location.href = "/login";
            } else if (error.response.status === 500) {
                console.error("Server error. Please try again later.");
            }
        } else if (error.code === "ECONNABORTED") {
            console.error("Request timeout. Please try again.");
        }
        return Promise.reject(error);
    }
);

export default axiosConfig;
