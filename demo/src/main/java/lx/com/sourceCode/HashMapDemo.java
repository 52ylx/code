package lx.com.sourceCode;

import java.util.*;

/**
 * Created by 游林夕 on 2019/10/23.
 */
public class HashMapDemo {
    static long t=0;
    private static void start(){
        t=System.currentTimeMillis();
    }
    private static void end(){
        System.out.println(System.currentTimeMillis()-t);
        start();
    }
    public static void main(String[]args){
//        TreeMap
//        System.out.println(33&(64-1));
        Var<String,String> var = new Var(4096);
//        HashMap var = new HashMap();
        start();
        for (int i =0;i<1000000;i++){
            var.put("k"+i,i+"");
        }
        end();
        System.out.println(var.get("k10000"));
        end();
    }
    static class Var<K,V>{
        class Entry{
            K k;
            V v;
            public Entry(K k,V v){
                this.k = k;
                this.v = v;
            }
        }

        //用来存储k
        private LinkLS[] arr = new LinkLS[16];
        private int size = 0;
        private static final double yz=3.0/4;

        public Var(){}
        public Var(int size){
            arr = new LinkLS[size];
        }

        public void put(K k,V v){
            int len = k.hashCode()& (arr.length-1);
            if (arr[len] == null){
                arr[len] = new LinkLS<>();
            }
            arr[len].add(new Entry(k,v));
            size++;
            dilatation();

        }
        //扩容
        private void dilatation(){
            if (size*1.0/arr.length>yz){
                LinkLS[] arr1 = arr;
                arr = new LinkLS[arr1.length*2];
                for (LinkLS<Entry> ls : arr1){
                    if (ls == null) continue;
                    for (Entry e : ls){
                        int len = e.k.hashCode() & (arr.length-1);
                        if (arr[len] == null){
                            arr[len] = new LinkLS<>();
                        }
                        arr[len].add(e);
                    }
                }
            }
        }
        public V get(K k){
            int len = k.hashCode()& (arr.length-1);
            if (arr[len] == null){
               return null;
            }
            LinkLS<Entry> ls = arr[len];
            for (Entry e : ls){
                if (e.k.equals(k)){
                    return e.v;
                }
            }
            return null;
        }
        public void remove(K k){

        }
        public int size(){
            return size;
        }
        public boolean containsKey(K k){
            int len = k.hashCode() & (arr.length-1);
            if (arr[len] != null){
                LinkLS<Entry> ls = arr[len];
                for (Entry e : ls){
                    if (e.k == k){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    static class LinkLS<E> implements Iterable<E>{
        public LinkLS(){
        }
        int size = 0;
        Node first;
        Node last;
        public void add(E e){
            Node node = new Node(e);
            if (size == 0) {
                first = node;
                last = node;
            }else{
                node.prev = last;
                last.next = node;
                last = node;
            }
            size ++;
        }

        class IteratorLS implements Iterator<E> {
            public IteratorLS(Node<E> first){
                this.curr = first;
            }
            Node<E> curr;
            @Override
            public boolean hasNext() {
                return curr != null;
            }

            @Override
            public E next() {
                Node<E> c = curr;
                curr = curr.next;
                return c.e;
            }

            @Override
            public void remove() {

            }
        };
        @Override
        public Iterator<E> iterator() {
            return new IteratorLS(first);
        }
    }
    static class Node<E>{
        public Node(E e){
            this.e = e;
        }
        E e;
        Node prev;
        Node next;

    }
}

