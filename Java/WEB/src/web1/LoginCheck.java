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
 * @ServerEndpoint ע����һ�����ε�ע�⣬���Ĺ�����Ҫ�ǽ�Ŀǰ���ඨ���һ��websocket��������,
 * ע���ֵ�������ڼ����û����ӵ��ն˷���URL��ַ,�ͻ��˿���ͨ�����URL�����ӵ�WebSocket��������
 */
@ServerEndpoint("/login")
public class LoginCheck{

    //concurrent�����̰߳�ȫSet���������ÿ���ͻ��˶�Ӧ��MyWebSocket������Ҫʵ�ַ�����뵥һ�ͻ���ͨ�ŵĻ�������ʹ��Map����ţ�����Key����Ϊ�û���ʶ
    private static CopyOnWriteArraySet<LoginCheck> webSocketSet = new CopyOnWriteArraySet<LoginCheck>();

    //��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
    private Session session;
    /**
     * ���ӽ����ɹ����õķ���
     * @param session  ��ѡ�Ĳ�����sessionΪ��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);     //����set��
        
    }

    /**
     * ���ӹرյ��õķ���
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //��set��ɾ��
    }

    /**
     * �յ��ͻ�����Ϣ����õķ���
     * @param message �ͻ��˷��͹�������Ϣ
     * @param session ��ѡ�Ĳ���
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
     * ��������ʱ����
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("��������");
        try {
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
    
    public boolean login(String name, String pwd) {
		
		// JDBC �����������ݿ� URL
	    final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	    final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
	 
	    // ���ݿ���û��������룬��Ҫ�����Լ�������
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
            	// ע�� JDBC ����
				Class.forName(JDBC_DRIVER);
				// ������
	            System.out.println("�������ݿ�...");
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
		
		// JDBC �����������ݿ� URL
	    final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	    final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
	 
	    // ���ݿ���û��������룬��Ҫ�����Լ�������
	    final String USER = "root";
	    final String PASS = "password";
		
		Connection conn = null;
		Statement statement = null;
		ResultSet rSet = null;
		
		if(name == ""||pwd =="") {
			return "�û���������Ϊ��";
		}
		else {
			
            try {
            	// ע�� JDBC ����
				Class.forName(JDBC_DRIVER);
				// ������
	            System.out.println("�������ݿ�...");
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
		            return "ע��ɹ�";
	            }
	            else {
	            	return "�û����Ѵ���";
	            }
	            
	            
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
           
		}
		return "ע��ʧ��";
	}
}
