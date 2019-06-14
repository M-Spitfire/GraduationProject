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
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/websocket")
public class ChatServer implements Watcher{
	//文件名
	private String fileName;
	//文件大小
	private double fileSize = 0;
	//已经接受的数据量
	private double loaded = 0;
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<ChatServer> webSocketSet = new CopyOnWriteArraySet<ChatServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    //串口读取器
    private COMReader comReader;
    
    //是否读取数据
    private boolean isReadable = true;
    
    //读取的数据
    private int experimentData = 0;
    private int i = 0;

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        for(ChatServer item:webSocketSet) {
        	try {
    			item.sendMessage("有新连接加入！当前在线人数为" + getOnlineCount());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
        comReader.closePort();
        for(ChatServer item:webSocketSet) {
        	try {
    			item.sendMessage("有一连接关闭！当前在线人数为" + getOnlineCount());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
	@OnMessage 
	public void onMessage(String message, Session session) {
	  if(message.equals("END")) { 
		  System.out.println("接收完成");
		  try {
			this.sendMessage("接收完成");
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
        //System.out.println("循环跳出");
	  }
	  else {
		  fileName = "";
		  String[] tempStrings = message.split("\\.");
		  for(int i=0;i<tempStrings.length - 1;i++) {
			  fileName += tempStrings[i];
		  }
		  //设置日期格式
		  SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		  // new Date()为获取当前系统时间
		  fileName = "[student]" + fileName + '_' + df.format(new Date()) + '.' + tempStrings[tempStrings.length - 1];
		  System.out.println("来自客户端的消息:" + fileName);
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
			this.sendMessage("收到二进制流");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        try {
        	comReader.closePort();
			this.sendMessage("接收到未知信息，无法处理");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
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
