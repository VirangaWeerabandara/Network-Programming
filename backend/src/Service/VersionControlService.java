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
    

    public boolean commitChanges(String repoName, String commitMessage, String content) {
        Commit commit = new Commit(repoName, commitMessage, content);
        return commit.save();
    }

public String pullChanges(String repoName) {
    Path filePath = Paths.get("repositories", repoName, "current.txt");
    try {
        // Check if repository exists
        if (!Files.exists(Paths.get("repositories", repoName))) {
            System.err.println("Repository not found: " + repoName);
            return null;
        }

        // Check if current.txt exists
        if (!Files.exists(filePath)) {
            // Create empty file if it doesn't exist
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, "".getBytes());
            return "";
        }

        return Files.readString(filePath);
    } catch (IOException e) {
        System.err.println("Error reading from repository: " + repoName);
        e.printStackTrace();
        return null;
    }
}

    public List<Map<String, String>> getCommitHistory(String repoName) {
        List<Map<String, String>> history = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(
                Paths.get("repositories/" + repoName + "/commit_log.txt"));
            for (String line : lines) {
                String[] parts = line.split("\\|");
                Map<String, String> commit = new HashMap<>();
                commit.put("timestamp", parts[0]);
                commit.put("hash", parts[1]);
                commit.put("message", parts[2]);
                history.add(commit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public String revertToCommit(String repoName, String hash) {
        String content = Commit.getVersion(repoName, hash);
        if (content != null) {
            try {
                Files.write(Paths.get("repositories/" + repoName + "/current.txt"), 
                    content.getBytes());
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
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