package lx.com.pdf.test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by 游林夕 on 2019/10/10.
 */
public class Demo01 {

    public static void main(String[]args){
        int[] arr = new int[10];
        Random r = new Random();
        for (int i=0;i<arr.length;i++){
            arr[i] = r.nextInt(1000)*(i%2==0?1:-1);
        }
        for (int i :t2(arr)){
            System.out.print(i+",");
        }
    }
    //几种常见的排序算法和分别的复杂度。
    //1.冒泡
    public static int[] t1(int[] ls){
        if (ls == null || ls.length<2) return ls;
        for (int i = 0;i<ls.length-1;i++){
            boolean flag = true;
            for (int j=0;j<ls.length-i-1;j++){
                if (ls[j]>ls[j+1]){
                    int t = ls[j];
                    ls[j]=ls[j+1];
                    ls[j+1]=t;
                    flag = false;
                }
            }
            if (flag){//没有任何交换 直接退出
                break;
            }
        }
        return ls;
    }
    //2.快速 O(NlongN)
    public static int[] t2(int[] ls){
        if (ls == null || ls.length<2) return ls;
        sort(ls,0,ls.length-1);
        return ls;
    }
    private static void sort(int [] arr,int min,int max){
        if(min<0 || max>=arr.length || max<=min) return;//不足两个无需排序
        int point = arr[min];//基准点
        int i = min, j = max,t;
        do{
            while (j>i && arr[j]>=point){
                j--;
            }
            while (j>i && arr[i]<=point){
                i++;
            }
            if (j>i) {
                t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
            }
        }while (j>i);
        arr[min] = arr[j];
        arr[j] = point;
        sort(arr,min,j-1);
        sort(arr,j+1,max);
    }



    //3.桶排序

    //4.插入排序

    //
}
