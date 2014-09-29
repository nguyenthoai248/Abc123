package com.airdroid;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.R.bool;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Element;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airdroid.R;
import com.airdroid.helper.TaskAdapter;
import com.airdroid.tools.devices.CPU;
import com.airdroid.tools.devices.RAM;
import com.airdroid.tools.tasks.*;

public class TasksActivity extends ListActivity {

	private ArrayList<Task> taskList;// chua danh sach cac task
	private ListView lvTask;
	private TaskAdapter myAdapter;// adapter
	private TasksManager tasksManager;

	private Button btAll;
	private Button btStop;
	private TextView ram;
	private TextView cpu;

	Drawable iconNotFound;// icon mac dinh
	Method killMethod;

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasks_tool);
		ram = (TextView) findViewById(R.id.free_memory);
		cpu = (TextView) findViewById(R.id.CPU_usage);

		setRamCpuInfo();

		lvTask = getListView();
		setMyAdapter();
		setButtonStop();
		setButtonCheckAll();

		this.setListAdapter(myAdapter);
		registerForContextMenu(lvTask);

	}

	/**
	 * tao menu
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.tasks_menu, menu);
	}

	/**
	 * xu ly su kien khi chon 1 item trong menu
	 */
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		Task task = (Task) myAdapter.getItem(info.position);
		Log.d("TAG", task.getPackageName() + "\t" + myAdapter.getCount());

		switch (item.getItemId()) {
		case R.id.task_menu_forcestop:// chon force stop
		case R.id.task_menu_details:// chon force stop
			myAdapter.clear();
			myAdapter.notifyDataSetChanged();
			try {
				// Open the specific App Info page:
				Intent intent = new Intent(
						android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + task.getPackageName()));
				startActivity(intent);
				setMyAdapter();// refresh lai danh sach task
				setListAdapter(myAdapter);
			} catch (ActivityNotFoundException e) {
				// Open the generic Apps page:
				Intent intent = new Intent(
						android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
				startActivity(intent);
				setMyAdapter();
				setListAdapter(myAdapter);
			}

			return true;

		case R.id.task_menu_unistall:// chon uninstall
			myAdapter.clear();
			myAdapter.notifyDataSetChanged();
			if (task.getIsUserApp()) {
				item.setChecked(true);
				try {
					Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
					intent.setData(Uri.parse("package:" + task.getPackageName()));
					startActivity(intent);
					setMyAdapter();
					setListAdapter(myAdapter);
				} catch (ActivityNotFoundException e) {
					Log.v("Activity not found", task.toString());
				}
			}
			setMyAdapter();
			setListAdapter(myAdapter);
			return true;

		}
		return super.onContextItemSelected(item);
	}

	int count = 0;// bien dem so lan chon vao nut All

	/**
	 * set button All
	 */
	private void setButtonCheckAll() {
		btAll = (Button) findViewById(R.id.task_btCheckAll);
		btAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAdapter.notifyDataSetChanged();
				if (count % 2 == 0) {
					for (Task element : taskList)
						element.setSelected(true);
				} else {
					for (Task element : taskList)
						element.setSelected(false);
				}
				count++;
				myAdapter = new TaskAdapter(TasksActivity.this, taskList);
				setListAdapter(myAdapter);
			}
		});
	}

	final ArrayList<String> packageNames = new ArrayList<String>();

	/**
	 * set Button Stop: kiem tra cac task xem co duoc danh dau khong neu duoc
	 * thi stop task do
	 */
	private void setButtonStop() {
		btStop = (Button) findViewById(R.id.task_btStop);
		btStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAdapter.notifyDataSetChanged();
				int count = 0;
				int i = 0;
				ArrayList<Task> tmp = new ArrayList<Task>();
				tmp.addAll(taskList);
				for (Task element : tmp) {
					if (element.getSelected()) {
						taskList.get(i).setSelected(false);
						tasksManager.killApp(element.getPackageName());
						android.os.Process.killProcess(element.getPid());
						count++;
					}
					i++;
				}

				Toast.makeText(getApplicationContext(),
						"Killed " + count + " process", Toast.LENGTH_SHORT)
						.show();

				setMyAdapter();
				setListAdapter(myAdapter);
				setRamCpuInfo();
			}
		});
	}

	/**
	 * cai dat du lieu vao adapter
	 */
	private void setMyAdapter() {
		tasksManager = new TasksManager();
		taskList = new ArrayList<Task>();
		iconNotFound = getResources().getDrawable(R.drawable.ic_launcher);
		taskList = tasksManager.listProcess(this, iconNotFound);
		taskList.remove(0);
		myAdapter = new TaskAdapter(this, taskList);
	}

	/**
	 * set thong tin cho cac TextView
	 */
	private void setRamCpuInfo() {
		ram.setText("Free memory: " + (new RAM()).getFree() + "MB");
		cpu.setText("CPU usage: " + (new CPU()).getPercentCPU() * 100 + "%");
	}

}
