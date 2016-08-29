package com.sr178.tool.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author 作者: HuHua
 * 
 * @version 2016年8月17日
 * 
 * 类说明 		主要用于把某个文件夹下面的包括子文件夹下面的文件内容替换成特定的内容标示（此处针对行操作）
 * 
 * 
 */
public class ResourceReplace {
	
	public static void main(String[] args) {
		String filePath = "E:\\job\\temp"; //读取的文件夹路径
		File outPath = new File("E:\\job\\out"); //随便给一个输出文件夹的路径(不存在也可以)
		readFolder(filePath,outPath);
	}

	/**
	 * 读取文件夹
	 * @return 
	 */
	public static void readFolder(String filePath,File outPath){
		try {
			//读取指定文件夹下的所有文件
			File file = new File(filePath);
			if (!file.isDirectory()) {
				System.err.println("---------- 该文件不是一个目录文件 ----------");
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
					System.out.println("readfile="+readfile);
					if(readfile.isDirectory()){
						readFolder(readfile.toString(),new File(outPath+"\\"+filelist[i]));
					}else{
						replaceProFile(absolutepath,filename,i,outPath);//解析配置文件并修改
					}
				}
				System.out.println("---------- 所有文件操作完毕 ----------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 替换配置文件
	 * @return 
	 */
	public static void replaceProFile(String absolutepath,String filename,int index,File outPath){
		try{
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(absolutepath),"UTF-8"));//数据流读取文件
			StringBuffer strBuffer = new StringBuffer();
			String tihuan = "";
			for (String temp = null; (temp = bufReader.readLine()) != null; temp = null) {
						if(temp.indexOf("=") != -1){ //判断当前行是否存在想要替换掉的字符 -1表示存在(检测配置文件里面是否含有“=”号)
							tihuan = temp.substring(0,temp.indexOf("="));//截取等号以前的字符串
							temp = temp.replace(tihuan,"<s:text name='"+tihuan+"'/>");//把相关内容改成特定的字符串信息
							System.out.println("temp="+temp);
						}
						strBuffer.append(temp);
						strBuffer.append(System.getProperty("line.separator"));//行与行之间的分割
			}
			bufReader.close();
			if(!outPath.exists()){ //检查输出文件夹是否存在，若不存在先创建
				outPath.mkdirs();
				System.out.println("已成功创建输出文件夹：" + outPath);
			}
			PrintWriter printWriter = new PrintWriter(outPath+"\\"+filename);//替换后输出的文件位置
			printWriter.write(strBuffer.toString().toCharArray());
			printWriter.flush();
			printWriter.close();
			System.out.println("第 " + (index+1) +" 个文件   "+ absolutepath +"  已成功输出到    " +outPath+"\\"+filename);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


}
