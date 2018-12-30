import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by Ins on 30.12.2018.
 */
public class FileIO {
    private String basePath;

    private static volatile FileIO instance;

    private FileIO() {
        File jarFile = null;
        try {
            jarFile = new File(DUC.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            basePath = "";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static FileIO getInstance() {
        FileIO localInstance = instance;
        if (instance == null) {
            synchronized (Logger.class) {
                localInstance = instance;
                if (localInstance == null)
                    instance = localInstance = new FileIO();
            }
        }

        return instance;
    }

    public String read(String file) throws Exception {
        Path path = Paths.get(basePath + file);

        StringBuilder sb = new StringBuilder();
        Files.lines(path).forEach(o -> {
            if (o != null) {
                o = o.trim().replace("\t", "");
                if (!(o.length() == 0) && !(o.startsWith("#"))) {
                    sb.append(o);
                }
            }
        });

        return sb.toString();
    }

    public void write(String file, String data) throws Exception {
        if (!Files.exists(Paths.get(basePath + file))) {
            Files.createFile(Paths.get(basePath + file));
        }

        Files.write(Paths.get(basePath + file), data.getBytes(), StandardOpenOption.APPEND);
    }
}
