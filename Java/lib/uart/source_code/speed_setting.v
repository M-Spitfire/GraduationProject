/////////////////////////////////////////////////////////////////////////////
//Altera ATPP�������� ��о�Ƽ� Я�� ��Ȩͬѧ ��ͬ���� FPGA������ϵ��
//����Ӳ��ƽ̨�� Altera Cyclone IV FPGA 
//�����׼��ͺţ� SF-CY4 ��Ȩ����
//��   Ȩ  ��   ���� �������ɡ�����ǳ����תFPGA�����ߡ���Ȩͬѧ��ԭ����
//				����SF-CY4�����׼�ѧϰʹ�ã�лл֧��
//�ٷ��Ա����̣� http://myfpga.taobao.com/
//�����������أ� http://pan.baidu.com/s/1jGpMIJc
//��                ˾�� �Ϻ��������ӿƼ����޹�˾
/////////////////////////////////////////////////////////////////////////////
module speed_setting(
				clk,rst_n,
				bps_start,clk_bps
			);

input clk;	// 25MHz��ʱ��
input rst_n;	//�͵�ƽ��λ�ź�
input bps_start;	//���յ����ݺ󣬲�����ʱ�������ź���λ
output clk_bps;	// clk_bps�ĸߵ�ƽΪ���ջ��߷�������λ���м������� 

`define BPS_9600
`define CLK_PERIORD	40	//����ʱ������Ϊ40ns(25MHz)
`define BPS_SET		96	//����ͨ�Ų�����Ϊ9600bps(����Ҫ�Ĳ�����ʡȥ����������弴��)

`define BPS_PARA	(10_000_000/`CLK_PERIORD/`BPS_SET)//10_000_000/`CLK_PERIORD/96;		//������Ϊ9600ʱ�ķ�Ƶ����ֵ
`define BPS_PARA_2	(`BPS_PARA/2)//BPS_PARA/2;	//������Ϊ9600ʱ�ķ�Ƶ����ֵ��һ�룬�������ݲ���

reg[12:0] cnt;			//��Ƶ����
reg clk_bps_r;			//������ʱ�ӼĴ���

//----------------------------------------------------------
reg[2:0] uart_ctrl;	// uart������ѡ���Ĵ���
//----------------------------------------------------------

always @ (posedge clk or negedge rst_n)
	if(!rst_n) cnt <= 13'd0;
	else if((cnt == `BPS_PARA) || !bps_start) cnt <= 13'd0;	//�����ʼ�������
	else cnt <= cnt+1'b1;			//������ʱ�Ӽ�������

always @ (posedge clk or negedge rst_n)
	if(!rst_n) clk_bps_r <= 1'b0;
	else if(cnt == `BPS_PARA_2) clk_bps_r <= 1'b1;	// clk_bps_r�ߵ�ƽΪ��������λ���м�������,ͬʱҲ��Ϊ�������ݵ����ݸı���
	else clk_bps_r <= 1'b0;

assign clk_bps = clk_bps_r;

endmodule

