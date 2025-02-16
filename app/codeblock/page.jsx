'use client';

import { useState } from 'react';
import MonacoEditor from '@monaco-editor/react';

const CodeEditor = () => {
  const [code, setCode] = useState('// Start coding...');
  const [language, setLanguage] = useState('javascript'); 

  const handleEditorChange = (value) => {
    setCode(value);
  };

  const handleLanguageChange = (e) => {
    setLanguage(e.target.value); 
  };

  return (
    <div className="max-w-4xl mx-auto w-full mt-10">
      <div className="mb-4 flex flex-col sm:flex-row sm:items-center">
        <label htmlFor="language" className="text-white mb-2 sm:mb-0 sm:mr-4">
          Select Language:
        </label>
        <select
          id="language"
          value={language}
          onChange={handleLanguageChange}
          className="p-2 bg-gray-700 text-white rounded"
        >
          <option value="javascript">JavaScript</option>
          <option value="python">Python</option>
          <option value="java">Java</option>
          <option value="cpp">C++</option>
          <option value="html">HTML</option>
          <option value="css">CSS</option>
          <option value="typescript">TypeScript</option>
          
        </select>
      </div>

      
      <div className="border-2 border-gray-600 rounded-lg p-4 bg-gray-800">
        <MonacoEditor
          height="60vh" 
          language={language} 
          value={code}
          onChange={handleEditorChange}
          theme="vs-dark"
          options={{
            selectOnLineNumbers: true,
            minimap: { enabled: false },
            fontSize: 16,
            automaticLayout: true,
          }}
        />
      </div>
    </div>
  );
};

export default CodeEditor;
