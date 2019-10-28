package lx.com.sourceCode;

import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by 游林夕 on 2019/10/24.
 */
public class TreeMapDemo {

    public static void main(String[]args){
//        TreeMap t = new TreeMap();
//        t.put(1,1);
//        t.put("a",2);
//        System.out.println(t.size());
        Tree t1 = new Tree<>();
        Random r = new Random();
        for (int i=0;i<10;i++){
            t1.put(r.nextInt(20));
        }
    }

    static class Tree<E extends Comparable>{
        private Node root;
        public void put(E e){
            if (root == null){
                this.root = new Node(e,null);
            }else{
                Node c = root;
                int i = 0;
                while (c != null){
                    i = c.e.compareTo(e);
                    if (i==0){
                        return;
                    }else if (i>0){
                        if (c.left == null){
                            Node n = new Node(e,c);
                            c.left = n;
                            return;
                        }
                        c = c.left;
                    }else{
                        if (c.right == null){
                            Node n = new Node(e,c);
                            c.right = n;
                            return;
                        }
                        c = c.right;
                    }
                }
            }
        }

        class Node<E extends Comparable>{
            Node parent;
            Node left;
            Node right;
            boolean color;
            E e;
            public Node(E e,Node parent){
                this.e = e;
                this.parent = parent;
            }
        }
    }
}
