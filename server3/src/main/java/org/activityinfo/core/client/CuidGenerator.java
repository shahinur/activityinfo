package org.activityinfo.core.client;

import com.google.gwt.user.client.Random;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.Cuids;

import java.util.Date;

/**
 * Generates a ResourceId.
 *
 * <p>Cuids can be safely generated on the client and relied upon to be universally unique,
 * even if generated at a very fast rate, thanks to the inclusion of the counter.</p>
 *
 * Adapted from https://github.com/dilvie/cuid
 */
public class CuidGenerator {

    public static final CuidGenerator INSTANCE = new CuidGenerator();

    public static final int BLOCK_SIZE = 4;

    private int c = 0;
    private int base = 36;
    private int discreteValues = (int) Math.pow(base, BLOCK_SIZE);
    private String fingerprint;

    public CuidGenerator() {

        this.fingerprint = pad(browserFingerPrint());
    }

    public static String pad(int num) {
        String s = "000000000" + Integer.toString(num, Cuids.RADIX);
        return s.substring(s.length() - BLOCK_SIZE);
    }

    private String randomBlock() {
        int x = (int)(Random.nextDouble() * discreteValues);
        return pad(x);
    }

    public ResourceId nextCuid() {

        // Starting with a lowercase letter makes
        // it HTML element ID friendly.
        // It also gives us a way to accommodating existing
        // values by using this character as a namespace.

        String letter = "c"; // hard-coded allows for sequential access

        // timestamp
        // warning: this exposes the exact date and time
        // that the uid was created.
        String timestamp = Long.toString(new Date().getTime(), base);

        // Prevent same-machine collisions.
        String counter;

        // A few chars to generate distinct ids for different
        // clients (so different computers are far less
        // likely to generate the same id)
        String fingerprint = this.fingerprint;

        // Grab some more chars from Math.random()
        String random = randomBlock() + randomBlock();

        c = (c < discreteValues) ? c : 0;
        counter = pad(c);

        c++; // this is not subliminal

        return ResourceId.create(letter + timestamp + counter + fingerprint + random);
    }

    private static native int browserFingerPrint() /*-{
        // count objects in $wnd
        var i, count = 0;
        for(i in $wnd) { count++ }

        // TODO(alex): this isn't quite right...
        return ($wnd.navigator.mimeTypes.length + $wnd.navigator.userAgent.length) +
            (count >> 4);
    }-*/;
}
