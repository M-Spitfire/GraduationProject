package web1;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import Interface.Watched;
import Interface.Watcher;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class COMReader implements SerialPortEventListener,Watched{
	
	private static String PortName;
	private static CommPortIdentifier PortID;
	private static SerialPort serialPort;
	private static InputStream iStream;
	
	
	public static int baud = 9600;
	
	public int experimentData = 0;
	
	// 存放观察者
    private List<Watcher> list = new ArrayList<Watcher>();
	
	public COMReader(String com, int baud) {
		COMReader.PortName = com;
		COMReader.baud = baud;
	}
	
	//初始化串口，返回输入流用于实践读取
	public void Init() {
		try {
			PortID = CommPortIdentifier.getPortIdentifier(PortName);
			//打开并命名串口
			serialPort = (SerialPort)PortID.open("JAVA_SERIAL", 2000);
			//注册监听事件
			serialPort.addEventListener(this);
			//有可用数据时触发事件
			serialPort.notifyOnDataAvailable(true);
			iStream = serialPort.getInputStream();
			serialPort.getOutputStream();
			//波特率9600，8位数据位，1位停止位，无奇偶校验
			serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
		} catch (PortInUseException e) {
            System.out.println("该串口正在使用中");
        } catch (NoSuchPortException e) {
            System.out.println("该串口当前不可使用");
        } catch (UnsupportedCommOperationException e) {
            System.out.println("错误的串口参数配置");
        } catch (TooManyListenersException e) {
            System.out.println("该串口已存在监听器");
        } catch (IOException e) {
            System.out.println("输入输出流打开失败");
        }
	}
	
	//关闭串口
	public void closePort() {
		// TODO Auto-generated method stub
		serialPort.close();
	}
	
	//获取数据
	public int getData() {
		return experimentData;
	}
	
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
			System.out.println("break interrupt通讯中断");
		case SerialPortEvent.FE:
			System.out.println("framing error帧错误");
		case SerialPortEvent.RI:
			System.out.println("ring indicator响铃指示");
		case SerialPortEvent.DSR:
			System.out.println("data set ready数据设备就绪");
		case SerialPortEvent.CD:
			System.out.println("carrier detect载波检测");
		case SerialPortEvent.CTS:
			System.out.println("clear to send清楚发送");
		case SerialPortEvent.PE:
			System.out.println("parity error校验错误");
		case SerialPortEvent.OE:
			System.out.println("overrun error溢为错误");
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			System.out.println("output buffer is empty清空输出缓冲区");
			break;
		//端口有可用数据
		case SerialPortEvent.DATA_AVAILABLE:
			String res = "";
			int c;
			try {
				if(iStream != null) {
					while(iStream.available() > 0) {
						c = iStream.read();
						//Character ch = new Character((char) c);
						//res = res.concat(ch.toString());
						if(c != experimentData) {
							experimentData = c;
							notifyWatchers(experimentData);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}
	}

	@Override
	public void addWatcher(Watcher watcher) {
		// TODO Auto-generated method stub
		list.add(watcher);
	}

	@Override
	public void removeWatcher(Watcher watcher) {
		// TODO Auto-generated method stub
		list.remove(watcher);
	}

	@Override
	public void notifyWatchers(int data) {
		// TODO Auto-generated method stub
		for (Watcher watcher : list)
        {
            watcher.update(data);
        }
	}
}
