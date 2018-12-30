import java.util.List;

/**
 * Created by Ins on 24.03.2018.
 */
public class DUC {

    public static void main(String[] args) {
        System.out.println("Service started");

        Logger logger = Logger.getInstance();

        Config config = new Config();
        IPService ipService = new IPService();
        Cloudflare cloudflare = new Cloudflare();

        // minttl fits update interval of DUC loop
        // it shouldn't be less than minimum ttl of your domains
        int minttl = 120;

        while (true) {
            try {
                logger.message("Getting present ip address");
                String ip = ipService.getIP();
                logger.message("Your current IP address is: " + ip);

                logger.message("Reading config file");
                config.read();
                List<Record> updateables = config.getRecords();
                logger.message("Records to update: " + updateables.size());

                // setting Cloudflare credentials
                cloudflare.setCredentials(config.getEmail(), config.getApiKey());

                logger.message("Fetching present record list from Cloudflare");
                List<Record> records = cloudflare.list(config.getZoneID());

                logger.message("Filling missing record data");
                for (Record updateable : updateables) {
                    for (Record record : records) {
                        if (updateable.getName().equals(record.getName())) {
                            if (!updateable.getId().isPresent())
                                updateable.setId(record.getId().get());

                            if (!updateable.getZoneID().isPresent())
                                updateable.setZoneID(record.getZoneID().get());

                            if (!updateable.getType().isPresent())
                                updateable.setType(record.getType().get());

                            if (!updateable.getTtl().isPresent())
                                updateable.setTtl(record.getTtl().get());

                            if (!updateable.getProxied().isPresent())
                                updateable.setProxied(record.getProxied().get());

                            break;
                        }
                    }
                }

                for (Record record : updateables) {
                    logger.message("Updating record for \"" + record.getName() + "\"");
                    cloudflare.update(record, ip);
                }

                // setting minttl
                for (Record record : updateables) {
                    if (record.getTtl().isPresent() && record.getTtl().get() >= 120 && record.getTtl().get() < minttl)
                        minttl = record.getTtl().get();
                }

            } catch (Exception e) {
                logger.error("DUC failed");
                e.printStackTrace();
            }

            try {
                logger.message("Sleeping for " + (minttl) + " seconds");
                Thread.sleep(1000 * minttl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}