package web1;

import java.io.File;
import java.io.FileOutputStream;


public class Downloader {
	//��д�������
    String sourceString = "%QUARTUS_ROOTDIR%\\\\bin\\\\quartus_pgm.exe -m jtag -c USB-Blaster[USB-0] -o \"p;";
    String path = "D:/uploaded";
    //���������������ļ���
    String fileName = "download.bat";
    public void createFile(String Name) {	//�������ļ����ļ���
    	sourceString = sourceString + Name + '"'; 
    	byte[] sourceBinary = sourceString.getBytes();
    	
        try {
    		if(sourceBinary != null) {
    			System.out.println("�ļ�����Ϊ��");
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
        		System.out.println("����д�롭��");
        		oStream.write(sourceBinary);
        		System.out.println("������������ɣ�");
        		oStream.flush();
        		oStream.close();
        	}
    	} catch (Exception e) {
    		// TODO: handle exception
    		e.printStackTrace();
    	}
    }
}
