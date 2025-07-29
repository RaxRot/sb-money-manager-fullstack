import './App.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home.jsx";
import Income from "./pages/Income.jsx";
import Expense from "./pages/Expense.jsx";
import Category from "./pages/Category.jsx";
import Login from "./pages/Login.jsx";
import SignUp from "./pages/SignUp.jsx";
import Filter from "./pages/Filter.jsx";

function App() {

  return (
    <>


     <BrowserRouter>
         <Routes>
             <Route path="/dashboard" element={<Home/>}/>
             <Route path="/income" element={<Income/>}/>
             <Route path="/expense" element={<Expense/>}/>
             <Route path="/category" element={<Category/>}/>
             <Route path="/filter" element={<Filter/>}/>
             <Route path="/login" element={<Login/>}/>
             <Route path="/signup" element={<SignUp/>}/>
         </Routes>
     </BrowserRouter>
    </>
  )
}

export default App
