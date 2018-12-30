import java.util.Optional;

/**
 * Created by Ins on 30.12.2018.
 */
public class Record {
    private String name;

    private Optional<String> id = Optional.empty(),
        zoneID = Optional.empty(),
        type = Optional.empty();
    private Optional<Boolean> proxied = Optional.of(true);
    private Optional<Integer> ttl = Optional.of(1);

    public Record(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return id.isPresent() && zoneID.isPresent() && type.isPresent();
    }

    public String getName() {
        return name;
    }

    public Optional<String> getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Optional.of(id);
    }

    public Optional<String> getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = Optional.of(zoneID);
    }

    public Optional<String> getType() {
        return type;
    }

    public void setType(String type) {
        this.type = Optional.of(type);
    }

    public Optional<Boolean> getProxied() {
        return proxied;
    }

    public void setProxied(Boolean proxied) {
        this.proxied = Optional.of(proxied);
    }

    public Optional<Integer> getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        if (ttl >= 120 && ttl <= 2147483647)
            this.ttl = Optional.of(ttl);
    }
}
