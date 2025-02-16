import React, { useState } from "react";

const CodeEditor = ({ code, setCode }) => {
  const handleChange = (event) => {
    setCode(event.target.value);
  };

  return (
    <div className="code-editor">
      <textarea
        value={code}
        onChange={handleChange}
        placeholder="Write your code here..."
        rows="20"
        cols="80"
      />
    </div>
  );
};

export default CodeEditor;
