package Model;

import java.io.File;

public class Repository {
    private String name;
    private String path;

    public Repository(String name) {
        this.name = name;
        this.path = "repositories/" + name;
    }

    public boolean create() {
        File repo = new File(path);
        return repo.mkdirs();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
