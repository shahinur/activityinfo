package org.activityinfo.io.load;

import com.google.common.io.ByteSource;
import com.google.common.primitives.UnsignedBytes;

import java.io.IOException;

public class FileSource {
    private byte[] head;
    private String filename;
    private ByteSource content;


    public FileSource(String filename, ByteSource content) throws IOException {
        this.filename = filename;
        this.content = content;
        head = content.slice(0, 100).read();
    }

    public boolean headMatches(int... bytes) {
        for(int i=0;i!=bytes.length;++i) {
            if(i >= head.length) {
                return false;
            }
            if(head[i] != bytes[i]) {
                return false;
            }
        }
        return true;
    }

    public String getFilename() {
        return filename;
    }

    public ByteSource getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "FileSource{filename=" +filename + ", head = " + magicBytes() + "}";
    }

    private String magicBytes() {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<Math.min(head.length, 10);++i) {
            if(i > 0) {
                sb.append(" ");
            }
            String s = UnsignedBytes.toString(head[i], 16);
            if(s.length() == 1) {
                sb.append('0');
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
