import { useEffect, useState } from "react";
import { getCategories, createCategory, updateCategory, deleteCategory } from "../services/categoryService";
import { toast } from "react-toastify";

export default function Category() {
    const [categories, setCategories] = useState([]);
    const [form, setForm] = useState({ name: "", type: "", icon: "" });
    const [editingId, setEditingId] = useState(null);

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const data = await getCategories();
            setCategories(data);
        } catch (error) {
            toast.error("Failed to load categories");
        }
    };

    const handleInputChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.name || !form.type) {
            toast.error("Name and Type are required");
            return;
        }

        try {
            if (editingId) {
                await updateCategory(editingId, form);
                toast.success("Category updated");
            } else {
                await createCategory(form);
                toast.success("Category created");
            }
            setForm({ name: "", type: "", icon: "" });
            setEditingId(null);
            loadCategories();
        } catch (error) {
            toast.error("Error saving category");
        }
    };

    const handleEdit = (cat) => {
        setForm({ name: cat.name, type: cat.type, icon: cat.icon || "" });
        setEditingId(cat.id);
    };

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this category?")) {
            try {
                await deleteCategory(id);
                toast.success("Category deleted");
                loadCategories();
            } catch (error) {
                toast.error("Failed to delete category");
            }
        }
    };

    return (
        <div className="max-w-4xl mx-auto p-6">
            <h1 className="text-3xl font-bold mb-6">Categories</h1>
            <form onSubmit={handleSubmit} className="mb-6 space-y-4 bg-white p-4 rounded shadow">
                <div>
                    <label className="block mb-1 font-semibold">Name</label>
                    <input
                        name="name"
                        value={form.name}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                        placeholder="Category name"
                        required
                    />
                </div>
                <div>
                    <label className="block mb-1 font-semibold">Type</label>
                    <select
                        name="type"
                        value={form.type}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                        required
                    >
                        <option value="">Select type</option>
                        <option value="income">Income</option>
                        <option value="expense">Expense</option>
                    </select>
                </div>
                <div>
                    <label className="block mb-1 font-semibold">Icon (optional)</label>
                    <input
                        name="icon"
                        value={form.icon}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                        placeholder="Icon name or URL"
                    />
                </div>
                <button
                    type="submit"
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                >
                    {editingId ? "Update Category" : "Add Category"}
                </button>
            </form>

            <ul className="bg-white rounded shadow divide-y divide-gray-200">
                {categories.map((cat) => (
                    <li key={cat.id} className="flex justify-between items-center px-4 py-3">
                        <div>
                            <p className="font-semibold">{cat.name}</p>
                            <p className="text-sm text-gray-500 capitalize">{cat.type}</p>
                        </div>
                        <div className="flex gap-3">
                            <button
                                onClick={() => handleEdit(cat)}
                                className="text-blue-600 hover:underline"
                            >
                                Edit
                            </button>
                            <button
                                onClick={() => handleDelete(cat.id)}
                                className="text-red-600 hover:underline"
                            >
                                Delete
                            </button>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
