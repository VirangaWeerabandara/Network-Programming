const SERVER_IP = process.env.NEXT_PUBLIC_SERVER_IP || "localhost";
const BASE_URL = `http://${SERVER_IP}:5000`;

export const createRepository = async (repoName) => {
  try {
    const response = await fetch(`${BASE_URL}/create`, {
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

export const commitChanges = async (message, content, repoName, branchName) => {
  try {
    const response = await fetch(`${BASE_URL}/commit`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "COMMIT",
        message,
        content,
        repoName,
        branchName: branchName || "master",
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.message || `HTTP error! status: ${response.status}`
      );
    }

    const data = await response.json();
    if (!data.success) {
      throw new Error(data.message || "Failed to commit changes");
    }
    return data;
  } catch (error) {
    console.error("Commit error details:", error);
    throw new Error(`Failed to commit: ${error.message}`);
  }
};

export const createBranch = async (repoName, branchName) => {
  try {
    const response = await fetch(`${BASE_URL}/branch`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "CREATE_BRANCH",
        repoName,
        branchName,
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.message || `HTTP error! status: ${response.status}`
      );
    }

    const data = await response.json();
    if (!data.success) {
      throw new Error(data.message || "Failed to create branch");
    }
    return data;
  } catch (error) {
    console.error("Error creating branch:", error);
    throw error;
  }
};

export const pullChanges = async (repoName, branchName = "master") => {
  try {
    const response = await fetch(`${BASE_URL}/pull`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "PULL",
        repoName,
        branchName,
      }),
    });
    return await response.json();
  } catch (error) {
    console.error("Error pulling changes:", error);
    return { success: false, content: "", message: error.message };
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

export const getCommitHistory = async (repoName, branchName) => {
  try {
    const response = await fetch(`${BASE_URL}/history`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "HISTORY",
        repoName,
        branchName,
      }),
    });
    return await response.json();
  } catch (error) {
    console.error("Error fetching history:", error);
    return { success: false, history: [], message: error.message };
  }
};

export const getCommitContent = async (repoName, hash, branchName) => {
  try {
    const response = await fetch(`${BASE_URL}/commit-content`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "GET_COMMIT_CONTENT",
        repoName,
        hash,
        branchName,
      }),
    });
    return await response.json();
  } catch (error) {
    console.error("Error fetching commit content:", error);
    return { success: false, content: "", message: error.message };
  }
};

export const revertToCommit = async (hash, repoName, branchName) => {
  try {
    const response = await fetch(`${BASE_URL}/revert`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "REVERT",
        hash,
        repoName,
        branchName,
      }),
    });
    return await response.json();
  } catch (error) {
    console.error("Error reverting commit:", error);
    return { success: false, message: error.message };
  }
};

export const getBranches = async (repoName) => {
  try {
    const response = await fetch(`${BASE_URL}/branches`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "GET_BRANCHES",
        repoName,
      }),
    });
    return await response.json();
  } catch (error) {
    console.error("Error fetching branches:", error);
    return { success: false, branches: [], message: error.message };
  }
};
