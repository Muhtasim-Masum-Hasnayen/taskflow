import axios from "axios";
export interface User { id: string; email: string; fullName: string; emailVerified: boolean }
export interface AuthResponse { accessToken: string; user: User }
let accessToken: string | null = null;
export const setAccessToken = (t: string | null) => { accessToken = t; };
export const api = axios.create({ baseURL: import.meta.env.VITE_API_URL ?? "http://localhost:8080/api/v1", withCredentials: true });
api.interceptors.request.use((c) => { if (accessToken) c.headers.Authorization = `Bearer ${accessToken}`; return c; });
export const errorMessage = (e: unknown): string =>
  axios.isAxiosError(e) ? (e.response?.data?.message ?? "Request failed") : "Something went wrong";
