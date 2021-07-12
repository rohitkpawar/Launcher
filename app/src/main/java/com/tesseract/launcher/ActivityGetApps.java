package com.tesseract.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityGetApps extends AppCompatActivity {

    public static List<AppInfo> mAppsList;
    public static ArrayAdapter<AppInfo> mAdapter;
    private PackageManager packageManager;
    private GridView mGrdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);
        mAppsList = null;
        mAdapter = null;
        loadApps();
        loadListView();
        addGridListeners();
    }

    public void addGridListeners() {
        try {
            mGrdView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = packageManager.getLaunchIntentForPackage(mAppsList.get(i).name.toString());
                ActivityGetApps.this.startActivity(intent);
            });
        } catch (Exception ex) {
            Toast.makeText(ActivityGetApps.this, ex.getMessage() + " Grid", Toast.LENGTH_LONG).show();
            Log.e("Error Grid", ex.getMessage() + " Grid");
        }

    }


    private void loadListView() {

        try {
            mGrdView = (GridView) findViewById(R.id.grd_allApps);
            if (mAdapter == null) {
                mAdapter = new ArrayAdapter<AppInfo>(this, R.layout.grd_items, mAppsList) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        ViewHolderItem viewHolder = null;

                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(
                                    R.layout.grd_items, parent, false
                            );
                            viewHolder = new ViewHolderItem();
                            viewHolder.icon = (ImageView) convertView.findViewById(R.id.img_icon);
                            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_name);
                            viewHolder.label = (TextView) convertView.findViewById(R.id.txt_label);

                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolderItem) convertView.getTag();
                        }

                        AppInfo appInfo = mAppsList.get(position);

                        if (appInfo != null) {
                            viewHolder.icon.setImageDrawable(appInfo.icon);
                            viewHolder.label.setText(appInfo.label);
                            viewHolder.name.setText(appInfo.name);
                        }
                        return convertView;

                    }

                    final class ViewHolderItem {
                        ImageView icon;
                        TextView label;
                        TextView name;
                    }
                };
            }

            mGrdView.setAdapter(mAdapter);
        } catch (Exception ex) {
            Toast.makeText(ActivityGetApps.this, ex.getMessage().toString() + " loadListView", Toast.LENGTH_LONG).show();
            Log.e("Error loadListView", ex.getMessage().toString() + " loadListView");
        }

    }

    private void loadApps() {
        try {
            if (packageManager == null)
                packageManager = getPackageManager();
            if (mAppsList == null) {
                mAppsList = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(packageManager);
                    appinfo.name = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(packageManager);
                    mAppsList.add(appinfo);

                }
            }

        } catch (Exception ex) {
            Toast.makeText(ActivityGetApps.this, ex.getMessage().toString() + " loadApps", Toast.LENGTH_LONG).show();
            Log.e("Error loadApps", ex.getMessage().toString() + " loadApps");
        }

    }
}
