package com.maple.bsdiff;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(perm, 200);
        }
    }

    /**
     * 合并差分包
     *
     * @param oldApkPath 旧包路径
     * @param patch      差分包路径
     * @param newApkPath 新包生成路径
     */
    public native void patch(String oldApkPath, String patch, String newApkPath);

    public void update(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory() ,"next.apk");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                patch(getApplicationInfo().sourceDir, new File(Environment.getExternalStorageDirectory(), "patch.new").getAbsolutePath(),
                        file.getAbsolutePath());
                install(MainActivity.this);
            }
        }).start();

    }

    public static void install(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(FileProvider.getUriForFile(context,"aaa.fileprovider",new File(Environment.getExternalStorageDirectory(),"next.apk")),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
