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
	
	// ��Ź۲���
    private List<Watcher> list = new ArrayList<Watcher>();
	
	public COMReader(String com, int baud) {
		COMReader.PortName = com;
		COMReader.baud = baud;
	}
	
	//��ʼ�����ڣ���������������ʵ����ȡ
	public void Init() {
		try {
			PortID = CommPortIdentifier.getPortIdentifier(PortName);
			//�򿪲���������
			serialPort = (SerialPort)PortID.open("JAVA_SERIAL", 2000);
			//ע������¼�
			serialPort.addEventListener(this);
			//�п�������ʱ�����¼�
			serialPort.notifyOnDataAvailable(true);
			iStream = serialPort.getInputStream();
			serialPort.getOutputStream();
			//������9600��8λ����λ��1λֹͣλ������żУ��
			serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
		} catch (PortInUseException e) {
            System.out.println("�ô�������ʹ����");
        } catch (NoSuchPortException e) {
            System.out.println("�ô��ڵ�ǰ����ʹ��");
        } catch (UnsupportedCommOperationException e) {
            System.out.println("����Ĵ��ڲ�������");
        } catch (TooManyListenersException e) {
            System.out.println("�ô����Ѵ��ڼ�����");
        } catch (IOException e) {
            System.out.println("�����������ʧ��");
        }
	}
	
	//�رմ���
	public void closePort() {
		// TODO Auto-generated method stub
		serialPort.close();
	}
	
	//��ȡ����
	public int getData() {
		return experimentData;
	}
	
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
			System.out.println("break interruptͨѶ�ж�");
		case SerialPortEvent.FE:
			System.out.println("framing error֡����");
		case SerialPortEvent.RI:
			System.out.println("ring indicator����ָʾ");
		case SerialPortEvent.DSR:
			System.out.println("data set ready�����豸����");
		case SerialPortEvent.CD:
			System.out.println("carrier detect�ز����");
		case SerialPortEvent.CTS:
			System.out.println("clear to send�������");
		case SerialPortEvent.PE:
			System.out.println("parity errorУ�����");
		case SerialPortEvent.OE:
			System.out.println("overrun error��Ϊ����");
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			System.out.println("output buffer is empty������������");
			break;
		//�˿��п�������
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
