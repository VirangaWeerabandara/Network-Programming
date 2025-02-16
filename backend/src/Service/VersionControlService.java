package Service;

import Model.Repository;
import Model.Commit;
import java.nio.file.*;
import java.util.*;
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
        try {
            return Files.readString(Paths.get("repositories/" + repoName + "/current.txt"));
        } catch (IOException e) {
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
}