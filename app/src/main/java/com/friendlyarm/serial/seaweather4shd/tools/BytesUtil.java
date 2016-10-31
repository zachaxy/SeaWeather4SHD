package com.friendlyarm.serial.seaweather4shd.tools;


public class BytesUtil {
	/**
	 * Convert byte[] to hex string. 把字节数组转化为字符串
	 * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	public static String bytesToHexString(byte[] src,int len) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || len <= 0) {
			return null;
		}
		for (int i = 0; i < len; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[] 把为字符串转化为字节数组
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		if (hexString.length() % 2 == 1) {
			hexString = "0" + hexString;
		}
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/***
	 * 
	 * @param hexString
	 *            the hex string
	 * @param n
	 *            自定义生成的字节位数
	 * @return 返回生成的字节数组
	 */
	public static byte[] hexStringToBytes(String hexString, int n) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		if (hexString.length() % 2 == 1) {
			hexString = "0" + hexString;
		}
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d1 = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d1[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		byte[] d2 = new byte[n - length];
		byte[] d = new byte[n];
		System.arraycopy(d2, 0, d, 0, d2.length);
		System.arraycopy(d1, 0, d, d2.length, d1.length);
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	public static String formatTime(char[] c){
		String date_text = "";
		for (int i = 0; i < c.length - 2; i++) {
			date_text = date_text + c[i];
			if (i % 2 == 1 && i < 5) {
				date_text = date_text + "-";
			}

			if (i == 5) {
				date_text = date_text + " ";
			}

			if (i == 7) {
				date_text = date_text + ":";
			}
		}		
		return date_text;
	}
}
