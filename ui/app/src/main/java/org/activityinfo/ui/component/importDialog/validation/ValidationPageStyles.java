package org.activityinfo.ui.component.importDialog.validation;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.ui.style.BaseStyles;

/**
 * @author yuriyz on 5/5/14.
 */
//@Source("ValidationPage.less")
public class ValidationPageStyles {

    public static final ValidationPageStyles INSTANCE = new ValidationPageStyles();

    public String stateOk() { return BaseStyles.STATE_OK.getClassNames(); }

    public String stateError() { return BaseStyles.STATE_ERROR.getClassNames(); }

    public String stateConfidence() { return BaseStyles.STATE_CONFIDENCE.getClassNames(); }

}
