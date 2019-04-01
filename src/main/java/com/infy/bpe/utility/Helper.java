package com.infy.bpe.utility;

import com.infy.bpe.core.DataStore;

public class Helper {
	public static final String PROXY_REQUIRED = "YES";
	public static String proxyReqSetting = !isEmpty(DataStore.PROXYREQUIRED) ? PROXY_REQUIRED
			: DataStore.PROXYREQUIRED;
	public static String NAMESPACEPREFIX = "''";
	
	private static boolean isEmpty(String str) {		
		if (str == null || str.isEmpty()) {		
			return true;
		}		
		return false;
	}
	
}
