package org.activityinfo.core.shared.form;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * The type of field, which influences how input is presented
 * the user, how it is validated, and what default measures
 * are available.
 */
public enum FormFieldType {

    /**
     * Numeric quantity, expressed in certain units
     */
    QUANTITY {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(NARRATIVE, FREE_TEXT);
        }
    },

    /**
     * A longish block of text
     * <p/>
     * Note: Defined exact length to differ between FREE_TEXT type.
     * {@link #FREE_TEXT_LENGTH}
     */
    NARRATIVE {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(FREE_TEXT, QUANTITY, LOCAL_DATE);
        }
    },

    /**
     * Short free text field
     * <p/>
     * Note: Defined exact length to differ between NARRATIVE type.
     * {@link #FREE_TEXT_LENGTH}
     */
    FREE_TEXT {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(QUANTITY, LOCAL_DATE, NARRATIVE);
        }
    },

    /**
     * A Gregorian calendar date, with no time zone attached
     */
    LOCAL_DATE {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(FREE_TEXT, NARRATIVE);
        }
    },

    /**
     * A geographic point, expressed as latitude / longitude
     */
    GEOGRAPHIC_POINT {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(FREE_TEXT, NARRATIVE);
        }
    },

    /**
     * References another FormInstance or RDFS resource
     */
    REFERENCE {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet();
        }
    };

    /**
     * Defined exact length of string to differ between FREE_TEXT and NARRATIVE types.
     * If string length less than #FREE_TEXT_LENGTH then type is #FREE_TEXT otherwise it is NARRATIVE.
     */
    public static final int FREE_TEXT_LENGTH = 80;

    FormFieldType() {
    }

    public abstract Set<FormFieldType> getAllowedConvertTo();

    public static FormFieldType valueOfSilently(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

}
