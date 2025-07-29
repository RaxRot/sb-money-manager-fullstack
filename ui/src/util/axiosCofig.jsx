import axios from "axios";
import {BASE_URL} from "./apiEndpoints.jsx";

const axiosConfig = axios.create({
    baseURL: BASE_URL,
    headers:{
        "Content-Type":"application/json",
        Accept:"application/json"
    }
});

// список эндпоинтов, куда не нужен токен
const excludeEndPoints = ["/login", "/register", "/health", "/status", "/activate"];

// request interceptor
axiosConfig.interceptors.request.use(
    (config) => {
        const shouldSkipToken = excludeEndPoints.some((endPoint) =>
            config.url?.includes(endPoint)
        );

        if (!shouldSkipToken) {
            const accessToken = localStorage.getItem("token");
            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
            }
        }

        return config;
    },
    (error) => Promise.reject(error)
);

//response interceptor
axiosConfig.interceptors.response.use((response) => {
    return response;
}, (error) => {
    if(error.response) {
        if (error.response.status === 401) {
            window.location.href = "/login";
        } else if (error.response.status === 500) {
            console.error("Server error. Please try again later");
        }
    } else if(error.code === "ECONNABORTED") {
        console.error("Request timeout. Please try again.");
    }
    return Promise.reject(error);
})

export default axiosConfig;