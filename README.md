## Project AirDroid clone.

Gồm hai project chuẩn của Eclipse.

- AirDroid : là project chính cài đặt phần giao diện (View/Activity và Controller) và sử dụng các Lớp được cài đặt ở project Library.

- Library : là project "thư viện". Project này cài đặt các lớp (Models) được thiết kế trong tài liệu [Wiki](http://git.appota.com/duongvh/airdroidclone/wikis/home)


## Ứng dụng trên Android.

### Mô tả : 
- Cài đặt 4 tools Devices, Apps, Files và Tasks trong AirDroid.
- Công việc [wiki](http://git.appota.com/duongvh/airdroidclone/wikis/Week2)
- Ngày bắt đầu 06/08/2014.

### Giao diện.

- Màn hình splash.Đây là màn hình chào sẽ khởi động đầu tiên.
 
- Sau khi kết thúc màn hình này sẽ chuyển đến màn hình MainTabActivity.Gồm 3 tab : Connection(hiển thị thông tin kết nối của Server)
 Tools (hiển thị các icon để sử dụng các công cụ ứng dụng), Abouts( Xem thông tin về ứng dụng).
 
- Tab tools : hiển thị 4 công cụ được cài đặt.
 
- Tab Connection : Hiển thị địa chỉ  ip và nút để bặt tắt service kèm theo là thông báo cho tình trạng service.
	

## Ứng dụng trên nền web.

### Mô tả : 

- Cài đặt 4 công cụ (Diveces, Apps, Files, Tasks) trên nền web và chức năng Upload và Download file.

- Ngày bắt đầu 20/08/2014.

- Hiện tại server chạy trên mạng LAN tại : http://192.168.51.101:7575
 
- Trong thư mục .../AirDroid/assets/ chứa các thư mục html, logs, conf... Các thư mục này là nhưng thư mục cần thiết cho server hoạt động.
 Tất cả sẽ được copy vào trong INSTALL_DIR của lớp PawServerActivity. (cụ thể là "pathToSDcard/www/ nếu sử dụng thư mục cài đặt là thẻ nhớ.) 
 
- Các file cài đặt được đặt tại thư mục ...AirDroid/assets/html.

- Công việc [wiki](http://git.appota.com/duongvh/airdroidclone/wikis/Week4)

- Client Package [wiki](http://git.appota.com/duongvh/airdroidclone/wikis/ClientPackage)
 
- Trong file MyServerActivity có cài đặt 2 phương thức checkInstallation và checkInstallationTmp. ta sẽ dùng checkInstallationTmp trong 
 quá trình cài đặt code. Và checkInstallation trong quá trình push code lên git.
 
 ```java
 	public void onCreate(Bundle savedInstanceState) {
 		/* 
		 * Kiểm tra nội dung thư mục INSTALL_DIR
		 * 
		 * copy toàn bộ file content.zip trong thư mục assets vào "INSTALL_DIR" trên điện thoại.
		 *
		 * Cho hiệu suất cao. 
		 */
		checkInstallation();

		/*
		 * Phiên bản khác của checkInstallation
		 * 
		 * Dùng để copy các thư mục trong assets vào "INSTALL_DIR" trên điện thoại.
		 * 
		 * Dùng khi trong quá trình cài đặt code. Tránh việc phải nén các thư mục thành file zip.
		 *
		 * Hiệu suất thâp. Tốn rất nhiều thời gian để hoàn thành.
		 */
		//checkInstallationTmp();
	} 
```
 
  
    
