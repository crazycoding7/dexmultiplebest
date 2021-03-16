package com.example.aidl.myapplicationttt;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.aidl.myapplicationttt.utils.AssetUtils;
import com.example.aidl.myapplicationttt.utils.DexUtils;

import java.io.File;
import java.io.IOException;

public class HotPatchApplication extends Application {
    private static String TAG = "HotPatchApplication";

    private static final String HACK_DEX = "hack.jar"; // assert中的hack.jar,用于加载AntilazyLoad.class
    private static final String Hot_DEX = "patch_dex.jar"; // assert中的hack.jar,用于加载AntilazyLoad.class
    private static final String DEX_DIR = "nuwa";
    private static final String DEX_OPT_DIR = "nuwaopt";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 加载assert中的hack.jar
//        init(this);
//
//        // 获取SdCard中的补丁，如果存在就执行注入操作
//        String dexPath = getExternalFilesDir(null) + "/" + Hot_DEX;
//        File file = new File(dexPath);
//        if (file.exists()) {
//            loadPatch(this, dexPath);
//        } else {
//            Log.e("BugFixApplication", dexPath + "不存在");
//        }
    }

    public static void init(Context context) {
        File dexDir = new File(context.getFilesDir(), DEX_DIR);
        dexDir.mkdir();

        String dexPath = null;
        try {
            dexPath = AssetUtils.copyAsset(context, HACK_DEX, dexDir);
        } catch (IOException e) {
            Log.e(TAG, "copy " + HACK_DEX + " failed");
            e.printStackTrace();
        }

        loadPatch(context, dexPath);
    }

    public static void loadPatch(Context context, String dexPath) {
        if (context == null) {
            Log.e(TAG, "context is null");
            return;
        }
        if (!new File(dexPath).exists()) {
            Log.e(TAG, dexPath + " is null");
            return;
        }
        File dexOptDir = new File(context.getFilesDir(), DEX_OPT_DIR);
        dexOptDir.mkdir();
        try {
            DexUtils.injectDexAtFirst(dexPath, dexOptDir.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "inject " + dexPath + " failed");
            e.printStackTrace();
        }
    }



    //------------------原生加载方法---------------------------
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // 获取补丁，如果存在就执行注入操作
//        String dexPathHack = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/patch_hack_dex.jar");
//        File fileHack = new File(dexPathHack);
//        if (fileHack.exists()) {
//            inject(dexPathHack);
//        } else {
//            Log.e("BugFixApplication", dexPathHack + "不存在");
//        }
//
//        // 获取补丁，如果存在就执行注入操作
//        String dexPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/patch_dex.jar");
//        File file = new File(dexPath);
//        if (file.exists()) {
//            inject(dexPath);
//        } else {
//            Log.e("BugFixApplication", dexPath + "不存在");
//        }
//    }

//    /**
//     * 要注入的dex的路径
//     *
//     * @param path
//     */
//    private void inject(String path) {
//        try {
//            // 获取classes的dexElements
//            Class<?> cl = Class.forName("dalvik.system.BaseDexClassLoader");
//            Object pathList = getField(cl, "pathList", getClassLoader());
//            Object baseElements = getField(pathList.getClass(), "dexElements", pathList);
//
//            // 获取patch_dex的dexElements（需要先加载dex）
//            String dexopt = getDir("dexopt", 0).getAbsolutePath();
//            DexClassLoader dexClassLoader = new DexClassLoader(path, dexopt, dexopt, getClassLoader());
//            Object obj = getField(cl, "pathList", dexClassLoader);
//            Object dexElements = getField(obj.getClass(), "dexElements", obj);
//
//            // 合并两个Elements
//            Object combineElements = combineArray(dexElements, baseElements);
//
//            // 将合并后的Element数组重新赋值给app的classLoader
//            setField(pathList.getClass(), "dexElements", pathList, combineElements);
//
//            //======== 以下是测试是否成功注入 =================
//            Object object = getField(pathList.getClass(), "dexElements", pathList);
//            int length = Array.getLength(object);
//            Log.e("BugFixApplication", path + " length = " + length);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }

}
