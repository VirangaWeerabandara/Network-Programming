package Server;

import Model.Repository;

public class RepositoryManager {
    public static boolean createRepository(String repoName) {
        Repository repo = new Repository(repoName);
        return repo.create();
    }
}
