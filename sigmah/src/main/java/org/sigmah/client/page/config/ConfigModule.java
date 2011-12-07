/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

/**
 * The set of pages comprising the configuration rubrique, including definition and creation
 * of databases, user settings, etc.
 */
package org.sigmah.client.page.config;

import org.sigmah.client.page.config.LockedPeriodsPresenter.LockedPeriodListEditor;
import org.sigmah.client.page.config.design.DesignPresenter;
import org.sigmah.client.page.config.design.DesignView;

import com.google.gwt.inject.client.AbstractGinModule;

/**
 * @author Alex Bertram
 */
public class ConfigModule extends AbstractGinModule {

    @Override
    protected void configure() {

        // binds the view components
        bind(AccountEditor.View.class).to(AccountPanel.class);
        bind(DbListPresenter.View.class).to(DbListPage.class);
        bind(DbUserEditor.View.class).to(DbUserGrid.class);
        bind(DbPartnerEditor.View.class).to(DbPartnerGrid.class);
        bind(DbProjectEditor.View.class).to(DbProjectGrid.class);
        bind(DesignPresenter.View.class).to(DesignView.class);
        bind(LockedPeriodListEditor.class).to(LockedPeriodGrid.class);
        bind(DbTargetEditor.View.class).to(DbTargetGrid.class);
        bind(LinkIndicatorPresenter.View.class).to(IndicatorLinkView.class);
    }
}