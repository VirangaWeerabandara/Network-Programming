"use client";

import React from "react";

const StatusBar = ({ status }) => {
  return (
    <div className="status-bar">
      <p>{status}</p>
    </div>
  );
};

export default StatusBar;
