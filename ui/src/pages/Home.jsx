import { useEffect, useState } from "react";
import axiosConfig from "../util/axiosConfig.jsx";
import { API_ENDPOINTS } from "../util/apiEndpoints";

export default function Home() {
    const [dashboard, setDashboard] = useState(null);

    useEffect(() => {
        axiosConfig.get(API_ENDPOINTS.DASHBOARD)
            .then(res => setDashboard(res.data))
            .catch(err => console.error(err));
    }, []);

    if (!dashboard) return <p className="text-center mt-20">Loading dashboard...</p>;

    return (
        <div className="p-6 max-w-5xl mx-auto">
            <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
            <div className="grid grid-cols-3 gap-6 mb-8">
                <div className="bg-green-100 p-4 rounded shadow">
                    <h2 className="font-semibold text-green-700">Total Income</h2>
                    <p className="text-xl font-bold">${dashboard.totalIncome.toFixed(2)}</p>
                </div>
                <div className="bg-red-100 p-4 rounded shadow">
                    <h2 className="font-semibold text-red-700">Total Expense</h2>
                    <p className="text-xl font-bold">${dashboard.totalExpense.toFixed(2)}</p>
                </div>
                <div className="bg-blue-100 p-4 rounded shadow">
                    <h2 className="font-semibold text-blue-700">Balance</h2>
                    <p className="text-xl font-bold">${dashboard.totalBalance.toFixed(2)}</p>
                </div>
            </div>

            <section className="mb-8">
                <h3 className="text-2xl font-semibold mb-4">Recent Transactions</h3>
                <ul className="divide-y divide-gray-300">
                    {dashboard.recentTransactions.map(txn => (
                        <li key={txn.id} className="py-2 flex justify-between items-center">
                            <div>
                                <p className="font-semibold">{txn.name}</p>
                                <p className="text-sm text-gray-600">{txn.type}</p>
                                <p className="text-xs text-gray-500">{new Date(txn.date).toLocaleDateString()}</p>
                            </div>
                            <div className={`${txn.type === 'income' ? 'text-green-600' : 'text-red-600'} font-bold`}>
                                ${txn.amount.toFixed(2)}
                            </div>
                        </li>
                    ))}
                </ul>
            </section>
        </div>
    );
}
