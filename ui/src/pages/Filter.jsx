import { useState } from "react";
import axiosConfig from "../util/axiosConfig";
import { API_ENDPOINTS } from "../util/apiEndpoints";

export default function Filter() {
    const [type, setType] = useState("income");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [keyword, setKeyword] = useState("");
    const [sortField, setSortField] = useState("date");
    const [sortOrder, setSortOrder] = useState("desc");
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleFilter = async () => {
        setLoading(true);
        try {
            const res = await axiosConfig.post(API_ENDPOINTS.FILTER, {
                type,
                startDate: startDate || null,
                endDate: endDate || null,
                keyword,
                sortField,
                sortOrder,
            });
            setResults(res.data);
        } catch (error) {
            console.error(error);
            setResults([]);
        }
        setLoading(false);
    };

    return (
        <div className="max-w-4xl mx-auto p-6">
            <h2 className="text-2xl font-semibold mb-4">Filter Transactions</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                <select
                    className="border p-2 rounded"
                    value={type}
                    onChange={(e) => setType(e.target.value)}
                >
                    <option value="income">Income</option>
                    <option value="expense">Expense</option>
                </select>
                <input
                    type="date"
                    className="border p-2 rounded"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    placeholder="Start Date"
                />
                <input
                    type="date"
                    className="border p-2 rounded"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    placeholder="End Date"
                />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                <input
                    type="text"
                    className="border p-2 rounded col-span-2"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                    placeholder="Keyword (e.g. Salary)"
                />
                <select
                    className="border p-2 rounded"
                    value={sortField}
                    onChange={(e) => setSortField(e.target.value)}
                >
                    <option value="date">Date</option>
                    <option value="amount">Amount</option>
                </select>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <select
                    className="border p-2 rounded"
                    value={sortOrder}
                    onChange={(e) => setSortOrder(e.target.value)}
                >
                    <option value="desc">Descending</option>
                    <option value="asc">Ascending</option>
                </select>

                <button
                    onClick={handleFilter}
                    className="col-span-2 bg-blue-600 text-white p-2 rounded hover:bg-blue-700 transition"
                    disabled={loading}
                >
                    {loading ? "Loading..." : "Apply Filter"}
                </button>
            </div>

            <div>
                {results.length === 0 && !loading && (
                    <p className="text-center text-gray-500">No results found.</p>
                )}
                <ul className="divide-y divide-gray-300">
                    {results.map((item) => (
                        <li key={item.id} className="py-3 flex justify-between items-center">
                            <div>
                                <p className="font-semibold">{item.name}</p>
                                <p className="text-sm text-gray-600">{item.categoryName}</p>
                                <p className="text-xs text-gray-500">{new Date(item.date).toLocaleDateString()}</p>
                            </div>
                            <div className="font-bold text-green-600">{type === "income" ? "+" : "-"}${item.amount.toFixed(2)}</div>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}
