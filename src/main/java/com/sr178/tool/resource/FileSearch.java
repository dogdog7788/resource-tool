package com.sr178.tool.resource;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 
 
 
public class FileSearch {
    public static void main(String args[]) throws Exception{
//        Scanner in = new Scanner(System.in);
//        System.out.println("base directory");
//        String directory = in.nextLine();
    	String directory ="E:\\github\\zgyq\\yq-user\\yq_user2.0\\yq-user-app\\src\\main\\webapp";
//    	String directory = "E:\\resource";
        String keywordString = "^[^//*]*[\\u4e00-\\u9fa5]+";
        String preFix = ".jsp,.html";
        String outFileName = "E:\\resource\\message_zh_CN.properties";
//        File outFile = new File(outFileName);
//        outFile.deleteOnExit();
        ExecutorService pool = Executors.newCachedThreadPool();//线程池
        MatchCounter dataArrayList = new MatchCounter(new File(directory), keywordString, pool,preFix);
        Future<ArrayList<String>> resultFuture = pool.submit(dataArrayList);//获取结果
         
        //输出结果
        int i = 0;

        FileOutputStream fos = new FileOutputStream(outFileName, false);
        for (String string : resultFuture.get()) {
            i++;
            fos.write(string.getBytes());
            System.out.print(string);
            if (i%3 == 0) {
                System.out.println();
                fos.write("\r\n".getBytes());
            }
        }
        fos.close();
        pool.shutdown();
        System.exit(0);
    }
}
 
class MatchCounter implements Callable<ArrayList<String>>{
    public MatchCounter(File directoryFile, String keyword, ExecutorService pool,String prefix){
        this.directoryFile = directoryFile;
        this.keyword = keyword;
        this.pool = pool;
        this.preFix = prefix;
    }
    @Override
    public ArrayList<String> call() throws Exception {
        // TODO Auto-generated method stub
        ArrayList<String> dataArrayList = new ArrayList<String>();
        ArrayList<String> temp = new ArrayList<String>();
        try{
            File[] files = directoryFile.listFiles();
            ArrayList<Future<ArrayList<String>>> results = new ArrayList<Future<ArrayList<String>>>();
             
            for(File file : files){
                if(file.isDirectory()){//查找所有文件，加入数组
                    MatchCounter counter = new MatchCounter(file, keyword, pool,preFix);
                    Future<ArrayList<String>> resultFuture = pool.submit(counter);
                    results.add(resultFuture);
                }
                else{//查找文件内容
                    System.out.println(file.getName());
                    if((temp = search(file)) != null)
                        dataArrayList.addAll(temp);
                }
            }
            //统计结果
            for(Future<ArrayList<String>> resultFuture : results){
                try{
                    dataArrayList.addAll(resultFuture.get());
                }
                catch (ExecutionException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return dataArrayList;
    }
     
    public ArrayList<String> search(File file) throws InterruptedException{
        try {
        	
        	if(!isAvalidPrex(file.getName())){
        		return null;
        	}
        	
            ArrayList<String> data = new ArrayList<String>(); 
            BufferedReader inScanner = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            boolean found = false;
            String string;
            int i = 0;
            int j=0;
        	String filePath = file.getPath();
            String[] folder = filePath.split("\\\\");
            String rootPath = "";
            boolean isAdd = false;
            for(String str:folder){
            	if(isAdd){
            		rootPath = rootPath + str;
            	}
            	if(str.equals("webapp")){
            		isAdd = true;
            	}
            }
            while ((string = inScanner.readLine()) != null) {
            	i++;
                //正则表达式匹配，选出行
                Pattern pattern = Pattern.compile(keyword);
                Matcher matcher = pattern.matcher(string);
                if(matcher.find()){
                    found = true;
                    Pattern con = Pattern.compile("[\u4e00-\u9fa5].*[\u4e00-\u9fa5]+");
//                    Pattern con = Pattern.compile("([\u4e00-\u9fa5]+)");
                    //再匹配一次，挑出内容
                    Matcher matcher2 = con.matcher(string);
                    System.out.println(string);
                    while(matcher2.find())
                    {
                    	j++;
                    	String path = file.getPath();
                        //System.out.println(matcher2.group());
                        //System.out.println(matcher2.groupCount());

                    	String str = matcher2.group();
                    	int hashCode = str.hashCode();
                        data.add(rootPath+"."+file.getName()+"."+hashCode);
                        data.add("=");//类型
                        data.add(str);//中文内容
                    }
                }
            }
            inScanner.close();
            return data;
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean isAvalidPrex(String fileName){
    	if(preFix!=null){
    		String[] pres = preFix.split(",");
    		for(String pre:pres){
    			if(fileName.endsWith(pre)){
    				return true;
    			}
    		}
    		return false;
    	}
    	return true;
    }
     
    private File directoryFile;
    private String keyword;
    private ExecutorService pool;
    private String preFix;
    private String outFileName;
}
