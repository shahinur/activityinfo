package org.activityinfo.ui.client.page.config.design;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.model.AttributeDTO;
import org.activityinfo.legacy.shared.model.LocationTypeDTO;

public class LocationTypeForm extends AbstractDesignForm {

    private FormBinding binding;

    public LocationTypeForm() {

        binding = new FormBinding(this);

        final NumberField idField = new NumberField();
        idField.setFieldLabel("ID");
        idField.setReadOnly(true);
        binding.addFieldBinding(new FieldBinding(idField, "id"));
        add(idField);

        TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel(I18N.CONSTANTS.name());
        nameField.setMaxLength(LocationTypeDTO.NAME_MAX_LENGTH);
        binding.addFieldBinding(new FieldBinding(nameField, "name"));

        add(nameField);

        hideFieldWhenNull(idField);
    }

    @Override
    public FormBinding getBinding() {
        return binding;
    }

}
