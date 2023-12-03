package com.hax.myapplication;
/*
 * 构建时间2023-12-03
 * 作者qq2972249837
 * 请勿做违法的勾当
 * 交流群687044733
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultipartUtility {

    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private DataOutputStream outputStream;

    public MultipartUtility(String requestURL) throws Exception {
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "*****");
        outputStream = new DataOutputStream(httpConn.getOutputStream());
    }

    public void addFilePart(String fieldName, File uploadFile) throws Exception {
        String fileName = uploadFile.getName();

        outputStream.writeBytes("--*****" + LINE_FEED);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + LINE_FEED);
        outputStream.writeBytes("Content-Type: " + "application/octet-stream" + LINE_FEED);
        outputStream.writeBytes(LINE_FEED);

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.writeBytes(LINE_FEED);
        inputStream.close();
    }

    public boolean finish() {
        try {
            outputStream.writeBytes("--*****--" + LINE_FEED);
            outputStream.flush();
            outputStream.close();

            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                //文件上传成功
                readResponse();
                return true;
            } else {
                //文件上传失败
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            httpConn.disconnect();
        }
    }

    private void readResponse() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        //处理服务器响应
        String serverResponse = response.toString();
        System.out.println(serverResponse);
    }
}
