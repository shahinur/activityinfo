package org.activityinfo.geoadmin;

import org.activityinfo.geoadmin.util.JaroWinklerDistance;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for matching place names.
 */
public class PlaceNames {
//
//    /**
//     * Calculates the similarity between two names based on Levenshtein edit
//     * distance on a scale of 0 to 1.
//     * 
//     * @return a score between 0=no similarity, 1=exact match
//     */
//    public static double similiarity(String a, String b) {
//        String cleanA = cleanName(a);
//        String cleanB = cleanName(b);
//        double distance = StringUtils.getLevenshteinDistance(cleanA, cleanB);
//        double maxDistance = Math.max(cleanA.length(), cleanB.length());
//        return (maxDistance - distance) / maxDistance;
//    }

	
	private static final JaroWinklerDistance JARO_WINKLER = new JaroWinklerDistance();
	
    /**
     * Calculates the edit distance between two names.
     * 
     * @param a
     * @param b
     * @return
     */
    public static int distance(String a, String b) {
        return StringUtils.getLevenshteinDistance(PlaceNames.cleanName(a), PlaceNames.cleanName(b));
    }

    /**
     * Cleans up a name by removing all punctuation and non-letters.
     */
    public static String cleanName(String name) {
        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i != name.length(); ++i) {
            int cp = name.codePointAt(i);
            if (Character.isLetter(cp)) {
                cleaned.appendCodePoint(Character.toLowerCase(cp));
            }
        }
        return cleaned.toString();
    }
    
    /**
     * gets the similarity of the two strings using Jaro distance.
     *
     * @param string1 the first input string
     * @param string2 the second input string
     * @return a value between 0-1 of the similarity
     */
    public static double similarity(final String string1, final String string2) {
        if(string1.equals(string2)) {
            return Double.MAX_VALUE;
        }
    	String c1 = cleanName(string1);
		String c2 = cleanName(string2);
		float distance = JARO_WINKLER.getDistance(c1, c2);
    	if(distance < 1.0) {
    		// if it's an approximate match, penalize scores that involve
    		// short strings
    		if(c1.length() <= 3 || c2.length() <= 3) {
    			distance *= 0.7;
    		}
    	}
		return distance;
    }

}
