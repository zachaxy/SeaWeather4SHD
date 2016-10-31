package com.friendlyarm.serial.seaweather4shd.tools;






public class Serial {

	public  static int fd1;
	public  static int fd3;

	/***
	 * 主要负责接收天气消息
	 */
	/*public static void openCom1() {
		fd1 = HardwareControler.openSerialPort("/dev/s3c2410_serial3",
				9600, 8, 1);
	}*/

	/***
	 * 主要负责配置参数
	 */
	/*public static void openCom3() {
		fd3 = HardwareControler.openSerialPort("/dev/s3c2410_serial2",
				115200, 8, 1);
	}*/

	/***
	 * 主要用于向串口中写数据,天气消息的串口暂时不需要
	 * @param b 
	 */
	/*public static void writeCom1(byte[] b) {
		HardwareControler.write(fd1, b);
	}*/

	/***
	 * 主要用于向串口写数据,在参数设置中会用的到
	 * @param b
	 */
	/*public static void writeCom3(byte[] b) {
		HardwareControler.write(fd3, b);
	}*/

	/*public static String readCom1() {
		int m = HardwareControler.select(fd1, 2, 20);
		int n = 0;
		int len = 0;
		byte[] bs = null;
		//ArrayList<Byte> bsList = null; 
		String text = null;
		if (m == 1) {
			byte[] buf = new byte[1024];
			//bsList = new ArrayList<Byte>();
			text = "";
			while ((n = HardwareControler.read(fd1, buf, buf.length)) > 0) {
				try {
					Thread.sleep(90); // 睡眠等待数据完全接收
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				 
				
				 * for (int i = 0; i < n; i++) { text += (char) buf[i];}
				 
				for(int i= 0;i<n;i++){
					bsList.add(buf[i]);
				}
				len = len + n;
				text = text + BytesUtil.bytesToHexString(buf,n);
			}
		}
		return text;
	}
*/
 

	//----------新的函数特性---------------
/*	public static boolean isReadable(){
		int m = HardwareControler.select(fd3, 2, 20);
		if(m==1){
			Log.e("###","这尼玛居然是可读的!!!!");
			return true;
		}else{
			return false;
		}
	}*/
	
/*	public static byte[] readCom3(){
		int n = 0;
		byte[] buf= new byte[512];
		n = HardwareControler.read(fd3, buf, buf.length);
		if(n==0) return null;
		byte[] buff = new byte[n];
		System.arraycopy(buf, 0, buff, 0, n);
		return buff;
		
	}*/
	
	/***
	 * 关闭串口,在程序退出的时候进行关闭清理
	 */
/*	public static void closeCom1() {
		HardwareControler.close(fd1);
	}*/

	/***
	 * 关闭串口,在程序退出的时候进行关闭清理
	 */
	/*public static void closeCom3() {
		HardwareControler.close(fd3);
	}*/

}
