package com.airdroid.tools.apps;

/**
 * 
 * @author hongduongvu93
 *
 * Interface cho Apps.
 */

public interface Apps {
	
	// Chi tiết ứng dụng.
	public void detail(PackageInf app);

    //chạy ứng dụng
    public void run(PackageInf app);

    //gỡ ứng dụng PackageInf.
    public boolean uninstall(PackageInf app);

    // chia sẻ ứng dụng thông qua bluetooth, email...
    public void share(PackageInf app);
}
