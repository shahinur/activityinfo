package org.activityinfo.model.hierarchy;

import com.google.common.base.Strings;

import java.util.List;


public class HierarchyPrettyPrinter {

    public static void prettyPrint(Hierarchy hierarchy) {
        printLevels(0, hierarchy.getRoots());
    }

    private static void printLevels(int indent, List<Level> parents) {
        for(Level parent : parents) {
            System.out.println(Strings.repeat(" ", indent) + parent.getLabel());
            for(Level child : parents) {
                printLevels(indent+5, child.getChildren());
            }
        }
    }
}
