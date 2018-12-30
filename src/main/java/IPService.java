/**
 * Created by Ins on 13.12.2018.
 */
public class IPService {
    private final String url = "http://wgetip.com/";
    private final Logger logger = Logger.getInstance();

    public IPService() {

    }

    public String getIP() throws Exception {
        try {
            String ip = NetworkIO.getInstance().request(url, "GET", null, null).trim();
            return ip;
        }
        catch (Exception e) {
            logger.error("Failed to get IP address");
            throw e;
        }
    }
}
