"use client";

import { useEffect, useState } from "react";

const StatusBar = ({ status }) => {
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  if (!isClient) {
    return null;
  }

  return (
    <div className="mt-6 p-4 bg-gray-700 rounded-lg">
      <p className="text-white text-sm font-['Inter']">{status || "Ready"}</p>
    </div>
  );
};

export default StatusBar;
