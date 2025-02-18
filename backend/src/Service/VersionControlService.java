package Service;

import Model.Repository;
import Model.Commit;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.io.*;

public class VersionControlService {
    private final ExecutorService executorService;
    private final Map<String, ReentrantLock> repoLocks;

    public VersionControlService() {
        this.executorService = Executors.newFixedThreadPool(10);
        this.repoLocks = new ConcurrentHashMap<>();
    }

    private ReentrantLock getRepoLock(String repoName) {
        return repoLocks.computeIfAbsent(repoName, k -> new ReentrantLock());
    }

    public boolean createRepository(String repoName) {
        Repository repository = new Repository(repoName);
        return repository.create();
    }
    


    public CompletableFuture<Boolean> commitChangesAsync(String repoName, String branchName, String commitMessage, String content) {
        return CompletableFuture.supplyAsync(() -> {
            ReentrantLock lock = getRepoLock(repoName);
            lock.lock();
            try {
                System.out.println("Attempting commit for repo: " + repoName + ", branch: " + branchName);
                
                // Verify repository exists
                Path repoPath = Paths.get("repositories", repoName);
                if (!Files.exists(repoPath)) {
                    System.err.println("Repository does not exist: " + repoPath);
                    return false;
                }
                
                // Verify branch exists
                Path branchPath = Paths.get("repositories", repoName, "branches", branchName);
                if (!Files.exists(branchPath)) {
                    System.err.println("Branch does not exist: " + branchPath);
                    return false;
                }
                
                Commit commit = new Commit(repoName, branchName, commitMessage, content);
                boolean result = commit.save();
                
                if (!result) {
                    System.err.println("Failed to save commit");
                }
                
                return result;
            } catch (Exception e) {
                System.err.println("Error during commit: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                lock.unlock();
            }
        }, executorService);
    }

    public CompletableFuture<Map<String, Object>> createBranchAsync(String repoName, String branchName) {
        return CompletableFuture.supplyAsync(() -> {
            ReentrantLock lock = getRepoLock(repoName);
            lock.lock();
            try {
                Repository repository = new Repository(repoName);
                boolean created = repository.createBranch(branchName);
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", created);
                if (created) {
                    result.put("branches", repository.getBranches());
                    String content = pullChanges(repoName, branchName);
                    result.put("content", content != null ? content : "");
                }
                return result;
            } finally {
                lock.unlock();
            }
        }, executorService);
    }

public String getCommitContent(String repoName, String hash, String branchName) {
    try {
        Path versionPath = Paths.get("repositories", repoName, "branches", branchName, "versions", hash + ".txt");
        if (Files.exists(versionPath)) {
            return Files.readString(versionPath);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}

public List<Map<String, String>> getCommitHistory(String repoName, String branchName) {
    List<Map<String, String>> history = new ArrayList<>();
    try {
        Path logPath = Paths.get("repositories", repoName, "branches", branchName, "commit_log.txt");
        if (Files.exists(logPath)) {
            List<String> lines = Files.readAllLines(logPath);
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    Map<String, String> commit = new HashMap<>();
                    commit.put("timestamp", parts[0]);
                    commit.put("hash", parts[1]);
                    commit.put("message", parts[2]);
                    commit.put("branch", parts.length >= 4 ? parts[3] : branchName);
                    history.add(commit);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return history;
}

    public String pullChanges(String repoName, String branchName) {
        Path filePath = Paths.get("repositories", repoName, "branches", branchName, "current.txt");
        try {
            if (!Files.exists(filePath)) {
                // Create the branch if it doesn't exist, regardless of branch name
                Repository repository = new Repository(repoName);
                repository.createBranch(branchName);
                return "";
            }
            return Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

  

    public String revertToCommit(String repoName, String hash, String branchName) {
    String content = Commit.getVersion(repoName, hash, branchName);
    if (content != null) {
        try {
            // Update current content in the specific branch
            Path currentPath = Paths.get("repositories", repoName, "branches", branchName, "current.txt");
            Files.write(currentPath, content.getBytes());
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return null;
}



    public List<String> getBranches(String repoName) {
        Repository repository = new Repository(repoName);
        return repository.getBranches();
    }

    public List<String> getAllRepositories() {
    File reposDir = new File("repositories");
    if (!reposDir.exists() || !reposDir.isDirectory()) {
        return new ArrayList<>();
    }
    
    return Arrays.stream(reposDir.list())
        .filter(name -> new File("repositories/" + name).isDirectory())
        .collect(Collectors.toList());
    }
    public void shutdown() {
        executorService.shutdown();
    }
}