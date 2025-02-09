package VersioningSystem;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommitHandler {
    private static final String REPO_DIR = "repo";

    public static void initializeRepo() {
        File repo = new File(REPO_DIR);
        if (!repo.exists()) repo.mkdir();
    }

    public static String saveCommit(String message) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = REPO_DIR + "/commit_" + timestamp + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(message);
        }
        return fileName;
    }

    public static String getCommitHistory() {
        File dir = new File(REPO_DIR);
        StringBuilder history = new StringBuilder();
        for (File file : dir.listFiles()) {
            history.append(file.getName()).append("\n");
        }
        return history.toString();
    }
}

