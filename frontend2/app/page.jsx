"use client";

import { useState } from "react";
import CodeEditor from "./components/CodeEditor";
import VersionControl from "./components/VersionControl";

export default function Home() {
  const [code, setCode] = useState("");

  return (
    <main className="min-h-screen bg-gradient-to-b from-gray-900 to-gray-800">
      <div className="container mx-auto p-4">
        <h1 className="text-3xl font-bold text-transparent bg-clip-text bg-white mb-4 text-center">
          Welcome to BitBranch
        </h1>
        <div className="flex gap-4">
          {/* Larger Code Editor - Takes 75% of the width */}
          <div className="w-4/5 h-[92vh] bg-gray-800 rounded-xl p-4 shadow-xl">
            <div className="flex items-center justify-between mb-2">
              <h2 className="text-xl font-bold text-white">Code Editor</h2>
            </div>
            <div className="h-[calc(100%-2.5rem)] rounded-lg overflow-hidden border border-gray-700">
              <CodeEditor code={code} setCode={setCode} />
            </div>
          </div>

          {/* Smaller Version Control - Takes 25% of the width */}
          <div className="w-1/3 h-[92vh] bg-gray-800 rounded-xl p-4 shadow-xl overflow-y-auto">
            <VersionControl code={code} setCode={setCode} />
          </div>
        </div>
      </div>
    </main>
  );
}
