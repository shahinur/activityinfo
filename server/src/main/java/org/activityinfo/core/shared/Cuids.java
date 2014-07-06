package org.activityinfo.core.shared;

import org.activityinfo.model.resource.ResourceId;

/**
 * Functions that operator on Collision-resistant Universal ids (CUID)
 */
public class Cuids {

    public static final String SCHEME = "cuid";

    public static final String IRI_PREFIX = SCHEME + ":";

    public static final int RADIX = Character.MAX_RADIX;

    /**
     * Constructs an IRI from a legacy id.
     *
     * @param cuidDomain a single char which provides a namespace for the ID
     * @param id the original numeric id
     * @return an IRI with the cuid: scheme
     */
    public static ResourceId toIri(char cuidDomain, int id) {
        return ResourceId.create(cuidDomain + Integer.toString(id, RADIX));
    }

}
