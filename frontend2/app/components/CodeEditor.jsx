"use client";

import Editor from "@monaco-editor/react";
import { useState } from "react";

const CodeEditor = ({ code, setCode }) => {
  const [theme, setTheme] = useState("vs-dark");

  const handleEditorChange = (value) => {
    setCode(value);
  };

  const editorOptions = {
    fontSize: 16,
    fontFamily: "JetBrains Mono, monospace",
    fontLigatures: true,
    minimap: {
      enabled: true,
      maxColumn: 120,
      scale: 0.75,
    },
    scrollBeyondLastLine: false,
    smoothScrolling: true,
    cursorBlinking: "phase",
    cursorSmoothCaretAnimation: true,
    formatOnPaste: true,
    formatOnType: true,
    renderWhitespace: "selection",
    lineNumbers: "on",
    lineHeight: 1.8,
    padding: { top: 24, bottom: 24 },
    folding: true,
    glyphMargin: false,
    bracketPairColorization: {
      enabled: true,
    },
    wordWrap: "on",
    mouseWheelZoom: true,
    lineDecorationsWidth: 8,
    lineNumbersMinChars: 3,
    readOnly: false,
  };

  return (
    <div className="w-full h-full relative group">
      <Editor
        height="100%"
        defaultLanguage="javascript"
        theme={theme}
        value={code}
        onChange={handleEditorChange}
        options={editorOptions}
        className="rounded-lg overflow-hidden transition-all duration-200"
      />
      <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
        <button
          onClick={() => setTheme(theme === "vs-dark" ? "light" : "vs-dark")}
          className="px-3 py-2 bg-gray-700/80 hover:bg-gray-600 rounded-md text-sm text-white transition-colors duration-200"
        >
          {theme === "vs-dark" ? "🌞" : "🌚"}
        </button>
      </div>
    </div>
  );
};

export default CodeEditor;
