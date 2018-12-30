import java.time.LocalDateTime;

/**
 * Created by Ins on 13.12.2018.
 */
public class Logger {
    private static volatile Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        Logger localInstance = instance;
        if (instance == null) {
            synchronized (Logger.class) {
                localInstance = instance;
                if (localInstance == null)
                    instance = localInstance = new Logger();
            }
        }

        return instance;
    }

    private final String logFile = "log.log";

    // 0 - silent
    // 1 - errors
    // 2 - everything
    private int logLevel = 2;
    private int outputLevel = 2;

    public void message(String message) {
        message = format(message);

        if (outputLevel == 2)
            System.out.println(message);
        if (logLevel == 2)
            write(message);
    }

    public void error(String message) {
        message = format(message);

        if (logLevel > 0)
            System.out.println(message);
        if (logLevel > 0)
            write(message);
    }

    private String format(String message) {
        return "[" + LocalDateTime.now().toString() + "] " + message;
    }

    private void write(String message) {
        try {
            FileIO.getInstance().write(logFile, message + "\r\n");
        }
        catch (Exception e) {
            System.out.println("Logger failed to write data");
            e.printStackTrace();
        }
    }

    public void setLogLevel(int logLevel) {
        if (!(logLevel >= 0 && logLevel <= 2))
            logLevel = this.logLevel;

        message("Log level is set to " + logLevel);
        this.logLevel = logLevel;
    }

    public void setOutputLevel(int outputLevel) {
        if (!(outputLevel >= 0 && outputLevel <= 2))
            outputLevel = this.outputLevel;

        message("Output level is set to " + outputLevel);
        this.outputLevel = outputLevel;
    }
}
