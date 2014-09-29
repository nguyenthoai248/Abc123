package com.airdroid;

import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.airdroid.helper.AppsAdapter;
import com.airdroid.tools.apps.AppsManager;
import com.airdroid.tools.apps.PackageInf;

/**
 * 
 * @author hongduongvu93
 * 
 * Cài đặt giao diện cho công cụ Apps.
 */
public class AppsActivity extends Activity implements OnItemClickListener {

	/*
	 * Các thuộc tính cần thiết. 
	 */
	private PackageManager packageManager;
	private ListView list;
	private AppsAdapter adapter;
	private AppsManager appManager;
	// Lưu danh sách các packkage.
	private List<PackageInf> packageList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_tool);

		// tạo thể hiện cho PackageManager.
		packageManager = getPackageManager();
		// Lấy danh sách package có trong thiết bị.
		List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
		// tạo một thể hiện cho appManager.
		appManager = new AppsManager(packageInfo, packageManager);
		// lấy danh sách các packageinf.
		packageList = appManager.getList();
		/*
		 *  thêm hai "vùng ngăn" User Apps và System Apps cho danh sách.
		 */
		Drawable image1 = getResources().getDrawable(R.drawable.ic_account_n);
		Drawable image2 = getResources().getDrawable(
				R.drawable.main_ic_local_mode);
		int po1 = appManager.getUser();
		int po2 = packageList.size() - po1;
		packageList.add(0, new PackageInf("User Apps (" + po1 + ")", "",
				image1, 0));
		packageList.add(po1 + 1, new PackageInf("System Apps (" + po2 + ")",
				"", image2, 0));
		list = (ListView) findViewById(R.id.user_list);
		// đặt adapter hiển thị.
		adapter = new AppsAdapter(this, packageList);
		list.setAdapter(adapter);
		// đăng kí sự kiện click cho list view.
		list.setOnItemClickListener(this);
		// đăng kí sự kiện long click cho list view.
		registerForContextMenu(list);
	}

	/**
	 *  cài đặt sự kiện click vào item.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// lấy package tương ứng với item được click.
		PackageInf pack = (PackageInf) parent.getAdapter().getItem(position);
		try {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + pack.getPackageName()));
			startActivity(intent);

		} catch (ActivityNotFoundException e) {
			Log.v("Activity not found", pack.toString());
		}
	}

	/**
	 *  cài đặt Context Menu.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		// lấy "menu" từ file xml.
		inflater.inflate(R.menu.user_menu, menu);
	}

	/**
	 *  Cài đặt sự kiện cho context menu khi một item được chọn.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		PackageInf pack = (PackageInf) adapter.getItem(info.position);
		switch (item.getItemId()) {
		// khi "Details" được chọn.
		case R.id.details:
			try {
				Intent intent = new Intent(
						android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + pack.getPackageName()));
				startActivity(intent);

			} catch (ActivityNotFoundException e) {
				Log.v("Activity not found", pack.toString());
			}

			return true;
		// Khi "Run" được chọn.
		case R.id.run:
			try {
				startActivity(packageManager.getLaunchIntentForPackage(pack
						.getPackageName()));
			} catch (ActivityNotFoundException e) {
				Log.v("Activity not found", pack.toString());
			}

			return true;
		// Khi "uninstall"
		case R.id.uninstall:
			try {
				Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
				intent.setData(Uri.parse("package:" + pack.getPackageName()));
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Log.v("Activity not found", pack.toString());
			}

			return true;
		
		// "share"
		case R.id.share:

			try {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(
						Intent.EXTRA_TEXT,
						"http://play.google.com/store/apps/details?id="
								+ pack.getPackageName());
				intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Check out this site!");
				startActivity(Intent.createChooser(intent, "Share"));
			} catch (ActivityNotFoundException e) {
				Log.v("Activity not found", pack.toString());
			}

			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}
}
