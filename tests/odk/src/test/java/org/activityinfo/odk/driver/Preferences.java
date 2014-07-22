package org.activityinfo.odk.driver;

import io.appium.java_client.AndroidKeyCode;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

public class Preferences {

    private final AppiumDriver driver;

    public Preferences(AppiumDriver driver) {
        this.driver = driver;
    }

    public Preferences go() {
        System.out.println("Current url = " + driver.getCurrentUrl());
        driver.get("and-activity://org.odk.collect.android.preferences.PreferencesActivity");
        return this;
    }

    public Preferences setUrl(String url) {
        return updatePreference("URL", url);
    }

    public Preferences setAccountEmail(String accountEmail) {
        return updatePreference("Username", accountEmail);
    }



    private Preferences updatePreference(String preferenceLabel, String newValue) {

        // Click the preference to open the dialog
        driver.findElement(By.partialLinkText(preferenceLabel)).click();

        // clear the existing dialog
        driver.findElement(By.xpath("//EditText")).clear();

        // type in the new result
        driver.getKeyboard().sendKeys(newValue);

        // move to OK button
        driver.sendKeyEvent(AndroidKeyCode.ENTER);

        // click ok
        driver.sendKeyEvent(AndroidKeyCode.ENTER);

        return this;
    }


    public Preferences setPassword(String password) {

        // Click the preference to open the dialog
        driver.findElement(By.partialLinkText("Password")).click();

        // clear the existing dialog
        driver.findElement(By.xpath("//EditText")).clear();

        // update password
        driver.getKeyboard().sendKeys(password);

        driver.sendKeyEvent(AndroidKeyCode.BACK);

        driver.findElement(By.xpath("//Button[@value='OK']")).click();

        return this;
    }
}
