package com.hy.weatherwd;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	private final Handler handler = new Handler();
	String url, jsons, city, cityID;
	RemoteViews views;
	JSONObject jsono, info;
	JSONArray results, weather_data;
	Context mContext;

	@Override
	public void onUpdate(Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		Intent actionIntent = new Intent(context, MainActivity.class);
		PendingIntent pending = PendingIntent.getActivity(context, 0,
				actionIntent, 0);
		views = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		views.setOnClickPendingIntent(R.id.widgetView, pending);
		Thread t = new Thread() {
			@Override
			public void run() {
				url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
				jsons = Utils.getURLResponse(url);
				try {
					jsono = new JSONObject(jsons);
					city = jsono.getString("city");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if (city.equals(""))
						city = "\u8861\u9633";
					city = java.net.URLEncoder.encode(city, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				url = "http://hao.weidunewtab.com/tianqi/city.php?city=" + city;
				cityID = Utils.getURLResponse(url);
				url = "http://hao.weidunewtab.com/myapp/weather/data/indexInTime.php?cityID="
						+ cityID;
				jsons = Utils.getURLResponse(url);
				try {
					JSONObject jsono = new JSONObject(jsons);
					JSONObject wi = jsono.getJSONObject("weatherinfo");
					String tempnow = wi.getString("temp") + "°C";
					views.setTextViewText(R.id.temp, tempnow);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				url = "http://hao.weidunewtab.com/myapp/weather/data/index.php?cityID="
						+ cityID;
				jsons = Utils.getURLResponse(url);
				try {
					JSONObject jsono = new JSONObject(jsons);
					JSONObject wi = jsono.getJSONObject("weatherinfo");
					views.setImageViewResource(R.id.weatherimage,
							Utils.getWeatherImage(wi.getString("weather1")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i("MainActivity", "weidu JSON");
					e.printStackTrace();
				}
				for (int i = 0; i < appWidgetIds.length; i++) {
					appWidgetManager.updateAppWidget(appWidgetIds[i], views);
				}
			}
		};
		new Thread(t).start();
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.i("widget", "onReceive");
		if (intent.getAction().equals("com.hy.weatherwd.updateWidget")) {
			String weather = intent.getStringExtra("weather");
			String tempnow = intent.getStringExtra("tempnow");
			// Bundle data = intent.getExtras();
			// String weather = intent.getString("weather");
			// String tempnow =intent.getString("tempnow");
			Log.i("widget", weather + "," + tempnow);
			views = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			views.setTextViewText(R.id.temp, tempnow);
			views.setImageViewResource(R.id.weatherimage,
					Utils.getWeatherImage(weather));
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			appWidgetManager.updateAppWidget(new ComponentName(context,
					WidgetProvider.class), views);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
}