package org.activityinfo.ui.app.client.chrome.connectivity;
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

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.HeaderBar;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VText;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * @author yuriyz on 9/8/14.
 */
public class ConnectivityWidget extends VComponent<HeaderBar> {

    private final Application application;

    public ConnectivityWidget(Application application) {
        this.application = application;
        application.getConnectivityStore().addChangeListener(new StoreChangeListener() {
            @Override
            public void onStoreChanged(Store store) {
                refresh();
            }
        });
    }

    @Override
    protected VTree render() {
        return div(className(BTN_GROUP),
                new VNode(HtmlTag.BUTTON, classNames(BTN, BTN_DEFAULT, DROPDOWN_TOGGLE, TP_ICON),
                        icon(), space(), label()));
    }

    private VNode icon() {
        return application.getConnectivityStore().isOnline()?
                FontAwesome.SIGNAL.render() : FontAwesome.EXCLAMATION_TRIANGLE.render();
    }

    private VText label() {
        return application.getConnectivityStore().isOnline() ?
                t("Online") : t("Offline");
    }

    // for test switch state randomly each 3 seconds
//    private void test() {
//        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
//            @Override
//            public boolean execute() {
//                Random random = new Random();
//                boolean b = random.nextBoolean();
//                System.out.println(b);
//                ConnectivityWidget.this.application.getDispatcher().dispatch(new UpdateConnectivityAction(b ? ConnectivityState.ONLINE : ConnectivityState.OFFLINE));
//                return true;
//            }
//        }, 3 * 1000);
//    }

}
