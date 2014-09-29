package com.airdroid;

import com.airdroid.helper.MainToolsAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ToolsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_tools);
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new MainToolsAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				switch (position) {
				case 0:
					 startActivity(new Intent(ToolsActivity.this, DevicesActivity.class));
					break;
				case 1:
					startActivity(new Intent("com.airdroid.AppsActivity"));
					break;
				case 2:
					startActivity(new Intent("com.airdroid.FilesActivity"));
					break;
				case 3:
					startActivity(new Intent("com.airdroid.TasksActivity"));
					break;
				case 4:
					Toast.makeText(getBaseContext(), "Cong cu chua duoc ho tro!",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		});
	}
}
