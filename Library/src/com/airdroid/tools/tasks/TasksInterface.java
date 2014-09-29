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

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public interface TasksInterface {
	/**
	 * Tra v danh sach cac tien trinh dang chay
	 */
	public ArrayList<Task> listProcess(Context context, Drawable iconNotFound);

	/**
	 * Dung 1 tien trinh dang chay
	 * @param pid la pid cua tien trinh
	 * @return true neu dung duoc, flase neu khong dung duoc
	 */
	public void killApp(String packageName);

	/**
	 * Dua ra thong tin chi tiet ve tien trinh dang chay
	 * @param packageName ten package
	 * @return
	 */
	public Intent details(String packageName);

}

