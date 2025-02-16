import "./globals.css";

export const metadata = {
  title: "Version Control System",
  description: "A simple version control system",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className="bg-gray-900">{children}</body>
    </html>
  );
}
