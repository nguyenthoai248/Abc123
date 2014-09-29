package com.airdroid.tools.devices;

import java.util.HashMap;

import android.content.Intent;

public class Battery implements DevicesInterface {

	public int percentBarterry;// muc pin con lai hien tai

	@Override
	/**
	 * Tra ve nhung thong tin cua pin: tong dung luong, nhiet do, muc pin
	 */
	public HashMap<String, Double> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPercentBarterry(Intent i) {
		return percentBarterry = i.getIntExtra("level", 0);// lấy % pin còn lai
															// của hệ thống
	}

	public void setPercentBarterry(int percentBarterry) {
		this.percentBarterry = percentBarterry;
	}

}

