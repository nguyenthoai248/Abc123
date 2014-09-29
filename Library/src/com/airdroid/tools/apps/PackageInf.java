/**
 * 
 * @author hongduongvu93
 *
 * Lớp cài đặt PackageInf - đại diện cho thoongtin của một ứng dụng.
 */

package com.airdroid.tools.apps;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

public class PackageInf implements Package {
	
	/*
	 * các thuộc tính.
	 */
	private String appName;
	private String packageName;
	private Drawable icon;
	private long size;

	// Hàm tạo 
	public PackageInf(String app, String pack, Drawable ic, long siz) {
		appName = app;
		packageName = pack;
		icon = ic;
		size = siz;		
	}

	/*
	 * Cài đặt các phương thức trong Interface.
	 */
	@Override
	public String getAppName() {
		return appName;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public Drawable getIcon() {
		return icon;
	}
	
	@Override
	public long getSize() {
		return size;
	}
	
	// toString().
	@SuppressLint("DefaultLocale")
	public String toString() {
		return String.format("Name : %s Package : %s Size : %d", appName, packageName, size);
	}
}