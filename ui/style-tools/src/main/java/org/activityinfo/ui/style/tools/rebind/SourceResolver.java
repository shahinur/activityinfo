package org.activityinfo.ui.style.tools.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

public interface SourceResolver {
    String resolveSourceText(TreeLogger logger, String relativePath) throws UnableToCompleteException;

    byte[] resolveByteArray(TreeLogger logger, String path) throws UnableToCompleteException;
}
