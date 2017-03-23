package com.by122006library.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class MD5 {
	
	public static String getFileMD5(File file) {
		  if (!file.isFile()) {
		   return null;
		  }
		  MessageDigest digest = null;
		  FileInputStream in = null;
		  byte buffer[] = new byte[1024];
		  int len;
		  try {
		   digest = MessageDigest.getInstance("MD5");
		   in = new FileInputStream(file);
		   while ((len = in.read(buffer, 0, 1024)) != -1) {
		    digest.update(buffer, 0, len);
		   }
		   in.close();
		  } catch (Exception e) {
		   e.printStackTrace();
		   return null;
		  }
		  BigInteger bigInt = new BigInteger(1, digest.digest());
		  return bigInt.toString(16);
		 }
	private static String md5=null;
	public static String getSignInfo(Context context) {
		if(md5!=null) return md5;
	    try {  
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
	        Signature[] signs = packageInfo.signatures;
	        Signature sign = signs[0];
	        md5=parseSignature(sign.toByteArray());
	        return md5;  
	    } catch (Exception e) {
	        e.printStackTrace();  
	    }
		return "null";  
	}

	public static String parseSignature(byte[] signature) {
	    try {  
	        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
	        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
	        String pubKey = cert.getPublicKey().toString();
	        String signNumber = cert.getSerialNumber().toString();
	        return signNumber;
	    } catch (CertificateException e) {
	        e.printStackTrace();  
	    }
		return "null";

	}  public static String parseSignature(String str) {
		return parseSignature(str.getBytes());
	}


	/**
	 * 将字符串转成MD5值
	 *
	 * @param string
	 * @return
	 */
	public static String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}
}
