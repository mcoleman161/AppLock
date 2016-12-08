package me.dawson.applock;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import me.dawson.applock.core.AppLock;
import me.dawson.applock.core.AppLockActivity;
import me.dawson.applock.core.ListVewActivity;

import static android.app.Service.*;

/**
 * Created by Michael on 12/1/2016.
 */

public class CheckAppsService extends IntentService {
    ActivityManager am = null;
    Context context = null;


    public CheckAppsService() {
        super("CheckAppsService");
        //CheckAppsService.this = context;
        //  this.context = con;
        //  am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public CheckAppsService(Context con) {
        super("CheckAppsService");
        this.context = con;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //  @Override
        // public int onStartCommand(Intent intent, int flags, int startId) {
        // default - return super.onStartCommand(intent, flags, startId);
        //Runnable r = new Runnable() {

//    public void run(Context con) {

//        Looper.prepare();
//        ListVewActivity vw = new ListVewActivity();
        ArrayList<String> blockedAppsList = new ArrayList<String>();
       // myapp.bmodel.getApplicationContext().getSharedPreferences("appsBlocked", Context.MODE_PRIVATE);
        SharedPreferences shared = getSharedPreferences("appsBlocked", MODE_PRIVATE);


        while (true) {
            //Set<String> set = shared.getStringSet("DATE_LIST", null);
            Set<String> stringSet = shared.getStringSet("blockedApps", null);
            String foregroundApp = getForegroundTask();
            if (stringSet != null) {
                int i;
                for (Iterator<String> it = stringSet.iterator(); it.hasNext(); )
                {
                    String f = it.next();
                    if (blockedAppsList.contains(f) == false)
                    {
                        blockedAppsList.add(f);
                        stringSet.remove(f);
                    }
                }
               // blockedAppsList.addAll(stringSet);



                if (blockedAppsList.contains(foregroundApp)) {
                    // show your activity here on top of PACKAGE_NAME.ACTIVITY_NAME);
                    Intent intent2 = new Intent(this, AppLockActivity.class);
                    intent2.putExtra(AppLock.TYPE, AppLock.UNLOCK_APP_PASSWORD);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent2);
                    break;
                }
            }
        }
    }
   //     Looper.loop();
    private String getForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        //getApplicationInfo().loadLabel(getPackageManager()).toString();
        return currentApp;
    }

}








      /*  Thread less42Thread = new Thread(r);
        less42Thread.start();
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        //Removed default super
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This is not a bound service
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    } */
//}

