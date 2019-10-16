package net.zhangqiu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	public static void saveFile(String path,String script,String encoding) throws Exception{
		
		File parentFile = new File(path).getParentFile();
		if(!parentFile.exists()){
			parentFile.mkdirs();
		}
		
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter  = null;
		BufferedWriter bufferedWriter=null;
		try{
			fileOutputStream = new FileOutputStream(path);
			if(encoding == null){
				outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			}
			else{
				outputStreamWriter = new OutputStreamWriter(fileOutputStream,encoding);
			}
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			bufferedWriter.write(script);
		}
		catch(Exception ex){
            logger.error("",ex);
			throw ex;
		}
		finally{
			if(bufferedWriter != null){
				bufferedWriter.close();
			}
			if(outputStreamWriter != null){
				outputStreamWriter.close();
			}
			if(fileOutputStream != null){
				fileOutputStream.close();
			}
		}
	}
	
	public static String readFile(String path,String encoding) throws Exception{
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try{
			fileInputStream = new FileInputStream(path);
			if(encoding == null){
				inputStreamReader = new InputStreamReader(fileInputStream);
			}
			else{
				inputStreamReader = new InputStreamReader(fileInputStream,encoding);
			}
			bufferedReader = new BufferedReader(inputStreamReader);
			
			StringBuilder stringBuilder = new StringBuilder();
			String line=null; 
			boolean firstLine = true;
			while((line=bufferedReader.readLine())!=null) { 
				if(!firstLine){
					stringBuilder.append(System.getProperty("line.separator"));
				}
				else{
					String utf8Formart = new String(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF },"UTF-8");
					String unicodeFormart = new String(new byte[] { (byte)0xFF, (byte)0xFE},"UNICODE");
					if(line.startsWith(utf8Formart)){
						line = line.substring(utf8Formart.length());
					}
					else if(line.startsWith(unicodeFormart)){
						line = line.substring(unicodeFormart.length());
					}
					firstLine = false;
				}
				stringBuilder.append(line);
			}
			return stringBuilder.toString();
		}
		catch(Exception ex){
            logger.error("",ex);
			throw ex;
		}
		finally{
			if(bufferedReader != null){
				bufferedReader.close();
			}
			if(inputStreamReader != null){
				inputStreamReader.close();
			}
			if(fileInputStream != null){
				fileInputStream.close();
			}
		}
	}
	
	public static List<String> readLineFile(String path,String encoding) throws Exception{
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		
		try{
			fileInputStream = new FileInputStream(path);
			if(encoding == null){
				inputStreamReader = new InputStreamReader(fileInputStream);
			}
			else{
				inputStreamReader = new InputStreamReader(fileInputStream,encoding);
			}
			bufferedReader = new BufferedReader(inputStreamReader);
			
			List<String> lineList = new ArrayList<String>();
			String line=null; 
			boolean firstLine = true;
			while((line=bufferedReader.readLine())!=null) {
				if(firstLine){
					if(!StrUtils.isEmpty(line)){
						String utf8Formart = new String(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF },"UTF-8");
						String unicodeFormart = new String(new byte[] { (byte)0xFF, (byte)0xFE},"UNICODE");
						if(line.startsWith(utf8Formart)){
							line = line.substring(utf8Formart.length());
						}
						else if(line.startsWith(unicodeFormart)){
							line = line.substring(unicodeFormart.length());
						}
					}
					firstLine = false;
				}
				if(!StrUtils.isEmpty(line)){
					lineList.add(line);
				}
			}
			return lineList;
		}
		catch(Exception ex){
            logger.error("",ex);
			throw ex;
		}
		finally{
			if(bufferedReader != null){
				bufferedReader.close();
			}
			if(inputStreamReader != null){
				inputStreamReader.close();
			}
			if(fileInputStream != null){
				fileInputStream.close();
			}
		}
	}

	public static String readZipFile(String fileName,byte[] fileByte,String encoding) throws Exception{
		ZipInputStream zipInputStream = null;
		InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
		try {			  
			zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileByte)); 
			if(StrUtils.isEmpty(encoding)){
				inputStreamReader = new InputStreamReader(zipInputStream);
			}
			else{
				inputStreamReader = new InputStreamReader(zipInputStream,encoding);
			}
            bufferedReader = new BufferedReader(inputStreamReader);
			ZipEntry zipEntry=zipInputStream.getNextEntry();	
			String result= null;
			while(zipEntry != null){
				if(!zipEntry.isDirectory()){
					if(zipEntry.getName().equals(fileName)){
					    /*
						final int bufferSize = 1024;
						final char[] buffer = new char[bufferSize];
						final StringBuilder stringBuilder = new StringBuilder();
						for (;;) {
						    int rsz = inputStreamReader.read(buffer, 0, buffer.length);
						    if (rsz < 0){
						    	break;
						    } 
						    stringBuilder.append(buffer, 0, rsz);
						}
						result = stringBuilder.toString();
						break;
                        */
                        StringBuilder stringBuilder = new StringBuilder();
                        String line=null;
                        boolean firstLine = true;
                        while((line=bufferedReader.readLine())!=null) {
                            if(!firstLine){
                                stringBuilder.append(System.getProperty("line.separator"));
                            }
                            else{
                                String utf8Formart = new String(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF },"UTF-8");
                                String unicodeFormart = new String(new byte[] { (byte)0xFF, (byte)0xFE},"UNICODE");
                                if(line.startsWith(utf8Formart)){
                                    line = line.substring(utf8Formart.length());
                                }
                                else if(line.startsWith(unicodeFormart)){
                                    line = line.substring(unicodeFormart.length());
                                }
                                firstLine = false;
                            }
                            stringBuilder.append(line);
                        }
                        result = stringBuilder.toString();
                        break;
					}
				}
				zipEntry=zipInputStream.getNextEntry();
			}
			return result;
		} 
		catch (Exception ex) {
            logger.error("",ex);
			throw ex;
		}
		finally{
		    if(bufferedReader != null){
                bufferedReader.close();
            }
			if(inputStreamReader != null){
				inputStreamReader.close();
			}
			if(zipInputStream != null){
				zipInputStream.close();
			}
		}
	}
	
	public static Map<String,String> readListZipFile(String fileName,byte[] fileByte,String encoding) throws Exception{
		ZipInputStream zipInputStream = null;
		InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
		try {			  
			zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileByte)); 
			if(StrUtils.isEmpty(encoding)){
				inputStreamReader = new InputStreamReader(zipInputStream);
			}
			else{
				inputStreamReader = new InputStreamReader(zipInputStream,encoding);
			}
            bufferedReader = new BufferedReader(inputStreamReader);
			ZipEntry zipEntry=zipInputStream.getNextEntry();	
			Map<String,String> resultMap = new HashMap<String,String>();
			while(zipEntry != null){
				if(!zipEntry.isDirectory()){
					if(zipEntry.getName().endsWith(fileName)){
					    /*
						final int bufferSize = 1024;
						final char[] buffer = new char[bufferSize];
						final StringBuilder stringBuilder = new StringBuilder();
						for (;;) {
						    int rsz = inputStreamReader.read(buffer, 0, buffer.length);
						    if (rsz < 0){
						    	break;
						    } 
						    stringBuilder.append(buffer, 0, rsz);
						}
						resultMap.put(zipEntry.getName(), stringBuilder.toString());
						*/
                        StringBuilder stringBuilder = new StringBuilder();
                        String line=null;
                        boolean firstLine = true;
                        while((line=bufferedReader.readLine())!=null) {
                            if(!firstLine){
                                stringBuilder.append(System.getProperty("line.separator"));
                            }
                            else{
                                String utf8Formart = new String(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF },"UTF-8");
                                String unicodeFormart = new String(new byte[] { (byte)0xFF, (byte)0xFE},"UNICODE");
                                if(line.startsWith(utf8Formart)){
                                    line = line.substring(utf8Formart.length());
                                }
                                else if(line.startsWith(unicodeFormart)){
                                    line = line.substring(unicodeFormart.length());
                                }
                                firstLine = false;
                            }
                            stringBuilder.append(line);
                        }
                        resultMap.put(zipEntry.getName(), stringBuilder.toString());
					}
				}
				zipEntry=zipInputStream.getNextEntry();
			}
			return resultMap;
		} 
		catch (Exception ex) {
            logger.error("",ex);
			throw ex;
		}
		finally{
		    if(bufferedReader != null){
                bufferedReader.close();
            }
			if(inputStreamReader != null){
				inputStreamReader.close();
			}
			if(zipInputStream != null){
				zipInputStream.close();
			}
		}
	}
	
	/*
	public static byte[] getFileByte(String path) throws Exception{
		FileInputStream fileInputStream = null;
		try{
			 fileInputStream = new FileInputStream (new File(path));
			 int len=fileInputStream.available();
			 byte[] fileByte = new byte[len];
	         fileInputStream.read(fileByte);
	         return fileByte;
		}
		catch(Exception ex){
			            logger.error("",ex);
			throw ex;
		}
		finally{
			if(fileInputStream != null){
				fileInputStream.close();
			}
		}
	}
	
	public static void writeByteFile(String path,byte[] fileByte) throws Exception{
		FileOutputStream fileOutputStream = null;
		try{
			 fileOutputStream = new FileOutputStream (path);
			 fileOutputStream.write(fileByte);
		}
		catch(Exception ex){
			            logger.error("",ex);
			throw ex;
		}
		finally{
			if(fileOutputStream != null){
				fileOutputStream.close();
			}
		}
	}
	*/
}
