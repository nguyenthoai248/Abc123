package com.airdroid.helper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airdroid.R;
import com.airdroid.tools.files.FilesManager;

/**
 * Lop nay dung giua lop Main activity va lop FileManager, lop nay dung de xu ly
 * cac su kien lien quan den giao dien nguoi dung va truyen thong tin do cho lop
 * FileManager.
 */
public class FilesEventHandler implements OnClickListener {
	/*
	 * ID de kiem soat cac hanh dong voi file
	 */
	private static final int SEARCH_TYPE = 0x00;
	private static final int COPY_TYPE = 0x01;
	private static final int ZIP_TYPE = 0x04;
	private static final int DELETE_TYPE = 0x05;
	private static final int MANAGE_DIALOG = 0x06;

	private final Context mContext;
	private final FilesManager mFileMang;
	private TableRow mDelegate;
	private FilesThumbnailCreator mThumbnail;

	private boolean multi_select_flag = false;
	private boolean isCheck = false;
	private boolean delete_after_copy = false;
	private boolean thumbnail_flag = true;
	private int mColor = Color.BLACK;

	// List su dung de truyen thong tin vao cac mang adapter va khi che do
	// multi-select duoc mo.
	private ArrayList<String> mDataSource, mMultiSelectData;
	private TextView mPathLabel;
	private TextView mInfoLabel;

	/**
	 * 
	 * Tao 1 doi tuong EventHandler. Doi tuong nay duoc dung de truyen tat ca
	 * hanh dong tu Main activity den lop FileManager.
	 * 
	 * @param context
	 *            Noi dung cua Main Activity.
	 * @param manager
	 *            Doi tuong FileManager duoc the hien tu Main.
	 */
	public FilesEventHandler(Context context, final FilesManager manager) {
		mContext = context;
		mFileMang = manager;

		mDataSource = new ArrayList<String>(mFileMang.setHomeDir(Environment
				.getExternalStorageDirectory().getPath()));
	}

	public FilesEventHandler(Context context, final FilesManager manager,
			String location) {
		mContext = context;
		mFileMang = manager;

		mDataSource = new ArrayList<String>(
				mFileMang.getNextDir(location, true));
	}

	public void setListAdapter(TableRow adapter) {
		mDelegate = adapter;
	}

	public void setUpdateLabels(TextView path, TextView label) {
		mPathLabel = path;
		mInfoLabel = label;
	}

	public void setTextColor(int color) {
		mColor = color;
	}

	public void setShowThumbnails(boolean show) {
		thumbnail_flag = show;
	}

	public void setDeleteAfterCopy(boolean delete) {
		delete_after_copy = delete;
	}

	public boolean isMultiSelected() {
		return multi_select_flag;
	}

	public boolean hasMultiSelectData() {
		return (mMultiSelectData != null && mMultiSelectData.size() > 0);
	}

	public void searchForFile(String name) {
		new BackgroundWork(SEARCH_TYPE).execute(name);
	}

	public void deleteFile(String name) {
		new BackgroundWork(DELETE_TYPE).execute(name);
	}

	public void copyFile(String oldLocation, String newLocation) {
		String[] data = { oldLocation, newLocation };

		new BackgroundWork(COPY_TYPE).execute(data);
	}

	public void copyFileMultiSelect(String newLocation) {
		String[] data;
		int index = 1;

		if (mMultiSelectData.size() > 0) {
			data = new String[mMultiSelectData.size() + 1];
			data[0] = newLocation;

			for (String s : mMultiSelectData)
				data[index++] = s;

			new BackgroundWork(COPY_TYPE).execute(data);
		}
	}

	public void zipFile(String zipPath) {
		new BackgroundWork(ZIP_TYPE).execute(zipPath);
	}

