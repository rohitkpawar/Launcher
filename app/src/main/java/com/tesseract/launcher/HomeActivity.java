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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {
    public static List<AppInfo> apps;
    public static ArrayAdapter<AppInfo> adapter;
    private TextView txtTime, txtDate;
    private Calendar c;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleTimeFormat;
    private PackageManager packageManager;
    private GridView grdView;
    private LinearLayout containAppDrawer;
    private RelativeLayout ContainerHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        simpleTimeFormat = new SimpleDateFormat("hh:mm:ss.SSS a");
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDate = (TextView) findViewById(R.id.txtDate);

        containAppDrawer = (LinearLayout) findViewById(R.id.containAppDrawer);
        ContainerHome = (RelativeLayout) findViewById(R.id.ContainerHome);
        HideAppDrawer(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    c = Calendar.getInstance();
                    txtDate.setText(simpleDateFormat.format(c.getTime()));
                    txtTime.setText(simpleTimeFormat.format(c.getTime()));
                });

            }
        }, 0, 10);
        apps = null;
        adapter = null;
        loadApps();
        loadListView();
        addGridListeners();
    }

    public void addGridListeners() {
        try {
            grdView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = packageManager.getLaunchIntentForPackage(apps.get(i).name.toString());
                HomeActivity.this.startActivity(intent);
            });
        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " Grid", Toast.LENGTH_LONG).show();
            Log.e("Error Grid", ex.getMessage().toString() + " Grid");
        }

    }


    private void loadListView() {
        try {
            grdView = (GridView) findViewById(R.id.grd_allApps);
            if (adapter == null) {
                adapter = new ArrayAdapter<AppInfo>(this, R.layout.grd_items, apps) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        ViewHolderItem viewHolder = null;

                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(
                                    R.layout.grd_items, parent, false);
                            viewHolder = new ViewHolderItem();
                            viewHolder.icon = (ImageView) convertView.findViewById(R.id.img_icon);
                            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_name);
                            viewHolder.label = (TextView) convertView.findViewById(R.id.txt_label);

                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolderItem) convertView.getTag();
                        }

                        AppInfo appInfo = apps.get(position);

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

            grdView.setAdapter(adapter);
        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " loadListView", Toast.LENGTH_LONG).show();
            Log.e("Error loadListView", ex.getMessage().toString() + " loadListView");
        }

    }

    private void loadApps() {
        try {
            if (packageManager == null)
                packageManager = getPackageManager();
            if (apps == null) {
                apps = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                /*List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
                for (PackageInfo ri : packs) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.packageName;
                    appinfo.name = ri.applicationInfo.loadLabel(packageManager);
                    appinfo.icon = ri.applicationInfo.loadIcon(getPackageManager());
                    apps.add(appinfo);
                }*/

                List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(packageManager);
                    appinfo.name = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(packageManager);
                    apps.add(appinfo);
                }
            }

        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " loadApps", Toast.LENGTH_LONG).show();
            Log.e("Error loadApps", ex.getMessage().toString() + " loadApps");
        }

    }


    public void showApps(View v) {
        // Intent i = new Intent(HomeActivity.this, ActivityGetApps.class);
        //startActivity(i);
        HideAppDrawer(true);
    }

    public void HideAppDrawer(Boolean visibility) {
        if (visibility) {
            containAppDrawer.setVisibility(View.VISIBLE);
            ContainerHome.setVisibility(View.GONE);
        } else {
            containAppDrawer.setVisibility(View.GONE);
            ContainerHome.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onBackPressed() {
        HideAppDrawer(false);
    }


}
