package lx.com.pdf;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import com.lx.entity.Var;
import com.lx.util.LX;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by 游林夕 on 2019/9/15.
 */
public class TextToVoiceUtil {
    public static void main(String[]args) throws Exception {
        TextToVoiceUtil.getInstance("阿宝吃出菠菜虫",new Var("{spd=4,pit=6,per=111}"));

    }
    //设置APPID/AK/SK
    public static final String APP_ID = "17250121";
    public static final String API_KEY = "17bCraZ9nrV1hjCWv8b6gaM7";
    public static final String SECRET_KEY = "z4qQCRyZj3r51n6SgwLWwjwiHdaNCkmk";

    public static final String FILE_DIR = "D:\\testVoice\\";   //文件路径，待修改。
    private static HashMap<String, Object> options=new Var("{spd=4,pit=6,per=111}");

    static AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
    private static final TextToVoiceUtil single = new TextToVoiceUtil();
    //静态工厂方法
    //语速，取值0-9，默认为5中语速
    //音调，取值0-9，默认为5中语调
    //发音人选择, 0为女声，1为男声，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女 度博文=106，度小童=110，度小萌=111，度米朵=103，度小娇=5
    public static TextToVoiceUtil getInstance(String text,Var op) throws Exception {
        text= text.replace("\n", ";");
        options = op;
        //根据每500个字就划分出一个文件
        int singlePage = 1000;
        int articleLength = text.length();
        final int number =articleLength%singlePage>0?articleLength/singlePage+1:articleLength/singlePage;  //记得这里是从1开始
        final String[] singleArticle = new String[number]; //生成指定个数的String数组
        String fileName = LX.getDate("yyyyMMddHHmmss");
        //小注释： 所有文件会被切割成number-1个
        File endFile =new File(FILE_DIR+fileName+"_end.mp3"); //最终文件
        File fileParent = endFile.getParentFile();
        if (fileParent != null &&!fileParent.exists())fileParent.mkdirs();
        FileOutputStream out = new FileOutputStream(endFile);
        System.out.println("number:"+number);
        for (int i = 0; i < number; i++) {   //先不生成音频文件。先分组利用双线程来提高速度
            if (i == number - 1) { // 是否是最后一页
                singleArticle[i] = text.substring(i * singlePage, articleLength);
            } else {
                singleArticle[i] = text.substring(i * singlePage, i * singlePage + singlePage);
            }
        }
        Callable<byte[]> callable1 = null;
        Callable<byte[]> callable2 = null;
        if (number > 1) { // 分段多开线程处理
            callable1 = new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    System.out.println("线程1启动成功");
                    byte[] byteDemo = new byte[1024];
                    for (int i = 0; i < number / 2; i++) {
                        byteDemo = unitByteArray(byteDemo,createFile(singleArticle[i]));// 获取分块的byte数据
                        System.out.println("执行第" + i + "段的线程");
                    }
                    return byteDemo;
                }
            };
            callable2 = new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    System.out.println("线程2启动成功");
                    byte[] byteDemo = new byte[1024];
                    for (int i = number / 2; i < number; i++) {
                        byteDemo = unitByteArray(byteDemo,createFile(singleArticle[i])); // 获取分块的byte数据
                        System.out.println("执行第" + i + "段的线程");
                    }
                    return byteDemo;
                }
            };
            FutureTask<byte[]> future1 = new FutureTask<byte[]>(callable1);
            new Thread(future1).start();
            FutureTask<byte[]> future2 = new FutureTask<byte[]>(callable2);
            new Thread(future2).start();
            out.write(future1.get());
            out.write(future2.get());
        } else {
            byte[] byteDemo = createFile(text);
            out.write(byteDemo);
        }

        out.close();
        return single;
    }

    /*
     * 创建文字转语音的文件————>mp3格式
     * text:文本内容
     */
    public static byte[] createFile(String text){
        //分块生成录音文件
        TtsResponse res = client.synthesis(text, "zh", 1, options);
        byte[] data = res.getData();
        if (data != null) {
            return data;
        } else{
            return null;
        }

    }
    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }
}
