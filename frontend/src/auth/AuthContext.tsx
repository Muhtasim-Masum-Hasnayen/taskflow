import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { api, setAccessToken, type AuthResponse, type User } from "../lib/api";
interface Ctx { user: User | null; loading: boolean; login(e: string, p: string): Promise<void>; logout(): Promise<void> }
const AuthCtx = createContext<Ctx | null>(null);
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const apply = (r: AuthResponse) => { setAccessToken(r.accessToken); setUser(r.user); };
  useEffect(() => { // restore session from the httpOnly refresh cookie
    api.post<AuthResponse>("/auth/refresh").then((r) => apply(r.data)).catch(() => {}).finally(() => setLoading(false));
  }, []);
  const login = async (email: string, password: string) => apply((await api.post<AuthResponse>("/auth/login", { email, password })).data);
  const logout = async () => { await api.post("/auth/logout").catch(() => {}); setAccessToken(null); setUser(null); };
  return <AuthCtx.Provider value={{ user, loading, login, logout }}>{children}</AuthCtx.Provider>;
}
export const useAuth = () => { const c = useContext(AuthCtx); if (!c) throw new Error("useAuth outside AuthProvider"); return c; };
