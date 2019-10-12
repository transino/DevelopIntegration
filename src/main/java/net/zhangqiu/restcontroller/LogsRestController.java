package net.zhangqiu.restcontroller;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class LogsRestController {

    @Autowired
    EnvironmentContext environmentContext;

    @RequestMapping(value = "/getLogDates")
    public String getLogDates(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("strProjectCode") String strProjectCode) throws Exception {
        List<String> logDateList = new ArrayList<String>();
        File logsFileRoot = new File(environmentContext.getRunLogPath());
        File[] logsFiles = logsFileRoot.listFiles();
        for(File file : logsFiles){
            if(file.isDirectory()){
                logDateList.add(file.getName());
            }
        }
        List<String> sortedLogDateList = new ArrayList<String>();
        int count = 0;
        for(int i = logDateList.size() - 1;i>=0;i--){
            sortedLogDateList.add(logDateList.get(i));
            count ++;
            if(count >= 100){
                break;
            }
        }
        String result = "";
        for(String str : sortedLogDateList){
            result += str + ",";
        }
        if(!StrUtils.isEmpty(result)){
            result = result.substring(0,result.length() - 1);
        }
        return result;
    }

    @RequestMapping("/downloadLog")
    public Object downloadLog (@RequestParam(value="indentification",required = false) String indentification, @RequestParam("strProjectCode") String strProjectCode,@RequestParam("date") String date) throws Exception{

        File logsFileRoot = new File(environmentContext.getRunLogPath() +  File.separator + date);
        File[] logsFiles = logsFileRoot.listFiles();

        String zipPath = environmentContext.getRunLogPath() +  File.separator + date + File.separator + date + "_service_log.zip";
        ZipOutputStream zipOutputStream=new ZipOutputStream(new FileOutputStream(zipPath));
        ZipEntry zipEntry=null;
        final int BUFFER = 1024 ;
        byte[] buf=new byte[BUFFER];
        int readLengh=0;

        for(File file : logsFiles){
            zipEntry = new ZipEntry(file.getName());
            zipEntry.setSize(file.length());
            zipEntry.setTime(file.lastModified());
            zipOutputStream.putNextEntry(zipEntry);
            InputStream inputStream=new BufferedInputStream(new FileInputStream(file));
            while ((readLengh=inputStream.read(buf, 0, BUFFER))!=-1) {
                zipOutputStream.write(buf, 0, readLengh);
            }
            inputStream.close();
        }
        zipOutputStream.close();

        File file = new File(zipPath);
        InputStream inputStream = new FileInputStream(file);
        byte[] body = null;
        body = new byte[inputStream.available()];
        inputStream.read(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + file.getName());
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, statusCode);
        inputStream.close();
        file.delete();

        //MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        //FileSystemResource resource = new FileSystemResource(file);
        //request.add("file", resource);

        /*
        String httpMethod = "";
        RestTemplate restTemplate = new RestTemplate();
        String args = "";

        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("args", args);
        paramMap.add("sign", "");

        File file=new File("D:\\5a658c98Nd0abcb82.jpg");
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();

        ByteArrayResource contentsAsResource = new ByteArrayResource(bytesArray) {
            @Override
            public String getFilename() {
                return "img";
            }
        };
        paramMap.add("img", contentsAsResource);
        */

        return entity;
    }
}
