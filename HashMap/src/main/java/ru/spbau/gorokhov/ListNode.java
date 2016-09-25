package ru.spbau.gorokhov;

/**
 * Created by wackloner on 12.09.16.
 */
public class ListNode<K, V> {
    private ListNode<K, V> prev;
    private ListNode<K, V> next;

    private K key;
    private V value;

    public ListNode() {

    }

    public ListNode(K key, V value, ListNode prev, ListNode next) {
        this.key = key;
        this.value = value;
        this.prev = prev;
        this.next = next;
        prev.setNext(this);
        next.setPrev(this);
    }

    public void setKey(K key) {
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }

    public ListNode getNext() {
        return next;
    }

    public void setPrev(ListNode prev) {
        this.prev = prev;
    }

    public ListNode getPrev() {
        return prev;
    }

    public void remove() {
        prev.setNext(next);
        next.setPrev(prev);
    }
}