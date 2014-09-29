package com.airdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.airdroid.R;

/**
 * 
 * @author hongduongvu93
 * 
 * Hiển thị màn hình "Chào".
 */
public class SplashActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// gọi phương thức onCreate của Activity.
		super.onCreate(savedInstanceState);
		// Thiết đặt nội dung hiển thị.
		setContentView(R.layout.splash_screen);

		// tạo thread.
		Thread thread = new Thread() {
			public void run() {
				try {
					// đặt thời gian "chờ"
					Thread.sleep(999);
					// Khởi chạy Activity mới.
					startActivity(new Intent(SplashActivity.this,
							MainTabActivity.class));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					// Kết thúc Activity.
					finish();
				}
			}
		};
		// chạy thread.
		thread.start();
	}
}
