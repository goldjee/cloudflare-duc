import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

/**
 * Created by Ins on 29.12.2018.
 */
public class Cloudflare {
    private final String baseURL = "https://api.cloudflare.com/client/v4/";
    private final Logger logger = Logger.getInstance();

    private boolean set = false;

    private String apiKey;
    private String email;

    public Cloudflare() {
        set = false;
    }

    public boolean setCredentials(String email, String apiKey) {
        this.apiKey = apiKey;
        this.email = email;

        return set;
    }

    public List<Record> list(String zone) throws Exception {
        try {
            String uri = baseURL + "zones/" + zone + "/dns_records";

            Map<String, String> headers = new HashMap<>();
            headers.put("X-Auth-Email", email);
            headers.put("X-Auth-Key", apiKey);
            headers.put("Content-Type", "application/json");

            String response = NetworkIO.getInstance().request(uri, "GET", headers, null);

            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(response).getAsJsonObject();
//            System.out.println(response);

            if (object.has("success") && object.get("success").getAsBoolean() == true) {
                JsonArray recordsJson = object.getAsJsonArray("result");
                List<Record> records = new ArrayList<>();

                for (JsonElement recordJson : recordsJson) {
                    JsonObject recordObject = recordJson.getAsJsonObject();

                    Record record = new Record(recordObject.get("name").getAsString());
                    record.setId(recordObject.get("id").getAsString());
                    record.setZoneID(recordObject.get("zone_id").getAsString());
                    record.setType(recordObject.get("type").getAsString());
                    record.setProxied(recordObject.get("proxied").getAsBoolean());
                    record.setTtl(recordObject.get("ttl").getAsInt());

                    records.add(record);
                }

                return records;
            }
        }
        catch (Exception e) {
            logger.error("Failed to list DNS records from Cloudflare");
            throw e;
        }

        return null;
    }

    public boolean update(Record record, String ip) throws Exception {
        try {
            if (record.isReady()) {
                String uri = baseURL + "zones/" + record.getZoneID().get() + "/dns_records/" + record.getId().get();

                Map<String, String> headers = new HashMap<>();
                headers.put("X-Auth-Email", email);
                headers.put("X-Auth-Key", apiKey);
                headers.put("Content-Type", "application/json");

                JsonObject dataJson = new JsonObject();
                dataJson.addProperty("type", record.getType().get());
                dataJson.addProperty("name", record.getName());
                dataJson.addProperty("content", ip);
                if (record.getTtl().isPresent())
                    dataJson.addProperty("ttl", record.getTtl().get());
                if (record.getProxied().isPresent())
                    dataJson.addProperty("proxied", record.getProxied().get());

                String data = dataJson.toString();
                String response = NetworkIO.getInstance().request(uri, "PUT", headers, data);

                JsonParser parser = new JsonParser();
                JsonObject object = parser.parse(response).getAsJsonObject();
                //        System.out.println(result);

                return object.get("success").getAsBoolean();
            }
        }
        catch (Exception e) {
            logger.error("Failed to update DNS record for \"" + record.getName() + "\"");
            throw e;
        }

        return false;
    }
}
