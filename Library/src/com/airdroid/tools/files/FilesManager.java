package com.airdroid.tools.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class FilesManager implements FilesInterface {

	private static final int SORT_NONE = 0;// khong sap xep
	private static final int SORT_ALPHA = 1;// theo a, b
	private static final int SORT_TYPE = 2;// theo the loai
	private static final int SORT_SIZE = 3;// theo kich thuoc file
	private static final int BUFFER = 0;

	private boolean mShowHiddenFiles = false;// co bao hien thi file an hay
												// khong
	private int mSortType = SORT_ALPHA;// kieu sap xep
	private long mDirSize = 0;// kich co file
	private Stack<String> mPathStack;// ngan chua cac duong dan
	private ArrayList<String> mDirContent;

	/* Khoi tao doi tuong cua lop FileManager */
	public FilesManager() {
		mDirContent = new ArrayList<String>();
		mPathStack = new Stack<String>();

		mPathStack.push("/");
		mPathStack.push(mPathStack.peek() + "sdcard");
	}

	/**
	 * Ham nay se tra ve xau chua duong dan hien tai
	 * 
	 * @return Duong dan hien tai
	 */
	public String getCurrentDir() {
		return mPathStack.peek();
	}

	/**
	 * Ham nay tra ve duong dan goc hien tai.
	 * 
	 * @return duong dan goc
	 */
	public ArrayList<String> setHomeDir(String name) {
		mPathStack.clear();
		mPathStack.push("/");
		mPathStack.push(name);

		return populateList();
	}

	/**
	 * Ham nay de cai dat an/hien file hay folder.
	 * 
	 * @param choice
	 *            - nhan gia tri true neu nguoi dung muon an file, nguoc lai thi
	 *            false.
	 */
	public void setShowHiddenFiles(boolean choice) {
		mShowHiddenFiles = choice;
	}

	/**
	 * 
	 * @param Kieu
	 *            sap xep.
	 */
	public void setSortType(int type) {
		mSortType = type;
	}

	/**
	 * Ham nay se tra ve xau hien thi duong dan truoc do.
	 * 
	 * @return Tra ve duong dan truoc do.
	 */
	public ArrayList<String> getPreviousDir() {
		int size = mPathStack.size();

		if (size >= 2)
			mPathStack.pop();

		else if (size == 0)
			mPathStack.push("/");

		return populateList();
	}

	/**
	 * 
	 * @param Duong
	 *            dan
	 * @param Co
	 *            phai duong dan cuoi cung khong.
	 * @return
	 */
	public ArrayList<String> getNextDir(String path, boolean isFullPath) {
		int size = mPathStack.size();

		if (!path.equals(mPathStack.peek()) && !isFullPath) {
			if (size == 1)
				mPathStack.push("/" + path);
			else
				mPathStack.push(mPathStack.peek() + "/" + path);
		}

		else if (!path.equals(mPathStack.peek()) && isFullPath) {
			mPathStack.push(path);
		}

		return populateList();
	}

	private static final Comparator alph = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			return arg0.toLowerCase().compareTo(arg1.toLowerCase());
		}
	};

	private final Comparator size = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			String dir = mPathStack.peek();
			Long first = new File(dir + "/" + arg0).length();
			Long second = new File(dir + "/" + arg1).length();

			return first.compareTo(second);
		}
	};

	private final Comparator type = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			String ext = null;
			String ext2 = null;
			int ret;

			try {
				ext = arg0.substring(arg0.lastIndexOf(".") + 1, arg0.length())
						.toLowerCase();
				ext2 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length())
						.toLowerCase();

			} catch (IndexOutOfBoundsException e) {
				return 0;
			}
			ret = ext.compareTo(ext2);

			if (ret == 0)
				return arg0.toLowerCase().compareTo(arg1.toLowerCase());

			return ret;
		}
	};

	/**
	 * copy toi thu muc moi
	 * 
	 * @param old
	 *            duong dan cu
	 * @param newDir
	 *            duong dan moi
	 * @return true neu copy duoc, false neu khong copy duoc
	 * 
	 */
	@Override
	public int copyToDirectory(String src, String targ) throws IOException {
		// TODO Auto-generated method stub
		File oldFile = new File(src);
		File tempDir = new File(targ);
		byte[] data = new byte[BUFFER];
		int read = 0;

		if (oldFile.isFile() && tempDir.isDirectory() && tempDir.canWrite()) {
			String fileName = src.substring(src.lastIndexOf("/"), src.length());
			File cpFile = new File(targ + fileName);

			try {
				BufferedOutputStream oStream = new BufferedOutputStream(
						new FileOutputStream(cpFile));
				BufferedInputStream iStream = new BufferedInputStream(
						new FileInputStream(oldFile));

				while ((read = iStream.read(data, 0, BUFFER)) != -1)
					oStream.write(data, 0, read);

				oStream.flush();
				iStream.close();
				oStream.close();

			} catch (FileNotFoundException e) {
				Log.e("FileNotFoundException", e.getMessage());
				return -1;

			} catch (IOException e) {
				Log.e("IOException", e.getMessage());
				return -1;
			}

		} else if (oldFile.isDirectory() && tempDir.isDirectory()
				&& tempDir.canWrite()) {
			String files[] = oldFile.list();
			String dir = targ
					+ src.substring(src.lastIndexOf("/"), src.length());
			int len = files.length;

			if (!new File(dir).mkdir())
				return -1;

			for (int i = 0; i < len; i++)
				copyToDirectory(src + "/" + files[i], dir);

		} else if (!tempDir.canWrite())
			return -1;

		return 0;
	}

	/**
	 * Tao 1 file nen
	 * 
	 * @param file
	 *            : Duong dan cua thu muc can tao file nen.
	 */
	@Override
	public void createZipFile(String path) {
		File dir = new File(path);
		String[] list = dir.list();
		String name = path.substring(path.lastIndexOf("/"), path.length());
		String _path;

		if (!dir.canRead() || !dir.canWrite())
			return;

		int len = list.length;

		if (path.charAt(path.length() - 1) != '/')
			_path = path + "/";
		else
			_path = path;

		try {
			ZipOutputStream zipOut = new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(_path + name
							+ ".zip"), BUFFER));

			for (int i = 0; i < len; i++)
				zipFolder(new File(_path + list[i]), zipOut);

			zipOut.close();

		} catch (FileNotFoundException e) {
			Log.e("File not found", e.getMessage());

		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		}
	}

	/*
	 * 
	 * @param file: file can nen
	 * 
	 * @param zout: luong ra de nen file
	 * 
	 * @throws IOException: Ngoai le xuat nhap.
	 */
	private void zipFolder(File file, ZipOutputStream zout) throws IOException {
		byte[] data = new byte[BUFFER];
		int read;

		if (file.isFile()) {
			ZipEntry entry = new ZipEntry(file.getName());
			zout.putNextEntry(entry);
			BufferedInputStream instream = new BufferedInputStream(
					new FileInputStream(file));

			while ((read = instream.read(data, 0, BUFFER)) != -1)
				zout.write(data, 0, read);

			zout.closeEntry();
			instream.close();

		} else if (file.isDirectory()) {
			String[] list = file.list();
			int len = list.length;

			for (int i = 0; i < len; i++)
				zipFolder(new File(file.getPath() + "/" + list[i]), zout);
		}
	}

	/**
	 * Sua lai ten file, folder
	 * 
	 * @param filePath
	 *            la duong dan
	 * @param newName
	 *            ten moi
	 * @return true neu sua duoc, false neu khong duoc
	 */
	@Override
	public boolean renameDir(String oldName, String newName) {
		File src = new File(oldName);
		String ext = "";
		File dest;

		if (src.isFile())
			/* Lay phan mo rong cua file */
			ext = oldName.substring(oldName.lastIndexOf("."), oldName.length());

		if (newName.length() < 1)
			return false;

		String temp = oldName.substring(0, oldName.lastIndexOf("/"));

		dest = new File(temp + "/" + newName + ext);
		if (src.renameTo(dest))
			return true;
		else
			return false;
	}

	/**
	 * tao 1 thu muc moi
	 * 
	 * @param path
	 *            duong dan den thu muc cha
	 * @param name
	 *            ten thu muc moi
	 */
	@Override
	public boolean createDir(String path, String name) {
		// TODO Auto-generated method stub
		int len = path.length();

		if (len < 1 || len < 1)
			return false;

		if (path.charAt(len - 1) != '/')
			path += "/";

		if (new File(path + name).mkdir())
			return true;

		return false;
	}

	/**
	 * Xoa 1 file, folder dang tro den
	 * 
	 * @param path
	 *            duong dan
	 * @return true neu xoa duoc, false neu khong duoc
	 */
	@Override
	public boolean deleteTarget(String path) {
		// TODO Auto-generated method stub
		File target = new File(path);

		if (target.exists() && target.isFile() && target.canWrite()) {
			target.delete();
			return true;
		}

		else if (target.exists() && target.isDirectory() && target.canRead()) {
			String[] fileList = target.list();

			if (fileList != null && fileList.length == 0) {
				target.delete();
				return true;

			} else if (fileList != null && fileList.length > 0) {

				for (int i = 0; i < fileList.length; i++) {
					File tempF = new File(target.getAbsolutePath() + "/"
							+ fileList[i]);

					if (tempF.isDirectory())
						deleteTarget(tempF.getAbsolutePath());
					else if (tempF.isFile())
						tempF.delete();
				}
			}
			if (target.exists())
				if (target.delete())
					return true;
		}
		return false;
	}

	/**
	 * 
	 * @param Kiem
	 *            tra name co phai duong dan khong
	 * @return
	 */
	public boolean isDirectory(String name) {
		return new File(mPathStack.peek() + "/" + name).isDirectory();
	}

	/**
	 * Tim kiem file trong 1 thu muc
	 * 
	 * @param key
	 *            tu khoa tim kiem
	 * @param duong
	 *            dan den thu muc
	 * @return Tra ve 1 danh sach cac file, thu muc co lien quan
	 */
	@Override
	public ArrayList<String> searchInDirectoty(String dir, String pathName) {
		// TODO Auto-generated method stub
		ArrayList<String> names = new ArrayList<String>();
		searchFile(dir, pathName, names);
		return null;
	}

	/*
	 * 
	 * @param dir Duong dan chua file can tim.
	 * 
	 * @param fileName Ten cua file can tim kiem.
	 * 
	 * @param n ArrayList de luu ket qua tim kiem.
	 */
	private void searchFile(String dir, String fileName, ArrayList<String> n) {
		File rootDir = new File(dir);
		String[] list = rootDir.list();

		if (list != null && rootDir.canRead()) {
			int len = list.length;

			for (int i = 0; i < len; i++) {
				File check = new File(dir + "/" + list[i]);
				String name = check.getName();

				if (check.isFile()
						&& name.toLowerCase().contains(fileName.toLowerCase())) {
					n.add(check.getPath());
				} else if (check.isDirectory()) {
					if (name.toLowerCase().contains(fileName.toLowerCase()))
						n.add(check.getPath());

					else if (check.canRead() && !dir.equals("/"))
						searchFile(check.getAbsolutePath(), fileName, n);
				}
			}
		}
	}

	/*
	 * 
	 * @param path Tra ve kich thuoc cua duong dan.
	 */
	@Override
	public long getDirSize(File path) {

		File[] list = path.listFiles();
		int len;

		if (list != null) {
			len = list.length;

			for (int i = 0; i < len; i++) {
				try {
					if (list[i].isFile() && list[i].canRead()) {
						mDirSize += list[i].length();

					} else if (list[i].isDirectory() && list[i].canRead()
							&& !isSymlink(list[i])) {
						getDirSize(list[i]);
					}
				} catch (IOException e) {
					Log.e("IOException", e.getMessage());
				}
			}
		}
		return mDirSize;
	}

	/*
	 * Lay ra danh sach file, thu muc trong 1 duong dan va sap xep
	 */
	@Override
	public ArrayList<String> populateList() {

		if (!mDirContent.isEmpty())
			mDirContent.clear();

		File file = new File(mPathStack.peek());

		if (file.exists() && file.canRead()) {
			String[] list = file.list();
			int len = list.length;

			/* Them files/folder vao arraylist, tuy theo trang thai an */
			for (int i = 0; i < len; i++) {
				if (!mShowHiddenFiles) {
					if (list[i].toString().charAt(0) != '.')
						mDirContent.add(list[i]);

				} else {
					mDirContent.add(list[i]);
				}
			}

			/* Sap xep arraylist ben tren bang vong lap */
			switch (mSortType) {
			case SORT_NONE:
				// Neu khong can sap xep
				break;

			case SORT_ALPHA:
				Object[] tt = mDirContent.toArray();
				mDirContent.clear();

				Arrays.sort(tt, alph);

				for (Object a : tt) {
					mDirContent.add((String) a);
				}
				break;

			case SORT_SIZE:
				int index = 0;
				Object[] size_ar = mDirContent.toArray();
				String dir = mPathStack.peek();

				Arrays.sort(size_ar, size);

				mDirContent.clear();
				for (Object a : size_ar) {
					if (new File(dir + "/" + (String) a).isDirectory())
						mDirContent.add(index++, (String) a);
					else
						mDirContent.add((String) a);
				}
				break;

			case SORT_TYPE:
				int dirindex = 0;
				Object[] type_ar = mDirContent.toArray();
				String current = mPathStack.peek();

				Arrays.sort(type_ar, type);
				mDirContent.clear();

				for (Object a : type_ar) {
					if (new File(current + "/" + (String) a).isDirectory())
						mDirContent.add(dirindex++, (String) a);
					else
						mDirContent.add((String) a);
				}
				break;
			}

		} else {
			mDirContent.add("Emtpy");
		}

		return mDirContent;
	}

	private static boolean isSymlink(File file) throws IOException {

		if (file == null) {
			throw new NullPointerException("File must not be null.");
		}
		if (File.separatorChar == '\\') {
			return false;
		}
		File fileInCanonicalDir = null;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}
		if (fileInCanonicalDir.getCanonicalFile().equals(
				fileInCanonicalDir.getAbsoluteFile())) {
			return false;
		}
		return true;
	}

}
