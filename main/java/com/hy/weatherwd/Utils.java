package com.hy.weatherwd;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.util.Log;

public class Utils {

	static String getURLResponse(String urlString) {
		HttpURLConnection conn = null;
		InputStream is = null;
		String resultData = "";
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader bufferReader = new BufferedReader(isr);
			String inputLine = "";
			while ((inputLine = bufferReader.readLine()) != null) {
				resultData += inputLine;
			}
			// 保存数据
			if (urlString.indexOf("sina") != -1) {
				WriteFile("sina.txt", resultData);
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/tianqi/city.php?city=") != -1) {
				WriteFile("city.txt", resultData);
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/myapp/weather/data/indexInTime.php?cityID=") != -1) {
				WriteFile("InTime.txt", resultData);
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/myapp/weather/data/index.php?cityID=") != -1) {
				WriteFile("weather.txt", resultData);
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/tianqi/json/index.php?id=") != -1) {
				WriteFile("aqi.txt", resultData);
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/tianqi/json/IAQI_") != -1) {
				WriteFile("aqis.txt", resultData);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e("utils", "MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("utils", "IOException1");
			e.printStackTrace();
			// MainApplication.setmsg("网络未连接");
			// 读数据
			if (urlString.indexOf("sina") != -1) {
				resultData = ReadFile("sina.txt");
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/tianqi/city.php?city=") != -1) {
				resultData = ReadFile("city.txt");
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/myapp/weather/data/indexInTime.php?cityID=") != -1) {
				resultData = ReadFile("InTime.txt");
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/myapp/weather/data/index.php?cityID=") != -1) {
				resultData = ReadFile("weather.txt");
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/tianqi/json/index.php?id=") != -1) {
				resultData = ReadFile("aqi.txt");
			}
			if (urlString
					.indexOf("http://hao.weidunewtab.com/tianqi/json/IAQI_") != -1) {
				// resultData = ReadFile("aqis.txt");
				resultData = "";
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("utils", "IOException2");
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return resultData;
	}

	public static int getWeatherImage(String weather) {
		if (weather.indexOf("霾") != -1)
			return R.drawable.haze;
		if (weather.indexOf("雾") != -1)
			return R.drawable.fog;
		if (weather.indexOf("雷") != -1)
			return R.drawable.lei;
		if (weather.indexOf("雪") != -1)
			return R.drawable.xue;
		if (weather.indexOf("暴雨") != -1)
			return R.drawable.baoyu;
		if (weather.indexOf("大雨") != -1)
			return R.drawable.dayu;
		if (weather.indexOf("中雨") != -1)
			return R.drawable.zhongyu;
		if (weather.indexOf("小雨") != -1)
			return R.drawable.xiaoyu;
		if (weather.indexOf("阵雨") != -1)
			return R.drawable.zhenyu;
		if (weather.indexOf("阴") != -1)
			return R.drawable.yin;
		if (weather.indexOf("多云") != -1)
			return R.drawable.duoyun;
		if (weather.indexOf("晴") != -1)
			return R.drawable.qing;
		return R.drawable.ic_launcher;
	}

	static void WriteFile(String filename, String s) {
		FileOutputStream fos = null;
		try {
			fos = MainApplication.getContext().openFileOutput(filename,
					Context.MODE_PRIVATE);
			fos.write(s.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String ReadFile(String filename) {
		String s = "";
		FileInputStream istream;
		try {
			istream = MainApplication.getContext().openFileInput(filename);
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			int len;
			while ((len = istream.read(buffer)) != -1) {
				ostream.write(buffer, 0, len);
			}
			s = new String(ostream.toByteArray());
			istream.close();
			ostream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "FileNotFound";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (s.equals(""))
			if (filename.equals("citylist.txt"))
				s = "定位";
		return s;
	}
}
