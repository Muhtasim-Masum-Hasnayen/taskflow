import { loginSchema, registerSchema } from "./schemas";
describe("auth schemas", () => {
  it("rejects invalid email", () => expect(loginSchema.safeParse({ email: "x", password: "p" }).success).toBe(false));
  it("accepts valid login", () => expect(loginSchema.safeParse({ email: "a@b.com", password: "p" }).success).toBe(true));
  it("enforces password length on register", () => expect(registerSchema.safeParse({ fullName: "A", email: "a@b.com", password: "short" }).success).toBe(false));
});
