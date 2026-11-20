import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { registerSchema, type RegisterForm } from "../lib/schemas";
import { api, errorMessage } from "../lib/api";
import AuthLayout from "./AuthLayout";
export default function RegisterPage() {
  const nav = useNavigate(); const [err, setErr] = useState("");
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<RegisterForm>({ resolver: zodResolver(registerSchema) });
  const onSubmit = handleSubmit(async (v) => { try { setErr(""); await api.post("/auth/register", v); nav("/login"); } catch (e) { setErr(errorMessage(e)); } });
  const field = (id: "fullName" | "email" | "password", label: string, type = "text") => (<div><label htmlFor={id} className="mb-1 block text-sm">{label}</label>
    <input id={id} type={type} className="input" {...register(id)} />{errors[id] && <p role="alert" className="mt-1 text-xs text-red-600">{errors[id]?.message}</p>}</div>);
  return (<AuthLayout title="Create your account"><form onSubmit={onSubmit} className="space-y-4" noValidate>
    {field("fullName", "Full name")}{field("email", "Email")}{field("password", "Password", "password")}
    {err && <p role="alert" className="text-sm text-red-600">{err}</p>}
    <button className="btn" disabled={isSubmitting}>Create account</button>
    <p className="text-center text-sm text-slate-500">Have an account? <Link className="text-brand" to="/login">Sign in</Link></p></form></AuthLayout>);
}
