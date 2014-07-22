package org.activityinfo.test;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * Manages the Local Storage folder for test runs.
 */
public class LocalStorage {
    private File dir;

    /**
     *
     * @return a LocalStorage directory that is completely empty, just
     * as the first time a user synchronizes
     */
    public static LocalStorage thatIsEmpty() {
        return new LocalStorage(Files.createTempDir());
    }

    /**
     *
     * Creates a LocalStorage for a users in a persistent location that can be
     * copied back to the Jenkins master and back to the test node
     *
     * @param user the user's email address
     */
    public static LocalStorage persistedAcrossBuilds(UserAccount user) {
        File localStorageDir = new File(Config.getRequiredProperty(Config.LOCAL_STORAGE_DIR));
        File userRoot = new File(localStorageDir, user.getUsername().replaceAll("[@]", "_"));

        return new LocalStorage(userRoot);
    }

    private LocalStorage(File dir) {
        this.dir = dir;

        if(!dir.exists()) {
            boolean created = dir.mkdirs();
            if(!created) {
                throw new RuntimeException("Could not create " + dir.getAbsolutePath());
            }
        }
    }

    /**
     *
     * @return true if this user has synchronized successfully on a previous build
     */
    public boolean havePreviouslySynchronizedSuccessfully() {
        return syncSuccessFile().exists();
    }

    /**
     * Records a successful synchronization for this users by touching a file
     * in the user's local storage file
     */
    public void recordSuccessfulSynchronization() throws IOException {
        Files.touch(syncSuccessFile());
    }

    private File syncSuccessFile() {
        return new File(dir, "synced");
    }

    public File getDir() {
        return dir;
    }

}
