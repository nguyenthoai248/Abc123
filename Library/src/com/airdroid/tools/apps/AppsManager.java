package com.airdroid.tools.apps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;

/**
 * 
 * @author hongduongvu93
 *
 * Lớp cài đặt các chức năng của công cụ Apps 
 */
public class AppsManager extends Activity implements Apps {
	// Lưu danh sách các PackageInf
	private List<PackageInf> list;
	private List<PackageInfo> packInfo;
	private PackageManager packageManager;
	// số ứng dụng của người dùng.
	private int user = 0;

	// Hàm tạo.
	public AppsManager(List<PackageInfo> packInfo, PackageManager packManager) {
		list = new ArrayList<PackageInf>();
		this.packInfo = packInfo;
		this.packageManager = packManager;
		toPackageInf();
	}

	// chuyển các thông tin từ PackageInfo sang PackageInf.
	private void toPackageInf() {
		// Lưu các ứng dụng của hệ thống.
		List<PackageInf> temp = new ArrayList<PackageInf>();
		
		for (PackageInfo p : packInfo) {
			PackageStats pa = new PackageStats(p.packageName);
			PackageInf pack = new PackageInf(packageManager
					.getApplicationLabel(p.applicationInfo).toString(),
					p.packageName,
					packageManager.getApplicationIcon(p.applicationInfo),
					pa.codeSize);
			// Kiểm tra là ứng dụng hệ thông?
			if (!isSystemPackage(p)){
				list.add(pack);
				user++;
			}
			else
				temp.add(pack);
		}
		// danh sách lưu ứng dụng người dùng rồi đến ứng dụng hệ thống.
		for(int i = 0, len = temp.size(); i < len; i++)
			list.add(temp.get(i));
	}

	// Kiểm tra có phải là ứng dụng hệ thống.
	private boolean isSystemPackage(PackageInfo pkgInfo) {
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}

	/**
	 *  Cài đặt các phương thức của Interface.
	 */
	@Override
	public void detail(PackageInf app) {
		try {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + app.getPackageName()));
			startActivity(intent);

		} catch (ActivityNotFoundException e) {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
			startActivity(intent);

		}
	}

	@Override
	public void run(PackageInf app) {
		Intent intent = packageManager.getLaunchIntentForPackage(app
				.getPackageName());
		startActivity(intent);
	}

	@Override
	public boolean uninstall(PackageInf app) {
		Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
		intent.setData(Uri.parse("package:" + app.getPackageName()));
		startActivity(intent);
		return true;
	}

	@Override
	public void share(PackageInf app) {

	}

	// trả về danh sách các ứng dụng của người dùng và hệ thống.
	public List<PackageInf> getList() {
		return list;
	}

	// trả về số ứng dụng người dùng đã cài đặt.
	public int getUser() {
		return user;
	}

}
