package com.airdroid.tools.devices;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class CPU implements DevicesInterface {

	private float percentCPU;// % su dung CPU

	/**
	 * Tra ve thong tin su dung CPU: so tien trinh dang chay va muc do su dung
	 * CPU
	 */
	@Override
	public HashMap<String, Double> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/* % CPU đã sử dụng --> tham khảo code trên mạng */
	public float getPercentCPU() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" ");

			long idle1 = Long.parseLong(toks[4]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[5]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" ");

			long idle2 = Long.parseLong(toks[4]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[5]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	public void setPercentCPU(float percentCPU) {
		this.percentCPU = percentCPU;
	}

}

