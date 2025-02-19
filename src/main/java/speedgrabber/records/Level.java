package speedgrabber.records;

import speedgrabber.records.interfaces.Identifiable;

public record Level(
        String weblink,
        String selflink,
        String id,
        String name,

        String gamelink

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
