"use client";

import { useState } from "react";
import CodeEditor from "./components/CodeEditor";
import StatusBar from "./components/StatusBar";
import VersionControl from "./components/VersionControl";

export default function Home() {
  const [code, setCode] = useState("");

  return (
    <div className="app-container">
      <h1>Version Control System</h1>
      <CodeEditor code={code} setCode={setCode} />
      <VersionControl code={code} setCode={setCode} />
      <StatusBar />
    </div>
  );
}
