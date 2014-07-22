package org.activityinfo.odk.driver;

import com.google.common.io.Resources;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Entry point for Page Objects representing the different
 * activities within the ODK Application.
 *
 * @see {@linkplain http://code.google.com/p/selenium/wiki/PageObjects }
 *
 */
public class OdkDriver {
    public static final String ODK_COLLECT_PACKAGE = "org.odk.collect.android";
    public static final String ODK_MAIN_MENU = ".activities.MainMenuActivity";
    private final AppiumDriver driver;


    public static OdkDriver create() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", System.getProperty("deviceName", "Android Emulator"));
        capabilities.setCapability("platformVersion", System.getProperty("platformVersion", "4.0.3"));
        capabilities.setCapability("app", getApkPath());
        capabilities.setCapability("appPackage", ODK_COLLECT_PACKAGE);
        capabilities.setCapability("appActivity", ODK_MAIN_MENU);
        capabilities.setCapability("automationName", "Selendroid");

        AppiumDriver driver = new AppiumDriver(localAppiumServer(), capabilities);

        return new OdkDriver(driver);
    }

    private static String getApkPath() {
        return new File(Resources.getResource("odk_collect_v1.4.3_rev_1042.apk").getFile()).getAbsolutePath();
    }

    private static URL localAppiumServer()  {
        try {
            return new URL(System.getProperty("appiumUrl", "http://127.0.0.1:4723/wd/hub"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public OdkDriver(AppiumDriver driver) {
        this.driver = driver;
    }

    /**
     * Opens the ODK "General Settings" preferences
     * page
     *
     * @return the Preferences Page Object
     */
    public Preferences openGeneralSettings() {
        return new Preferences(driver).go();
    }

    /**
     * Opens the ODK FormList activity. Equivalent to
     * choose "Get Blank Forms" from the main menu
     *
     * @return the FormList Page Object
     */
    public FormList openFormList() {
        return new FormList(driver);
    }

    /**
     * Closes the ODK Collect app.
     */
    public void close() {
        driver.closeApp();
        driver.quit();
    }
}
