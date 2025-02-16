import React, { useState } from "react";
import CodeEditor from "./components/CodeEditor";
import StatusBar from "./components/StatusBar";
import VersionControl from "./components/VersionControl";

function App() {
  const [code, setCode] = useState("");

  return (
    <div className="app-container">
      <h1>Version Control System</h1>
      <CodeEditor code={code} setCode={setCode} />
      <VersionControl code={code} /> {/* Pass code prop here */}
      <StatusBar />
    </div>
  );
}

export default App;
