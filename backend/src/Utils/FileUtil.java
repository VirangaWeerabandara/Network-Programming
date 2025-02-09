package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static boolean createFile(String path, String content) {
        try {
            File file = new File(path);
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
