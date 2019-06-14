package web1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import Interface.Watched;
import Interface.Watcher;




/**
 * @ServerEndpoint ע����һ�����ε�ע�⣬���Ĺ�����Ҫ�ǽ�Ŀǰ���ඨ���һ��websocket��������,
 * ע���ֵ�������ڼ����û����ӵ��ն˷���URL��ַ,�ͻ��˿���ͨ�����URL�����ӵ�WebSocket��������
 */
@ServerEndpoint("/websocket")
public class ChatServer implements Watcher{
	//�ļ���
	private String fileName;
	//�ļ���С
	private double fileSize = 0;
	//�Ѿ����ܵ�������
	private double loaded = 0;
    //��̬������������¼��ǰ������������Ӧ�ð�����Ƴ��̰߳�ȫ�ġ�
    private static int onlineCount = 0;

    //concurrent�����̰߳�ȫSet���������ÿ���ͻ��˶�Ӧ��MyWebSocket������Ҫʵ�ַ�����뵥һ�ͻ���ͨ�ŵĻ�������ʹ��Map����ţ�����Key����Ϊ�û���ʶ
    private static CopyOnWriteArraySet<ChatServer> webSocketSet = new CopyOnWriteArraySet<ChatServer>();

    //��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
    private Session session;
    
    //���ڶ�ȡ��
    private COMReader comReader;
    
    //�Ƿ��ȡ����
    private boolean isReadable = true;
    
    //��ȡ������
    private int experimentData = 0;
    private int i = 0;

    /**
     * ���ӽ����ɹ����õķ���
     * @param session  ��ѡ�Ĳ�����sessionΪ��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //����set��
        addOnlineCount();           //��������1
        System.out.println("�������Ӽ��룡��ǰ��������Ϊ" + getOnlineCount());
        for(ChatServer item:webSocketSet) {
        	try {
    			item.sendMessage("�������Ӽ��룡��ǰ��������Ϊ" + getOnlineCount());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
    }

    /**
     * ���ӹرյ��õķ���
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //��set��ɾ��
        subOnlineCount();           //��������1
        System.out.println("��һ���ӹرգ���ǰ��������Ϊ" + getOnlineCount());
        comReader.closePort();
        for(ChatServer item:webSocketSet) {
        	try {
    			item.sendMessage("��һ���ӹرգ���ǰ��������Ϊ" + getOnlineCount());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
    }

    /**
     * �յ��ͻ�����Ϣ����õķ���
     * @param message �ͻ��˷��͹�������Ϣ
     * @param session ��ѡ�Ĳ���
     */
	@OnMessage 
	public void onMessage(String message, Session session) {
	  if(message.equals("END")) { 
		  System.out.println("�������");
		  try {
			this.sendMessage("�������");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Downloader downloader = new Downloader();
		downloader.createFile(fileName);
		downloadFile();
		comReader = new COMReader("COM3", 9600);
        comReader.Init();
        comReader.addWatcher(this);
        //while(isReadable) {
        	//int temp;
        	//temp = comReader.getData();
        	//if(temp != experimentData) {
        		//experimentData = temp;
        		//System.out.println(experimentData);
        		//try {
    				//this.sendMessage(String.valueOf(experimentData));
    				
    			//} catch (IOException e) {
    				// TODO Auto-generated catch block
    				//e.printStackTrace();
    			//}
        	//}
        //}
        //System.out.println("ѭ������");
	  }
	  else {
		  fileName = "";
		  String[] tempStrings = message.split("\\.");
		  for(int i=0;i<tempStrings.length - 1;i++) {
			  fileName += tempStrings[i];
		  }
		  //�������ڸ�ʽ
		  SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		  // new Date()Ϊ��ȡ��ǰϵͳʱ��
		  fileName = "[student]" + fileName + '_' + df.format(new Date()) + '.' + tempStrings[tempStrings.length - 1];
		  System.out.println("���Կͻ��˵���Ϣ:" + fileName);
		  try { 
			  this.sendMessage(message); 
		  } catch (IOException e) {
			  e.printStackTrace(); 
			  //continue; 
		  }
	  }
	}
	 
    
    @OnMessage
    public void processBinary(byte[] messageData, Session session) {
		// TODO Auto-generated method stub
    	String path = "C:\\Program Files\\Tomcat 7.0";
    	File descFile = new File(path, fileName);
    	FileOutputStream fe = null;
		try {
			fe = new FileOutputStream(descFile,true);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
			fe.write(messageData);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
			fe.flush();
			fe.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	try {
			this.sendMessage("�յ���������");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * ��������ʱ����
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("��������");
        try {
        	comReader.closePort();
			this.sendMessage("���յ�δ֪��Ϣ���޷�����");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        error.printStackTrace();
    }

    /**
     * ������������漸��������һ����û����ע�⣬�Ǹ����Լ���Ҫ��ӵķ�����
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ChatServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ChatServer.onlineCount--;
    }
    
    public void downloadFile() {
    	String path = "D:\\uploaded\\download.bat";
        Runtime run = Runtime.getRuntime();
        try {
            // run.exec("cmd /k shutdown -s -t 3600");
			
			//run.exec("cmd.exe /k start D:");
			//run.exec("cmd.exe /k start cd uploaded");
			//run.exec("cmd.exe /c start cd uploaded");
			 
            run.exec("cmd.exe /C " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void update(int data) {
		// TODO Auto-generated method stub
		try {
			this.sendMessage(String.valueOf(data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
