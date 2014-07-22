package org.activityinfo.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

/**
 * Verifies that we can successfully synchronize the local browser
 * database for a given user account.
 *
 * This test attempts to persist state across builds, to ensure that we don't
 * break the synchronization for users who synchronized with older versions of the application.
 */
@RunWith(Parameterized.class)
public class SyncTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebElement statusElement;

    private LocalStorage localStorage;
    private String statusText;

    private UserAccount account;

    /**
     *
     * @return a list of users for whom to test synchronization.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> getUsers() {
        List<Object[]> parameters = Lists.newArrayList();
        for(UserAccount account : Config.getUsers()) {
            parameters.add(new Object[] { account });
        }
        return parameters;
    }

    /**
     * Constructed for each parameter returned by {@link #getUsers()}
     */
    public SyncTest(UserAccount account) {
        this.account = account;
    }

    /**
     * Test the ability to synchronize from scratch
     */
    @Test
    public void loginAndSynchronizeFresh() {
        createDriverWith(LocalStorage.thatIsEmpty());
        login();
        assertThatWeAreInOnlineMode();
        enableOfflineMode();
    }

    /**
     * Test the ability to synchronize against a local
     * database potentially from a previous version of the web app
     */
    @Test
    public void resynchronize() throws IOException {
        createDriverWith(LocalStorage.persistedAcrossBuilds(account));

        if(!localStorage.havePreviouslySynchronizedSuccessfully()) {
            login();
            assertThatWeAreInOnlineMode();
            enableOfflineMode();
        } else {
            assertThatWeAreInOfflineMode();
            synchronize();
            localStorage.recordSuccessfulSynchronization();
        }
    }

    private void createDriverWith(LocalStorage localStorage) {

        this.localStorage = localStorage;
        driver = new PhantomJs()
                .ignoreSslErrors()
                .with(localStorage)
                .createDriver();

        wait = new WebDriverWait(driver, 10);
    }

    private void login() {
        driver.get(Config.getRootUrl() + "/login");
        driver.findElement(By.name("email")).sendKeys(account.getUsername());
        driver.findElement(By.name("password")).sendKeys(account.getPassword());
        driver.findElement(xpath("//button[@type='submit']")).click();
    }

    private WebDriverWait assertThatWeAreInOnlineMode() {
        statusElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                xpath("//*[text() = 'Working online']")));
        return wait;
    }

    private WebDriverWait assertThatWeAreInOfflineMode() {
        statusElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                xpath("//*[text() = 'Working offline']")));
        return wait;
    }

    private void enableOfflineMode() {
        // Click on the gear icon
        wait.until(ExpectedConditions.presenceOfElementLocated(xpath(
                "//div[text() = 'ActivityInfo']/following-sibling::div[2]"))).click();

        // Enable offline mode
        wait.until(visibilityOfElementLocated(xpath("//div[text() = 'Enable offline mode']"))).click();

        waitUntilSynchronizationSucceeds();
    }

    private void synchronize() {
        // Click on sync status label
        wait.until(ExpectedConditions.presenceOfElementLocated(
                xpath("//div[descendant::span[contains(., 'Last Sync')]]"))).click();

        waitUntilSynchronizationSucceeds();
    }

    private void waitUntilSynchronizationSucceeds() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        while(stopwatch.elapsed(TimeUnit.MINUTES) < 5) {
            String currentStatus = statusElement.getText();
            if(!Objects.equals(statusText, currentStatus)) {
                this.statusText = currentStatus;
                System.out.println(statusText);
            }

            if(statusText.equals("Working offline")) {
                if(driver.findElements(By.xpath("//span[contains(., 'A minute ago')]")).isEmpty()) {
                    throw new AssertionError("Failed to synchronize");
                }
                // success
                break;
            } else if(statusText.equals("Sync error")) {
                throw new AssertionError("Synchronization failed");
            }
            if(stopwatch.elapsed(TimeUnit.MINUTES) >= 5) {
                throw new AssertionError("Synchronization timed out");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
