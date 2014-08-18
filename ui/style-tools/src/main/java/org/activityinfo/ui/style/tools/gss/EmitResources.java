package org.activityinfo.ui.style.tools.gss;

import com.google.common.base.Preconditions;
import com.google.common.css.compiler.ast.CssFunctionNode;
import com.google.common.css.compiler.ast.CssStringNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;
import com.google.common.io.ByteSource;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.activityinfo.ui.style.tools.rebind.SourceResolver;

import java.io.IOException;

public class EmitResources extends DefaultTreeVisitor {

    private MutatingVisitController visitController;
    private final SourceResolver sourceResolver;
    private TreeLogger logger;
    private boolean errors = false;
    private ResourceWriter resourceWriter;

    public EmitResources(MutatingVisitController visitController,
                         TreeLogger parentLogger,
                         SourceResolver sourceResolver,
                         ResourceWriter resourceWriter) {
        this.resourceWriter = resourceWriter;
        Preconditions.checkNotNull(sourceResolver);

        this.visitController = visitController;
        this.sourceResolver = sourceResolver;
        this.logger = parentLogger.branch(Type.INFO, "Emitting resources...");
    }

    @Override
    public boolean enterFunctionNode(CssFunctionNode value) {
        if (value.getFunctionName().equals("url")) {

            logger.log(Type.DEBUG, "Encountered URL: " + value.toString());
            if (value.getArguments().getChildren().size() == 1 &&
                value.getArguments().getChildAt(0) instanceof CssStringNode) {
                CssStringNode urlString = (CssStringNode) value.getArguments().getChildAt(0);

                try {
                    emitResource(urlString);

                } catch (Exception e) {
                    logger.log(Type.ERROR, "Error while processing resource", e);
                    errors = true;
                }
            }
        }
        return false;
    }

    private void emitResource(CssStringNode urlNode) throws UnableToCompleteException, IOException {

        String url = urlNode.getValue();

        byte[] data = sourceResolver.resolveByteArray(logger, getFileName(url));
        String strongName = resourceWriter.writeResource(ByteSource.wrap(data), parseExtension(url));

        urlNode.setValue(replaceFile(url, strongName));

        logger.log(Type.DEBUG, "Wrote " + getFileName(url) + " to " + strongName);

    }

    private String parseExtension(String url) {

        // Bootstrap uses a hack involving a ?iefix query string
        String file = getFileName(url);

        int finalDot = file.lastIndexOf('.');
        return file.substring(finalDot+1);
    }

    private String getFileName(String url) {
        String file = url;
        int queryStringStart = file.lastIndexOf('?');
        if(queryStringStart >= 0) {
            file = file.substring(0, queryStringStart);
        }
        int hashStart = file.lastIndexOf('#');
        if(hashStart >= 0) {
            file = file.substring(0, hashStart);
        }
        return file;
    }

    private String replaceFile(String url, String strongName) {
        int fileEnds = url.length();
        int queryStringStart = url.lastIndexOf('?');
        if(queryStringStart >= 0) {
            fileEnds = queryStringStart;
        } else {
            int hashStart = url.lastIndexOf('#');
            if (hashStart >= 0) {
                fileEnds = hashStart;
            }
        }
        return strongName + url.substring(fileEnds);
    }

    public void runPass() {
        visitController.startVisit(this);
    }

    public boolean hasErrors() {
        return errors;
    }
}
