package lx.com.pdf.test;

import java.io.*;
import java.util.*;

/**
 * Created by 游林夕 on 2019/10/11.
 */
public class Demo {
    public static void main(String[]args){

    }

    public void sort(int [] arr,int min,int max){
        if (max<=min) return;
        if (max-min>2){
            int z = min+(max-min)/2;
            int s = z+1;
            sort(arr,min,z);
            sort(arr,s,max);
            int[] ls = new int[max-min];
            while (s>=max||min>=z){

            }
        }else if (max - min == 2){
            if(arr[min]>arr[max]){
                int t = arr[min];
                arr[min] = arr[max];
                arr[max] = t;
            }
        }

    }




























//    public static void main(String[]args){
//        File file = new File("C:\\Users\\ylx\\Desktop\\第三方对账");
//        String[] list = file.list();
//        double sum = 0;
//        for (String s : list){
//            String line = null;
//            try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()+"/"+s),"gbk"))){
//                while ((line = br.readLine())!=null){
//                    if (line.startsWith("合计")){
//                        System.out.println(s.substring(0,8)+": "+line.split(",")[7]);
//                        sum+= Double.parseDouble(line.split(",")[7]);
//                    }
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println(sum);
//    }
}
