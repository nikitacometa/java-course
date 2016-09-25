package ru.spbau.gorokhov;

/**
 * Created by wackloner on 12.09.16.
 */
public class HashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 8;

    private List<K, V>[] buckets;

    private int capacity;
    private int size;

    public HashMap(int initialCapacity) {
        capacity = initialCapacity;
        buckets = new List[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new List<K, V>();
        }
    }

    public HashMap() {
        this(DEFAULT_CAPACITY);
    }

    public V put(K key, V value) {
        ensureCapacity();
        int bucketId = getBucketId(key);
        ListNode<K, V> node = buckets[bucketId].find(key);
        V res = null;
        if (node == null) {
            buckets[bucketId].addFirst(key, value);
            size++;
        } else {
            res = node.getValue();
            node.setValue(value);
        }
        return res;
    }

    private ListNode getNode(K key) {
        return buckets[getBucketId(key)].find(key);
    }

    public V get(K key) {
        ListNode<K, V> node = getNode(key);
        return node == null ? null : node.getValue();
    }

    public boolean contains(K key) {
        ListNode node = getNode(key);
        return node != null;
    }

    public void remove(K key) {
        int bucketId = getBucketId(key);
        buckets[bucketId].remove(key);
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            buckets[i].clear();
        }
        size = 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    private int getBucketId(K key) {
        return key.hashCode() % capacity;
    }

    private void ensureCapacity() {
        if (size == capacity) {
            capacity *= 2;
            List[] oldBuckets = buckets;
            buckets = new List[capacity];
            for (int i = 0; i < capacity; i++) {
                buckets[i] = new List();
            }
            size = 0;
            for (int i = 0; i < capacity / 2; i++) {
                ListNode<K, V> ptr = oldBuckets[i].getHead().getNext();
                while (!ptr.equals(oldBuckets[i].getTail())) {
                    put(ptr.getKey(), ptr.getValue());
                    ptr = ptr.getNext();
                }
            }
        }
    }
}