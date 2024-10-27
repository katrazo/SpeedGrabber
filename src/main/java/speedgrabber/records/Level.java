package speedgrabber.records;

public record Level(
        String weblink,
        String selflink,
        String id,
        String name,

        String gameLink

) implements Identifiable {
    @Override
    public String identify() {
        return selflink;
    }

    @Override
    public String toString() {
        return name;
    }
}