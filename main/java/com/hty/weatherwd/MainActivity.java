package com.hty.weatherwd;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    int icon[];
    String city, url, jsons, scity, rts, swindnow, stempnow, sweather1, stemp1, swind1, shumidity, stoday, sdate[], sweekday[], sweather[], stemp[], swind[], cityID = "101010100", weekday[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"}, aqiv = "", aqis = "", spm25, errorfw = "", sidx;
    private final Handler handler = new Handler();
    LinearLayout future;
    public static RemoteViews remoteViews;
    public static AppWidgetManager appWidgetManager;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
    TextView tvcity, updatetime, tempnow, tvaqi, tvhumidity, tvwind0;
    WebView wvaqis;
    boolean isShowAQIS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvcity = (TextView) findViewById(R.id.city);
        updatetime = (TextView) findViewById(R.id.updatetime);
        tempnow = (TextView) findViewById(R.id.tempnow);
        tvwind0 = (TextView) findViewById(R.id.wind0);
        future = (LinearLayout) findViewById(R.id.future);
        tvaqi = (TextView) findViewById(R.id.aqi);
        tvhumidity = (TextView) findViewById(R.id.humidity);
        wvaqis = (WebView) findViewById(R.id.aqis);
        wvaqis.setBackgroundColor(0);
        if (Build.VERSION.SDK_INT > 18) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            RelativeLayout.LayoutParams lp;
            lp = (RelativeLayout.LayoutParams) tvcity.getLayoutParams();
            lp.topMargin = 50;
            tvcity.setLayoutParams(lp);
            lp = (RelativeLayout.LayoutParams) updatetime.getLayoutParams();
            lp.topMargin = 150;
            updatetime.setLayoutParams(lp);
            lp = (RelativeLayout.LayoutParams) tempnow.getLayoutParams();
            lp.topMargin = 100;
            tempnow.setLayoutParams(lp);
        }
        String InTimeSave = Utils.ReadFile("InTime.txt");
        if (InTimeSave.equals("FileNotFound")) {
            new Thread(t).start();
        } else {
            DrawInTime(InTimeSave);
        }
        String WeatherSave = Utils.ReadFile("weather.txt");
        Log.e("weather.txt", WeatherSave);
        if (WeatherSave.equals("FileNotFound") || WeatherSave.indexOf("error") != -1) {
            new Thread(t).start();
        } else {
            DrawWeather(WeatherSave);
        }
    }

    Thread t = new Thread() {
        @Override
        public void run() {
            PDM.sendEmptyMessage(0);
            url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
            jsons = Utils.getURLResponse(url);
            try {
                JSONObject jsono = new JSONObject(jsons);
                city = jsono.getString("city");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Log.i("MainActivity", "sina.json");
            }
            if (city.equals("")) {
                city = "\u8861\u9633";
                MainApplication.setmsg("IP返回城市为空，设置默认为衡阳。");
            }
            getWeather(city);
        }
    };

    void getWeather(String city) {
        Log.e("city", city);
        errorfw = "";
        PDM.sendEmptyMessage(1);
        try {
            city = java.net.URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        url = "http://hao.weidunewtab.com/tianqi/city.php?city=" + city;
        cityID = Utils.getURLResponse(url);
        Log.e("cityID", cityID);
        url = "http://hao.weidunewtab.com/myapp/weather/data/indexInTime.php?cityID=" + cityID;
        jsons = Utils.getURLResponse(url);
        Log.e("weathernow", jsons);
        try {
            JSONObject jsono = new JSONObject(jsons);
            JSONObject wi = jsono.getJSONObject("weatherinfo");
            // scity = wi.getString("city");
            rts = timeformat.format(new Date()) + "刷新";
            stempnow = wi.getString("temp") + "°C";
            shumidity = wi.getString("SD");
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        url = "http://hao.weidunewtab.com/myapp/weather/data/index.php?cityID=" + cityID;
        jsons = Utils.getURLResponse(url);
        Log.e("weatherfuture", jsons);
        if (jsons.indexOf("error") == -1) {
            JSONObject jsono;
            try {
                jsono = new JSONObject(jsons);
                JSONObject wi = jsono.getJSONObject("weatherinfo");
                scity = wi.getString("city");
                String datestr = wi.getString("date_y");
                if (datestr.indexOf("日") == -1) {
                    datestr += "日";
                }
                Log.e("date", datestr);
                Date today = null;
                try {
                    today = sdf.parse(datestr);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                int j = 7;
                sdate = new String[j];
                sweekday = new String[j];
                icon = new int[j];
                sweather = new String[j];
                stemp = new String[j];
                swind = new String[j];
                for (int i = 0; i < j; i++) {
                    int m = c.get(Calendar.MONTH) + 1;
                    int d = c.get(Calendar.DAY_OF_MONTH);
                    int wd = c.get(Calendar.DAY_OF_WEEK);
                    sdate[i] = m + "月" + d + "日";
                    sweekday[i] = weekday[wd - 1];
                    sweather[i] = wi.getString("weather" + (i + 1));
                    icon[i] = Utils.getWeatherImage(sweather[i]);
                    stemp[i] = wi.getString("temp" + (i + 1));
                    swind[i] = wi.getString("wind" + (i + 1));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
                stoday = datestr + " " + sweekday[0];
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            errorfw = jsons;
        }
        // AQI
        url = "http://hao.weidunewtab.com/tianqi/json/index.php?id=" + cityID;
        jsons = Utils.getURLResponse(url);
        Log.e("AQI:" + url, jsons);
        if (!jsons.equals("")) {
            JSONObject jsono;
            try {
                jsono = new JSONObject(jsons);
                aqiv = jsono.getString("AQIValue");
                url = "http://hao.weidunewtab.com/tianqi/json/IAQI_" + cityID + ".json";
                jsons = Utils.getURLResponse(url);
                Log.e("AQIS:" + url, jsons);
                jsono = new JSONObject(jsons);
                aqis = jsono.getString("AQIData");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            aqiv = sidx;
            aqis = "";
        }
        handler.post(mUpdateResults);
    }

    final Runnable mUpdateResults = new Runnable() {
        @Override
        public void run() {
            // PDM.sendEmptyMessage(4);
            tvcity.setText(scity);
            updatetime.setText(rts);
            tempnow.setText(stempnow);
            TextView weather0 = (TextView) findViewById(R.id.weather0);
            weather0.setText(sweather[0]);
            ImageView bg = (ImageView) findViewById(R.id.bg);
            bg.setImageDrawable(MainActivity.this.getResources().getDrawable(getBgImage(sweather[0])));
            TextView temp0 = (TextView) findViewById(R.id.temp0);
            temp0.setText(stemp[0]);
            // TextView wind0 = (TextView) findViewById(R.id.wind0);
            // wind0.setText(swind[0]);
            TextView humidity = (TextView) findViewById(R.id.humidity);
            humidity.setText("湿度：" + shumidity);
            TextView today = (TextView) findViewById(R.id.today);
            today.setText(stoday);
            future.removeAllViews();
            if (errorfw.equals("")) {
                int fts = 12;
                for (int i = 1; i < 7; i++) {
                    LinearLayout f = new LinearLayout(MainActivity.this);
                    f.setOrientation(LinearLayout.VERTICAL);
                    f.setGravity(Gravity.CENTER);
                    int p = 0;
                    f.setPadding(p, p, p, p);
                    future.addView(f, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    TextView date = new TextView(MainActivity.this);
                    date.setText(sdate[i]);
                    date.setTextSize(fts);
                    date.setTextColor(0xffffffff);
                    f.addView(date);
                    date.setGravity(Gravity.CENTER);
                    TextView weekday = new TextView(MainActivity.this);
                    weekday.setText(sweekday[i]);
                    weekday.setTextSize(fts);
                    weekday.setTextColor(0xffffffff);
                    f.addView(weekday);
                    weekday.setGravity(Gravity.CENTER);
                    ImageView wico = new ImageView(MainActivity.this);
                    wico.setImageDrawable(MainActivity.this.getResources().getDrawable(icon[i]));
                    f.addView(wico);
                    LayoutParams params = wico.getLayoutParams();
                    params.width = 100;
                    params.height = 100;
                    wico.setLayoutParams(params);
                    TextView weather = new TextView(MainActivity.this);
                    weather.setText(sweather[i]);
                    weather.setTextSize(fts);
                    weather.setGravity(Gravity.CENTER);
                    weather.setTextColor(0xffffffff);
                    f.addView(weather);
                    TextView temp = new TextView(MainActivity.this);
                    temp.setText(stemp[i]);
                    temp.setTextSize(fts);
                    temp.setGravity(Gravity.CENTER);
                    temp.setTextColor(0xffffffff);
                    f.addView(temp);
                    TextView wind = new TextView(MainActivity.this);
                    wind.setText(swind[i]);
                    wind.setTextSize(fts);
                    wind.setGravity(Gravity.CENTER);
                    wind.setTextColor(0xffffffff);
                    f.addView(wind);
                }
            } else {
                WebView wv = new WebView(MainActivity.this);
                wv.setBackgroundColor(0);
                wv.loadDataWithBaseURL("", errorfw, "text/html", "utf-8", "");
                future.addView(wv);
            }
            // AQI
            tvaqi.setText(aqiv);
            if (!aqiv.equals("")) {
                tvaqi.setVisibility(View.VISIBLE);
                colorAQI(Integer.parseInt(aqiv));
                wvaqis.loadDataWithBaseURL("", "<style>body{margin:0;}table{border-collapse:collapse;}#aqid td{text-align:center;border:1px solid #c3d9ff;padding:5px;}}</style><div id=aqid>" + aqis + "</div>", "text/html", "utf-8", "");
            } else {
                tvaqi.setVisibility(View.INVISIBLE);
            }
        }
    };

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // menu.add(0, 0, 0, "退出");
    // menu.add(0, 1, 1, "关于");
    // menu.add(0, 2, 2, "更新日志");
    // return true;
    // }

    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // int item_id = item.getItemId();
    // switch (item_id) {
    // case 0:
    // MainActivity.this.finish();
    // break;
    // case 1:
    // new
    // AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("微度天气 V5.12")
    // .setMessage("图形天气预报，动态天气图标，切换城市天气预报。\n作者：黄颖\nQQ: 84429027").setPositiveButton("确定",
    // null)
    // .show();
    // break;
    // case 2:
    // new AlertDialog.Builder(this)
    // .setIcon(R.drawable.ic_launcher)
    // .setTitle("更新日志")
    // .setMessage(
    // "V5.12 (2017-01-04)\n1.天气预报从6天增加到7天。\nV5.11 (2016-05-09)\n1.使用onActivityResult接收选择的城市代替onResume接收。\n\nV5.10 (2016-03-11)\n1.修复读取缓存文件不存在时引起的崩溃。\n2.适应平板，取消沉浸式。\n\nV5.9 (2016-02-17)\n1.添加城市到列表前判断是否已经在列表中。\n\nV5.8 (2016-01-17)\n1.修复未来天气预报返回错误信息依然解析引起的崩溃；\n2.无网络连接时不加载AQI数据缓存，以免加载AQI数据的城市不一致。\n\nV5.7 (2015-12-12)\n1.增加城市空气质量指数AQI；\n2.增加城市空气监测站点数据，并设置显隐开关；\n3.修复增加AQI产生的BUG。\n\nV5.6 (2015-12-01)\n1.更改城市列表标题颜色，以适应沉浸式窗体；\n2.更换城市取消修改桌面图标，桌面图标根据IP定位城市独立周期更新。\n\nV5.5 (2015-11-13)\n1.修复没有打开Activity时，调用Activity里的线程显示消息引起的崩溃;/n2.优化沉浸布局代码。\n\nV5.4 (2015-11-09)\n1.新增：API 19以上使用沉浸窗体；\n2.修复读取缓存错误。\n\nV5.3 (2015-10-10)\n1.打开界面不访问网络，而是读取缓存数据文件，获取文件修改日期为刷新时间；\n2.提取文件读写模块，简化代码。\n\nV5.2 (2015-09-30)\n优化结构。\n\nV5.0 (2015)\n增加城市选择功能。\n\nV4.X (2014)\n增加离线缓存功能。\n\nV3.X (2014)\n设计图形界面。\n\nV2.X (2014)\n增加桌面Widget。\n\nV1.X (2014)\n获取API，解析后以文字方式显示。")
    // .setPositiveButton("确定", null).show();
    // }
    // return true;
    // }

    public void refresh(View v) {
        city = tvcity.getText().toString();
        new Thread(c).start();
    }

    public void citylist(View v) {
        // startActivity(new Intent(MainActivity.this, CityList.class));
        startActivityForResult(new Intent(MainActivity.this, CityList.class), 1);
    }

    public void showAQIS(View v) {
        isShowAQIS = isShowAQIS ? false : true;
        if (isShowAQIS)
            wvaqis.setVisibility(View.VISIBLE);
        else {
            wvaqis.setVisibility(View.INVISIBLE);
        }
    }

    private int getBgImage(String weather) {
        if (weather.indexOf("雪") != -1)
            return R.drawable.bgsnow;
        if (weather.indexOf("霾") != -1)
            return R.drawable.bghaze;
        if (weather.indexOf("雾") != -1)
            return R.drawable.bgfog;
        if (weather.indexOf("雷") != -1)
            return R.drawable.bglei;
        if (weather.indexOf("冻雨") != -1)
            return R.drawable.bgdongyu;
        if (weather.indexOf("雨") != -1)
            return R.drawable.bgrain;
        if (weather.indexOf("阴") != -1)
            return R.drawable.bgyin;
        if (weather.indexOf("多云") != -1)
            return R.drawable.bgduoyun;
        if (weather.indexOf("晴") != -1)
            return R.drawable.bgqing;
        return R.drawable.ic_launcher;
    }

    final Handler PDM = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updatetime.setText("正在定位城市...");
                    break;
                case 1:
                    updatetime.setText("正在获取数据...");
                    break;
                case 2:
                    // PD.setMessage("IP返回城市为空，设置默认城市为衡阳。");
                    break;
                case 3:
                    // PD.setMessage("正在处理数据");
                    break;
                case 4:
                    // PD.setMessage("正在布局...");
                    break;
                case 5:
                    // PD.setMessage("网络未连接");
                    break;
                case 6:
                    getWeather(city);
            }
        }
    };

    static Handler STH = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainApplication.getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    class ChangeCity extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            city = b.getString("city");
            future.removeAllViews();
            getWeather(city);
        }
    }

    // must store the new intent unless getIntent() will return the old one
    // @Override
    // protected void onNewIntent(Intent intent) {
    // super.onNewIntent(intent);
    // setIntent(intent);
    // }

    Thread c = new Thread() {
        @Override
        public void run() {
            getWeather(city);
        }
    };

    void DrawInTime(String jsons) {
        JSONObject jsono;
        try {
            jsono = new JSONObject(jsons);
            JSONObject wi = jsono.getJSONObject("weatherinfo");
            scity = wi.getString("city");
            Date rt = new Date();
            String fp = "data/data/com.hty.weatherwd/files/InTime.txt";
            File file = new File(fp);
            if (file.exists()) {
                rt = new Date(file.lastModified());
            }
            // Log.e("file=" + fp, "file.exists()=" + file.exists());
            rts = timeformat.format(rt) + "刷新";
            stempnow = wi.getString("temp") + "°C";
            shumidity = wi.getString("SD");
            swindnow = wi.getString("WD") + wi.getString("WS");
            tvwind0.setText(swindnow);
            sidx = wi.getString("idx");
            updatetime.setText(rts);
            tvcity.setText(scity);
            tempnow.setText(stempnow);
            tvhumidity.setText("湿度：" + shumidity);
            tvaqi.setText(sidx);
            colorAQI(Integer.parseInt(sidx));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void DrawWeather(String jsons) {
        JSONObject jsono;
        try {
            jsono = new JSONObject(jsons);
            JSONObject wi = jsono.getJSONObject("weatherinfo");
            scity = wi.getString("city");
            String datestr = wi.getString("date_y");
            if (datestr.indexOf("日") == -1) {
                datestr += "日";
            }
            Log.e("date", datestr);
            Date today = sdf.parse(datestr);
            Calendar c = Calendar.getInstance();
            c.setTime(today);
            int j = 7;
            sdate = new String[j];
            sweekday = new String[j];
            icon = new int[j];
            sweather = new String[j];
            stemp = new String[j];
            swind = new String[j];
            for (int i = 0; i < j; i++) {
                int m = c.get(Calendar.MONTH) + 1;
                int d = c.get(Calendar.DAY_OF_MONTH);
                int wd = c.get(Calendar.DAY_OF_WEEK);
                sdate[i] = m + "月" + d + "日";
                sweekday[i] = weekday[wd - 1];
                sweather[i] = wi.getString("weather" + (i + 1));
                icon[i] = Utils.getWeatherImage(sweather[i]);
                stemp[i] = wi.getString("temp" + (i + 1));
                swind[i] = wi.getString("wind" + (i + 1));
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            stoday = datestr + " " + sweekday[0];
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TextView tvcity = (TextView) findViewById(R.id.city);
        tvcity.setText(scity);
        TextView weather0 = (TextView) findViewById(R.id.weather0);
        weather0.setText(sweather[0]);
        ImageView bg = (ImageView) findViewById(R.id.bg);
        bg.setImageDrawable(MainActivity.this.getResources().getDrawable(getBgImage(sweather[0])));
        TextView temp0 = (TextView) findViewById(R.id.temp0);
        temp0.setText(stemp[0]);
        TextView today = (TextView) findViewById(R.id.today);
        today.setText(stoday);
        future.removeAllViews();
        int fts = 12;
        for (int i = 1; i < 7; i++) {
            LinearLayout f = new LinearLayout(MainActivity.this);
            f.setOrientation(LinearLayout.VERTICAL);
            f.setGravity(Gravity.CENTER);
            int p = 0;
            f.setPadding(p, p, p, p);
            future.addView(f, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            TextView date = new TextView(MainActivity.this);
            date.setText(sdate[i]);
            date.setTextSize(fts);
            date.setTextColor(0xffffffff);
            f.addView(date);
            date.setGravity(Gravity.CENTER);
            TextView weekday = new TextView(MainActivity.this);
            weekday.setText(sweekday[i]);
            weekday.setTextSize(fts);
            weekday.setTextColor(0xffffffff);
            f.addView(weekday);
            weekday.setGravity(Gravity.CENTER);
            ImageView wico = new ImageView(MainActivity.this);
            wico.setImageDrawable(MainActivity.this.getResources().getDrawable(icon[i]));
            f.addView(wico);
            LayoutParams params = wico.getLayoutParams();
            params.width = 100;
            params.height = 100;
            wico.setLayoutParams(params);
            TextView weather = new TextView(MainActivity.this);
            weather.setText(sweather[i]);
            weather.setTextSize(fts);
            weather.setGravity(Gravity.CENTER);
            weather.setTextColor(0xffffffff);
            f.addView(weather);
            TextView temp = new TextView(MainActivity.this);
            temp.setText(stemp[i]);
            temp.setTextSize(fts);
            temp.setGravity(Gravity.CENTER);
            temp.setTextColor(0xffffffff);
            f.addView(temp);
            TextView wind = new TextView(MainActivity.this);
            wind.setText(swind[i]);
            wind.setTextSize(fts);
            wind.setGravity(Gravity.CENTER);
            wind.setTextColor(0xffffffff);
            f.addView(wind);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("MainActivity", "onActivityResult");
        if (resultCode == 1) {
            isShowAQIS = false;
            wvaqis.setVisibility(View.INVISIBLE);
            city = intent.getStringExtra("city");
            if (city.equals("定位")) {
                new Thread(t).start();
            } else {
                new Thread(c).start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void MenuDialog() {
        final String items[] = {"关于", "更新历史", "退出"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("菜单");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.ic_launcher).setTitle("微度天气 V5.13").setMessage("图形天气预报，动态天气图标，切换城市天气预报。\n作者：黄颖\nQQ: 84429027").setPositiveButton("确定", null).show();
                        break;
                    case 1:
                        new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.ic_launcher).setTitle("更新日志").setMessage("V5.13 (2018-01-26)\n1.适配雨夹雪、冻雨图标，冻雨背景。\n\nV5.12 (2017-01-04)\n1.天气预报从6天增加到7天。\n2.从实时天气中读取AQI指数。\n3.当前风力从实时天气中读取。\n\nV5.11 (2016-05-09)\n1.使用onActivityResult接收选择的城市代替onResume接收。\n2.适配 Android X86，返回键弹出菜单。\n3.AQI站点信息移动到顶层显示。\n\nV5.10 (2016-03-11)\n1.修复读取缓存文件不存在时引起的崩溃。\n2.适应平板，取消沉浸式。\n\nV5.9 (2016-02-17)\n1.添加城市到列表前判断是否已经在列表中。\n\nV5.8 (2016-01-17)\n1.修复未来天气预报返回错误信息依然解析引起的崩溃；\n2.无网络连接时不加载AQI数据缓存，以免加载AQI数据的城市不一致。\n\nV5.7 (2015-12-12)\n1.增加城市空气质量指数AQI；\n2.增加城市空气监测站点数据，并设置显隐开关；\n3.修复增加AQI产生的BUG。\n\nV5.6 (2015-12-01)\n1.更改城市列表标题颜色，以适应沉浸式窗体；\n2.更换城市取消修改桌面图标，桌面图标根据IP定位城市独立周期更新。\n\nV5.5 (2015-11-13)\n1.修复没有打开Activity时，调用Activity里的线程显示消息引起的崩溃;/n2.优化沉浸布局代码。\n\nV5.4 (2015-11-09)\n1.新增：API 19以上使用沉浸窗体；\n2.修复读取缓存错误。\n\nV5.3 (2015-10-10)\n1.打开界面不访问网络，而是读取缓存数据文件，获取文件修改日期为刷新时间；\n2.提取文件读写模块，简化代码。\n\nV5.2 (2015-09-30)\n优化结构。\n\nV5.0 (2015)\n增加城市选择功能。\n\nV4.X (2014)\n增加离线缓存功能。\n\nV3.X (2014)\n设计图形界面。\n\nV2.X (2014)\n增加桌面Widget。\n\nV1.X (2014)\n获取API，解析后以文字方式显示。").setPositiveButton("确定", null).show();
                        break;
                    case 2:
                        finish();
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MenuDialog();
        }
        return true;
    }

    void colorAQI(int aqi) {
        if (aqi <= 50) {
            tvaqi.setBackgroundColor(Color.GREEN);
        } else {
            if (aqi <= 100)
                tvaqi.setBackgroundColor(0xff97e600);
            else {
                if (aqi <= 150)
                    tvaqi.setBackgroundColor(Color.YELLOW);
                else {
                    if (aqi <= 200)
                        tvaqi.setBackgroundColor(0xffff6200);
                    else {
                        if (aqi <= 300)
                            tvaqi.setBackgroundColor(Color.RED);
                        else {
                            if (aqi > 300)
                                tvaqi.setBackgroundColor(0xff800000);
                        }
                    }
                }
            }
        }
    }

}
