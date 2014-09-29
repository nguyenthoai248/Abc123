package com.airdroid.helper;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.airdroid.R;
import com.airdroid.tools.apps.PackageInf;

@SuppressLint("InflateParams")
public class AppsAdapter extends BaseAdapter {

	private Activity context;
	private List<PackageInf> list;

	public AppsAdapter(Activity con, List<PackageInf> li) {
		context = con;
		list = li;
	}

	private class ViewHolder {
		TextView pack;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.package_item, null);
			holder = new ViewHolder();

			holder.pack = (TextView) convertView.findViewById(R.id.appname);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PackageInf packa = (PackageInf) getItem(position);
		Drawable appIcon = packa.getIcon();
		String appName = packa.getAppName();
		appIcon.setBounds(0, 0, 75, 75);
		holder.pack.setCompoundDrawables(appIcon, null, null, null);
		holder.pack.setCompoundDrawablePadding(15);
		holder.pack.setText(appName);
		return convertView;
	}
}
