package com.airdroid.server;

import de.fun2code.android.pawserver.PawServerService;
import com.airdroid.R;

/**
 * 
 * @author hongduongvu93
 *
 * Lớp Service thừa kế từ lớp PawServerService.
 */
public class MyServerService extends PawServerService {

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	/*
	 * Service options are: TAG = Tag name for message logging. startOnBoot =
	 * Indicates if service has been started on boot. isRuntime = If set to true
	 * this will only allow local connections. serverConfig = Path to server
	 * configuration directory. pawHome = PAW installation directory.
	 * useWakeLock = Switch wakelock on or off. hideNotificationIcon = Set to
	 * true if no notifications should be shown. execAutostartScripts = Set to
	 * true if scripts inside the autostart directory should be executed
	 * onstartup. showUrlInNotification = Set to true if URL should be shown in
	 * notification. notificationTitle = The notification title.
	 * notificationMessage = The notification message. appName = Application
	 * name" activityClass = Activity class name. notificationDrawableId = ID of
	 * the notification icon to display.
	 */

	private void init() {
		TAG = getString(R.string.app_name);
		startedOnBoot = true;
		isRuntime = false;
		serverConfig = MyServerActivity.INSTALL_DIR + "/conf/server.xml";
		pawHome = MyServerActivity.INSTALL_DIR + "/";
		useWakeLock = true;
		hideNotificationIcon = false;
		execAutostartScripts = false;
		showUrlInNotification = false;
		notificationTitle = "Service is running";
		notificationMessage = "Notification Message";
		appName = getString(R.string.app_name);
		activityClass = "com.airdroid.server.MyServerActivity";
		notificationDrawableId = R.drawable.ml_ic_notification;
	}

}
