import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { loginSchema, type LoginForm } from "../lib/schemas";
import { errorMessage } from "../lib/api";
import { useAuth } from "../auth/AuthContext";
import AuthLayout from "./AuthLayout";
export default function LoginPage() {
  const { login } = useAuth(); const nav = useNavigate(); const [err, setErr] = useState("");
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginForm>({ resolver: zodResolver(loginSchema) });
  const onSubmit = handleSubmit(async (v) => { try { setErr(""); await login(v.email, v.password); nav("/"); } catch (e) { setErr(errorMessage(e)); } });
  return (<AuthLayout title="Sign in"><form onSubmit={onSubmit} className="space-y-4" noValidate>
    <div><label htmlFor="email" className="mb-1 block text-sm">Email</label><input id="email" className="input" {...register("email")} />
      {errors.email && <p role="alert" className="mt-1 text-xs text-red-600">{errors.email.message}</p>}</div>
    <div><label htmlFor="password" className="mb-1 block text-sm">Password</label><input id="password" type="password" className="input" {...register("password")} />
      {errors.password && <p role="alert" className="mt-1 text-xs text-red-600">{errors.password.message}</p>}</div>
    {err && <p role="alert" className="text-sm text-red-600">{err}</p>}
    <button className="btn" disabled={isSubmitting}>Sign in</button>
    <p className="text-center text-sm text-slate-500">No account? <Link className="text-brand" to="/register">Create one</Link></p></form></AuthLayout>);
}
