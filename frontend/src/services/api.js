const BASE_URL = "http://localhost:5000";

export const createRepository = async (repoName) => {
  try {
    const response = await fetch("http://localhost:5000/create", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "CREATE",
        repoName: repoName,
      }),
    });

    if (!response.ok) {
      throw new Error("Failed to create repository");
    }

    return await response.json();
  } catch (error) {
    console.error("Error creating repository:", error);
    throw error;
  }
};

export const commitChanges = async (message, content, selectedRepo) => {
  try {
    console.log(`Committing to repository: ${selectedRepo}`); // Debug log
    const response = await fetch(`${BASE_URL}/commit`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "COMMIT",
        message,
        content,
        repoName: selectedRepo, // Make sure repoName is being sent
      }),
    });
    return await response.json();
  } catch (error) {
    console.error("Error committing changes:", error);
    return { success: false, message: error.message };
  }
};

export const pullChanges = async (repoName) => {
  try {
    const response = await fetch(`${BASE_URL}/pull`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "PULL",
        repoName,
      }),
    });
    return await response.json();
  } catch (error) {
    console.error("Error pulling changes:", error);
    return { success: false, content: "", message: error.message };
  }
};

export const getCommitHistory = async () => {
  try {
    const response = await fetch("http://localhost:5000/history", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "HISTORY",
      }),
    });

    return await response.json();
  } catch (error) {
    console.error("Error getting history:", error);
    throw error;
  }
};

export const revertToCommit = async (hash) => {
  try {
    const response = await fetch("http://localhost:5000/revert", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "REVERT",
        hash: hash,
      }),
    });

    return await response.json();
  } catch (error) {
    console.error("Error reverting changes:", error);
    throw error;
  }
};

export const getRepositories = async () => {
  try {
    const response = await fetch(`${BASE_URL}/repos`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "GET_REPOS",
      }),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const contentType = response.headers.get("content-type");
    if (!contentType || !contentType.includes("application/json")) {
      throw new TypeError("Received non-JSON response");
    }

    return await response.json();
  } catch (error) {
    console.error("Error fetching repositories:", error);
    return { success: false, repositories: [], message: error.message };
  }
};
