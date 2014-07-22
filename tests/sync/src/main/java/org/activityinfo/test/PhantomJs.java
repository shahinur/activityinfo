package org.activityinfo.test;

import com.google.common.base.Strings;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class PhantomJs {

    private List<String> args = new ArrayList<>();


    private final DesiredCapabilities capabilities = new DesiredCapabilities();

    public PhantomJs() {
    }

    public PhantomJs ignoreSslErrors() {
        args.add("--web-security=false");
        args.add("--ssl-protocol=any");
        args.add("--ignore-ssl-errors=true");
        return this;
    }

    public PhantomJs with(LocalStorage localStorage) {
        args.add("--local-storage-path=" + localStorage.getDir().getAbsolutePath());
        return this;
    }

    public PhantomJSDriver createDriver() {

        // Prepare capabilities
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args);

        // Control LogLevel for GhostDriver, via CLI arguments
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,
                new String[]{"--logLevel=" + System.getProperty(Config.PHANTOMJS_LOGLEVEL, "INFO")});

        // Fetch PhantomJS-specific configuration parameters
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                Config.getRequiredProperty(Config.PHANTOMJS_BIN));

        if (!Strings.isNullOrEmpty(System.getProperty(Config.PHANTOMJS_DRIVER_PATH))) {
            System.out.println("Test will use an external GhostDriver");
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_PATH_PROPERTY,
                    System.getProperty(Config.PHANTOMJS_DRIVER_PATH));
        } else {
            System.out.println("Test will use PhantomJS internal GhostDriver");
        }

        PhantomJSDriver driver = new PhantomJSDriver(capabilities);

        // resize app to desktop resolution
        // otherwise key UI components are not clickable
        driver.manage().window().setSize(new Dimension(1920,1080));

        return driver;
    }
}
