package com.airdroid.helper;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airdroid.R;
import com.airdroid.tools.tasks.Task;

public class TaskAdapter extends ArrayAdapter<Task> {

	private Activity context;
	private List<Task> list;

	final int MB = 1024;

	public List<Task> getList() {
		return list;
	}

	public TaskAdapter(Activity context, List<Task> list) {
		super(context, R.layout.task_item, list);
		this.context = context;
		this.list = list;
	}

	// chua cac thanh phan cua 1 item
	static class ViewHoler {
		protected ImageView icon;
		protected TextView label;
		protected TextView memory;
		protected CheckBox checkbox;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = null;
		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.task_item, null);
			final ViewHoler viewHolder = new ViewHoler();
			viewHolder.label = (TextView) row.findViewById(R.id.task_name);
			viewHolder.memory = (TextView) row.findViewById(R.id.task_memory);
			viewHolder.icon = (ImageView) row.findViewById(R.id.task_icon);
			viewHolder.checkbox = (CheckBox) row
					.findViewById(R.id.task_check_box);
			// set su kien chon checkbox
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Task element = (Task) viewHolder.checkbox.getTag();
							element.setSelected(buttonView.isChecked());
						}
					});
			row.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			row = convertView;
			((ViewHoler) row.getTag()).checkbox.setTag(list.get(position));
		}
		// lay du lieu hien thi free memory va cpu
		String memory = null;
		DecimalFormat dcf = new DecimalFormat("#.00");
		memory = dcf.format((float) list.get(position).getMemory() / MB) + "MB";

		ViewHoler holer = (ViewHoler) row.getTag();
		holer.label.setText(list.get(position).getLabel());
		holer.memory.setText("Memory: " + memory);
		holer.icon.setImageDrawable(list.get(position).getIcon());
		if (list.get(position).getPackageName() != "com.airdroid") {
			holer.checkbox.setVisibility(View.VISIBLE);
			holer.checkbox.setChecked(list.get(position).getSelected());
		} else
			holer.checkbox.setVisibility(View.GONE);
		return row;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Task getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
