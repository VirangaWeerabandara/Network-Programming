import React, { useState, useEffect } from "react";
import {
  createRepository,
  commitChanges,
  pullChanges,
  getRepositories,
} from "../services/api";
import StatusBar from "./StatusBar";

export default function VersionControl({ code, setCode }) {
  const [repoName, setRepoName] = useState("");
  const [commitMessage, setCommitMessage] = useState("");
  const [status, setStatus] = useState("");
  const [repositories, setRepositories] = useState([]);
  const [selectedRepo, setSelectedRepo] = useState("");

  useEffect(() => {
    loadRepositories();
  }, []);

  const loadRepositories = async () => {
    try {
      const response = await getRepositories();
      if (response.success) {
        setRepositories(response.repositories);
      }
    } catch (error) {
      setStatus("Failed to load repositories");
    }
  };

  const handlePull = async () => {
    if (!selectedRepo) {
      setStatus("Please select a repository first");
      return;
    }
    try {
      const response = await pullChanges(selectedRepo);
      if (response.success) {
        setCode(response.content || "");
        setStatus("Successfully pulled latest changes");
      } else {
        setStatus("Failed to pull changes");
      }
    } catch (error) {
      setStatus(`Failed to pull changes: ${error.message}`);
    }
  };
  const handleCreateRepo = async () => {
    try {
      const response = await createRepository(repoName);
      if (response.success) {
        setStatus(`Repository ${repoName} created successfully`);
        await loadRepositories();
        setSelectedRepo(repoName);
        // Clear the code editor when creating a new repo
        setCode("");
        // Clear the repo name input
        setRepoName("");
      }
    } catch (error) {
      setStatus(`Failed to create repository: ${error.message}`);
    }
  };

  const handleRepoChange = async (e) => {
    const repo = e.target.value;
    setSelectedRepo(repo);
    // Clear code editor when selecting a new repo
    setCode("");
    setStatus(repo ? `Selected repository: ${repo}` : "");
  };

  const handleCommit = async () => {
    if (!selectedRepo) {
      setStatus("Please select a repository first");
      return;
    }
    if (!commitMessage) {
      setStatus("Please enter a commit message");
      return;
    }
    try {
      console.log(`Committing to: ${selectedRepo}`); // Debug log
      const response = await commitChanges(commitMessage, code, selectedRepo);
      if (response.success) {
        setStatus(`Changes committed successfully to ${selectedRepo}`);
        setCommitMessage(""); // Clear commit message after successful commit
      } else {
        setStatus(`Commit failed: ${response.message}`);
      }
    } catch (error) {
      setStatus(`Commit failed: ${error.message}`);
    }
  };

  return (
    <div className="version-control">
      <div className="repo-selection">
        <select value={selectedRepo} onChange={handleRepoChange}>
          <option value="">Select Repository</option>
          {repositories.map((repo) => (
            <option key={repo} value={repo}>
              {repo}
            </option>
          ))}
        </select>
        <button onClick={handlePull} disabled={!selectedRepo}>
          Pull Changes
        </button>
      </div>
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
        <button onClick={handleCommit} disabled={!selectedRepo}>
          Commit
        </button>
      </div>
      <StatusBar status={status} />
    </div>
  );
}
