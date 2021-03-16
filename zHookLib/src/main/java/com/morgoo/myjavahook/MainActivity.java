
package com.morgoo.myjavahook;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.morgoo.myjavahook.test.HooKNotificationUtil;
import com.morgoo.zhooklib.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.morgoo.myjavahook.test.HookOnClickUtil.hookOnClickListener;

public class MainActivity extends Activity {
    public static String TAG = MainActivity.class.getSimpleName();

    private Button mHookOnClickBtn;
    private Button mNotificationOnClickBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        mHookOnClickBtn = findViewById(R.id.hook_onclick);
        mNotificationOnClickBtn = findViewById(R.id.notification_onclick);

        //1. dalvik 虚拟机下hook一个class所有方法
//        final TextView text = (TextView) findViewById(R.id.text1);
//        HookTest.main(new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 0) {
//                    if (msg.obj instanceof String) {
//                        text.setText(text.getText() + "\r\n\r\n" + msg.obj);
//                    }
//                }
//            }
//        });
//        text.setText("haha");

        // 2. hook onclick
        mHookOnClickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "view clicked ."  );
            }
        });

        try {
            hookOnClickListener(mHookOnClickBtn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. hook notification
        mNotificationOnClickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HooKNotificationUtil.hookNotificationManager(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HooKNotificationUtil.notification(MainActivity.this);
            }
        });



    }



   
}
