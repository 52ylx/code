package lx.com.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能 PDF读写类
 * @CreateTime 2011-4-14 下午02:44:11
 */
public class PDFUtil {


    public static void main(String[] args) throws IOException {
        String str = getPdfFileText("C:\\Users\\ylx\\Desktop\\资料\\近现代白皮书.pdf");
//        String [] arr = str.split("\\d\\.");
//        for (String s : arr){
//            System.out.println(s.replace("\n",""));
//        }
        str = str.replace("\n","");
        Pattern pattern = Pattern.compile("\\d+\\.");
        Matcher matcher = pattern.matcher(str);
        Set<String> s = new HashSet<>();
        while (matcher.find()){
            s.add(matcher.group());
        }
        for (String s1 : s){
            str = str.replace(s1,"\n"+s1);
        }
        System.out.println(str);
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