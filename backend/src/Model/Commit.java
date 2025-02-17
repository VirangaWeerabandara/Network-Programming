package Model;

import java.io.*;
import java.time.LocalDateTime;
import java.nio.file.*;

public class Commit {
    private String repoName;
    private String branchName;
    private String message;
    private String content;
    private String hash;
    private LocalDateTime timestamp;

    public Commit(String repoName, String branchName, String message, String content) {
        this.repoName = repoName;
        this.branchName = branchName;
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
            String branchPath = "repositories/" + repoName + "/branches/" + branchName;
            String versionPath = branchPath + "/versions";
            
            // Save version
            String contentPath = versionPath + "/" + hash + ".txt";
            Files.write(Paths.get(contentPath), content.getBytes());

            // Update log
            String logPath = branchPath + "/commit_log.txt";
            String logEntry = String.format("%s|%s|%s|%s|%s\n", 
                timestamp, hash, message, branchName, contentPath);
            Files.write(Paths.get(logPath), logEntry.getBytes(), 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            // Update current
            Files.write(Paths.get(branchPath + "/current.txt"), content.getBytes());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getVersion(String repoName, String hash, String branchName) {
        try {
            String versionPath = "repositories/" + repoName + "/branches/" + branchName + "/versions/" + hash + ".txt";
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