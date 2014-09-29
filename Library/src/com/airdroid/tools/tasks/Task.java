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

import android.graphics.drawable.Drawable;

public class Task {

	private String label;// ten cua app
	private Drawable icon;// icon
	private int memory;// bo nho RAM su dung cua task
	private Boolean selected;// chon trong checkbox
	private int pid;// pid cua task
	private String packageName;// package name cua app
	private Boolean isUserApp;// co kiem tra xem co phai la app cua nguoi dung hay khong

	// ham khoi tao thieu 1 doi so selected
	public Task(String label, Drawable icon, int memory, int pid,
			String packageName, Boolean isUserApp){
		this.label = label;
		this.icon = icon;
		this.memory = memory;
		this.pid = pid;
		this.packageName = packageName;
		this.isUserApp = false;
		this.setSelected(false);
	}
	
	// ham khoi tao day du cac doi so
	public Task(String label, Drawable icon, int memory, int pid,
			String packageName, Boolean isUserApp, Boolean selected) {
		this.label = label;
		this.icon = icon;
		this.memory = memory;
		this.pid = pid;
		this.packageName = packageName;
		this.setIsUserApp(isUserApp);
		this.setSelected(selected);
	}

	// ham khoi tao khong doi so
	public Task() {
		this.label = null;
		this.icon = null;
		this.memory = 0;
		this.pid = -1;
		this.packageName = null;
		this.isUserApp = null;
		this.setSelected(false);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String name) {
		this.label = name;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Boolean getIsUserApp() {
		return isUserApp;
	}

	public void setIsUserApp(Boolean isUserApp) {
		this.isUserApp = isUserApp;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

}

