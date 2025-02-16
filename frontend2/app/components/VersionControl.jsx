"use client";

import React, { useState, useEffect } from "react";
import {
  createRepository,
  commitChanges,
  pullChanges,
  getRepositories,
  getCommitHistory,
  revertToCommit,
} from "../services/api";

export default function VersionControl({ code, setCode }) {
  const [repoName, setRepoName] = useState("");
  const [commitMessage, setCommitMessage] = useState("");
  const [status, setStatus] = useState("");
  const [repositories, setRepositories] = useState([]);
  const [selectedRepo, setSelectedRepo] = useState("");
  const [commitHistory, setCommitHistory] = useState([]);

  useEffect(() => {
    loadRepositories();
  }, []);

  useEffect(() => {
    if (selectedRepo) {
      loadCommitHistory();
    }
  }, [selectedRepo]);

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
  const loadCommitHistory = async () => {
    try {
      const response = await getCommitHistory(selectedRepo);
      if (response.success) {
        setCommitHistory(response.history);
      }
    } catch (error) {
      setStatus("Failed to load commit history");
    }
  };

  const handleRevertCommit = async (hash) => {
    try {
      const response = await revertToCommit(hash, selectedRepo);
      if (response.success) {
        setCode(response.content);
        setStatus(`Reverted to commit: ${hash}`);
      } else {
        setStatus("Failed to revert to commit");
      }
    } catch (error) {
      setStatus(`Failed to revert: ${error.message}`);
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
    <div className="space-y-6">
      <div>
        <p className="text-white text-2xl font-bold font-['Inter'] mb-2">
          New Repository
        </p>
        <div className="flex gap-4">
          <input
            type="text"
            value={repoName}
            onChange={(e) => setRepoName(e.target.value)}
            placeholder="Enter repository name"
            className="flex-1 p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button onClick={handleCreateRepo} className="p-[3px] relative">
            <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg" />
            <div className="px-8 py-2 bg-gray-900 rounded-[6px] relative group transition duration-200 text-white hover:bg-transparent">
              Create Repository
            </div>
          </button>
        </div>
      </div>
      <div>
        <p className="text-white text-2xl font-bold font-['Inter'] mb-2">
          Repository
        </p>
        <div className="flex gap-4">
          <select
            value={selectedRepo}
            onChange={handleRepoChange}
            className="flex-1 p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Select Repository</option>
            {repositories.map((repo) => (
              <option key={repo} value={repo}>
                {repo}
              </option>
            ))}
          </select>
          <button
            onClick={handlePull}
            disabled={!selectedRepo}
            className="p-[3px] relative disabled:opacity-50"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg" />
            <div className="px-8 py-2 bg-gray-900 rounded-[6px] relative group transition duration-200 text-white hover:bg-transparent">
              Pull Changes
            </div>
          </button>
        </div>
      </div>
      {selectedRepo && commitHistory.length > 0 && (
        <div>
          <p className="text-white text-2xl font-bold font-['Inter'] mb-2">
            Commit History
          </p>
          <div className="bg-gray-700 rounded-lg p-4 max-h-60 overflow-y-auto">
            {commitHistory.map((commit, index) => (
              <div
                key={commit.hash}
                className="flex items-center justify-between p-2 hover:bg-gray-600 rounded cursor-pointer"
                onClick={() => handleRevertCommit(commit.hash)}
              >
                <div className="flex flex-col">
                  <span className="text-white font-mono text-sm">
                    {commit.hash.substring(0, 8)}
                  </span>
                  <span className="text-gray-300 text-sm">
                    {commit.message}
                  </span>
                  <span className="text-gray-400 text-xs">
                    {new Date(commit.timestamp).toLocaleString()}
                  </span>
                </div>
                <button
                  className="p-[2px] relative"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRevertCommit(commit.hash);
                  }}
                >
                  <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg opacity-75" />
                  <div className="px-4 py-1 bg-gray-900 rounded-[4px] relative group transition duration-200 text-white hover:bg-transparent text-sm">
                    View
                  </div>
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      <div>
        <p className="text-white text-2xl font-bold font-['Inter'] mb-2">
          Commit Changes
        </p>
        <div className="flex gap-4">
          <input
            type="text"
            value={commitMessage}
            onChange={(e) => setCommitMessage(e.target.value)}
            placeholder="Enter commit message"
            className="flex-1 p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            onClick={handleCommit}
            disabled={!selectedRepo}
            className="p-[3px] relative disabled:opacity-50"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg" />
            <div className="px-8 py-2 bg-gray-900 rounded-[6px] relative group transition duration-200 text-white hover:bg-transparent">
              Commit
            </div>
          </button>
        </div>
      </div>
    </div>
  );
}
