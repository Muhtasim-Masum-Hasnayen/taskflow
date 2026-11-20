import { useAuth } from "../auth/AuthContext";
export default function DashboardPage() {
  const { user, logout } = useAuth();
  return (<div className="flex min-h-screen"><aside className="w-56 bg-sidebar p-5 text-slate-200"><div className="text-lg font-semibold text-white">TaskFlow</div></aside>
    <main className="flex-1 p-8"><div className="flex items-center justify-between"><h1 className="text-2xl font-semibold">Welcome, {user?.fullName}</h1>
      <button onClick={logout} className="rounded-lg border px-3 py-1.5 text-sm">Sign out</button></div>
      <p className="mt-4 text-sm text-slate-500">Projects, tasks and the Kanban board arrive in the next phases.</p></main></div>);
}
