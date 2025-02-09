import { useState } from "react";

export default function App() {
  const [commitMessage, setCommitMessage] = useState("");
  const [history, setHistory] = useState([]);
  const SERVER_URL = "http://localhost:8080";

  const handleCommit = async () => {
    const response = await fetch(SERVER_URL, {
      method: "POST",
      body: "COMMIT " + commitMessage,
    });
    const text = await response.text();
    alert(text);
    setCommitMessage("");
  };

  const fetchHistory = async () => {
    const response = await fetch(SERVER_URL, {
      method: "POST",
      body: "HISTORY",
    });
    const text = await response.text();
    setHistory(text.split("\n"));
  };

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold">Version Control System</h1>
      <input
        type="text"
        className="border p-2 m-2"
        value={commitMessage}
        onChange={(e) => setCommitMessage(e.target.value)}
        placeholder="Enter commit message"
      />
      <button className="bg-blue-500 text-white p-2" onClick={handleCommit}>
        Commit
      </button>
      <button
        className="bg-green-500 text-white p-2 ml-2"
        onClick={fetchHistory}
      >
        Show History
      </button>
      <ul className="mt-4">
        {history.map((item, index) => (
          <li key={index} className="border-b p-2">
            {item}
          </li>
        ))}
      </ul>
    </div>
  );
}
