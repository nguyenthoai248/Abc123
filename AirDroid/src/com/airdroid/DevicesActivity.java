package com.airdroid;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airdroid.tools.devices.Battery;
import com.airdroid.tools.devices.CPU;
import com.airdroid.tools.devices.RAM;
import com.airdroid.tools.devices.SDcard;

public class DevicesActivity extends Activity {

	Battery bat = new Battery();
	CPU cpu = new CPU();
	RAM ram = new RAM();
	SDcard sdcard = new SDcard();
	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;
	DecimalFormat dcf = new DecimalFormat("#.0");// dinh dang chu so thap phan
	Alarm alarm;
	TextView tvBattery, tvRam, tvCpu, tvSDcard;
	ProgressBar pg_ram, pg_bat, pg_sdcard, pg_cpu;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices_tool);
		// Khoi tao
		alarm = new Alarm();
		tvBattery = (TextView) findViewById(R.id.tv_bat);
		tvRam = (TextView) findViewById(R.id.tv_ram);
		tvCpu = (TextView) findViewById(R.id.tv_cpu);
		tvSDcard = (TextView) findViewById(R.id.tv_sdcard);
		pg_ram = (ProgressBar) findViewById(R.id.ram);
		pg_cpu = (ProgressBar) findViewById(R.id.cpu);
		pg_sdcard = (ProgressBar) findViewById(R.id.sdcard);
		pg_bat = (ProgressBar) findViewById(R.id.battery);

		//khoi tao cac doi tuong
		int level = new Intent().getIntExtra("level", 0);
		pg_bat.setProgress(level);
		tvBattery.setText("Battery: " + Integer.toString(level) + "%");

		pg_ram.setProgress(ram.getPercentRAM());
		tvRam.setText("RAM: " + ram.getPercentRAM() + "%");

		int iCpu = (int) (cpu.getPercentCPU());
		pg_cpu.setProgress(iCpu);
		tvCpu.setText("CPU: " + iCpu + "%");

		pg_sdcard.setProgress(sdcard.getPercentSDCard());
		tvSDcard.setText("SDcard: " + sdcard.getPercentSDCard() + "%");

		// dang ky broadcast
		registerReceiver(mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		registerReceiver(alarm, new IntentFilter("update"));
		startRepeating();

	}

	public void startRepeating() {

		
		int startTime = 3000;  //thoi gian bat dau lap
		long intervals = 3000; //lap lai sau moi 3s
		// We prepare the pendingIntent for the AlarmManager
		Intent intent = new Intent();
		intent.setAction("update");
		pendingIntent = PendingIntent.getBroadcast(
				this.getApplicationContext(), 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MILLISECOND, startTime);
		// We set a repeating alarm
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), intervals, pendingIntent);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		alarmManager.cancel(pendingIntent);
		unregisterReceiver(alarm);
		unregisterReceiver(mBatInfoReceiver);
	}

	public class Alarm extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			pg_ram.setProgress(ram.getPercentRAM());
			tvRam.setText("RAM: " + ram.getPercentRAM() + "%");
			int iCpu = (int) (cpu.getPercentCPU());
			pg_cpu.setProgress(iCpu);
			tvCpu.setText("CPU: " + iCpu + "%");

			pg_sdcard.setProgress(sdcard.getPercentSDCard());
			tvSDcard.setText("SDcard: " + sdcard.getPercentSDCard() + "%");
		}

	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context c, Intent i) {

			int level = i.getIntExtra("level", 0);
			pg_bat.setProgress(level);
			tvBattery.setText("Battery: " + Integer.toString(level) + "%");

		}

	};

}
