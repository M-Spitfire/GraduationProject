package util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.tomcat.util.net.URL;

public class JDBCutils {
	private static String url;
	private static String user;
	private static String password;
	private static String driver;
	
	//��ȡ�ļ�����ȡ��ֵ
	static {
		
		try {
			Properties properties = new Properties();
			ClassLoader cLoader = JDBCutils.class.getClassLoader();
			java.net.URL res = cLoader.getResource("jdbc.properties");
			String path = res.getPath();
			System.out.println(path);
			
			properties.load(new FileReader(path));
			url = properties.getProperty("url");
			user = properties.getProperty("user");
			password = properties.getProperty("password");
			driver = properties.getProperty("driver");
			//ע������
			Class.forName(driver);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//�������Ӷ���
	public static Connection getConnection() throws SQLException{
		
		return DriverManager.getConnection(url, user, password);
	}
	
	//�ͷ���Դ
	public void close(Statement stmt, Connection conn) {
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}