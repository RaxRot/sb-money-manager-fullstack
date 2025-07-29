import { useEffect, useState } from "react";
import axiosConfig from "../util/axiosConfig";
import { API_ENDPOINTS } from "../util/apiEndpoints";
import { toast } from "react-toastify";

export default function Income() {
    const [categories, setCategories] = useState([]);
    const [incomes, setIncomes] = useState([]);
    const [form, setForm] = useState({
        name: "",
        amount: "",
        categoryId: "",
    });

    // Загрузка категорий только типа "income"
    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const res = await axiosConfig.get(`${API_ENDPOINTS.CATEGORIES}/income`);
                setCategories(res.data);
            } catch {
                toast.error("Failed to load categories");
            }
        };

        fetchCategories();
    }, []);

    // Загрузка доходов
    useEffect(() => {
        const fetchIncomes = async () => {
            try {
                const res = await axiosConfig.get(API_ENDPOINTS.INCOMES);
                setIncomes(res.data);
            } catch {
                toast.error("Failed to load incomes");
            }
        };

        fetchIncomes();
    }, []);

    // Обработка изменения полей формы
    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    // Создание нового дохода
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.name || !form.amount || !form.categoryId) {
            toast.error("Please fill all fields");
            return;
        }
        try {
            const res = await axiosConfig.post(API_ENDPOINTS.INCOMES, {
                name: form.name,
                amount: parseFloat(form.amount),
                categoryId: parseInt(form.categoryId),
            });
            setIncomes(prev => [res.data, ...prev]);
            setForm({ name: "", amount: "", categoryId: "" });
            toast.success("Income added!");
        } catch {
            toast.error("Failed to add income");
        }
    };

    return (
        <div className="max-w-3xl mx-auto p-6">
            <h1 className="text-2xl font-bold mb-4">Incomes</h1>

            <form onSubmit={handleSubmit} className="mb-6 space-y-4">
                <input
                    name="name"
                    placeholder="Income name"
                    value={form.name}
                    onChange={handleChange}
                    className="w-full border px-3 py-2 rounded"
                />
                <input
                    name="amount"
                    placeholder="Amount"
                    type="number"
                    step="0.01"
                    value={form.amount}
                    onChange={handleChange}
                    className="w-full border px-3 py-2 rounded"
                />
                <select
                    name="categoryId"
                    value={form.categoryId}
                    onChange={handleChange}
                    className="w-full border px-3 py-2 rounded"
                >
                    <option value="">Select category</option>
                    {categories.map(cat => (
                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                    ))}
                </select>
                <button
                    type="submit"
                    className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
                >
                    Add Income
                </button>
            </form>

            <ul>
                {incomes.map(i => (
                    <li key={i.id} className="mb-2 p-2 border rounded flex justify-between">
                        <div>
                            <p className="font-semibold">{i.name}</p>
                            <p className="text-sm text-gray-600">Category: {i.categoryName}</p>
                            <p className="text-xs text-gray-500">{new Date(i.date).toLocaleDateString()}</p>
                        </div>
                        <div className="font-bold text-green-700">${i.amount.toFixed(2)}</div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
