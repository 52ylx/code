package lx.com.pdf;

import com.lx.util.LX;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.UnsupportedEncodingException;

/**
 * Created by 游林夕 on 2019/9/17.
 */
public class Test {
    public static void main(String[] args) {
        byte[] by = new byte[2];
//        RedisUtil re = new RedisUtil();
        for (int b1 = 176; b1 < 248; b1++) {
            by[0] = (byte) b1;
            for (int b2 = 161; b2 < 255; b2++) {
                by[1] = (byte) b2;
                String str = "";
                try {
                    str = new String(by, "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println(str);
//                if (!re.getRedis().exists(("py:"+str).getBytes())&&!"�".equals(str)){
//                    LX.sleep(500);
//                    byte[]a = TextToVoiceUtil.createFile(str);
//                    re.getRedis().set(("py:"+str).getBytes(),a);
//                }
            }
            System.out.println();
        }
    }
}
