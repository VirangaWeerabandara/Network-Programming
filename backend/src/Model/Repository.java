package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Repository {
    private String name;
    private String path;

    public Repository(String name) {
        this.name = name;
        this.path = "repositories/" + name;
    }

    public boolean create() {
        try {
            // Create main repository directory
            Files.createDirectories(Paths.get(path));
            // Create branches directory
            Files.createDirectories(Paths.get(path, "branches"));
            // Create main branch (master) - this should always be created first
            createBranch("master");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createBranch(String branchName) {
    try {
        String branchPath = path + "/branches/" + branchName;
        Files.createDirectories(Paths.get(branchPath));
        Files.createDirectories(Paths.get(branchPath, "versions"));

        // Get the current branch content (from master if no other branch exists)
        String sourceBranchPath = path + "/branches/master";
        if (Files.exists(Paths.get(sourceBranchPath))) {
            // Copy current.txt content
            String currentContent = Files.exists(Paths.get(sourceBranchPath, "current.txt")) ?
                Files.readString(Paths.get(sourceBranchPath, "current.txt")) : "";
            Files.write(Paths.get(branchPath, "current.txt"), currentContent.getBytes());

            // Copy commit_log.txt content
            String logContent = Files.exists(Paths.get(sourceBranchPath, "commit_log.txt")) ?
                Files.readString(Paths.get(sourceBranchPath, "commit_log.txt")) : "";
            Files.write(Paths.get(branchPath, "commit_log.txt"), logContent.getBytes());

            // Copy versions directory
            Path versionsSource = Paths.get(sourceBranchPath, "versions");
            Path versionsTarget = Paths.get(branchPath, "versions");
            if (Files.exists(versionsSource)) {
                Files.walk(versionsSource)
                    .filter(Files::isRegularFile)
                    .forEach(source -> {
                        try {
                            Path target = versionsTarget.resolve(versionsSource.relativize(source));
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            }
        } else {
            // Initialize empty files for new branch
            Files.write(Paths.get(branchPath, "current.txt"), "".getBytes());
            Files.write(Paths.get(branchPath, "commit_log.txt"), "".getBytes());
        }
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
    public List<String> getBranches() {
        try {
            Path branchesPath = Paths.get(path, "branches");
            return Files.list(branchesPath)
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}