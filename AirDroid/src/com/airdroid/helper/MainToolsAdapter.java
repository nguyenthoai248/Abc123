package com.airdroid.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.airdroid.R;

public class MainToolsAdapter extends BaseAdapter {

	private Activity context;
	// ---the images to display---
	Integer[] imageIDs = { R.drawable.main_ae_ic_device,
			R.drawable.main_ae_ic_app, R.drawable.main_ae_ic_fm,
			R.drawable.main_ae_ic_pm, R.drawable.main_ae_ic_help };

	String[] textIDs = { "  Devices", "	 Apps", "	 Files", "   Tasks", "   Helps" };

	public MainToolsAdapter(Activity c) {
		context = c;
	}

	private class ViewHolder {
		ImageView image;
		TextView txt;
	}

	// ---returns the number of images---
	public int getCount() {
		return imageIDs.length;
	}

	// ---returns the item---
	public Object getItem(int position) {
		return position;
	}

	// ---returns the ID of an item---
	public long getItemId(int position) {
		return position;
	}

	// ---returns an ImageView view---
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.icons_main_tools, null);
			holder = new ViewHolder();
			holder.txt = (TextView) convertView
					.findViewById(R.id.main_tools_name);
			holder.image = (ImageView) convertView
					.findViewById(R.id.main_tools_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setImageResource(imageIDs[position]);
		holder.txt.setText(textIDs[position]);

		return convertView;
	}
}
