import { useState } from 'react';
import MonacoEditor from '@monaco-editor/react';

const CodeEditor = () => {
  const [code, setCode] = useState('// Start coding...');

  const handleEditorChange = (value) => {
    setCode(value);
  };

  return (
    <div style={{ height: '80vh' }}>
      <MonacoEditor
        height="100%"
        language="javascript" 
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
  );
};

export default CodeEditor;
