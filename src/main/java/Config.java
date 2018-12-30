import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ins on 13.12.2018.
 */
public class Config {
    private String configFile = "cloudflare-duc.conf";
    private final Gson gson = new Gson();
    private final FileIO fileIO = FileIO.getInstance();
    private final Logger logger = Logger.getInstance();

    private String
        accountID,
        email,
        apiKey,
        zoneID;

    private List<Record> records;

    public Config() {
        records = new ArrayList<>();
    }

    public void read() throws Exception {
        String JsonString = fileIO.read(configFile);

        JsonParser parser = new JsonParser();
        JsonObject conf = parser.parse(JsonString).getAsJsonObject();

        int errors = 0;

        // optional logger settings
        if (conf.has("logLevel"))
            logger.setLogLevel(conf.get("logLevel").getAsInt());

        if (conf.has("outputLevel"))
            logger.setOutputLevel(conf.get("outputLevel").getAsInt());

        // mandatory settings
        if (conf.has("accountID"))
            accountID = conf.get("accountID").getAsString();
        else errors++;

        if (conf.has("email"))
            email = conf.get("email").getAsString();
        else errors++;

        if (conf.has("apiKey"))
            apiKey = conf.get("apiKey").getAsString();
        else errors++;

        if (conf.has("zoneID"))
            zoneID = conf.get("zoneID").getAsString();
        else errors++;

        if (conf.has("records")) {
            JsonArray recordsJson = conf.get("records").getAsJsonArray();
            records.clear();

            for (JsonElement recordJson : recordsJson) {
                JsonObject recordObject = recordJson.getAsJsonObject();

                if (recordObject.has("name")) {
                    Record record = new Record(recordObject.get("name").getAsString());

                    if (recordObject.has("proxied"))
                        record.setProxied(recordObject.get("proxied").getAsBoolean());

                    if (recordObject.has("ttl"))
                        record.setTtl(recordObject.get("ttl").getAsInt());

                    records.add(record);
                }
            }
        }
        else errors++;

        logger.message("Config imported");
        if (errors != 0) {
            logger.error("Found " + errors + " error(s). Check configuration file.");
            throw new Exception("Found " + errors + " error(s). Check configuration file.");
        }
    }

    public List<Record> getRecords() {
        return records;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getEmail() {
        return email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getZoneID() {
        return zoneID;
    }
}
