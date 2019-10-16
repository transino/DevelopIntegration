package net.zhangqiu.service.database.imps.batch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.zhangqiu.service.database.interfaces.ListTextHandler;
import net.zhangqiu.utils.StrUtils;

public class ReadTextDataHandler {
	
	private ListTextHandler textDataHandler;
	
	private String resource;
	
	private String encoding;
	
	private int cacheLine;

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getCacheLine() {
		return cacheLine;
	}

	public void setCacheLine(int cacheLine) {
		this.cacheLine = cacheLine;
	}
	
	public void setTextDataHandler(ListTextHandler textDataHandler) {
		this.textDataHandler = textDataHandler;
	}

	public ListTextHandler getTextDataHandler() {
		return textDataHandler;
	}

	public void handleData() throws Exception{
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try{
			//Resource res = new PathResource(resource);
			fileInputStream = new FileInputStream(resource);
			if(StrUtils.isEmpty(encoding)){
				inputStreamReader = new InputStreamReader(fileInputStream);
			}
			else{
				inputStreamReader = new InputStreamReader(fileInputStream,encoding);
			}
			bufferedReader = new BufferedReader(inputStreamReader);
			
			String data=null; 
			int count=0;
			List<String> dataList = new ArrayList<String>();
            boolean firstLine = true;
            int currentHandle = 0;
            try{
                while((data=bufferedReader.readLine())!=null) {
                    if(firstLine){
                        if(!StrUtils.isEmpty(data)){
                            String utf8Formart = new String(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF },"UTF-8");
                            String unicodeFormart = new String(new byte[] { (byte)0xFF, (byte)0xFE},"UNICODE");
                            if(data.startsWith(utf8Formart)){
                                data = data.substring(utf8Formart.length());
                            }
                            else if(data.startsWith(unicodeFormart)){
                                data = data.substring(unicodeFormart.length());
                            }
                        }
                        firstLine = false;
                    }
                    dataList.add(data);
                    count++;
                    if(count == cacheLine){
                        textDataHandler.handleData(dataList);
                        count=0;
                        dataList = new ArrayList<String>();
                        currentHandle ++;
                    }

                }
                if(count > 0){
                    textDataHandler.handleData(dataList);
                }
            }
            catch (Exception ex){
                if(ex.getMessage().startsWith("行")){
                    String strRow = ex.getMessage().substring(2,ex.getMessage().indexOf(","));
                    int row = Integer.valueOf(strRow);
                    row = currentHandle * cacheLine + row;
                    String message = "行:" + row + ex.getMessage().substring(ex.getMessage().indexOf(","));
                    throw new Exception(message);
                }
                else{
                    throw  ex;
                }
            }

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
}
