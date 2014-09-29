package com.airdroid;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airdroid.R;
import com.airdroid.helper.FilesEventHandler;
import com.airdroid.tools.files.FilesManager;


public class FilesActivity extends ListActivity {
	public static final String ACTION_WIDGET = "com.airdroid.tools.files.ACTION_WIDGET";

	private static final String PREFS_NAME = "ManagerPrefsFile";

	private static final String PREFS_HIDDEN = "hidden";
	private static final String PREFS_COLOR = "black";
	private static final String PREFS_THUMBNAIL = "thumbnail";
	private static final String PREFS_SORT = "sort";
	private static final String PREFS_STORAGE = "sdcard space";

	// ID quan ly cac thanh phan cua Option Menu
	private static final int MENU_MKDIR = 0x00;
	private static final int MENU_SETTING = 0x01;
	private static final int MENU_SEARCH = 0x02;
	private static final int MENU_SPACE = 0x03;
	private static final int MENU_REFRESH = 0x04;
	private static final int MENU_SORT = 0x05;
	private static final int MENU_HIDDEN = 0x06;
	private static final int SEARCH_B = 0x09;
	private static final int SORT_NAME = 0x11;
	private static final int SORT_SIZE = 0x12;
	private static final int SORT_DATE = 0x13;
	private static final int SORT_TYPE = 0x14;
	private int sort_state;

	// ID quan ly cac thanh phan cua Context Menu
	private static final int D_MENU_DELETE = 0x05;
	private static final int D_MENU_RENAME = 0x06;
	private static final int D_MENU_COPY = 0x07;
	private static final int D_MENU_PASTE = 0x08;
	private static final int D_MENU_ZIP = 0x0e;
	private static final int D_MENU_MOVE = 0x30;
	private static final int F_MENU_MOVE = 0x20;
	private static final int F_MENU_DELETE = 0x0a;
	private static final int F_MENU_RENAME = 0x0b;
	private static final int F_MENU_COPY = 0x0d;
	private static final int SETTING_REQ = 0x10;

	private boolean mSortChanged = false;
	private FilesManager mFileMag;
	private FilesEventHandler mHandler;
	private FilesEventHandler.TableRow mTable;

