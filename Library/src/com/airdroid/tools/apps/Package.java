/**
 *  Interface cho PackageInf.
 */
package com.airdroid.tools.apps;

import android.graphics.drawable.Drawable;

public interface Package {
	// Lấy thông tin tên ứng dụng.
	public String getAppName();

	// Lấy thông tin tên gói ứng dụng.
	public String getPackageName();
	
	// Lấy icon của ứng dụng.
	public Drawable getIcon();
	
	// Lấy kích thước ứng dụng.
	public long getSize();
}
