package org.activityinfo.io.load.excel;

import org.activityinfo.io.load.FormImportOptions;
import org.activityinfo.model.resource.Resources;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.junit.internal.AssumptionViolatedException;

import java.io.File;

public class LccaImport {

    public static void main(String args[]) throws Exception{
        File xlsxFile = new File("/home/alex/Downloads/20140901_BambasSurvey_Cleaned_final_v2.1 AB_for import.xlsx");
        if (!xlsxFile.exists()) {
            throw new AssumptionViolatedException("Can't find import file " + xlsxFile);
        }

        FormImportOptions options = new FormImportOptions();
        options.setOwnerId(Resources.ROOT_ID);

        try(JsonWriter writer = new JsonWriter()) {
            // The package open is instantaneous, as it should be.
            OPCPackage p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
            ExcelStreamingImporter xlsx2csv = new ExcelStreamingImporter(p, writer);
            xlsx2csv.process();
        }
    }
}