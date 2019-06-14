package web1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;



/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/login")
public class LoginCheck{

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<LoginCheck> webSocketSet = new CopyOnWriteArraySet<LoginCheck>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //加入set中
        
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //从set中删除
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
	@OnMessage 
	public void onMessage(String message, Session session) {
		String[] tempStrings = message.split("%");
		if(tempStrings[0].equals("login")) {
			boolean flag = this.login(tempStrings[1], tempStrings[2]);
			if(flag) {
				System.out.println("checking...");
				try {
					this.sendMessage("true");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				try {
					this.sendMessage("false");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			String resString = this.register(tempStrings[1], tempStrings[2]);
			try {
				this.sendMessage(resString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    
    public boolean login(String name, String pwd) {
		
		// JDBC 驱动名及数据库 URL
	    final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	    final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
	 
	    // 数据库的用户名与密码，需要根据自己的设置
	    final String USER = "root";
	    final String PASS = "password";
		
		Connection conn = null;
		Statement statement = null;
		ResultSet rSet = null;
		if(name == ""||pwd =="") {
			return false;
		}
		else {
			
            try {
            	// 注册 JDBC 驱动
				Class.forName(JDBC_DRIVER);
				// 打开链接
	            System.out.println("连接数据库...");
	            //conn = DriverManager.getConnection(DB_URL,USER,PASS);
	            conn = DriverManager.getConnection(DB_URL, USER, PASS);
	            statement = conn.createStatement();
	            String sql = "select * from USERS where NAME='"+name+"' and PASSWORD='"+pwd+"'";
	            rSet = statement.executeQuery(sql);
	            return rSet.next();
	            
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
           
		}
		return false;
	}
    
    public String  register(String name, String pwd) {
		
		// JDBC 驱动名及数据库 URL
	    final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	    final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
	 
	    // 数据库的用户名与密码，需要根据自己的设置
	    final String USER = "root";
	    final String PASS = "password";
		
		Connection conn = null;
		Statement statement = null;
		ResultSet rSet = null;
		
		if(name == ""||pwd =="") {
			return "用户名或密码为空";
		}
		else {
			
            try {
            	// 注册 JDBC 驱动
				Class.forName(JDBC_DRIVER);
				// 打开链接
	            System.out.println("连接数据库...");
	            //conn = DriverManager.getConnection(DB_URL,USER,PASS);
	            conn = DriverManager.getConnection(DB_URL, USER, PASS);
	            statement = conn.createStatement();
	            
	            String sql = "select * from USERS where NAME='"+name+"'";
	            System.out.println(sql);
	            rSet = statement.executeQuery(sql);
	            if(!rSet.next()) {
	            	sql = "insert into USERS values('" + name + "\',\'" + pwd + "')";
	            	System.out.println(sql);
		            statement.executeUpdate(sql);
		            return "注册成功";
	            }
	            else {
	            	return "用户名已存在";
	            }
	            
	            
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
           
		}
		return "注册失败";
	}
}
