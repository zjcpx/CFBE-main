/**  
* <p>Title: cfbe.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2017</p>  
* <p>Company: www.zjcpx.com</p>  
* @author Mike.Cai 
* @date Mar 5, 2020  
* @version 1.0  
*/  
package com.cfbe.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils.Null;

import com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;
import com.zjcpx.utils.ExcelUtil;

/**  
* <p>Title: cfbe</p>  
* <p>Description: </p>  
* @author Mike.Cai  
* @date Mar 5, 2020  
 * @author zjcpx
 *
 */
public class cfbe {

	/**  
	 * <p>Title: main</p>  
	 * <p>Description: </p>  
	* @author Mike.Cai  
	 * @param <E>
	* @date Mar 5, 2020  
	 * @param args  
	 * @throws Exception 
	 */
	public static <E> void main(String[] args) throws Exception {
		String iKEANo = "";
		String customsBillNo = "";
		String invoiceNo = "";
		String message = "";
		List<File> fileList = new ArrayList<File>();

		// 指定路径 
		//  /Users/Mike/Desktop/CS/sj/01monthly summary.xls
        //C:\Users\zjcpx\Desktop\CS2\sj\1111.xlsx
		//d:\CS2\sj\1111.xlsx
		//D:\CS\sj\01monthly summary副本.xls
        //  /Users/Mike/Desktop/CS/sj/01monthly summary.xls
		 Scanner sc = new Scanner(System.in); 
         System.out.println("请输入月度汇总文件的路径："); 
         String summaryDir = sc.nextLine();
         
         System.out.println("请输入商检文件的路径："); 
         String sJDir = sc.nextLine();
         sJDir = dealFilePath(sJDir);
         //处理输入路径最后的的“/”问题
        
         
         //获取商检文件夹中的所有文件:fileList
         getFiles(sJDir,fileList);

         System.out.println("请输入结果存放的路径："); 
         String resultDir = sc.nextLine(); 
         resultDir = dealFilePath(resultDir);
         
         File f= new File(summaryDir) ; 
         InputStream in = null;
		try {
			in = new FileInputStream(f);
			List<List<Object>> bankListByExcel = ExcelUtil.getBankListByExcel(in, summaryDir);
			for(int i = 0,n = bankListByExcel.size(); i < n ; i++) {
//				
				//IKEANo：IKEA流水号，如果读到的内容为空，则默认为上一次读到的内容
				iKEANo = bankListByExcel.get(i).get(7) == null ? "":(String) bankListByExcel.get(i).get(7);
				//customsBillNo: 报关单号
				customsBillNo = (String) bankListByExcel.get(i).get(3);
				//invoiceNo:发票号,如果读到的内容为空，则默认为上一次读到的内容
				invoiceNo = bankListByExcel.get(i).get(8) == null ? "":(String) bankListByExcel.get(i).get(8);
				
				//根据指定的路径生成结果文件夹
				File file = new File(resultDir+File.separator+"result"+File.separator+iKEANo+ File.separator);
				
				//根据Excel中的内容创建目录
				if(!file.exists()) {
					file.mkdirs();
				}
				//判断文件是否存在
				File fileExist = isFileExist(customsBillNo+".pdf",fileList);
				if(fileExist != null) {
					copyFile(fileExist, file);
					
				}else {
					message += "报关单号为："+customsBillNo+"没找到"; 
				}
				File fileExist1 = isFileExist(invoiceNo+".pdf",fileList);
				if(fileExist1 != null) {
					copyFile(fileExist1, file);
				}else {
					message += "发票号号为："+invoiceNo+".pdf没找到"; 
				}
				File fileExist2 = isFileExist(invoiceNo+".xls",fileList);
				if(fileExist2 != null) {
					copyFile(fileExist2, file);
				}else {
					message += "发票号号为："+invoiceNo+".xls没找到"; 
				}
				File fileExist3 = isFileExist(invoiceNo+"-1.pdf",fileList);
				if(fileExist3 != null) {
					copyFile(fileExist3, file);
				}else {
					message += "发票号号为："+invoiceNo+"-1.pdf没找到"; 
				}
				File fileExist4 = isFileExist(invoiceNo+"-5.pdf",fileList);
				if(fileExist4 != null) {
					copyFile(fileExist4, file);
				}else {
					message += "发票号号为："+invoiceNo+"-5.pdf没找到"; 
				}

			}
			System.out.println(message);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//Users/Mike/Desktop/CS/sj/01monthly summary.xls
			e.printStackTrace();
		}
		
		
	}
	
	public static String dealFilePath(String filePath) {
		final String separatorChar = File.separator;
		if (filePath.endsWith(separatorChar)) {
			filePath = filePath.substring(0,filePath.lastIndexOf(separatorChar));
		}
		return filePath;
	}
	
	/**
	 * 
	 * <p>Title: getFileList</p>  
	 * <p>Description: 获取指定文件夹内的所有文件</p>  
	* @author Mike.Cai  
	* @date Mar 6, 2020  
	 * @param path
	 * @return
	 */
	
	public static File[] getFileList(String path){
		File file = new File(path);
		File[] fs = file.listFiles();
		return fs;
	}
	
	
	/**
	 * 
	 * <p>Title: copyFile</p>  
	 * <p>Description: 复制文件</p>  
	* @author Mike.Cai  
	* @date Mar 9, 2020  
	 * @param oldPath
	 * @param newPath
	 */
	public static Boolean copyFile(File file, File desPath) { 
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			String fileName = file.getName();
			String path = desPath.getAbsolutePath();
			InputStream inStream = new FileInputStream(file); //读入原文件 
			FileOutputStream fs = new FileOutputStream(path+File.separator+fileName); 
			byte[] buffer = new byte[1444]; 
			int length; 
			while ( (byteread = inStream.read(buffer)) != -1) { 
				bytesum += byteread; //字节数 文件大小 
				
				fs.write(buffer, 0, byteread); 
			} 
			inStream.close(); 

			return true;
		} 
		catch (Exception e) { 
			System.out.println("复制单个文件操作出错"); 
			e.printStackTrace();
			return false;
			}
		}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static void getFiles(String path,List<File> fileList) {
		
		File root = new File(path);
		File[] files = root.listFiles();
		for (File file : files) {
			if(file.isDirectory()) {
				getFiles(file.getAbsolutePath(),fileList);
			}
			fileList.add(file);
		}
		
	}
	
	/**
	 * 
	 * @param fileName
	 * @param files
	 * @return
	 */
	public static File isFileExist(String fileName, List<File> fileList) {
		for(int i = 0,len = fileList.size(); i < len; i++) {
			String fileString = fileList.get(i).getAbsolutePath();
			String substring = fileString.substring(fileString.lastIndexOf(File.separator)+1);
//			System.out.println(substring);
			if(substring.equals(fileName)) {
				return fileList.get(i);
			}
		}
		return null;
	}
}
