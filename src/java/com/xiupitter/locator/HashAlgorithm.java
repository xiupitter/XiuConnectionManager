package com.xiupitter.locator;

public interface HashAlgorithm {

	long hash(String k);
	
	byte[] md5(String k);
}
