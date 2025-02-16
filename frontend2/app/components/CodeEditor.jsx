"use client";

const CodeEditor = ({ code, setCode }) => {
  const handleChange = (event) => {
    setCode(event.target.value);
  };

  return (
    <div className="h-full">
      <textarea
        value={code}
        onChange={handleChange}
        placeholder="Write your code here..."
        className="w-full h-full p-4 rounded-lg bg-gray-700 text-white 
          resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 
          font-mono text-sm leading-relaxed"
      />
    </div>
  );
};

export default CodeEditor;
