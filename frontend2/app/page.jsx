"use client";

import { useState } from "react";
import CodeEditor from "./components/CodeEditor";
import StatusBar from "./components/StatusBar";
import VersionControl from "./components/VersionControl";

export default function Home() {
  const [code, setCode] = useState("");

  return (
    <div className="h-screen bg-gray-900 p-10">
      <h1 className="text-white text-[40px] font-bold font-['Inter'] text-center mb-8">
        Version Control System
      </h1>
      <div className="flex gap-8 h-[calc(100vh-160px)]">
        {/* Left Side - Version Control */}
        <div className="w-1/2 bg-gray-800 p-6 rounded-lg shadow-lg overflow-y-auto">
          <VersionControl code={code} setCode={setCode} />
          <StatusBar />
        </div>

        {/* Right Side - Code Editor */}
        <div className="w-1/2 bg-gray-800 p-6 rounded-lg shadow-lg">
          <CodeEditor code={code} setCode={setCode} />
        </div>
      </div>
    </div>
  );
}