	private SharedPreferences mSettings;
	private boolean mReturnIntent = false;
	private boolean mHoldingFile = false;
	private boolean mHoldingZip = false;
	private boolean mUseBackKey = true;
	private String mCopiedTarget;
	private String mZippedTarget;
	private String mSelectedListItem;
	private TextView mDetailLabel;
	private Button mPathLabel;
	private TextView tPathLabel;
	private Intent is = new Intent();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.files_manager);

		/* Doc cac thiet lap */
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		boolean hide = mSettings.getBoolean(PREFS_HIDDEN, true);
		boolean thumb = mSettings.getBoolean(PREFS_THUMBNAIL, false);
		int sort = mSettings.getInt(PREFS_SORT, 1);

		mFileMag = new FilesManager();
		mFileMag.setShowHiddenFiles(hide);
		mFileMag.setSortType(sort);

		if (savedInstanceState != null)
			mHandler = new FilesEventHandler(FilesActivity.this, mFileMag,
					savedInstanceState.getString("location"));
		else
			mHandler = new FilesEventHandler(FilesActivity.this, mFileMag);

		mHandler.setTextColor(Color.BLACK);
		mHandler.setShowThumbnails(thumb);
		mTable = mHandler.new TableRow();

		// Cai ListAdapter
		mHandler.setListAdapter(mTable);
		setListAdapter(mTable);

		// Dang ky ContextMenu cho ListView
		registerForContextMenu(getListView());

		// Button cua Context Menu
		int[] button_id = { R.id.hidden_copy, R.id.hidden_delete,
				R.id.hidden_move };
		Button[] bt = new Button[button_id.length];
		
		CheckBox cbx = (CheckBox) findViewById(R.id.checkBox1);
		
		for (int i = 0; i < button_id.length; i++) {
			bt[i] = (Button) findViewById(button_id[i]);
			bt[i].setOnClickListener(mHandler);
		}

		Intent intent = getIntent();

		if (intent.getAction().equals(Intent.ACTION_GET_CONTENT)) {
			cbx.setVisibility(View.GONE);
			mReturnIntent = true;

		} else if (intent.getAction().equals(ACTION_WIDGET)) {
			Log.e("FILE ACTIVITY", "Widget action, string = "
					+ intent.getExtras().getString("folder"));
			mHandler.updateDirectory(mFileMag.getNextDir(intent.getExtras()
					.getString("folder"), true));

		}

		mPathLabel = (Button) findViewById(R.id.pathlabel);
		
		mPathLabel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(R.id.pathlabel);
			}
		});

		mHandler.setUpdateLabels(mPathLabel, mDetailLabel);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("location", mFileMag.getCurrentDir());
	}

	private void returnIntentResults(File data) {
		mReturnIntent = false;

		Intent ret = new Intent();
		ret.setData(Uri.fromFile(data));
		setResult(RESULT_OK, ret);

		finish();
	}

	/**
	 * De them chuc nang va cho phep nguoi dung tuong tac voi nhieu loai file
	 * hon.
	 * 
	 */
	@Override
	public void onListItemClick(ListView parent, View view, int position,
			long id) {
		final String item = mHandler.getData(position);
		boolean multiSelect = mHandler.isMultiSelected();
		File file = new File(mFileMag.getCurrentDir() + "/" + item);
		String item_ext = null;

		try {
			item_ext = item.substring(item.lastIndexOf("."), item.length());

		} catch (IndexOutOfBoundsException e) {
			item_ext = "";
		}

		/*
		 * Neu nguoi dung chon nhieu muc cung luc, ta chi can ghi vao file,
		 * khong can tao intent.
		 */
		if (multiSelect) {
			mTable.addMultiPosition(position, file.getPath());

		} else {
			if (file.isDirectory()) {
				if (file.canRead()) {
					mHandler.stopThumbnailThread();
					mHandler.updateDirectory(mFileMag.getNextDir(item, false));
					mPathLabel.setText(mFileMag.getCurrentDir());

					/*
					 * 
					 * Thiet lap nut back chuyen thanh true.
					 */
					if (!mUseBackKey)
						mUseBackKey = true;

				} else {
					Toast.makeText(this,
							"Can't read folder due to permissions",
							Toast.LENGTH_SHORT).show();
				}
			}

			/* File nhac duoc chon */
			else if (item_ext.equalsIgnoreCase(".mp3")
					|| item_ext.equalsIgnoreCase(".m4a")
					|| item_ext.equalsIgnoreCase(".mp4")) {

				if (mReturnIntent) {
					returnIntentResults(file);
				} else {
					Intent i = new Intent();
					i.setAction(android.content.Intent.ACTION_VIEW);
					i.setDataAndType(Uri.fromFile(file), "audio/*");
					startActivity(i);
				}
			}

			/* File anh duoc chon */
			else if (item_ext.equalsIgnoreCase(".jpeg")
					|| item_ext.equalsIgnoreCase(".jpg")
					|| item_ext.equalsIgnoreCase(".png")
					|| item_ext.equalsIgnoreCase(".gif")
					|| item_ext.equalsIgnoreCase(".tiff")) {

				if (file.exists()) {
					if (mReturnIntent) {
						returnIntentResults(file);

					} else {
						Intent picIntent = new Intent();
						picIntent.setAction(android.content.Intent.ACTION_VIEW);
						picIntent.setDataAndType(Uri.fromFile(file), "image/*");
						startActivity(picIntent);
					}
				}
			}

			/* File video duoc chon */
			else if (item_ext.equalsIgnoreCase(".m4v")
					|| item_ext.equalsIgnoreCase(".3gp")
					|| item_ext.equalsIgnoreCase(".wmv")
					|| item_ext.equalsIgnoreCase(".mp4")
					|| item_ext.equalsIgnoreCase(".ogg")
					|| item_ext.equalsIgnoreCase(".wav")) {

				if (file.exists()) {
					if (mReturnIntent) {
						returnIntentResults(file);

					} else {
						Intent movieIntent = new Intent();
						movieIntent
								.setAction(android.content.Intent.ACTION_VIEW);
						movieIntent.setDataAndType(Uri.fromFile(file),
								"video/*");
						startActivity(movieIntent);
					}
				}
			}

			/* File zip */
			else if (item_ext.equalsIgnoreCase(".zip")) {

				if (mReturnIntent) {
					returnIntentResults(file);

				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					AlertDialog alert;
					mZippedTarget = mFileMag.getCurrentDir() + "/" + item;
					alert = builder.create();
					alert.show();
				}
			}

			/* file pdf duoc chon */
			else if (item_ext.equalsIgnoreCase(".pdf")) {

				if (file.exists()) {
					if (mReturnIntent) {
						returnIntentResults(file);

					} else {
						Intent pdfIntent = new Intent();
						pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
						pdfIntent.setDataAndType(Uri.fromFile(file),
								"application/pdf");

						try {
							startActivity(pdfIntent);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(this,
									"Sorry, couldn't find a pdf viewer",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}

			/* file ung dung android */
			else if (item_ext.equalsIgnoreCase(".apk")) {

				if (file.exists()) {
					if (mReturnIntent) {
						returnIntentResults(file);

					} else {
						Intent apkIntent = new Intent();
						apkIntent.setAction(android.content.Intent.ACTION_VIEW);
						apkIntent.setDataAndType(Uri.fromFile(file),
								"application/vnd.android.package-archive");
						startActivity(apkIntent);
					}
				}
			}

			/* HTML file */
			else if (item_ext.equalsIgnoreCase(".html")) {

				if (file.exists()) {
					if (mReturnIntent) {
						returnIntentResults(file);

					} else {
						Intent htmlIntent = new Intent();
						htmlIntent
								.setAction(android.content.Intent.ACTION_VIEW);
						htmlIntent.setDataAndType(Uri.fromFile(file),
								"text/html");

						try {
							startActivity(htmlIntent);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(this,
									"Sorry, couldn't find a HTML viewer",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}

			/* text file */
			else if (item_ext.equalsIgnoreCase(".txt")) {

				if (file.exists()) {
					if (mReturnIntent) {
						returnIntentResults(file);

					} else {
						Intent txtIntent = new Intent();
						txtIntent.setAction(android.content.Intent.ACTION_VIEW);
						txtIntent.setDataAndType(Uri.fromFile(file),
								"text/plain");

						try {
							startActivity(txtIntent);
						} catch (ActivityNotFoundException e) {
							txtIntent.setType("text/*");
							startActivity(txtIntent);
						}
					}
				}
			}

			/* Intent tong quat */
			else {
				if (file.exists()) {
					if (mReturnIntent) {
						returnIntentResults(file);

					} else {
						Intent generic = new Intent();
						generic.setAction(android.content.Intent.ACTION_VIEW);
						generic.setDataAndType(Uri.fromFile(file), "text/plain");

						try {
							startActivity(generic);
						} catch (ActivityNotFoundException e) {
							Toast.makeText(
									this,
									"Sorry, couldn't find anything "
											+ "to open " + file.getName(),
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		SharedPreferences.Editor editor = mSettings.edit();
		boolean check;
		boolean thumbnail;
		int color, sort, space;

		if (requestCode == SETTING_REQ && resultCode == RESULT_CANCELED) {

			// Luu lai thong tin ta lay duoc tu cac hanh dong cai dat
			check = data.getBooleanExtra("HIDDEN", false);
			thumbnail = data.getBooleanExtra("THUMBNAIL", true);
			sort = data.getIntExtra("SORT", 0);

			editor.putBoolean(PREFS_HIDDEN, check);
			editor.putBoolean(PREFS_THUMBNAIL, thumbnail);
			editor.putInt(PREFS_SORT, sort);
			editor.commit();

			mFileMag.setShowHiddenFiles(check);
			mFileMag.setSortType(sort);
			mHandler.setShowThumbnails(thumbnail);
			mHandler.updateDirectory(mFileMag.getNextDir(
					mFileMag.getCurrentDir(), true));
		}
	}

	/*
	 * Menu, options menu va context menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMenu = menu.addSubMenu(Menu.NONE, MENU_SORT, 0, "Sort by");
		subMenu.add(11, SORT_NAME, 0, "Name");
		subMenu.add(11, SORT_SIZE, 0, "Size");
		subMenu.add(11, SORT_DATE, 0, "Date");
		subMenu.add(11, SORT_TYPE, 0, "Type");
		subMenu.setGroupCheckable(11, true, true);
		menu.add(0, MENU_MKDIR, 0, "New directory").setIcon(
				R.drawable.fm_ic_menu_new_folder);
		menu.add(0, MENU_HIDDEN, 0, "Show hidden").setIcon(
				R.drawable.fm_ic_menu_show_sys);
		menu.add(0, MENU_REFRESH, 0, "Refresh").setIcon(
				R.drawable.fm_ic_menu_refresh);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		switch (item.getItemId()) {
		case MENU_SORT:
			Toast.makeText(this, "Clicked: Menu Sort", Toast.LENGTH_SHORT)
					.show();
			return true;
		case SORT_NAME:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			Toast.makeText(this, "Sorted by name", Toast.LENGTH_SHORT).show();
			int sort = mSettings.getInt(PREFS_SORT, 1);
			mFileMag.setSortType(sort);
			return true;
		case SORT_SIZE:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			Toast.makeText(this, "Sorted by size", Toast.LENGTH_SHORT).show();
			int sort2 = mSettings.getInt(PREFS_SORT, 3);
			mFileMag.setSortType(sort2);
			return true;
		case SORT_DATE:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			Toast.makeText(this, "Sorted by date", Toast.LENGTH_SHORT).show();
			int sort3 = mSettings.getInt(PREFS_SORT, 2);
			mFileMag.setSortType(sort3);
			return true;
		case SORT_TYPE:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			Toast.makeText(this, "Sorted by type", Toast.LENGTH_SHORT).show();
			return true;

		case MENU_MKDIR:
			showDialog(MENU_MKDIR);
			return true;

		case MENU_HIDDEN:
			mSettings = getSharedPreferences(PREFS_NAME, 0);
			boolean hide = mSettings.getBoolean(PREFS_HIDDEN, true);
			mFileMag.setShowHiddenFiles(!hide);
			return true;

		case MENU_REFRESH:
			finish();
			return true;
		}

		return false;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, View v,
			ContextMenuInfo info) {
		super.onCreateContextMenu(menu, v, info);

		final boolean multi_data = mHandler.hasMultiSelectData();
		AdapterContextMenuInfo _info = (AdapterContextMenuInfo) info;
		mSelectedListItem = mHandler.getData(_info.position);
		CheckBox check = (CheckBox) findViewById(R.id.checkBox1);

		/*
		 * Kiem tra xem co phai la duong dan khong va co phai che do
		 * multi-select da duoc tat khong
		 */
		if (mFileMag.isDirectory(mSelectedListItem)
				&& !mHandler.isMultiSelected()) {
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					menu.setHeaderTitle("Folder operations");
					menu.add(0, D_MENU_DELETE, 0, "Delete Folder");
					menu.add(0, D_MENU_RENAME, 0, "Rename Folder");
					menu.add(0, D_MENU_COPY, 0, "Copy Folder");
					menu.add(0, D_MENU_MOVE, 0, "Move(Cut) Folder");
					menu.add(0, D_MENU_ZIP, 0, "Zip Folder");
					menu.add(0, D_MENU_PASTE, 0, "Paste into folder")
							.setEnabled(mHoldingFile || multi_data);
				}
			});

		} else if (!mFileMag.isDirectory(mSelectedListItem)
				&& !mHandler.isMultiSelected()) {
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					menu.setHeaderTitle("File Operations");
					menu.add(0, F_MENU_DELETE, 0, "Delete File");
					menu.add(0, F_MENU_RENAME, 0, "Rename File");
					menu.add(0, F_MENU_COPY, 0, "Copy File");
					menu.add(0, F_MENU_MOVE, 0, "Move(Cut) File");
				}
			});
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case D_MENU_DELETE:
		case F_MENU_DELETE:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning ");
			builder.setIcon(R.drawable.warning);
			builder.setMessage("Deleting " + mSelectedListItem
					+ " cannot be undone. Are you sure you want to delete?");
			builder.setCancelable(false);

			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mHandler.deleteFile(mFileMag.getCurrentDir() + "/"
									+ mSelectedListItem);
						}
					});
			AlertDialog alert_d = builder.create();
			alert_d.show();
			return true;

		case D_MENU_RENAME:
			showDialog(D_MENU_RENAME);
			return true;

		case F_MENU_RENAME:
			showDialog(F_MENU_RENAME);
			return true;

		case F_MENU_MOVE:
		case D_MENU_MOVE:
		case F_MENU_COPY:
		case D_MENU_COPY:
			if (item.getItemId() == F_MENU_MOVE
					|| item.getItemId() == D_MENU_MOVE)
				mHandler.setDeleteAfterCopy(true);

			mHoldingFile = true;

			mCopiedTarget = mFileMag.getCurrentDir() + "/" + mSelectedListItem;
			mDetailLabel.setText("Holding " + mSelectedListItem);
			return true;

		case D_MENU_PASTE:
			boolean multi_select = mHandler.hasMultiSelectData();

			if (multi_select) {
				mHandler.copyFileMultiSelect(mFileMag.getCurrentDir() + "/"
						+ mSelectedListItem);

			} else if (mHoldingFile && mCopiedTarget.length() > 1) {

				mHandler.copyFile(mCopiedTarget, mFileMag.getCurrentDir() + "/"
						+ mSelectedListItem);
				mDetailLabel.setText("");
			}

			mHoldingFile = false;
			return true;

		case D_MENU_ZIP:
			String dir = mFileMag.getCurrentDir();

			mHandler.zipFile(dir + "/" + mSelectedListItem);
			return true;

		}
		return false;
	}

	/*
	 * Ket thuc phan Menu, Option Menu, va Contex Menu
	 */

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog = new Dialog(FilesActivity.this);

		switch (id) {
		case R.id.pathlabel:
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.files_view_dir);
			dialog.setCancelable(false);
			TextView label = (TextView) dialog.findViewById(R.id.dir_view);
			label.setText(mFileMag.getCurrentDir());
			Button cancel = (Button) dialog.findViewById(R.id.cancel);
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			break;
		case MENU_MKDIR:
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.files_input_layout);
			dialog.setCancelable(false);

			TextView dir = (TextView) dialog.findViewById(R.id.input_label);
			dir.setText(mFileMag.getCurrentDir());
			final EditText input = (EditText) dialog
					.findViewById(R.id.input_inputText);

			Button cancel1 = (Button) dialog.findViewById(R.id.input_cancel_b);
			Button create = (Button) dialog.findViewById(R.id.input_create_b);

			create.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (input.getText().length() > 1) {
						if (mFileMag.createDir(mFileMag.getCurrentDir() + "/",
								input.getText().toString()) == true)
							Toast.makeText(
									FilesActivity.this,
									"Folder " + input.getText().toString()
											+ " created", Toast.LENGTH_LONG)
									.show();
						else
							Toast.makeText(FilesActivity.this,
									"New folder was not created",
									Toast.LENGTH_SHORT).show();
					}

					dialog.dismiss();
					String temp = mFileMag.getCurrentDir();
					mHandler.updateDirectory(mFileMag.getNextDir(temp, true));
				}
			});
			cancel1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		case D_MENU_RENAME:
		case F_MENU_RENAME:
			dialog.setContentView(R.layout.files_input_layout);
			dialog.setTitle("Rename " + mSelectedListItem);
			dialog.setCancelable(false);

			TextView rename_label = (TextView) dialog
					.findViewById(R.id.input_label);
			rename_label.setText("Rename");
			final EditText rename_input = (EditText) dialog
					.findViewById(R.id.input_inputText);

			Button rename_cancel = (Button) dialog
					.findViewById(R.id.input_cancel_b);
			Button rename_create = (Button) dialog
					.findViewById(R.id.input_create_b);
			rename_create.setText("Rename");

			rename_create.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (rename_input.getText().length() < 1)
						dialog.dismiss();

					if (mFileMag.renameDir(mFileMag.getCurrentDir() + "/"
							+ mSelectedListItem, rename_input.getText()
							.toString()) == false) {
						Toast.makeText(
								FilesActivity.this,
								mSelectedListItem + " was renamed to "
										+ rename_input.getText().toString(),
								Toast.LENGTH_LONG).show();
					} else
						Toast.makeText(FilesActivity.this,
								mSelectedListItem + " was not renamed",
								Toast.LENGTH_LONG).show();

					dialog.dismiss();
					String temp = mFileMag.getCurrentDir();
					mHandler.updateDirectory(mFileMag.getNextDir(temp, true));
				}
			});
			rename_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;

		}
		return dialog;
	}

	/*
	 * (non-Javadoc) Phuong thuc nay se check co phai nguoi dung dang o thu muc
	 * goc khong. Neu dung thi neu ho nhan back 1 lan nua thi dong ung dung.
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		String current = mFileMag.getCurrentDir();

		if (keycode == KeyEvent.KEYCODE_SEARCH) {
			showDialog(SEARCH_B);

			return true;

		} else if (keycode == KeyEvent.KEYCODE_BACK && mUseBackKey
				&& !current.equals("/")) {
			if (mHandler.isMultiSelected()) {
				mTable.killMultiSelect(true);
				Toast.makeText(FilesActivity.this, "Multi-select is now off",
						Toast.LENGTH_SHORT).show();

			} else {
				// Dung viec update thumbnail neu no dang chay
				mHandler.stopThumbnailThread();
				mHandler.updateDirectory(mFileMag.getPreviousDir());
				mPathLabel.setText(mFileMag.getCurrentDir());
			}
			return true;

		} else if (keycode == KeyEvent.KEYCODE_BACK && mUseBackKey
				&& current.equals("/")) {
			Toast.makeText(FilesActivity.this, "Press back again to quit.",
					Toast.LENGTH_SHORT).show();

			if (mHandler.isMultiSelected()) {
				mTable.killMultiSelect(true);
				Toast.makeText(FilesActivity.this, "Multi-select is now off",
						Toast.LENGTH_SHORT).show();
			}

			mUseBackKey = false;
			mPathLabel.setText(mFileMag.getCurrentDir());

			return false;

		} else if (keycode == KeyEvent.KEYCODE_BACK && !mUseBackKey
				&& current.equals("/")) {
			finish();

			return false;
		}
		return false;
	}
}
