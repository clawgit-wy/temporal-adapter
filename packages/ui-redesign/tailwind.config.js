/** @type {import('tailwindcss').Config} */

export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    container: {
      center: true,
    },
    extend: {
      colors: {
        base: {
          900: "#0a0e1a",
          800: "#0f172a",
          700: "#1e293b",
          600: "#334155",
        },
        accent: {
          DEFAULT: "#6366f1",
          light: "#818cf8",
          dark: "#4f46e5",
        },
        status: {
          running: "#fbbf24",
          completed: "#22c55e",
          failed: "#ef4444",
          timeout: "#f97316",
          canceled: "#64748b",
          terminated: "#dc2626",
        },
      },
    },
  },
  plugins: [],
};
