package org.activityinfo.ui.vdom.shared;

import org.activityinfo.ui.vdom.shared.diff.PatchComponentOp;
import org.activityinfo.ui.vdom.shared.diff.PatchOp;
import org.activityinfo.ui.vdom.shared.diff.VPatchSet;
import org.activityinfo.ui.vdom.shared.tree.VComponent;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VDomLogger {

    public static final String PADDING = "                                  ";
    private static int nextId = 1;

    private static final Logger LOGGER = Logger.getLogger("VDom");

    private static final Level LOG_LEVEL = Level.INFO;

    public static boolean ENABLED = false;

    public static boolean STD_OUT = true;

    public static void event(VComponent component, String eventName) {
       // LOGGER.log(LOG_LEVEL, componentId(component) + "." + eventName);
        if(STD_OUT) {
            System.out.println(padString(eventName, 15) + mountIcon(component) + " " +
                dirtyIcon(component) + " " + component.getDebugId());
        }
    }

    private static String mountIcon(VComponent component) {
        return component.isMounted() ? "m" : " ";
    }

    private static String dirtyIcon(VComponent component) {
        return component.isDirty() ? "d" : " ";
    }

    private static String padString(String s, int len) {
        return s + PADDING.substring(0, Math.max(0, len - s.length()));
    }

    public static int nextDebugId() {
        return nextId++;
    }

    public static void start(String event) {
        if(STD_OUT) {
            System.out.println();
            System.out.println("---- " + event + " -----");
        }
    }

    public static void dump(VPatchSet patchSet) {
        if(STD_OUT) {
            if(!patchSet.isEmpty()) {
                System.out.println();
                System.out.println("---- patchSet -----");
                printPatches("", patchSet);
            }
        }
    }

    public static void event(String message) {
        if(STD_OUT) {
            System.out.println();
            System.out.println("#### " + message + " ####");
            System.out.println();
        }
    }

    private static void printPatches(String indent, VPatchSet patchSet) {
        for (Integer index : patchSet.getPatchedIndexes()) {
            List<PatchOp> patchOps = patchSet.get(index);
            if(patchOps.size() == 1 && patchOps.get(0) instanceof PatchComponentOp) {
                PatchComponentOp op = (PatchComponentOp) patchOps.get(0);
                System.out.println(indent + index + " = PATCH COMPONENT " +
                    (op.getPrevious() == null ? "-" : op.getPrevious().getDebugId()));
                printPatches(indent + "  ", op.getPatchSet());
            } else {
                System.out.println(indent + index + " = " + patchOps);
            }
        }
    }
}
