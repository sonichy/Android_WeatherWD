package com.hy.weatherwd;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
	private static Context mContext;
	private static String msg;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	public static void setmsg(String s) {
		msg = s;
	}

	public static String getmsg() {
		return msg;
	}

	public static Context getContext() {
		return mContext;
	}

}
