package com.gionee.note.utils;
import android.os.*;
//Gionee <pengwei><2013-11-01> modify for CR00941779 begin
public class PlatForm {

	private static final String PLATFORM_MTK = "MTK";
	private static final String PLATFORM_QCOM = "QCOM";
	public static boolean isMTK(){
		String platFormProperty = SystemProperties.get("ro.gn.platform.support");
		Log.v("PlatFor-platFormProperty == " + platFormProperty);
		if(PLATFORM_MTK.equals(platFormProperty)){
			return true;
		}else if(PLATFORM_QCOM.equals(platFormProperty)){
			return false;
		}
		return true;
	}
	
}
//Gionee <pengwei><2013-11-01> modify for CR00941779 end
