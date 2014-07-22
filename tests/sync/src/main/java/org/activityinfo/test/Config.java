package org.activityinfo.test;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

public class Config {
    public static final String PHANTOMJS_BIN = "phantomjs.bin";
    public static final String PHANTOMJS_DRIVER_PATH = "phantomjs.driver.path";
    public static final String PHANTOMJS_LOGLEVEL = "phantomjs.loglevel";

    public static final String ROOT_URL = "root.url";
    public static final String USER_LIST = "users";
    public static final String LOCAL_STORAGE_DIR = "local.storage.dir";

    protected static String getRequiredProperty(String key) {
        return Preconditions.checkNotNull(System.getProperty(key), "-D" + key);
    }

    /**
     *
     * Returns a list of user credentials against which to test from the
     * "users" system property, in the format:
     *
     * <pre>
     *     jim@test.org:secret,bob@test.org:pa33,sue@test.org:password
     * </pre>
     *
     */
    static List<UserAccount> getUsers() {

        String users = getRequiredProperty(USER_LIST);

        List<UserAccount> userAccounts = Lists.newArrayList();
        for(String credential : users.split(",")) {
            String userPass[] = credential.trim().split(":");
            userAccounts.add(new UserAccount(userPass[0], userPass[1]));
        }
        return userAccounts;
    }

    public static String getRootUrl() {
        String url = System.getProperty(ROOT_URL, "https://www.activityinfo.org");
        while(url.endsWith("/")) {
            url = url.substring(0, url.length()-1);
        }
        return url;
    }
}
