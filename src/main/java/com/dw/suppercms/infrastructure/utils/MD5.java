package com.dw.suppercms.infrastructure.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	/*
	 * md5加密算法主要功能描叙：给定一定的字节数组，md5算法将产生一个定长的hash码(长度32)
	 */
	public static String getMD5(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes());// 源头
			byte[] res = md.digest();// 产生摘要
			return byte2HexStr(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * SHA加密算法
	 * 
	 * @param codeType
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getSHA(String message) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA");
		digest.update(message.getBytes());
		byte[] pass = digest.digest();
		return byte2HexStr(pass);
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	private static String byte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toLowerCase());
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(MD5.getMD5("admin"));
	}

}
