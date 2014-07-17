package org.activityinfo.legacy.shared.impl.pivot.calc;

import org.activityinfo.legacy.shared.reports.content.DimensionCategory;
import org.activityinfo.legacy.shared.reports.content.EntityCategory;

import java.util.Arrays;

public class BucketKey {

    private final EntityCategory indicatorCategory;
    private final DimensionCategory[] categories;
    private final int hashCode;

    public BucketKey(EntityCategory indicatorCategory, DimensionCategory[] siteDimCategories) {
        this.indicatorCategory = indicatorCategory;
        this.categories = siteDimCategories;
        this.hashCode = computeHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BucketKey bucketKey = (BucketKey) o;

        if (!Arrays.equals(categories, bucketKey.categories)) {
            return false;
        }
        if (!indicatorCategory.equals(bucketKey.indicatorCategory)) {
            return false;
        }

        return true;
    }

    private int computeHashCode() {
        int result = indicatorCategory.hashCode();
        result = 31 * result + Arrays.hashCode(categories);
        return result;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

}
