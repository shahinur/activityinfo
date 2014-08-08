package org.activityinfo.service.store;

public class UpdateResult {

    private CommitStatus commitStatus;
    private long newVersion;

    public UpdateResult(CommitStatus commitStatus, long newVersion) {
        this.commitStatus = commitStatus;
        this.newVersion = newVersion;
    }

    public UpdateResult(CommitStatus commitStatus) {
        this.commitStatus = commitStatus;
    }

    public CommitStatus getCommitStatus() {
        return commitStatus;
    }

    public long getNewVersion() {
        if(commitStatus != CommitStatus.COMMITTED) {
            throw new IllegalStateException("Update was not committed");
        }
        return newVersion;
    }

    @Override
    public String toString() {
        if(commitStatus == CommitStatus.COMMITTED) {
            return "<Committed as version " + newVersion + ">";
        } else {
            return "<" + commitStatus.name() + ">";
        }
    }
}
