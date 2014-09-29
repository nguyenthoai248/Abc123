package com.airdroid;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.airdroid.server.MyServerActivity;

/**
 * 
 * @author hongduongvu93
 *
 * MainTabActivity hiển thị giao diện chính của ứng dụng theo dạng tab.
 */
@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {

	// Khai báo TabHost
	private TabHost myTabHost;

	// onCreate
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		
		// tạo thể hiện cho myTabHost.
		myTabHost = getTabHost();
		// bắt đầu thêm nội dung vào TabHost.
		myTabHost.setup();
		
		// Khai báo TabSpec Connection. Hiển thị thông tin của server và service.
		TabSpec connection = myTabHost.newTabSpec("connection");
		connection.setIndicator("Connection",
				getResources().getDrawable(android.R.drawable.ic_dialog_info));
		Intent intent1 = new Intent(this, MyServerActivity.class);
		// thiết đặt nội dung hiển thị cho tabspec.
		connection.setContent(intent1);

		/*
		 * Khai báo tabspec tools.
		 */
		TabSpec tools = myTabHost.newTabSpec("connection");
		tools.setIndicator("Tools",
				getResources().getDrawable(android.R.drawable.ic_dialog_info));
		Intent intent2 = new Intent(this, ToolsActivity.class);
		tools.setContent(intent2);

		/*
		 * Khai báo tabspec Abouts.
		 */
		TabSpec about = myTabHost.newTabSpec("connection");
		about.setIndicator("Abouts",
				getResources().getDrawable(android.R.drawable.ic_dialog_info));
		Intent intent3 = new Intent(this, AboutsActivity.class);
		about.setContent(intent3);		

		/*
		 * Thêm các tabspec vào tabhost.
		 */
		try{
		myTabHost.addTab(connection);
		}
		catch (Exception e){
			
		}
		myTabHost.addTab(tools);
		myTabHost.addTab(about);

		// Thiết đặt tab hiển thị đầu tiên.
		myTabHost.setCurrentTab(0);
	}

	// Cài đặt sự kiện cho phím Back.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Toast.makeText(getBaseContext(), "Chuc nang chua duoc ho tro",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
