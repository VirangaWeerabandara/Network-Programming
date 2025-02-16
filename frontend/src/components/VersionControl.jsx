import React, { useState } from "react";
import { createRepository, commitChanges, pullChanges } from "../services/api";
import StatusBar from "./StatusBar";

export default function VersionControl({ code }) {
  const [repoName, setRepoName] = useState("");
  const [commitMessage, setCommitMessage] = useState("");
  const [status, setStatus] = useState("");

  const handleCreateRepo = async () => {
    try {
      const response = await createRepository(repoName);
      setStatus(`Repository ${repoName} created successfully`);
    } catch (error) {
      setStatus(`Failed to create repository: ${error.message}`);
    }
  };

  const handleCommit = async () => {
    try {
      const response = await commitChanges(commitMessage, code); // Pass code here
      setStatus(
        response.success ? "Changes committed successfully" : "Commit failed"
      );
    } catch (error) {
      setStatus(`Commit failed: ${error.message}`);
    }
  };

  const handlePull = async () => {
    try {
      const response = await pullChanges();
      setStatus("Changes pulled successfully");
    } catch (error) {
      setStatus(`Pull failed: ${error.message}`);
    }
  };

  return (
    <div className="version-control">
      <div className="repo-creation">
        <input
          type="text"
          value={repoName}
          onChange={(e) => setRepoName(e.target.value)}
          placeholder="Repository name"
        />
        <button onClick={handleCreateRepo}>Create Repository</button>
      </div>
      <div className="commit-section">
        <input
          type="text"
          value={commitMessage}
          onChange={(e) => setCommitMessage(e.target.value)}
          placeholder="Commit message"
        />
        <button onClick={handleCommit}>Commit</button>
      </div>
      <button onClick={handlePull}>Pull</button>
      <StatusBar status={status} />
    </div>
  );
}