	@Override
	public void onClick(View v) {
		KeyEvent event;
		switch (v.getId()) {

		case R.id.back_button:
			if (mFileMang.getCurrentDir() != "/") {
				if (multi_select_flag) {
					mDelegate.killMultiSelect(true);
					Toast.makeText(mContext, "Multi-select is now off",
							Toast.LENGTH_SHORT).show();
				}

				updateDirectory(mFileMang.getPreviousDir());
				if (mPathLabel != null)
					mPathLabel.setText(mFileMang.getCurrentDir());
			}
			break;

		case KeyEvent.KEYCODE_HOME:
			if (multi_select_flag) {
				mDelegate.killMultiSelect(true);
				Toast.makeText(mContext, "Multi-select is now off",
						Toast.LENGTH_SHORT).show();
			}

			updateDirectory(mFileMang.setHomeDir("/sdcard"));
			if (mPathLabel != null)
				mPathLabel.setText(mFileMang.getCurrentDir());
			break;
		case R.id.checkBox1:
			if (multi_select_flag) {
				mDelegate.killMultiSelect(true);

			} else {
				LinearLayout hidden_lay = (LinearLayout) ((Activity) mContext)
						.findViewById(R.id.hidden_buttons);

				multi_select_flag = true;
				hidden_lay.setVisibility(LinearLayout.VISIBLE);
			}
			break;
		case R.id.hidden_move:
		case R.id.hidden_copy:

			if (mMultiSelectData == null || mMultiSelectData.isEmpty()) {
				mDelegate.killMultiSelect(true);
				break;
			}

			if (v.getId() == R.id.hidden_move)
				delete_after_copy = true;

			mInfoLabel.setText("Holding " + mMultiSelectData.size()
					+ " file(s)");

			mDelegate.killMultiSelect(false);
			break;

		case R.id.hidden_delete:

			if (mMultiSelectData == null || mMultiSelectData.isEmpty()) {
				mDelegate.killMultiSelect(true);
				break;
			}

			final String[] data = new String[mMultiSelectData.size()];
			int at = 0;

			for (String string : mMultiSelectData)
				data[at++] = string;

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("Are you sure you want to delete " + data.length
					+ " files? This cannot be " + "undone.");
			builder.setCancelable(false);
			builder.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new BackgroundWork(DELETE_TYPE).execute(data);
							mDelegate.killMultiSelect(true);
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mDelegate.killMultiSelect(true);
							dialog.cancel();
						}
					});

			builder.create().show();
			break;
		}
	}
	
	public String getData(int position) {

		if (position > mDataSource.size() - 1 || position < 0)
			return null;

		return mDataSource.get(position);
	}

	public void updateDirectory(ArrayList<String> content) {
		if (!mDataSource.isEmpty())
			mDataSource.clear();

		for (String data : content)
			mDataSource.add(data);

		mDelegate.notifyDataSetChanged();
	}

	private static class ViewHolder {
		TextView topView;
		TextView bottomView;
		ImageView icon;
		CheckBox mCheck;
	}
	
	
	public class TableRow extends ArrayAdapter<String> {
		private final int KB = 1024;
		private final int MG = KB * KB;
		private final int GB = MG * KB;
		private String display_size;
		private ArrayList<Integer> positions;
		private LinearLayout hidden_layout;

		public TableRow() {
			super(mContext, R.layout.files_row, mDataSource);
		}

		public void addMultiPosition(int index, String path) {
			if (positions == null)
				positions = new ArrayList<Integer>();

			if (mMultiSelectData == null) {
				positions.add(index);
				add_multiSelect_file(path);

			} else if (mMultiSelectData.contains(path)) {
				if (positions.contains(index))
					positions.remove(new Integer(index));

				mMultiSelectData.remove(path);

			} else {
				positions.add(index);
				add_multiSelect_file(path);
			}

			notifyDataSetChanged();
		}

		public void killMultiSelect(boolean clearData) {
			hidden_layout = (LinearLayout) ((Activity) mContext)
					.findViewById(R.id.hidden_buttons);
			hidden_layout.setVisibility(LinearLayout.GONE);
			multi_select_flag = false;

			if (positions != null && !positions.isEmpty())
				positions.clear();

			if (clearData)
				if (mMultiSelectData != null && !mMultiSelectData.isEmpty())
					mMultiSelectData.clear();

			notifyDataSetChanged();
		}

		public String getTimeModified(File file) {
			Date lastModified = new Date(file.lastModified());
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			String formattedDateString = formatter.format(lastModified);

			return formattedDateString;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder mViewHolder;
			int num_items = 0;
			String temp = mFileMang.getCurrentDir();
			File file = new File(temp + "/" + mDataSource.get(position));
			String time = getTimeModified(file);
			String[] list = file.list();
			
			if (list != null)
				num_items = list.length;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.files_row, parent,
						false);

				mViewHolder = new ViewHolder();
				mViewHolder.topView = (TextView) convertView
						.findViewById(R.id.top_view);
				mViewHolder.bottomView = (TextView) convertView
						.findViewById(R.id.bottom_view);
				mViewHolder.icon = (ImageView) convertView
						.findViewById(R.id.row_image);
				mViewHolder.mCheck = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				mViewHolder.mCheck.setOnClickListener(FilesEventHandler.this);

				convertView.setTag(mViewHolder);

			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}

			mViewHolder.topView.setTextColor(mColor);
			mViewHolder.bottomView.setTextColor(mColor);

			if (mThumbnail == null)
				mThumbnail = new FilesThumbnailCreator(52, 52);

			if (file != null && file.isFile()) {
				String ext = file.toString();
				String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);

				if (sub_ext.equalsIgnoreCase("pdf")) {
					mViewHolder.icon.setImageResource(R.drawable.fm_icon_pdf);

				} else if (sub_ext.equalsIgnoreCase("mp3")
						|| sub_ext.equalsIgnoreCase("wma")
						|| sub_ext.equalsIgnoreCase("m4a")
						|| sub_ext.equalsIgnoreCase("m4p")) {

					mViewHolder.icon.setImageResource(R.drawable.fm_icon_mp3);

				} else if (sub_ext.equalsIgnoreCase("png")
						|| sub_ext.equalsIgnoreCase("jpg")
						|| sub_ext.equalsIgnoreCase("jpeg")
						|| sub_ext.equalsIgnoreCase("gif")
						|| sub_ext.equalsIgnoreCase("tiff")) {

					if (thumbnail_flag && file.length() != 0) {
						Bitmap thumb = mThumbnail
								.isBitmapCached(file.getPath());

						if (thumb == null) {
							final Handler handle = new Handler(
									new Handler.Callback() {
										public boolean handleMessage(Message msg) {
											notifyDataSetChanged();

											return true;
										}
									});

						} else {
							mViewHolder.icon.setImageBitmap(thumb);
						}

					} else {
						mViewHolder.icon
								.setImageResource(R.drawable.fm_icon_picture);
					}

				} else if (sub_ext.equalsIgnoreCase("zip")
						|| sub_ext.equalsIgnoreCase("gzip")
						|| sub_ext.equalsIgnoreCase("gz")) {

					mViewHolder.icon.setImageResource(R.drawable.fm_icon_rar);

				} else if (sub_ext.equalsIgnoreCase("m4v")
						|| sub_ext.equalsIgnoreCase("wmv")
						|| sub_ext.equalsIgnoreCase("3gp")
						|| sub_ext.equalsIgnoreCase("mp4")) {

					mViewHolder.icon.setImageResource(R.drawable.fm_icon_video);

				} else if (sub_ext.equalsIgnoreCase("doc")
						|| sub_ext.equalsIgnoreCase("docx")) {

					mViewHolder.icon
							.setImageResource(R.drawable.fm_icon_office);

				} else if (sub_ext.equalsIgnoreCase("xls")
						|| sub_ext.equalsIgnoreCase("xlsx")) {

					mViewHolder.icon
							.setImageResource(R.drawable.fm_icon_office);

				} else if (sub_ext.equalsIgnoreCase("ppt")
						|| sub_ext.equalsIgnoreCase("pptx")) {

					mViewHolder.icon
							.setImageResource(R.drawable.fm_icon_office);

				} else if (sub_ext.equalsIgnoreCase("html")) {
					mViewHolder.icon.setImageResource(R.drawable.fm_icon_txt);

				} else if (sub_ext.equalsIgnoreCase("xml")) {
					mViewHolder.icon.setImageResource(R.drawable.fm_icon_txt);

				} else if (sub_ext.equalsIgnoreCase("conf")) {
					mViewHolder.icon.setImageResource(R.drawable.config32);

				} else if (sub_ext.equalsIgnoreCase("apk")) {
					mViewHolder.icon.setImageResource(R.drawable.appicon);

				} else if (sub_ext.equalsIgnoreCase("jar")) {
					mViewHolder.icon.setImageResource(R.drawable.jar32);

				} else {
					mViewHolder.icon.setImageResource(R.drawable.fm_icon_txt);
				}

			} else if (file != null && file.isDirectory()) {
				if (file.canRead() && file.list().length > 0)
					mViewHolder.icon
							.setImageResource(R.drawable.fm_icon_folder);
				else
					mViewHolder.icon
							.setImageResource(R.drawable.fm_icon_folder);
			}

			if (file.isFile()) {

				double size = file.length();
				if (size > GB)
					display_size = String
							.format("%.2f Gb ", (double) size / GB);
				else if (size < GB && size > MG)
					display_size = String
							.format("%.2f Mb ", (double) size / MG);
				else if (size < MG && size > KB)
					display_size = String
							.format("%.2f Kb ", (double) size / KB);
				else
					display_size = String.format("%.2f bytes ", (double) size);

				if (file.isHidden())
					mViewHolder.bottomView.setText(time);
				else
					mViewHolder.bottomView.setText(time);

			} else {
				if (file.isHidden())
					mViewHolder.bottomView.setText(time);
				else
					mViewHolder.bottomView.setText(time);
			}

			mViewHolder.topView
					.setText(file.getName() + " (" + num_items + ")");

			return convertView;
		}

		private void add_multiSelect_file(String src) {
			if (mMultiSelectData == null)
				mMultiSelectData = new ArrayList<String>();

			mMultiSelectData.add(src);
		}
	}
	
	private class BackgroundWork extends
			AsyncTask<String, Void, ArrayList<String>> {
		private String file_name;
		private ProgressDialog pr_dialog;
		private int type;
		private int copy_rtn;

		private BackgroundWork(int type) {
			this.type = type;
		}

		@Override
		protected void onPreExecute() {

			switch (type) {
			case SEARCH_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Searching",
						"Searching current file system...", true, true);
				break;

			case COPY_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Copying",
						"Copying file...", true, false);
				break;
			case ZIP_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Zipping",
						"Zipping folder...", true, false);
				break;

			case DELETE_TYPE:
				pr_dialog = ProgressDialog.show(mContext, "Deleting",
						"Deleting files...", true, false);
				break;
			}
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {

			switch (type) {
			case COPY_TYPE:
				int len = params.length;

				if (mMultiSelectData != null && !mMultiSelectData.isEmpty()) {
					for (int i = 1; i < len; i++) {
						try {
							copy_rtn = mFileMang.copyToDirectory(params[i],
									params[0]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (delete_after_copy)
							mFileMang.deleteTarget(params[i]);
					}
				} else {
					try {
						copy_rtn = mFileMang.copyToDirectory(params[0],
								params[1]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (delete_after_copy)
						mFileMang.deleteTarget(params[0]);
				}

				delete_after_copy = false;
				return null;

			case ZIP_TYPE:
				mFileMang.createZipFile(params[0]);
				return null;

			case DELETE_TYPE:
				int size = params.length;

				for (int i = 0; i < size; i++)
					mFileMang.deleteTarget(params[i]);

				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final ArrayList<String> file) {
			final CharSequence[] names;
			int len = file != null ? file.size() : 0;

			switch (type) {
			case SEARCH_TYPE:
				if (len == 0) {
					Toast.makeText(mContext, "Couldn't find " + file_name,
							Toast.LENGTH_SHORT).show();

				} else {
					names = new CharSequence[len];

					for (int i = 0; i < len; i++) {
						String entry = file.get(i);
						names[i] = entry.substring(entry.lastIndexOf("/") + 1,
								entry.length());
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext);
					builder.setTitle("Found " + len + " file(s)");
					builder.setItems(names,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int position) {
									String path = file.get(position);
									updateDirectory(mFileMang.getNextDir(
											path.substring(0,
													path.lastIndexOf("/")),
											true));
								}
							});

					AlertDialog dialog = builder.create();
					dialog.show();
				}

				pr_dialog.dismiss();
				break;

			case COPY_TYPE:
				if (mMultiSelectData != null && !mMultiSelectData.isEmpty()) {
					multi_select_flag = false;
					mMultiSelectData.clear();
				}

				if (copy_rtn == 0)
					Toast.makeText(mContext,
							"File successfully copied and pasted",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(mContext, "Copy pasted failed",
							Toast.LENGTH_SHORT).show();

				pr_dialog.dismiss();
				mInfoLabel.setText("");
				break;

			case ZIP_TYPE:
				updateDirectory(mFileMang.getNextDir(mFileMang.getCurrentDir(),
						true));
				pr_dialog.dismiss();
				break;

			case DELETE_TYPE:
				if (mMultiSelectData != null && !mMultiSelectData.isEmpty()) {
					mMultiSelectData.clear();
					multi_select_flag = false;
				}

				updateDirectory(mFileMang.getNextDir(mFileMang.getCurrentDir(),
						true));
				pr_dialog.dismiss();
				mInfoLabel.setText("");
				break;
			}
		}
	}

	public void stopThumbnailThread() {
		// TODO Auto-generated method stub
		if (mThumbnail != null) {
			mThumbnail.setCancelThumbnails(true);
			mThumbnail = null;
		}
	}
}
