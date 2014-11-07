package org.activityinfo.io.feeds.xform;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by alex on 11/6/14.
 */
public class XFormImporter {

    public static void main(String[] args) throws Exception {
        URL formUrl = Resources.getResource(XFormImporter.class, "testv13.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource());

        // 1 =
        // HH 2 = module 1
        // 3 questionnaires in each module

    }
}
