import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import LoginPage from "./LoginPage";
vi.mock("../auth/AuthContext", () => ({ useAuth: () => ({ login: vi.fn() }) }));
it("shows validation errors on empty submit", async () => {
  render(<MemoryRouter><LoginPage /></MemoryRouter>);
  await userEvent.click(screen.getByRole("button", { name: /sign in/i }));
  expect(await screen.findByText("Enter a valid email")).toBeInTheDocument();
});
