package org.activityinfo.ui.style.tools.gss;

import com.google.common.io.ByteSource;

import java.io.IOException;

public interface ResourceWriter {

    String writeResource(ByteSource content, String extension) throws IOException;
}
