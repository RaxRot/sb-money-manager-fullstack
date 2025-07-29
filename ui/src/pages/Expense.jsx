import { useEffect, useState } from "react";
import axiosConfig from "../util/axiosConfig";
import { API_ENDPOINTS } from "../util/apiEndpoints";
import { toast } from "react-toastify";

export default function Expense() {
    const [categories, setCategories] = useState([]);
    const [expenses, setExpenses] = useState([]);
    const [form, setForm] = useState({
        name: "",
        amount: "",
        categoryId: "",
    });

    // Загрузка категорий только типа "expense"
    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const res = await axiosConfig.get(`${API_ENDPOINTS.CATEGORIES}/expense`);
                setCategories(res.data);
            } catch {
                toast.error("Failed to load categories");
            }
        };

        fetchCategories();
    }, []);

    // Загрузка расходов
    useEffect(() => {
        const fetchExpenses = async () => {
            try {
                const res = await axiosConfig.get(API_ENDPOINTS.EXPENSES);
                setExpenses(res.data);
            } catch {
                toast.error("Failed to load expenses");
            }
        };

        fetchExpenses();
    }, []);

    // Обработка изменения полей формы
    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    // Создание нового расхода
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.name || !form.amount || !form.categoryId) {
            toast.error("Please fill all fields");
            return;
        }
        try {
            const res = await axiosConfig.post(API_ENDPOINTS.EXPENSES, {
                name: form.name,
                amount: parseFloat(form.amount),
                categoryId: parseInt(form.categoryId),
            });
            setExpenses(prev => [res.data, ...prev]);
            setForm({ name: "", amount: "", categoryId: "" });
            toast.success("Expense added!");
        } catch {
            toast.error("Failed to add expense");
        }
    };

    return (
        <div className="max-w-3xl mx-auto p-6">
            <h1 className="text-2xl font-bold mb-4">Expenses</h1>

            <form onSubmit={handleSubmit} className="mb-6 space-y-4">
                <input
                    name="name"
                    placeholder="Expense name"
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
                    className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                >
                    Add Expense
                </button>
            </form>

            <ul>
                {expenses.map(e => (
                    <li key={e.id} className="mb-2 p-2 border rounded flex justify-between">
                        <div>
                            <p className="font-semibold">{e.name}</p>
                            <p className="text-sm text-gray-600">Category: {e.categoryName}</p>
                            <p className="text-xs text-gray-500">{new Date(e.date).toLocaleDateString()}</p>
                        </div>
                        <div className="font-bold text-red-700">${e.amount.toFixed(2)}</div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
