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

import com.zjcpx.utils.ExcelUtil;

/**  
* <p>Title: cfbe</p>  
* <p>Description: </p>  
* @author Mike.Cai  
* @date Mar 5, 2020  
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
	
		// 指定路径
		 Scanner sc = new Scanner(System.in); 
         System.out.println("请输入月度汇总文件的路径："); 
         String summaryDir = sc.nextLine();
         //  /Users/Mike/Desktop/CS/sj/01monthly summary.xls
         
         
         //C:\Users\zjcpx\Desktop\CS\sj
         //  /Users/Mike/Desktop/CS/sj/01monthly summary.xls
         System.out.println("请输入商检文件的路径："); 
         String sJDir = sc.nextLine();
         //处理输入路径最后的的“/”问题
         String separatorChar = File.separator;
         if (sJDir.endsWith(separatorChar)) {
			sJDir = sJDir.substring(0,sJDir.lastIndexOf(separatorChar));
		}
         
         //获取商检文件夹中的所有文件：files
         List<File> files = getFiles(sJDir);
         //查看检索到的文件 
         for(int i = 0,len = files.size(); i < len; i++) {
        	 System.out.println(files.get(i));
         }
         System.out.println("请输入结果存放的路径："); 
         String resultDir = sc.nextLine(); 
     //  /Users/Mike/Desktop/CS/sj/
         File f= new File(summaryDir) ;    // 声明File对象
//         ArrayList<List<String>> resultList = new ArrayList<List<String>>();
         InputStream in = null;
		try {
			in = new FileInputStream(f);
			List<List<Object>> bankListByExcel = ExcelUtil.getBankListByExcel(in, summaryDir);
			for(int i = 1,n = bankListByExcel.size(); i < n ; i++) {
//				for(int j = 0,len=bankListByExcel.get(i).size();j<len; j++) {
//					System.out.println("bankListByExcel("+i+","+j+")="+bankListByExcel.get(i).get(j));
//				}		
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
					System.out.println(file.mkdirs());
				}
				if (!copyFile((sJDir+File.separator+customsBillNo+".pdf"),file.toString()+File.separator+customsBillNo+".pdf")) {
					message += customsBillNo+".PDF没有找到;\t";
				}
//				if(!copyFile(fileList,(customsBillNo+".pdf"),file)) {
////					System.out.println(customsBillNo+".PDF没有找到");
//					message += customsBillNo+".PDF没有找到;\t";
//				};
//				if(!copyFile(fileList,(invoiceNo+".pdf"),file)) {
////					System.out.println(invoiceNo+".PDF没有找到");
//					message += invoiceNo+".PDF没有找到\t";
//					
//				};
//				if(!copyFile(fileList,(invoiceNo+"-1.PDF"),file)) {
////					System.out.println(invoiceNo+"-1.PDF没有找到\t");
//					message += invoiceNo+"-1.PDF没有找到\t";
//				};
//				if(!copyFile(fileList,(invoiceNo+"-5.PDF"),file)) {
////					System.out.println(invoiceNo+"-5.PDF没有找到");
//					message += invoiceNo+"-5.PDF"+".PDF没有找到\r\n\r\n;";
//				};
			}
			System.out.println(message);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//Users/Mike/Desktop/CS/sj/01monthly summary.xls
			e.printStackTrace();
		}
		
		
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
	 * <p>Description: 根据参数file，遍历文件列表，匹配到，将文件拷贝到指定文件夹，返回TRUE，没匹配到返回false</p>  
	* @author Mike.Cai  
	* @date Mar 6, 2020  
	 * @param files：文件列表
	 * @param fileName：需要比对的文件名
	 * @param dest：指定的文件夹
	 */
	
//	public static Boolean copyFile(File[]files,String fileName,File dest) {
//		//验证传参
////		System.out.println("fileName is :"+fileName);
////		System.out.println("dest is :"+dest);
//		//遍历文件数组中的所有文件
//		for(int i = 0,n = files.length; i < n; i++) {
//			String file = files[i].toString();
////			System.out.println(fileName);
//			String fileNameNow = file.substring(file.lastIndexOf(File.separator)+1);
////			System.out.println(fileNameNow);
//			//
//			if (fileName.equals(fileNameNow)) {
//				try {
//					FileUtils.copyFile(files[i], dest);
//				}
//				catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				return true;
//				}
//		}
//		return false;
//	}
	/**
	 * 
	 * <p>Title: copyFile</p>  
	 * <p>Description: 复制文件</p>  
	* @author Mike.Cai  
	* @date Mar 9, 2020  
	 * @param oldPath
	 * @param newPath
	 */
	public static Boolean copyFile(String oldPath, String newPath) { 
		System.out.println("oldPath is " + oldPath+"; newPath is "+newPath);
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File("/Users/Mike/Desktop/CS/sj/222920200000015086.pdf"); 
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件 
				FileOutputStream fs = new FileOutputStream(newPath); 
				byte[] buffer = new byte[1444]; 
				int length; 
				while ( (byteread = inStream.read(buffer)) != -1) { 
					bytesum += byteread; //字节数 文件大小 
					System.out.println(bytesum); 
					fs.write(buffer, 0, byteread); 
				} 
				inStream.close(); 
			} 
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
	public static List<File> getFiles(String path) {
		List<File> fileList = new ArrayList<File>();
		File root = new File(path);
		File[] files = root.listFiles();
		for (File file : files) {
			if(file.isDirectory()) {
				getFiles(file.getAbsolutePath());
			}
			fileList.add(file);
		}
		return fileList;
	}
}
