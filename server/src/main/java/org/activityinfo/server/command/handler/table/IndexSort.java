package org.activityinfo.server.command.handler.table;


import com.google.common.collect.Ordering;
import org.activityinfo.model.table.ColumnView;

public class IndexSort {

    private ColumnView columnView;
    private Ordering ordering;

    public IndexSort(ColumnView columnView, Ordering ordering) {
        this.columnView = columnView;
        this.ordering = ordering;
    }

    // quicksort a[left] to a[right]
    public void quicksort(float[] a, int[] index, int left, int right) {
        if (right <= left) return;
        int i = partition(a, index, left, right);
        quicksort(a, index, left, i-1);
        quicksort(a, index, i+1, right);
    }

    // partition a[left] to a[right], assumes left < right
    private static int partition(float[] a, int[] index,
                                 int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while (less(++i, right))      // find item on left to swap
                ;                               // a[right] acts as sentinel
            while (less(right, --j))      // find item on right to swap
                if (j == left) break;           // don't go out-of-bounds
            if (i >= j) break;                  // check if pointers cross
            exch(a, index, i, j);               // swap two elements into place
        }
        exch(a, index, i, right);               // swap with partition element
        return i;
    }

    // is x < y ?
    private static boolean less(int x, int y) {
        return (x < y);
    }

    // exchange a[i] and a[j]
    private static void exch(float[] a, int[] index, int i, int j) {
        float swap = a[i];
        a[i] = a[j];
        a[j] = swap;
        int b = index[i];
        index[i] = index[j];
        index[j] = b;
    }
}
