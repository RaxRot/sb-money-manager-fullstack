import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {assets} from "../assets/assets.js";
import Input from "../components/Input.jsx";

export default function SignUp() {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);

    const navigate=useNavigate();

    return <div className="h-screen w-full relative flex items-center justify-center overflow-hidden">
        {/*BG IMAGE with blur*/}
        <img src={assets.login_bg}alt="bg" className="absolute inset w-full h-full object-cover filter blur-sm"/>

        <div className="relative z-10 w-full max-w-lg px-6">
            <div className="bg-white bg-opacity-95 backdrop-blur-sm rounded-lg shadow-2xl p-8 max-h-[90vh] overflow-y-auto">
                <h3 className="text-2xl font-semibold text-black text-center mb-2">
                    Create an Account
                </h3>
                <p className="text-sm text-slate-700 text-center mb-8">
                    Start tracking your spendings by joining our community.
                </p>
                <form className="space-y-4">
                    <div className="flex justify-center mb-6">
                        {/* Profile Image*/}
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <Input
                        label={fullName}
                        onChange={(event) => setFullName(event.target.value)}
                        label="Full Name"
                        placeholder="Enter your full name"
                        type="text"/>

                        <Input
                            label={email}
                            onChange={(event) => setEmail(event.target.value)}
                            label="Email"
                            placeholder="Enter your email"
                            type="text"/>

                      <div className="col-span-2">
                          <Input
                              label={password}
                              onChange={(event) => setPassword(event.target.value)}
                              label="Password"
                              placeholder="Enter your password"
                              type="password"/>
                      </div>

                    </div>

                    {error && <p className="text-red-800 text-sm text-center bg-red-50 p-2 rounded">{error}</p>}

                    <button
                        type="submit"
                        className="w-full py-3 text-lg font-semibold text-white bg-blue-600 rounded-lg shadow-md
             transition duration-300 ease-in-out transform hover:scale-105 hover:bg-blue-700 hover:shadow-xl"
                    >
                        SIGN UP
                    </button>

                    <p className="text-sm text-slate-800 text-center mt-6">
                        Already have an account? <Link to="/login" className="text-blue-500 hover:underline">Login</Link>
                    </p>

                </form>
            </div>
        </div>

    </div>
}