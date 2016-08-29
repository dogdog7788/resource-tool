package com.sr178.tool.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 作者: HuHua
 * @version 2016年8月17日
 * 类说明   	把文件夹下面的文件中的中文全部替换成stuts2国际化能识别的标签
 */
public class WebFileReplace {
	static String regEx = "[\u4e00-\u9fa5]";
	static Pattern pat = Pattern.compile(regEx);

	public static void main(String[] args) {
		File key = new File("E:/job/out/messageResource_zh_CN.properties");//中文需要替换的标签格式(此处对于struts2国际化处理的；eg:<s:text name='login'/>=登 录)
		String filePath = "C:\\Users\\Administrator\\Desktop\\job\\temp";//需要处理的项目（项目里面的所有文件将会被处理）
		File outPath = new File("e:\\output"); //随便给一个输出文件夹的路径(不存在也可以)
		readFolder(filePath,outPath,key);
	}
	
	/**
	 * 读取文件夹
	 * @param filePath  工程目录
	 * 
	 * @param outPath	处理后的输出目录
	 * 
	 * @param key		特定格式
	 * 
	 * @return 
	 */
	public static void readFolder(String filePath,File outPath,File key){
		try {
			//读取指定文件夹下的所有文件
			File file = new File(filePath);
			if (!file.isDirectory()) {
				System.out.println("---------- 该文件不是一个目录文件 ----------");
			} else if (file.isDirectory()) {
				System.out.println("---------- 这是一个目录文件夹 ----------");
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filePath + "\\" + filelist[i]);
					String path = readfile.getPath();//文件路径
					System.out.println("文件路径path="+path);
					String absolutepath = readfile.getAbsolutePath();//文件的绝对路径
					System.out.println("读到的文件名absolutepath="+absolutepath);
					String filename = readfile.getName();//读到的文件名
					System.out.println("读到的文件名filename="+filename);
					if(readfile.isDirectory()){//如果是目录，递归
						readFolder(readfile.toString(),new File(outPath+"\\"+filelist[i]),key);
					}else{
						readFile(absolutepath,filename,i,outPath,key);//调用 readFile 方法读取文件夹下所有文件
					}
				}
				System.out.println("---------- 所有文件操作完毕 ----------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 读取文件夹下的文件
	 * @return 
	 */
	public static void readFile(String absolutepath,String filename,int index,File outPath,File key){
		try{
			File recourceFile = key;
			BufferedReader recourceFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(recourceFile),"UTF-8"));
			
			StringBuffer strBuffer = new StringBuffer();
			String filetemp = null; 
			List filetemplist = new ArrayList<Object>();
			List templist = new ArrayList<Object>();
			while ((filetemp = recourceFileReader.readLine()) != null) {
				filetemplist.add(filetemp);
			}
			String interTag = "";//国际化标签
			String chinese = "";//国际化前的中文
			String source = "";//源行
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(absolutepath),"UTF-8"));//数据流读取文件(文件夹所有文件)
			for (String temp = null; (temp = bufReader.readLine()) != null; temp = null) {
				for (int i = 0; i < filetemplist.size(); i++) {
					if(filetemplist.get(i)!=null && filetemplist.get(i).toString().length()>3){
						chinese = filetemplist.get(i).toString().substring( filetemplist.get(i).toString().lastIndexOf("/>=")+3, filetemplist.get(i).toString().length());
						interTag = filetemplist.get(i).toString().substring(0, filetemplist.get(i).toString().indexOf("/>=")+2);
						source = temp;
						if(temp.contains(chinese)){ //判断当前行是否存在想要替换掉的中文字符块
							temp = partemReplace(chinese,interTag,temp);//把相关内容改成特定的字符串信息
							if(isChineseChar(temp)){//判断是否完全把中文字符块给替换完，如果没有替换完，把此行还原，留着以后处理（人工处理，或者下一个执行点处理）
								System.out.println("isChineseChar><temp="+temp);
								temp = source;
								System.out.println("sourceChar><temp="+temp);
							}
						}
					}
				}
				strBuffer.append(temp);
				strBuffer.append(System.getProperty("line.separator"));//行与行之间的分割
			}
			bufReader.close();
			if(!outPath.exists()){ //检查输出文件夹是否存在，若不存在先创建
				outPath.mkdirs();
				System.out.println("-------------------------------已成功创建输出文件夹：" + outPath);
			}
			PrintWriter printWriter = new PrintWriter(outPath+"\\"+filename);//替换后输出的文件位置
			printWriter.write(strBuffer.toString().toCharArray());
			printWriter.flush();
			printWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 是否含有中文字符
	 * 
	 * */
	public static boolean isChineseChar(String str){
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find()){
			flg = true;
		}
		return flg;
	}
	
	/**
	 * 
	 * 把某行的中文替换成特定标签
	 * 
	 * @param chinese 中文字符块
	 * 
	 * @param international 国际化标签
	 * 
	 * @param source 数据源
	 * 
	 * */
	public static String partemReplace(String chinese,String international,String source){
		//被替换关键字的的数据源
	    Map<String,String> tokens = new HashMap<String,String>();
	    tokens.put(chinese, international);
	    //生成匹配模式的正则表达式
	    String patternString = "(" + StringUtils.join(tokens.keySet(), "|") + ")";
	    Pattern pattern = Pattern.compile(patternString);
	    Matcher matcher = pattern.matcher(source);
	 
	    //两个方法：appendReplacement, appendTail
	    StringBuffer sb = new StringBuffer();
	    while(matcher.find()) {
	        matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
	    }
	    matcher.appendTail(sb);
	    return sb.toString();
	}
	
	
}
