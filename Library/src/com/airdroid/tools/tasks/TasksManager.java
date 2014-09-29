/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.airdroid.tools.tasks;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;


public class TasksManager extends Activity implements TasksInterface {

	private static int size;
	private ActivityManager manager;

	/**
	 * Dua ra cac tien trinh dang chay
	 * 
	 * @return
	 * @throws NameNotFoundException co nhung tien trinh chay khong co nhan hoac icon thi de mac dinh
	 */

	public ArrayList<Task> listProcess(Context context, Drawable iconNotFound) {
		ArrayList<Task> list = new ArrayList<Task>();

		manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();

		size = tasks.size();
		Log.d("TAG", "size = " + size);

		int i = 0;
		for (RunningAppProcessInfo procInfo : tasks) {
			String packageName = procInfo.processName.split(":")[0];
			int[] myMemPid = new int[] { procInfo.pid };
			Debug.MemoryInfo[] memoryInfo = manager
					.getProcessMemoryInfo(myMemPid);
			int memSize = memoryInfo[0].dalvikPrivateDirty
					+ memoryInfo[0].dalvikPss + memoryInfo[0].dalvikSharedDirty;

			if (packageName != "system"
					&& packageName != "com.google.process.gapps"
					&& packageName != "android.process.acore"
					&& packageName != "android.process.media")
				try {
					ApplicationInfo app = context.getPackageManager()
							.getApplicationInfo(packageName,
									PackageManager.GET_META_DATA);
					String label = context.getPackageManager()
							.getApplicationLabel(app).toString();
					Drawable icon = context.getPackageManager()
							.getApplicationIcon(app);
					Boolean isUserApp = isUserApp(app);

					Task task = new Task(label, icon, memSize,
							tasks.get(i).pid, packageName, isUserApp, false);
					list.add(task);
				} catch (Exception e) {
					Task task = new Task(packageName, iconNotFound, memSize,
							tasks.get(i).pid, packageName, true, false);
					list.add(task);
				}
			i++;
		}

		return list;
	}

	/**
	 * kill process duoc chon
	 * 
	 * @param pid
	 *            la pid cua process
	 * @return true neu stop duoc tien trinh, false neu khong stop duoc.
	 */
	@SuppressWarnings("deprecation")
	public void killApp(String packgeName) {
		Method method = getKillMethod();
		try {
			if (method != null) {
				method.invoke(this.manager, packgeName);
			} else {
				this.manager.restartPackage(packgeName);
			}
			Log.d("TAG", "" + packgeName);
		} catch (Exception e) {
			Log.e("TAG", e.getMessage());
		}

	}

	private Method getKillMethod() {
		try {
			Method method = ActivityManager.class.getDeclaredMethod(
					"killBackgroundProcesses", String.class);
			return method;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Dua ra thong tin ve tien trinh dang chay
	 * 
	 * @param packageName
	 *            ten goi cai dat cua tien trinh
	 * @return
	 */
	public Intent details(String packageName) {
		Intent intent;
		try {
			// Open the specific App Info page:
			intent = new Intent(
					android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + packageName));

		} catch (ActivityNotFoundException e) {
			// Open the generic Apps page:
			intent = new Intent(
					android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
		}
		return intent;

	}

	/**
	 * Kiem tra xem app co phai la app cua nguoi su dung khong
	 * 
	 * @param ai
	 * @return
	 */
	public boolean isUserApp(ApplicationInfo ai) {
		int mask = ApplicationInfo.FLAG_SYSTEM
				| ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
		return (ai.flags & mask) == 0;
	}

	/**
	 * @return tra ve kich co cua list cac task dang chay
	 */
	public static int size() {
		return size;
	}

}

