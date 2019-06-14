module LED_run(
			input ext_clk_25m,	//外部输入25MHz时钟信号
			input ext_rst_n,	//外部输入复位信号，低电平有效
			input [7:0] led_in,
			output wire[7:0] led,		//8个LED指示灯接口	
			output wire[7:0] tx_data,
			output reg rx_int,
			output wire clk_s
		);													

//-------------------------------------
reg[10:0] cnt;		//20位计数器	
reg [7:0] led_r;	
assign led=~led_r;													
assign tx_data=led;
assign clk_s=cnt[3];
	//cnt计数器进行循环计数
always @ (posedge ext_clk_25m or negedge ext_rst_n)									
	if(!ext_rst_n) cnt<=0;											
	else cnt<=cnt+1;																		

//-------------------------------------

	//计数器cnt计数到最大值时，切换点亮的指示灯

always @ (posedge ext_clk_25m or negedge ext_rst_n) 
	if(!ext_rst_n) led_r <= 8'b0000_0001;	//默认只点亮一个指示灯D2
	else if(cnt == 3'd2) begin led_r <=  led_in ;rx_int<=1;	end	//循环移位操作
	else if(cnt == 3'd7)  rx_int<=0;	
	

endmodule 

