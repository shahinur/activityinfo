package org.activityinfo.model.resource;

import java.util.Random;

/**
 * Collision-resistant universal identifier generator.
 *
 * Timestamp: 5 bytes / 6 characters
 *
 */
public class CuidGenerator {
    private long clientId;
    private long initialTimestamp;
    private int counter = 1;
    private Random random = new Random();

    public CuidGenerator(long clientId, long initialTimestamp) {
        this.clientId = clientId;
        this.initialTimestamp = initialTimestamp;
    }

    /**
     *
     * @return the current time stamp, as a 32-bit integer
     */
    int timestamp() {
        long secondSinceEpoch = (System.currentTimeMillis() - initialTimestamp) / 1000L;
        return (int)secondSinceEpoch;
    }

    int random() {
        return random.nextInt(62 * 62);
    }

    int counter() {
        return counter++;
    }

    public String generate() {
        StringBuilder cuid = new StringBuilder("c");
        appendTo(cuid);
        return cuid.toString();
    }

    public ResourceId generateResourceId() {
        return ResourceId.valueOf(generate());
    }

    private void appendTo(StringBuilder cuid) {
        Base62.encodeTo(clientId, cuid);
        Base62.encodeTo(random(), cuid);
        Base62.encodeTo(timestamp(), cuid);
        Base62.encodeTo(counter(), cuid);
    }
}
