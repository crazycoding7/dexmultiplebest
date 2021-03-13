package com.example.aidl.myapplicationttt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * 热修复测试
 */
public class MainActivity extends AppCompatActivity {

    private Cat mCat;

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

}