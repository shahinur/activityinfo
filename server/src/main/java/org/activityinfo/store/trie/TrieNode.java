package org.activityinfo.store.trie;

import java.util.Map;

public class TrieNode {

    private byte[] key;
    private byte[] value;
    private Map<Byte, TrieNode> table;

    public TrieNode(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
    }

}
