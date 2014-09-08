package org.activityinfo.model.resource;

public class Base62 {

    private static final String DIGITS = "0123456789abcdefghijklmnopqrstuvwyxzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE = 62;

    private static final int MORE_DATA_BITMASK = 0x01;


    public static String encode(long value) {
        StringBuilder sb = new StringBuilder();
        encodeTo(value, sb);
        return sb.toString();
    }

    /**
     * Encodes a long value and its length to base-62 in a fashion
     * similar to protobuf's varying-length integers.
     */
    public static void encodeTo(long value, StringBuilder s) {
        long q = value;
        long d1, d2;

        // write in two char blocks.
        do {
            d1 = q % BASE;
            q = q / BASE;

            // in the second digit of the block, reserve
            // 1 bit to indicate whether the sequence continues
            d2 = q % (BASE/2);
            q = q / (BASE/2);

            // if there is more, mark the second digit
            if(q > 0) {
                d2 = (d2 << 1) | MORE_DATA_BITMASK;
            } else {
                d2 = (d2 << 1);
            }

            s.append(DIGITS.charAt((int)d1));
            s.append(DIGITS.charAt((int)d2));

        } while(q > 0);
    }

    public static long decode(CharSequence sequence, int offset) {
        long value = 0;
        long pow = 1;
        int pos = offset;

        while(true) {
            int d1 = DIGITS.indexOf(sequence.charAt(pos++));
            int d2 = DIGITS.indexOf(sequence.charAt(pos++));

            value += pow * d1;
            pow *= BASE;

            value += pow * (d2 >> 1);
            pow *= (BASE/2);

            if( (d2 & MORE_DATA_BITMASK) == 0) {
                break;
            }
        }
        return value;
    }



}
