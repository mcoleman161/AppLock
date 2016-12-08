package me.dawson.applock.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.dawson.applock.CheckAppsService;
import me.dawson.applock.HomePage;
import me.dawson.applock.R;

/**
 * Created by Michael on 11/30/2016.
 */

public class ListVewActivity extends BaseActivity {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> appList = null;
    private ApplicationAdapter listAdapter = null;
    public ArrayList<String> blockedAppsList = new ArrayList<String>();
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    public static final String PREF_FILE_NAME = "appsBlocked";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_list);
        // shared = PreferenceManager.getDefaultSharedPreferences(this);
        shared = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);


        final ListView listview = (ListView) findViewById(R.id.list);
        packageManager = getPackageManager();
        appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        listAdapter = new ApplicationAdapter(this,
                R.layout.snippet_list_row, appList);

        listview.setAdapter(listAdapter);
        //final ArrayList<String> blockedAppsList = new ArrayList<String>();

        listview.setOnItemClickListener(
                new ListView.OnItemClickListener(){
                    @Override
                   public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        ApplicationInfo item = listAdapter.getItem(position);
                        if(blockedAppsList.contains(item.processName))
                       //if (blockedAppsList.contains(item.loadLabel(packageManager).toString()))
                        {
                            blockedAppsList.add(item.processName);
                            //blockedAppsList.remove(item.loadLabel(packageManager).toString());
                            packageSharedPreferences();
                        }
                        else {
                            blockedAppsList.add(item.processName);
                            //blockedAppsList.add(item.loadLabel(packageManager).toString());
                            packageSharedPreferences();
                        }
                    }
               }

        );

     //   Intent intent = new Intent(this, CheckAppsService.class);
     //   startService(intent);
    }
/*    public void saveBlockedAppsSet(){
        Set<String> set = new HashSet<String>();
        set.addAll(blockedAppsList);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet("blockedApps", set);
        editor.commit();
    }
    */


    private void packageSharedPreferences() {
        SharedPreferences.Editor editor = shared.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(blockedAppsList);
        editor.putStringSet("blockedApps", set);
        editor.apply();
    }

    public ArrayList<String> retriveSharedValue() {
        Set<String> set = shared.getStringSet("blockedApps", null);
        blockedAppsList.addAll(set);
        return blockedAppsList;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }


    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                   // if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1) { uncomment following lines to remove system apps from the listview
                        applist.add(info);
                    //    continue;
                  //  }
                 //   applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applist;
    }

}


