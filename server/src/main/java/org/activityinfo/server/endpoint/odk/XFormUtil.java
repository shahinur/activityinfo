package org.activityinfo.server.endpoint.odk;

/**
 * Created by Mithun <shahinur.bd@gmail.com>.
 */
public class XFormUtil {
    public static String encoding = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    public static String startHtmlTag = "<h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:jr=\"http://openrosa.org/javarosa\">\n";
    public static String endHtmlTag = "</h:html>";
    public static String startHeadTag = "<h:head>\n";
    public static String endHeadTag = "</h:head>\n";
    public static String startBodyTag = "<h:body>\n";
    public static String endBodyTag = "</h:body>\n";

    public static String getTitle( String title ){
        return "<h:title>"+title+"</h:title>\n";
    }

    public static String buildXFrom(){
        StringBuilder xform = new StringBuilder();
        xform.append(encoding);
        xform.append(startHtmlTag);
        xform.append(startHeadTag);
        xform.append(getTitle("Mithun"));
        xform.append(endHeadTag);
        xform.append(startBodyTag);
        xform.append(endBodyTag);
        xform.append(endHtmlTag);

        return xform.toString();
    }
}
