package lx.com.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.lx.authority.controller.WebContorller;
import com.lx.util.LX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能 PDF读写类
 * @CreateTime 2011-4-14 下午02:44:11
 */
@org.springframework.stereotype.Component
public class PDFUtil {
    @Autowired
    private WebContorller webContorller;

    public static void main(String[]args) throws IOException {
        String name = "近代史";
        String str = getPdfFileText("C:\\Users\\huyis\\Desktop\\资料\\"+name+"白皮书.pdf");
//        System.out.println(str);
        str = str.replace("\n","");
        Pattern pattern = Pattern.compile("\\d+\\.");
        Matcher matcher = pattern.matcher(str);
        List<String> s = new ArrayList();
        while (matcher.find()){
            s.add(matcher.group());
        }
        for (int j =0;j<s.size();j++) {
            int i = str.indexOf(s.get(j));
            System.out.println(str.substring(0, i));
            str = str.substring(i);
        }
        System.out.println(str);
    }
    public static void main1(String[]args) throws IOException {
        String name = "马克思";
        String str = getPdfFileText("C:\\Users\\huyis\\Desktop\\资料\\"+name+"大题海.pdf");
        String str1 = getPdfFileText("C:\\Users\\huyis\\Desktop\\资料\\"+name+"大题海答案.pdf");
        Pattern pattern = Pattern.compile("\\d+\\.");
        Matcher matcher = pattern.matcher(str);
        List<String> s = new ArrayList();
        while (matcher.find()){
            s.add(matcher.group());
        }

        matcher = pattern.matcher(str1);
        List<String> s2 = new ArrayList();
        while (matcher.find()){
            s2.add(matcher.group());
        }

        for (int j =0;j<s.size();j++){
            int i = str.indexOf(s.get(j));
            System.out.println(str.substring(0,i));
            str = str.substring(i);

            i = str1.indexOf(s2.get(j));
            System.out.println(str1.substring(0,i));
            str1 = str1.substring(i);
        }
    }

//    @PostConstruct
    public void main() throws IOException {
        String name = "马克思";
        String str = getPdfFileText("C:\\Users\\huyis\\Desktop\\资料\\"+name+"大题海.pdf");
        Pattern pattern = Pattern.compile("\\d+\\.");
        Matcher matcher = pattern.matcher(str);
        List<String> s = new ArrayList();
        while (matcher.find()){
            s.add(matcher.group());
        }
        for (String s1 : s){
            int i = str.indexOf(s1);
            String line = str.substring(0,i);
            line = line.replace("B","\nB");
            line = line.replace("D","\nD");
            webContorller.ins(LX.toMap("{'题目'='{0}',status=1}",line),name);
            System.out.println(str.substring(0,i));
            str = str.substring(i);
        }
        System.out.println(str);
        webContorller.ins(LX.toMap("{题目:{0}}",str),name);
    }

    public static String getPdfFileText(String fileName) throws IOException {
        PdfReader reader = new PdfReader(fileName);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        StringBuffer buff = new StringBuffer();
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i,
                    new SimpleTextExtractionStrategy());
            buff.append(strategy.getResultantText());
        }
        return buff.toString();
    }
}