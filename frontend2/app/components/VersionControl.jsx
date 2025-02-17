"use client";

import React, { useState, useEffect } from "react";
import { toast, Toaster } from "react-hot-toast";
import {
  createRepository,
  commitChanges,
  pullChanges,
  getRepositories,
  getCommitHistory,
  revertToCommit,
  getBranches,
  createBranch,
  getCommitContent,
} from "../services/api";

export default function VersionControl({ code, setCode }) {
  const [repoName, setRepoName] = useState("");
  const [commitMessage, setCommitMessage] = useState("");
  const [repositories, setRepositories] = useState([]);
  const [selectedRepo, setSelectedRepo] = useState("");
  const [commitHistory, setCommitHistory] = useState([]);
  const [branches, setBranches] = useState([]);
  const [selectedBranch, setSelectedBranch] = useState("master");
  const [newBranchName, setNewBranchName] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    loadRepositories();
  }, []);

  useEffect(() => {
    if (selectedRepo && selectedBranch) {
      loadCommitHistory();
    }
  }, [selectedRepo, selectedBranch]);

  useEffect(() => {
    if (selectedRepo) {
      loadBranches();
    }
  }, [selectedRepo]);

  const loadRepositories = async () => {
    try {
      setIsLoading(true);
      const response = await getRepositories();
      if (response.success) {
        setRepositories(response.repositories);
      } else {
        toast.error("Failed to load repositories");
      }
    } catch (error) {
      toast.error("Failed to load repositories");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCommit = async () => {
    if (!selectedRepo || !commitMessage) {
      toast.error(
        !selectedRepo
          ? "Please select a repository first"
          : "Please enter a commit message"
      );
      return;
    }

    try {
      setIsLoading(true);
      const response = await commitChanges(
        commitMessage,
        code,
        selectedRepo,
        selectedBranch
      );
      toast.success(
        `Changes committed successfully to ${selectedRepo}/${selectedBranch}`
      );
      setCommitMessage("");
      if (response.history) {
        setCommitHistory(response.history);
      } else {
        await loadCommitHistory();
      }
    } catch (error) {
      toast.error(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateBranch = async () => {
    if (!newBranchName) {
      toast.error("Please enter a branch name");
      return;
    }

    try {
      setIsLoading(true);
      const response = await createBranch(selectedRepo, newBranchName);
      toast.success(`Branch ${newBranchName} created successfully`);
      setBranches(response.branches);
      setNewBranchName("");
      setSelectedBranch(newBranchName);

      if (response.content !== undefined) {
        setCode(response.content);
      }

      await loadCommitHistory();
    } catch (error) {
      toast.error(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleBranchChange = async (e) => {
    const branch = e.target.value;
    setSelectedBranch(branch);

    try {
      setIsLoading(true);
      const [historyResponse, pullResponse] = await Promise.all([
        getCommitHistory(selectedRepo, branch),
        pullChanges(selectedRepo, branch),
      ]);

      if (historyResponse.success) {
        setCommitHistory(historyResponse.history);
      }

      if (pullResponse.success) {
        setCode(pullResponse.content || "");
        toast.success(`Switched to branch: ${branch}`);
      } else {
        toast.error("Failed to load branch content");
      }
    } catch (error) {
      toast.error(`Error switching branches: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const loadBranches = async () => {
    const response = await getBranches(selectedRepo);
    if (response.success) {
      setBranches(response.branches);
      return response.branches;
    }
    return [];
  };

  const loadCommitHistory = async () => {
    try {
      const response = await getCommitHistory(selectedRepo, selectedBranch);
      if (response.success) {
        setCommitHistory(response.history);
      }
    } catch (error) {
      toast.error("Failed to load commit history");
    }
  };

  const handleRevertCommit = async (hash) => {
    try {
      setIsLoading(true);
      const response = await revertToCommit(hash, selectedRepo, selectedBranch);
      if (response.success) {
        setCode(response.content);
        toast.success(`Reverted to commit: ${hash}`);
        await loadCommitHistory();
      } else {
        toast.error("Failed to revert to commit");
      }
    } catch (error) {
      toast.error(`Failed to revert: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleViewCommit = async (hash, e) => {
    e.stopPropagation();
    try {
      setIsLoading(true);
      const response = await getCommitContent(
        selectedRepo,
        hash,
        selectedBranch
      );
      if (response.success) {
        setCode(response.content);
        toast.success(`Viewing commit: ${hash}`);
      } else {
        toast.error("Failed to load commit content");
      }
    } catch (error) {
      toast.error(`Failed to load commit content: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePull = async () => {
    if (!selectedRepo) {
      toast.error("Please select a repository first");
      return;
    }

    try {
      setIsLoading(true);
      const response = await pullChanges(selectedRepo, selectedBranch);
      if (response.success) {
        setCode(response.content || "");
        toast.success(
          `Successfully pulled latest changes from ${selectedBranch}`
        );
      } else {
        toast.error("Failed to pull changes");
      }
    } catch (error) {
      toast.error(`Failed to pull changes: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateRepo = async () => {
    if (!repoName) {
      toast.error("Please enter a repository name");
      return;
    }

    try {
      setIsLoading(true);
      const response = await createRepository(repoName);
      if (response.success) {
        toast.success(`Repository ${repoName} created successfully`);
        await loadRepositories();
        setSelectedRepo(repoName);
        setCode("");
        setRepoName("");
      } else {
        toast.error(response.message || "Failed to create repository");
      }
    } catch (error) {
      toast.error(`Failed to create repository: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRepoChange = async (e) => {
    const repo = e.target.value;
    setSelectedRepo(repo);
    setCode("");
    if (repo) {
      toast.success(`Selected repository: ${repo}`);
    }
  };

  return (
    <div className="space-y-6 relative">
      <Toaster position="top-right" />

      {isLoading && (
        <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-purple-500"></div>
        </div>
      )}

      {/* Repository Creation Section */}
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
          <button
            onClick={handleCreateRepo}
            disabled={isLoading}
            className="p-[3px] relative disabled:opacity-50"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg" />
            <div className="px-8 py-2 bg-gray-900 rounded-[6px] relative group transition duration-200 text-white hover:bg-transparent">
              Create Repository
            </div>
          </button>
        </div>
      </div>

      {/* Repository Selection Section */}
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
            disabled={!selectedRepo || isLoading}
            className="p-[3px] relative disabled:opacity-50"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg" />
            <div className="px-8 py-2 bg-gray-900 rounded-[6px] relative group transition duration-200 text-white hover:bg-transparent">
              Pull Changes
            </div>
          </button>
        </div>
      </div>

      {/* Branches Section */}
      {selectedRepo && (
        <div>
          <p className="text-white text-2xl font-bold font-['Inter'] mb-2">
            Branches
          </p>
          <div className="flex gap-4 mb-4">
            <select
              value={selectedBranch}
              onChange={handleBranchChange}
              className="flex-1 p-3 rounded-lg bg-gray-700 text-white"
            >
              {branches.map((branch) => (
                <option key={branch} value={branch}>
                  {branch}
                </option>
              ))}
            </select>
          </div>
          <div className="flex gap-4">
            <input
              type="text"
              value={newBranchName}
              onChange={(e) => setNewBranchName(e.target.value)}
              placeholder="New branch name"
              className="flex-1 p-3 rounded-lg bg-gray-700 text-white"
            />
            <button
              onClick={handleCreateBranch}
              disabled={isLoading}
              className="p-[3px] relative disabled:opacity-50"
            >
              <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg" />
              <div className="px-8 py-2 bg-gray-900 rounded-[6px] relative group transition duration-200 text-white hover:bg-transparent">
                Create Branch
              </div>
            </button>
          </div>
        </div>
      )}

      {/* Commit History Section */}
      {selectedRepo && commitHistory.length > 0 && (
        <div>
          <p className="text-white text-2xl font-bold font-['Inter'] mb-2">
            Commit History for {selectedBranch}
          </p>
          <div className="bg-gray-700 rounded-lg p-4 max-h-60 overflow-y-auto">
            {commitHistory.map((commit) => (
              <div
                key={commit.hash}
                className="flex items-center justify-between p-2 hover:bg-gray-600 rounded cursor-pointer mb-2"
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
                <div className="flex gap-2">
                  <button
                    className="p-[2px] relative"
                    onClick={(e) => handleViewCommit(commit.hash, e)}
                    disabled={isLoading}
                  >
                    <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-lg opacity-75" />
                    <div className="px-4 py-1 bg-gray-900 rounded-[4px] relative group transition duration-200 text-white hover:bg-transparent text-sm">
                      View
                    </div>
                  </button>
                  <button
                    className="p-[2px] relative"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleRevertCommit(commit.hash);
                    }}
                    disabled={isLoading}
                  >
                    <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg opacity-75" />
                    <div className="px-4 py-1 bg-gray-900 rounded-[4px] relative group transition duration-200 text-white hover:bg-transparent text-sm">
                      Revert
                    </div>
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Commit Changes Section */}
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
            disabled={!selectedRepo || isLoading}
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
