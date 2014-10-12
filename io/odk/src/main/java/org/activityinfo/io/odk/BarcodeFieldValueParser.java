package org.activityinfo.io.odk;

import org.activityinfo.model.type.barcode.BarcodeValue;
import org.w3c.dom.Element;


/**
 * BarcodeFieldValueParser.
 *
 * @author Mithun<shahinur.bd@gmail.com>
 */
class BarcodeFieldValueParser implements OdkFieldValueParser {
    @Override
    public BarcodeValue parse(Element element) {
        String code = OdkHelper.extractText(element);

        if (code == null) throw new IllegalArgumentException("Malformed Element passed to OdkFieldValueParser.parse()");

        return BarcodeValue.valueOf(code);
    }
}
