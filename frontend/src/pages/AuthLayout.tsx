import type { ReactNode } from "react";
export default function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  return (<div className="flex min-h-screen items-center justify-center p-4"><div className="w-full max-w-sm rounded-2xl border border-slate-200 bg-white p-8 shadow-sm dark:border-slate-800 dark:bg-slate-900">
    <div className="mb-6 text-xl font-semibold text-brand">TaskFlow</div><h1 className="mb-4 text-lg font-semibold">{title}</h1>{children}</div></div>);
}
