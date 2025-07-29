import axiosConfig from "../util/axiosConfig";
import { API_ENDPOINTS } from "../util/apiEndpoints";

export const getCategories = async () => {
    const res = await axiosConfig.get(API_ENDPOINTS.CATEGORIES);
    return res.data;
};

export const createCategory = async (category) => {
    const res = await axiosConfig.post(API_ENDPOINTS.CATEGORIES, category);
    return res.data;
};

export const updateCategory = async (id, category) => {
    const res = await axiosConfig.put(`${API_ENDPOINTS.CATEGORIES}/${id}`, category);
    return res.data;
};

export const deleteCategory = async (id) => {
    await axiosConfig.delete(`${API_ENDPOINTS.CATEGORIES}/${id}`);
};