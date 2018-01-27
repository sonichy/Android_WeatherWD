package com.hty.weatherwd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class CityList extends Activity {
	Button btn1;
	EditText et1;
	GridView gv1, gv2;
	MyAdapter adapter;
	MyAdapter1 adapter1;
	List<String> al1, al2;

	// ArrayList<String[]> al2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citylist);
		gv1 = (GridView) findViewById(R.id.gridView1);
		gv2 = (GridView) findViewById(R.id.gridView2);
		btn1 = (Button) findViewById(R.id.button1);
		btn1.setOnClickListener(new ButtonListener());
		et1 = (EditText) findViewById(R.id.editText1);
		String s = Utils.ReadFile("citylist.txt");
		if (s.equals("FileNotFound"))
			s = "定位";
		al1 = new ArrayList<>(Arrays.asList(s.split(", ")));
		// Log.e("String->Array", al1.toString());
		String hotcity = "北京,上海,广州,杭州,武汉,重庆,成都,长沙,深圳,三亚,沈阳,珠海,香港,澳门";
		al2 = new ArrayList<>(Arrays.asList(hotcity.split(",")));
		adapter = new MyAdapter(this);
		adapter1 = new MyAdapter1(this);
		gv1.setAdapter(adapter1);
		adapter1.notifyDataSetChanged();
		gv2.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		gv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String city = al1.get(arg2);
				// Log.e("CityList->", city);
				Intent intent = new Intent(CityList.this, MainActivity.class);
				intent.putExtra("city", city);
				// startActivityForResult(intent, 1);
				// startActivity(intent);
				setResult(1, intent);
				finish();
			}
		});
		gv1.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				al1.remove(arg2);
				adapter1.notifyDataSetChanged();
				return true;
			}
		});
		gv2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String city = al2.get(arg2);
				String als1 = al1.toString();
				if (als1.contains(city)) {
					Toast.makeText(MainApplication.getContext(), "城市已存在", Toast.LENGTH_SHORT).show();
				} else {
					al1.add(city);
					adapter1.notifyDataSetChanged();
				}
			}
		});
	}

	class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				String city = et1.getText().toString();
				if (!city.equals("")) {
					String als1 = al1.toString();
					if (als1.contains(city)) {
						Toast.makeText(MainApplication.getContext(), "城市已存在", Toast.LENGTH_SHORT).show();
					} else {
						al1.add(city);
						adapter1.notifyDataSetChanged();
					}
				}
				break;
			}
		}
	}

	public void back(View v) {
		// startActivity(new Intent(CityList.this, MainActivity.class));
		Intent intent = new Intent(CityList.this, MainActivity.class);
		setResult(0, intent);
		finish();
	}

	private class MyAdapter1 extends BaseAdapter {
		private final Context context1;
		private final LayoutInflater inflater1;

		public MyAdapter1(Context context) {
			super();
			this.context1 = context;
			inflater1 = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return al1.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater1.inflate(R.layout.list_item, null);
			}
			TextView tv1 = (TextView) view.findViewById(R.id.textView1);
			tv1.setText(al1.get(position).toString());
			tv1.setTextColor(Color.GRAY);
			return view;
		}
	}

	private class MyAdapter extends BaseAdapter {
		private final Context context2;
		private final LayoutInflater inflater2;

		public MyAdapter(Context context) {
			super();
			this.context2 = context;
			inflater2 = LayoutInflater.from(context);
			// arr = new ArrayList<String>();
			// for (int i = 0; i < 3; i++) { // listview初始化3个子项
			// arr.add("南雄");
			// }
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return al2.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater2.inflate(R.layout.list_item, null);
			}
			TextView tv1 = (TextView) view.findViewById(R.id.textView1);
			tv1.setText(al2.get(position).toString());
			tv1.setTextColor(Color.GRAY);
			// final EditText et1 = (EditText)
			// view.findViewById(R.id.editText1);
			// et1.setText(arr.get(position)); // 在重构adapter的时候不至于数据错乱
			// Button btn2 = (Button) view.findViewById(R.id.button2);
			// et1.setOnFocusChangeListener(new OnFocusChangeListener() {
			// @Override
			// public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			// if (arr.size() > 0) {
			// arr.set(position, et1.getText().toString());
			// }
			// }
			// });
			// btn2.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// 从集合中删除所删除项的EditText的内容
			// arr.remove(position);
			// adapter.notifyDataSetChanged();
			// }
			// });
			return view;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		String als1 = al1.toString();
		als1 = als1.substring(1, als1.length() - 1);
		// Log.e("Array->String", als1);
		Utils.WriteFile("citylist.txt", als1);
	}
}
