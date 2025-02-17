package Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            // Initialize empty files
            Files.write(Paths.get(branchPath, "current.txt"), "".getBytes());
            Files.write(Paths.get(branchPath, "commit_log.txt"), "".getBytes());
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