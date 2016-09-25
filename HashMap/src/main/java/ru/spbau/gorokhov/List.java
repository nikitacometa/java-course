package ru.spbau.gorokhov;

/**
 * Created by wackloner on 12.09.16.
 */
public class List<K, V> {
    private ListNode head;
    private ListNode tail;

    private int size;

    public List() {
        init();
    }

    public void addFirst(K key, V value) {
        ListNode newNode = new ListNode(key, value, head, head.getNext());
        size++;
    }

    public void addLast(K key, V value) {
        ListNode newNode = new ListNode(key, value, tail.getPrev(), tail);
        size++;
    }

    public ListNode find(K key) {
        ListNode ptr = head.getNext();
        while (!ptr.equals(tail)) {
            if (ptr.getKey().equals(key)) {
                return ptr;
            }
            ptr = ptr.getNext();
        }
        return null;
    }

    public void remove(K key) {
        ListNode node = find(key);
        if (node != null) {
            node.remove();
            size--;
        }
    }

    public void clear() {
        init();
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ListNode getHead() {
        return head;
    }

    public ListNode getTail() {
        return tail;
    }

    private void init() {
        head = new ListNode();
        tail = new ListNode();
        head.setNext(tail);
        tail.setPrev(head);
    }
}