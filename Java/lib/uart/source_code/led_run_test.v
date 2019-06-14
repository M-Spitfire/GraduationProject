module led_run_test(
			input ext_clk_25m,	//外部输入25MHz时钟信号
			input ext_rst_n,	//外部输入复位信号，低电平有效
			output reg[7:0] led		//8个LED指示灯接口	
			
		);													

//-------------------------------------
reg[25:0] cnt;		//20位计数器															

	//cnt计数器进行循环计数
always @ (posedge ext_clk_25m or negedge ext_rst_n)									
	if(!ext_rst_n) cnt <= 3'd0;											
	else cnt <= cnt+1'b1;																		
assign led_uart=led;
//-------------------------------------

	//计数器cnt计数到最大值时，切换点亮的指示灯
always @ (posedge ext_clk_25m or negedge ext_rst_n) 
	if(!ext_rst_n) led <= 8'b00000001;	//默认只点亮一个指示灯D2
	else if(cnt == 3'd5) led <= {led[0],led[7:1]};		//循环移位操作
	else ;					

endmodule
