package com.example.aidl.myapplicationttt;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 热修复测试
 */
public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private Cat mCat;

    final String mApkPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/1/app-debug.apk");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCat = new Cat();

        checkPermission();
    }



    // 热修复成功，喵叫会正常.
    public void click(View view) {
        Toast.makeText(this, mCat.say(), Toast.LENGTH_SHORT).show();
    }

    public void clickPlugin(View view) {
        updateSkin();
    }

    private void checkPermission() {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            // 创建目录,存放补丁文件
            getExternalFilesDir(null).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Resources getPluginResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssertPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssertPath.invoke(assetManager, mApkPath);
            Resources superRes = this.getResources();
            Resources mResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
            return mResources;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public void updateSkin() {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(mApkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            String pkgName = packageInfo.applicationInfo.packageName;
            Log.e(TAG, "插件包名称： " + pkgName);

            File optDir = getDir("dex", MODE_PRIVATE);
            DexClassLoader dexClassLoader = new DexClassLoader(mApkPath, optDir.getPath(), null, ClassLoader.getSystemClassLoader());

            try {
                Class clazz = dexClassLoader.loadClass(pkgName + ".R$mipmap");
                Field field = clazz.getDeclaredField("go_login_icon");
                try {
                    int resId = field.getInt(R.id.class);

                    Log.e(TAG, "插件包 资源Id： " + resId + " " + R.mipmap.ic_launcher);

                    Resources mResources = getPluginResources();
                    findViewById(R.id.imageView).setBackground(mResources.getDrawable(R.mipmap.ic_launcher));

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
    }
}