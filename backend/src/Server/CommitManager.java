package Server;

import Model.Commit;

public class CommitManager {
    public static boolean commit(String repoName, String commitMessage) {
        Commit commit = new Commit(repoName, commitMessage);
        return commit.save();
    }
}
