package org.activityinfo.odk.driver;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;

public class FormList {

    private final AppiumDriver driver;
    private List<String> forms;

    public FormList(AppiumDriver driver) {
        this.driver = driver;
        driver.get("and-activity://org.odk.collect.android.activities.FormDownloadList");
    }

    /**
     * Refreshes the list of Blank Forms and waits until the
     * list is fully loaded
     */
    public FormList load() {

        refreshIfNeeded();

        pause();

        Stopwatch stopwatch = Stopwatch.createStarted();

        while(true) {
            if(stopwatch.elapsed(TimeUnit.SECONDS) > 30) {
                throw new AssertionError("Timed-out waiting for blank forms");
            }
            if(stillLoading()) {
                pause();

            } else if(weArePromptedForAuthentication()) {
                // we've already supplied username/password, just submit
                justClickOK();

            } else {
                // loaded
                break;
            }
        }

        PageSource pageSource = new PageSource(driver.getPageSource());
        this.forms = parseFormList(pageSource);

        return this;
    }

    private void refreshIfNeeded() {
        List<WebElement> refreshButton = driver.findElements(xpath("//Button[@text='Refresh']"));
        if(!refreshButton.isEmpty()) {
            refreshButton.get(0).click();
        }
    }

    private void justClickOK() {
        driver.findElement(By.partialLinkText("OK")).click();
    }

    private void pause() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }

    private boolean stillLoading() {
        return driver.findElements(xpath("//TextView[@text='Connecting to Server']")).size() > 0;
    }

    private boolean weArePromptedForAuthentication() {
        List<WebElement> elements = driver.findElements(
                xpath("//DialogTitle[@value='Server Requires Authentication']"));

        return elements.size() > 0;
    }

    @VisibleForTesting
    static List<String> parseFormList(PageSource page) {

        NodeList nodes = page.query("//*[@id='list']/*/TextView[1]/@value");
        List<String> names = Lists.newArrayList();
        for(int i=0;i!=nodes.getLength();++i) {
            Attr value = (Attr) nodes.item(i);
            names.add(value.getValue());
        }
        return names;
    }

    public List<String> getFormNames() {
        if(forms == null) {
            load();
        }
        return forms;
    }

}
