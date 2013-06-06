package com.xiupitter.locator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashAlgorithmImpl implements HashAlgorithm {

	@Override
	public long hash(String k) {
		// TODO Auto-generated method stub
		byte[] digest = md5(k);
		long rv = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        return rv & 0xffffffffL;
	}

	@Override
	public byte[] md5(String k) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		try {
			MessageDigest dig = MessageDigest.getInstance("md5");
			dig.reset();
			ret = dig.digest(k.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}

}
