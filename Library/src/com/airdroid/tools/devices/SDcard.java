package com.airdroid.tools.devices;

import java.io.File;
import java.util.HashMap;

import android.os.Environment;
import android.os.StatFs;

public class SDcard implements DevicesInterface {

	private long available = 0L;// dung luong con trong cua the nho ngoai
	private long total = 0L;// tong dung luong the nho ngoai
	private long used = -1L;// đã dùng
	private int percentSDCard;// % su dung bo nho cua the nho ngoai

	@Override
	/**
	 * Tra ve thong tin cua bo nho ngoai.
	 */
	public HashMap<String, Double> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public SDcard() {
		getInfoSDCard();
	}

	/**
	 * lấy các thông tin về available và total thẻ nhớ tính theo GB --->
	 * 1024*1024=1048576
	 */
	public long getInfoSDCard() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		available = ((long) stat.getBlockSize() * (long) stat
				.getAvailableBlocks()) / 1048576;
		total = ((long) stat.getBlockSize() * (long) stat.getBlockCount()) / 1048576;
		return used = total - available;
	}

	public long getAvailable() {
		return available;
	}

	public void setAvailable(long available) {
		this.available = available;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPercentSDCard() {
		return percentSDCard = (int) (((float) used / total) * 100);
	}

}
