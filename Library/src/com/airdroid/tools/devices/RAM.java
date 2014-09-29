package com.airdroid.tools.devices;

import java.util.HashMap;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;

public class RAM implements DevicesInterface {

	private long free = 0L;// dung luong RAM con trong
	private long total = 0L;// tong dung luong cua RAM
	private long used = -1L;// RAM đã dùng
	private int percentRAM;// % su dung RAM

	@Override
	/**
	 * Tra ve thong tin su dung RAM: tong dung luong, dung luong con trong,
	 * % su dung RAM
	 */
	public HashMap<String, Double> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public RAM() {
		getRamInfo();
	}

	public long getRamInfo() {
		try {
			Runtime info = Runtime.getRuntime();
			free = info.freeMemory() / 1024; // free tính theo MB nen chia cho
												// 1024
			total = (info.totalMemory()) / 1024; // total tính theo MB nên chia
													// cho 2014
			used = total - free;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return used;
	}

	public long getFree() {
		return free;
	}

	public void setFree(long avaiable) {
		this.free = avaiable;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPercentRAM() {
		return percentRAM = (int) (((float) free / total) * 100);
	}

}
