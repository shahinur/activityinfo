package org.activityinfo.io.load;

public class FileFormatException extends RuntimeException {

    public enum Kind {
        UNKNOWN_FORMAT,
        ERROR
    }

    private final FileSource fileSource;
    private final Kind errorKind;

    public FileFormatException(FileSource fileSource, Throwable cause) {
        super(cause.getMessage(), cause);
        this.fileSource = fileSource;
        this.errorKind = Kind.ERROR;
    }

    public FileFormatException(FileSource fileSource, Kind errorKind) {
        super(fileSource + ": " + errorKind.name());
        this.fileSource = fileSource;
        this.errorKind = errorKind;
    }

    public FileSource getFileSource() {
        return fileSource;
    }

    public Kind getErrorKind() {
        return errorKind;
    }
}
