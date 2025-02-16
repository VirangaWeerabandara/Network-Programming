package Model;

import java.io.*;
import java.time.LocalDateTime;
import java.nio.file.*;

public class Commit {
    private String repoName;
    private String message;
    private LocalDateTime timestamp;
    private String content;
    private String hash;

    public Commit(String repoName, String message, String content) {
        this.repoName = repoName;
        this.message = message;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.hash = generateHash();
    }

    private String generateHash() {
        return timestamp.toString().replace(":", "-") + "-" + Math.abs(content.hashCode());
    }

    public boolean save() {
        try {
            // Create repository directory if it doesn't exist
            String repoPath = "repositories/" + repoName;
            Files.createDirectories(Paths.get(repoPath));

            // Save the content in versions directory
            String versionPath = repoPath + "/versions";
            Files.createDirectories(Paths.get(versionPath));
            
            // Save the content with commit hash
            String contentPath = versionPath + "/" + hash + ".txt";
            Files.write(Paths.get(contentPath), content.getBytes());

            // Update commit log
            String logPath = repoPath + "/commit_log.txt";
            String logEntry = String.format("%s|%s|%s|%s\n", 
                timestamp, hash, message, contentPath);
            Files.write(Paths.get(logPath), logEntry.getBytes(), 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            // Update current version
            Files.write(Paths.get(repoPath + "/current.txt"), content.getBytes());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getVersion(String repoName, String hash) {
        try {
            String versionPath = "repositories/" + repoName + "/versions/" + hash + ".txt";
            return Files.readString(Paths.get(versionPath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRepoName() {
        return repoName;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}