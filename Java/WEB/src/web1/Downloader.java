package web1;

import java.io.File;
import java.io.FileOutputStream;


public class Downloader {
	//待写入的内容
    String sourceString = "%QUARTUS_ROOTDIR%\\\\bin\\\\quartus_pgm.exe -m jtag -c USB-Blaster[USB-0] -o \"p;";
    String path = "D:/uploaded";
    //创建的下载器的文件名
    String fileName = "download.bat";
    public void createFile(String Name) {	//被下载文件的文件名
    	sourceString = sourceString + Name + '"'; 
    	byte[] sourceBinary = sourceString.getBytes();
    	
        try {
    		if(sourceBinary != null) {
    			System.out.println("文件内容为空");
        		File batFile = new File(path, fileName);
        		File file = new File("D:\\uploaded\\download.bat");
        		if(file.exists()) {
        			file.delete();
        		}
        		if(!batFile.exists()) {
        			File dir = new File(batFile.getParent());
        			dir.mkdirs();
        			batFile.createNewFile();
        		}
        		FileOutputStream oStream = new FileOutputStream(batFile);
        		System.out.println("正在写入……");
        		oStream.write(sourceBinary);
        		System.out.println("下载器配置完成！");
        		oStream.flush();
        		oStream.close();
        	}
    	} catch (Exception e) {
    		// TODO: handle exception
    		e.printStackTrace();
    	}
    }
}
