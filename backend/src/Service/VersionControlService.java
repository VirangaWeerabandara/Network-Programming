package Service;

import Model.Repository;
import Model.Commit;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class VersionControlService {
    public boolean createRepository(String repoName) {
        Repository repository = new Repository(repoName);
        return repository.create();
    }
    


    public boolean commitChanges(String repoName, String branchName, String commitMessage, String content) {
        Commit commit = new Commit(repoName, branchName, commitMessage, content);
        return commit.save();
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
            if ("master".equals(branchName)) {
                // Create master branch if it doesn't exist
                Repository repository = new Repository(repoName);
                repository.createBranch("master");
                return "";
            }
            return null;
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

    public boolean createBranch(String repoName, String branchName) {
        Repository repository = new Repository(repoName);
        return repository.createBranch(branchName);
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
}