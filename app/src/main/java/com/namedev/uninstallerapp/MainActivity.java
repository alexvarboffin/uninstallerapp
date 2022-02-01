package com.namedev.uninstallerapp;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.AppInfo;
import adapter.MyAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener, MyAdapter.Callback {

    private static final String TAG = "@@@";

    private ArrayList<AppInfo> res = new ArrayList<>();
    private ArrayList<String> allPackagesSelected = new ArrayList<>();
    private int appCounter = 0;


    LinearLayout linearlayout;
    public String packageDeleted = "";

    int appTotal = 0;

    CheckBox checkbox;
    Boolean bool = false;
    private TextView unInstall;

    ImageButton sort;
    int sortCounter = 0;


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        unInstall = findViewById(R.id.uninstall);
        checkbox = findViewById(R.id.checkbox);
        sort = findViewById(R.id.sort);
        sort.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sortbydate));

        unInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allPackagesSelected.size() > 0) {
                    for (String everyApp : allPackagesSelected) {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + everyApp));
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No app to uninstall", Toast.LENGTH_LONG).show();
                }

            }
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    bool = true;
                    loadApp();
                } else {
                    bool = false;
                    loadApp();
                }
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sortCounter == 0) {

                    sortCounter++;
                    Collections.sort(res, new Comparator<AppInfo>() {
                        public int compare(AppInfo m1, AppInfo m2) {
                            return m2.getSize().compareTo(m1.getSize());
                        }
                    });

                    sort.setImageDrawable(null);
                    sort.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sortbysizeable));

                } else if (sortCounter == 1) {

                    sortCounter++;
                    Collections.sort(res, new Comparator<AppInfo>() {
                        public int compare(AppInfo m1, AppInfo m2) {
                            return m1.getAppName().compareTo(m2.getAppName());
                        }
                    });

                    sort.setImageDrawable(null);
                    sort.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sortbyname));

                } else if (sortCounter == 2) {

                    sortCounter = 0;
                    Collections.sort(res, new Comparator<AppInfo>() {
                        public int compare(AppInfo m1, AppInfo m2) {
                            return m2.getDateInstalled().compareTo(m1.getDateInstalled());
                        }
                    });

                    sort.setImageDrawable(null);
                    sort.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sortbydate));
                }


                sortSort(res);

            }
        });

        linearlayout = findViewById(R.id.unitads);
        //@@@@ admobBannerCall(this, linearlayout);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadApp();

    }

    private void loadApp() {
        res.clear();

        //Cool stub
        List<PackageInfo> apps = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < apps.size(); i++) {
            PackageInfo p = apps.get(i);

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "package: " + p.packageName);
            }

            // check if system Packages
            if (!isSystemPackage(p) && !p.packageName.contains(getPackageName())
                    && !p.packageName.startsWith("com.android")
                    //&& !p.packageName.contains("com.google.android")
                    ) {

                if (bool) {
                    allPackagesSelected.add(p.packageName);
                    appCounter++;
                } else {
                    allPackagesSelected.clear();
                    appCounter = 0;
                }

                AppInfo newInfo = buildAppInfo(p);
                res.add(newInfo);
                appTotal++;

            }
        }


        //part-2

        if (bool) {
            setTextForUninstallerCounterApp(appCounter);
        } else {
            setTextForUninstallerCounterApp(appCounter);
        }

        if (!res.isEmpty()) {
            Collections.sort(res, new Comparator<AppInfo>() {
                public int compare(AppInfo m1, AppInfo m2) {
                    return m2.getDateInstalled().compareTo(m1.getDateInstalled());
                }
            });
        }

        sortSort(res);
    }

    private AppInfo buildAppInfo(PackageInfo p) {

        ApplicationInfo applicationInfo = p.applicationInfo;
        Long size = Long.parseLong("0");

        try {
            File file = new File(applicationInfo.sourceDir);
            size = file.length();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //com.alexuvarov.android.iqtest - Кривое приложение крошит
        //applicationInfo.loadLabel(getPackageManager());

        //Log.i(TAG, ">>: " + applicationInfo);

        CharSequence app_name = applicationInfo.loadLabel(getPackageManager());
        return new AppInfo(app_name.toString(), p.packageName, p.versionName,
                p.versionCode, p.applicationInfo.loadIcon(getPackageManager()),
                size, p.firstInstallTime, false);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.userapp) {

            loadApp();
            Toast.makeText(this, "User APPs", Toast.LENGTH_LONG).show();


        } else if (id == R.id.rateus) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }


        } else if (id == R.id.sharethisapp) {

            Intent myapp = new Intent(Intent.ACTION_SEND);
            myapp.setType("text/plain");
            myapp.putExtra(Intent.EXTRA_TEXT, "Hey my friend check out this app\n https://play.google.com/store/apps/details?id=" + getPackageName() + " \n");
            startActivity(myapp);

        } else if (id == R.id.moreapp) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + getResources().getString(R.string.namedev))));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pub:" + getResources().getString(R.string.namedev))));
            }


        } else if (id == R.id.quit) {

            finish();
            System.exit(0);

        } else if (id == R.id.feedback) {

            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:arpatorinc@gmail.com" +
                        "?subject=" + Uri.encode(getPackageName())));
                startActivity(intent);
            } catch (Exception ignored) {
            }

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onResume() {

        super.onResume();
        bool = false;
        loadApp();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<AppInfo> newfilter = new ArrayList<>();

        for (int i = 0; i < res.size(); i++) {

            if (res.get(i).getAppName().toLowerCase().contains(newText.toLowerCase())) {
                newfilter.add(res.get(i));
            }
        }

        sortSort(newfilter);
        return false;
    }

    private void sortSort(ArrayList<AppInfo> o) {
        RecyclerView.Adapter mAdapter = new MyAdapter(this, this, o, bool);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e("onQueryTextSubmit", query);
        return false;
    }


    @Override
    public void allPackagesSelectedAdd(String packageName) {
        allPackagesSelected.add(packageName);
        appCounter++;
        setTextForUninstallerCounterApp(appCounter);
    }

    @Override
    public void allPackagesSelectedRemove(String packageName) {
        appCounter--;
        setTextForUninstallerCounterApp(appCounter);
    }


    public void packageDeleted(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        startActivity(intent);
    }

    @Override
    public void packageDeleted(int adapterPosition) {
        final AppInfo appati = res.get(adapterPosition);
        packageDeleted(appati.getPackageName());
    }


    private void setTextForUninstallerCounterApp(int appTextCounter) {
        if (appTextCounter > 0) {
            unInstall.setText("UnInstall(" + appTextCounter + ")");
            unInstall.setTypeface(null, Typeface.BOLD);
        } else {
            unInstall.setText("UnInstall");
            unInstall.setTypeface(null, Typeface.NORMAL);
        }
    }
}
