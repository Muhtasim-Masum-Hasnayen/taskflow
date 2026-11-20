import { BrowserRouter, Route, Routes } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthProvider } from "./auth/AuthContext";
import ProtectedRoute from "./auth/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
const qc = new QueryClient();
export default function App() {
  return (<QueryClientProvider client={qc}><BrowserRouter><AuthProvider><Routes>
    <Route path="/login" element={<LoginPage />} /><Route path="/register" element={<RegisterPage />} />
    <Route element={<ProtectedRoute />}><Route path="/" element={<DashboardPage />} /></Route>
  </Routes></AuthProvider></BrowserRouter></QueryClientProvider>);
}
