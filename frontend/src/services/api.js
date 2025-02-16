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

export const commitChanges = async (message, content) => {
  try {
    const response = await fetch("http://localhost:5000/commit", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "COMMIT",
        message: message,
        content: content,
      }),
    });

    return await response.json();
  } catch (error) {
    console.error("Error committing changes:", error);
    throw error;
  }
};

export const pullChanges = async () => {
  try {
    const response = await fetch("http://localhost:5000/pull", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        type: "PULL",
      }),
    });

    return await response.json();
  } catch (error) {
    console.error("Error pulling changes:", error);
    throw error;
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
