package com.example.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
	
	/**
	 * 随机生成六位数验证码 
	 * @return
	 */
	public static int getRandomNum(){
		 Random r = new Random();
		 return r.nextInt(900000)+100000;//(Math.random()*(999999-100000)+100000)
	}
	
	/**
	 * 检测字符串是否不为空(null,"","null")
	 * @param s
	 * @return 不为空则返回true，否则返回false
	 */
	public static boolean notEmpty(String s){
		return s!=null && !"".equals(s) && !"null".equals(s);
	}
	
	/**
	 * 检测字符串是否为空(null,"","null")
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isEmpty(String s){
		return s==null || "".equals(s) || "null".equals(s);
	}
	/**
	 * 检测PageData字符串是否为空(null,"","null")
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isEmpty(Object s){
		return s==null || isEmpty(s.toString());
	}
	/**
	 * 检测对象是否为空(null,"","null")
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isNullOrEmpty(Object s){
		return s==null || "".equals(s) || "null".equals(s);
	}
	/**
	 * 字符串转换为字符串数组
	 * @param str 字符串
	 * @param splitRegex 分隔符
	 * @return
	 */
	public static String[] str2StrArray(String str,String splitRegex){
		if(isEmpty(str)){
			return null;
		}
		return str.split(splitRegex);
	}
	
	/**
	 * 用默认的分隔符(,)将字符串转换为字符串数组
	 * @param str	字符串
	 * @return
	 */
	public static String[] str2StrArray(String str){
		return str2StrArray(str,",\\s*");
	}
	
	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String date2Str(Date date){
		return date2Str(date,"yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
	 * @param date
	 * @return
	 */
	public static Date str2Date(String date){
		if(notEmpty(date)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date();
		}else{
			return null;
		}
	}
	
	/**
	 * 按照参数format的格式，日期转字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2Str(Date date,String format){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}else{
			return "";
		}
	}
	
	/**
	 * 把时间根据时、分、秒转换为时间段
	 * @param StrDate
	 */
	public static String getTimes(String StrDate){
		String resultTimes = "";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    java.util.Date now;
	    
	    try {
	    	now = new Date();
	    	java.util.Date date=df.parse(StrDate);
	    	long times = now.getTime()-date.getTime();
	    	long day  =  times/(24*60*60*1000);
	    	long hour = (times/(60*60*1000)-day*24);
	    	long min  = ((times/(60*1000))-day*24*60-hour*60);
	    	long sec  = (times/1000-day*24*60*60-hour*60*60-min*60);
	        
	    	StringBuffer sb = new StringBuffer();
	    	//sb.append("发表于：");
	    	if(hour>0 ){
	    		sb.append(hour+"小时前");
	    	} else if(min>0){
	    		sb.append(min+"分钟前");
	    	} else{
	    		sb.append(sec+"秒前");
	    	}
	    		
	    	resultTimes = sb.toString();
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	    
	    return resultTimes;
	}
	
	/**
	 * 写txt里的单行内容
	 * @param filePath  文件路径
	 * @param content  写入的内容
	 */
	public static void writeFile(String fileP,String content){
		String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";	//项目路径
		filePath = (filePath.trim() + fileP.trim()).substring(6).trim();
		if(filePath.indexOf(":") != 1){
			filePath = File.separator + filePath;
		}
		try {
	        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filePath),"utf-8");      
	        BufferedWriter writer=new BufferedWriter(write);          
	        writer.write(content);      
	        writer.close(); 

	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	  * 验证邮箱
	  * @param email
	  * @return
	  */
	 public static boolean checkEmail(String email){
	  boolean flag = false;
	  try{
	    String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	    Pattern regex = Pattern.compile(check);
	    Matcher matcher = regex.matcher(email);
	    flag = matcher.matches();
	   }catch(Exception e){
	    flag = false;
	   }
	  return flag;
	 }
	
	 /**
	  * 验证手机号码
	  * @param mobiles
	  * @return
	  */
	 public static boolean checkMobileNumber(String mobileNumber){
	  boolean flag = false;
	  try{
	    Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
	    Matcher matcher = regex.matcher(mobileNumber);
	    flag = matcher.matches();
	   }catch(Exception e){
	    flag = false;
	   }
	  return flag;
	 }
	 
	/**
	 * 读取txt里的单行内容
	 * @param filePath  文件路径
	 */
	public static String readTxtFile(String fileP) {
		try {
			
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";	//项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			if(filePath.indexOf(":") != 1){
				filePath = File.separator + filePath;
			}
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { 		// 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), encoding);	// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					return lineTxt;
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件,查看此路径是否正确:"+filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return "";
	}
	
	/**
	 * 验证密码
	 * @param input
	 * @return
	 */
	public static boolean checkPassword(String input) {
		// 6-32 位，字母、数字、下划线
		String regStr = "^([A-Z]|[a-z]|[0-9]|[_]){6,32}$";
		return input.matches(regStr);
	}
	
	/**
	 * 补齐空位
	 * @param total 总位数
	 * @param phStr 占位字符
	 * @param exists 已有位数
	 * @return
	 */
	public static String placeholder (int total, String phStr, int exists){
		StringBuilder sb = new StringBuilder("");
		for(int i=0;i<total-exists;i++){
			sb.append(phStr);
		}
		return sb.toString();
	}
	
	/**
	 * 序列号生成(挂号序列号, 入库批次号等)
	 * @param prefix 前缀, 如GH,RK
	 * @param needTime 是否需要时间前缀
	 * @param extp 扩展位的总位数
	 * @param phStr 占位字符
	 * @param serNo 序列号
	 * @return
	 */
	public static String SerNOGenerator (String prefix, boolean needTime, 
			int extp, String phStr, int serNo) {
		StringBuilder sb = new StringBuilder("");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String nowString = null;
		int exists = (serNo + "").length();
		if (needTime) {
			nowString = sdf.format(new Date());
		} else {
			nowString = sdf.format(new Date()).substring(0, 8);
		}
		if (prefix!=null) {
			sb.append(prefix);
		}
		sb.append(nowString);
		sb.append(placeholder(extp, phStr, exists));
		sb.append(serNo);
		return sb.toString();
	}
	
	/**
	 * 根据身份证号确定籍贯
	 */
	public static String getNativePlace(String idCardNo) {
		StringBuilder sBuilder = new StringBuilder("");
		Map<String, String> placeCodeMap = new HashMap<String, String>();
		placeCodeMap.put("11", "北京");
		placeCodeMap.put("12", "天津");
		placeCodeMap.put("13", "河北");
		placeCodeMap.put("14", "山西");
		placeCodeMap.put("15", "内蒙古");
		placeCodeMap.put("21", "辽宁");
		placeCodeMap.put("22", "吉林");
		placeCodeMap.put("23", "黑龙江");
		placeCodeMap.put("31", "上海");
		placeCodeMap.put("32", "江苏");
		placeCodeMap.put("33", "浙江");
		placeCodeMap.put("34", "安徽");
		placeCodeMap.put("35", "福建");
		placeCodeMap.put("36", "江西");
		placeCodeMap.put("37", "山东");
		placeCodeMap.put("41", "河南");
		placeCodeMap.put("42", "湖北");
		placeCodeMap.put("43", "湖南");
		placeCodeMap.put("44", "广东");
		placeCodeMap.put("45", "广西");
		placeCodeMap.put("46", "海南");
		placeCodeMap.put("50", "重庆");
		placeCodeMap.put("51", "四川");
		placeCodeMap.put("52", "贵州");
		placeCodeMap.put("53", "云南");
		placeCodeMap.put("54", "西藏");
		placeCodeMap.put("61", "陕西");
		placeCodeMap.put("62", "甘肃");
		placeCodeMap.put("63", "青海");
		placeCodeMap.put("64", "宁夏");
		placeCodeMap.put("65", "新疆");
		placeCodeMap.put("71", "台湾");
		placeCodeMap.put("81", "香港");
		placeCodeMap.put("82", "澳门");
		placeCodeMap.put("91", "国外");
		String placeCode = idCardNo.substring(0, 2);
		for (Map.Entry<String, String> entry : placeCodeMap.entrySet()) {  
			if (placeCode.equals(entry.getKey())) {
				sBuilder.append(entry.getValue());
				break;
			}
		}  
		return sBuilder.toString();
	}
	
	/**
	 * 15位身份证号码转18位
	 * @param idno15
	 * @throws Exception 
	 */
	public static String IdCar15to18(String idno15) throws Exception {
		if (idno15 == null) {
			throw new Exception("您输入的身份证号码为空, 请重新输入");
		}
		if (idno15.length() != 15&&idno15.length() != 18) {
			throw new Exception("您输入的身份证号码位数有误");
		}
		idno15 = idno15.trim();
		StringBuffer idCard18 = new StringBuffer(idno15);
		// 加权因子
		// 校验码值
		char[] checkBit = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		int sum = 0;
		if (idno15.length() == 15) {
			idCard18.insert(6, "19");
			for (int index = 0; index < idCard18.length(); index++) {
				char c = idCard18.charAt(index);
				int ai = Integer.parseInt(new Character(c).toString());
				// 加权因子的算法
				int Wi = ((int) Math.pow(2, idCard18.length() - index)) % 11;
				sum = sum + ai * Wi;
			}
			int indexOfCheckBit = sum % 11; // 取模
			idCard18.append(checkBit[indexOfCheckBit]);
		}
		return idCard18.toString();
	}
	
	/**
	 * 正则表达式验证时间格式
	 * 
	 * @param input
	 * @return
	 */
	public static boolean rexCheckTimeFormat(String input) {
		String regStr = "^([0-1][0-9]|[2][0-3]):[0-5][0-9]:[0-5][0-9]$";
		return input.matches(regStr);
	}
	
	/**
	 * 读取txt里的单行内容
	 * @param filePath  文件路径
	 */
	public static String readTxtFile2(String fileP) {
		String filePath = "";
		try {
			/*
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""))+"../../";	//项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			if(filePath.indexOf(":") != 1){
				filePath = File.separator + filePath;
			}
			*/
			filePath = fileP.trim();
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { 		// 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), encoding);	// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					return lineTxt;
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件,查看此路径是否正确:"+filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return "";
	}
	
	public static void main(String[] args) throws Exception {
	//	System.out.println(IdCar15to18("110105710923582"));
		/*
		int no = 10;
		System.out.println(SerNOGenerator(null, false, 4, "0", no));
		System.out.println(Tools.liangweixiaoshu(11.3865465, 4));*/
		//readTxtFile2("admin/config/YIBAO.txt");
		String fnString="F:/zzz/zzz.txt";
		System.out.println(readTxtFile2(fnString));
		System.out.println(Tools.liangweixiaoshu(0.285, 2));
	}
	/**
	 * 查看是否包含指定的权限。
	 * 
	 * @return
	 */
	public static Boolean checkPowers (String powers, String powerCode) {
		if(powers.indexOf(powerCode) > -1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * compare two strings
	 * 
	 * @return
	 */
	public static boolean compSTR(String s1, String s2) {
		long s1num = Long.parseLong(s1);
		long s2num = Long.parseLong(s2);
		return s1num>=s2num;
	}
	
	/**
	 * 四舍五入保留小数位数
	 * @param innum
	 * @param baoliu
	 * @return
	 */
	public static double liangweixiaoshu(double innum,int baoliu) {
		//BigDecimal b = new BigDecimal(innum);
		BigDecimal b = new BigDecimal(new Double(innum).toString());
		double f1 = b.setScale(baoliu,BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}


}
