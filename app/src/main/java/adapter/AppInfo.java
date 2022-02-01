package adapter;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by mac on 14/09/16.
 */


public class AppInfo {

    private String appname = "";
    private String packagename = "";
    private String versionName = "";
    private int versionCode = 0;
    private Drawable icon;

    private Long size;
    private Long dateInstalled;
    private boolean isChecked;

    private AppInfo() {}

    public AppInfo(String appname, String packagename, String versionName, int versionCode, Drawable icon, Long size, Long dateInstalled, Boolean isChecked) {
        this.appname = appname;
        this.packagename = packagename;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.icon = icon;
        this.size = size;
        this.dateInstalled = dateInstalled;
    }


    public String getAppName() {
        return appname;
    }

    public String getPackageName() {
        return packagename;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Long getSize() {
        return size;
    }

    public Long getDateInstalled() {
        return dateInstalled;
    }

    public Boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }
}
