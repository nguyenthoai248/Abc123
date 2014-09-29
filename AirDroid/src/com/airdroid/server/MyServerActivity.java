package com.airdroid.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.airdroid.R;

import de.fun2code.android.pawserver.PawServerActivity;
import de.fun2code.android.pawserver.PawServerService;
import de.fun2code.android.pawserver.listener.ServiceListener;
import de.fun2code.android.pawserver.util.Utils;

/**
 * 
 * @author hongduongvu93
 * 
 *  Lớp cài đặt Server cho ứng dụng.
 */
public class MyServerActivity extends PawServerActivity implements
		ServiceListener {
	@SuppressWarnings("unused")
	private Handler handler;
	private TextView viewUrl;
	private ToggleButton toogle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		TAG = "BuildOwnPawServer";

		// thư mục trên điện thoại dùng cho lưu trữ mọi thứ trong "assets"
		INSTALL_DIR = Environment.getExternalStorageDirectory().getPath()
				+ "/www";

		calledFromRuntime = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_connection);
		viewUrl = (TextView) findViewById(R.id.url);
		toogle = (ToggleButton) findViewById(R.id.toggleButton1);
		// Thiết lập mặc định cho nút bấm toggle.
		toogle.setChecked(true);

		// Khởi động service.
		startServices();

		handler = new Handler();

		/*
		 * Kiểm tra thư mục INSTALL_DIR
		 * 
		 * copy toàn bộ file content.zip trong thư mục assets vào "INSTALL_DIR"
		 * trên điện thoại.
		 */
		checkInstallation();

		/*
		 * Phiên bản khác của checkInstallation
		 * 
		 * Dùng trong việc copy toàn bộ thư mục trong assets vào INSTALL_DIR.
		 * 
		 * Dùng trong quá trình cài đặt code ứng dụng. Tránh việc phải nén thư
		 * mục trong.
		 */
		// checkInstallationTmp();

		messageHandler = new MessageHandler(this);
		MyServerService.setActivityHandler(messageHandler);
		MyServerService.setActivity(this);
	}

	/*
	 * Bắt sự kiện cho nút toggle.
	 */
	public void onToggleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();

		if (on)
			startServices();
		else
			stopServices();
	}

	/**
	 * Cài đè các phương thức thừa kế từ PawServerActivity.
	 */
	@Override
	public void onResume() {
		super.onResume();
		MyServerService.registerServiceListener(this);
		startService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService();
		MyServerService.unregisterServiceListener(this);
	}

	@Override
	public void stopService() {
		// Do nothing
	}

	// Tắt service.
	public void stopServices() {
		Intent serviceIntent = new Intent(this, MyServerService.class);
		stopService(serviceIntent);
	}

	@Override
	public void startService() {
		// do nothing
	}

	// Khởi động service.
	public void startServices() {
		// Kiểm tra trạng thái của Service.
		if (MyServerService.isRunning()) {
			return;
		}

		Intent serviceIntent = new Intent(MyServerActivity.this,
				MyServerService.class);

		startService(serviceIntent);
	}

	/**
	 * Gọi khi service được khởi động.
	 * 
	 * @param success
	 *            <code>true</code> service khởi động thành công.
	 *            <code>false</code> service khởi động lỗi.
	 */
	@Override
	public void onServiceStart(boolean success) {
		if (success) {
			PawServerService service = MyServerService.getService();
			final String url = service.getPawServer().server.protocol + "://"
					+ Utils.getLocalIpAddress() + ":"
					+ service.getPawServer().serverPort;

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					viewUrl.setText(url);
				}
			});
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					viewUrl.setText("Server could not be started!");
				}
			});
		}

	}

	// Gọi khi service tắt.
	@Override
	public void onServiceStop(boolean success) {
	}

	// cài đặt checkInstallation.

	private void checkInstallation() {
		File installDir = new File(INSTALL_DIR);
		// kiểm tra sự tồn tại của thư mục INSTALL_DIR.
		if (installDir.exists())
			deleteRecursive(installDir);

		// tạo thư mục INSTALL_DIR trên điện thoại.
		installDir.mkdirs();

		// những file không cần ghi đè.
		HashMap<String, Integer> keepFiles = new HashMap<String, Integer>();

		// Giải nén file content.zip trong assets.
		try {
			extractZip(getAssets().open("content.zip"), INSTALL_DIR, keepFiles);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	// xóa thư mục trên điện thoại.
	private void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				deleteRecursive(child);

		fileOrDirectory.delete();
	}

	// cài đặt checkInstallationTmp.
	private void checkInstallationTmp() {
		File installdir = new File(INSTALL_DIR);
		if (installdir.exists())
			// xóa thư mục installdir.
			deleteRecursive(installdir);
		installdir.mkdirs();
		// Copy file
		copyFileOrDir("");
	}

	private void copyFileOrDir(String path) {
		AssetManager assetManager = this.getAssets();
		String assets[] = null;
		try {
			Log.i("tag1", "copyFileOrDir() " + path);
			assets = assetManager.list(path);
			if (assets.length == 0) {
				copyFile(path);
			} else {
				String fullPath = INSTALL_DIR + "/" + path;
				Log.i("tag2", "path=" + fullPath);
				File dir = new File(fullPath);
				if (!dir.exists())
					if (!dir.mkdirs())
						;
				Log.i("tag3", "could not create dir " + fullPath);
				for (int i = 0; i < assets.length; ++i) {
					String p;
					if (path.equals(""))
						p = "";
					else
						p = path + "/";

					copyFileOrDir(p + assets[i]);
				}
			}
		} catch (IOException ex) {
			Log.e("tag4", "I/O Exception", ex);
		}
	}

	private void copyFile(String filename) {
		AssetManager assetManager = this.getAssets();

		InputStream in = null;
		OutputStream out = null;
		String newFileName = null;
		try {
			Log.i("tag5", "copyFile() " + filename);
			in = assetManager.open(filename);
			if (filename.endsWith(".jpg"))
				newFileName = INSTALL_DIR + "/"
						+ filename.substring(0, filename.length() - 4);
			else
				newFileName = INSTALL_DIR + "/" + filename;
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag6", "Exception in copyFile() of " + newFileName);
			Log.e("tag7", "Exception in copyFile() " + e.toString());
		}

	}

}