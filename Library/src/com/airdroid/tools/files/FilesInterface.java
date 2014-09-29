package com.airdroid.tools.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface FilesInterface {
	/**
	 * copy toi thu muc moi
	 * @param old duong dan cu
	 * @param newDir duong dan moi
	 * @return true neu copy duoc, false neu khong copy duoc
	 * @throws IOException 
	 */
	public int copyToDirectory(String old, String newDir) throws IOException;

	/**
	 * Tao 1 file nen
	 * @param path duong dan den file nen do
	 */
	public void createZipFile(String path);
	
	 /**
	 * Sua lai ten file, folder
	 * @param oldName la ten file/folder hien tai
	 * @param newName ten moi
	 * @return true neu sua duoc, false neu khong duoc
	 */
	public boolean renameDir(String oldName, String newName);
	
	/**
	 * tao 1 thu muc moi
	 * @param path  duong dan den thu muc cha
	 * @param name ten thu muc moi
	 */
	public boolean createDir(String path, String name);

	/**
	 * Xoa 1 file, folder dang tro den
	 * @param path duong dan 
	 * @return true neu xoa duoc, false neu khong duoc
	 */
	public boolean deleteTarget(String path);
	
	/**
	 * Tim kiem file trong 1 thu muc
	 * @param key tu khoa tim kiem
	 * @param duong dan den thu muc
	 * @return Tra ve 1 danh sach cac file, thu muc co lien quan
	 */
	public ArrayList<String> searchInDirectoty(String dir, String pathName);

	/**
	 * Dua ra kich thuoc thu muc
	 */
	public long getDirSize(File path);

	/**
	 * Dua ra danh sach cac folder, file
	 */
	public ArrayList<String> populateList();

}
