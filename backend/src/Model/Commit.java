package Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Commit {
    private String repoName;
    private String message;
    private LocalDateTime timestamp;

    public Commit(String repoName, String message) {
        this.repoName = repoName;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public boolean save() {
        File commitFile = new File("repositories/" + repoName + "/commit_log.txt");
        try (FileWriter writer = new FileWriter(commitFile, true)) {
            writer.write(timestamp + " - " + message + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
