package kr.ac.mmu;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class HttpTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "httptask";
    private String ip = "218.157.77.240:51212";
    private String url;
    private String urlParameters;
    private URL obj;
    private HttpURLConnection con;
    @Override
    protected String doInBackground(String... params) {
        if (params[0].equals("#login")) {
            url = "http://" + ip + "//app//login.php";
            urlParameters = "id=" + params[1] + "&pass=" + params[2];

        }
        else if (params[0].equals("#capture")){
            url = "http://" + ip + "//app//image.php";
            urlParameters = params[1];

        }
        else if (params[0].equals("#capture_info")){
            url = "http://" + ip + "//app//capture_info.php";
            Log.e("httppp", params[4]);
            try {
                urlParameters = "&id=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&latitude=" + URLEncoder.encode(params[2], "UTF-8")
                        + "&longitude=" + URLEncoder.encode(params[3], "UTF-8")
                        + "&address=" + URLEncoder.encode(params[4], "UTF-8")
                        + "&imagePath=" + URLEncoder.encode(params[5], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        else if (params[0].equals("#getColor")) {
            url = "http://" + ip + "//app//getColor.php";
            urlParameters = "";
        }
        else if (params[0].equals("#report")){
            url = "http://" + ip + "//app//report.php";
            try {
                urlParameters = "id=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&carNum=" + URLEncoder.encode(params[2], "UTF-8")
                        + "&reportType=" + URLEncoder.encode(params[3], "UTF-8")
                        + "&imagePath=" + URLEncoder.encode(params[4], "UTF-8")
                        + "&latitude=" + URLEncoder.encode(params[5], "UTF-8")
                        + "&longitude=" + URLEncoder.encode(params[6], "UTF-8")
                        + "&address=" + URLEncoder.encode(params[7], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        else if (params[0].equals("#showList")) {
            url = "http://" + ip + "//app//showList.php";
            urlParameters = "limit=" + params[1] + "&id=" + params[2];
        }
        else if (params[0].equals("#showInfo")) {
            url = "http://" + ip + "//app//showInfo.php";
            urlParameters = "reportId=" + params[1];
        }
        else if (params[0].equals("#showRank")) {
            url = "http://" + ip + "//app//showRank.php";
            urlParameters = "limit=" + params[1];

        }
        else if (params[0].equals("#commentReg")) {
            url = "http://" + ip + "//app//commentReg.php";
            try {
                urlParameters = "g_id=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&id=" + URLEncoder.encode(params[2], "UTF-8")
                        + "&content=" + URLEncoder.encode(params[3], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        }
        else if (params[0].equals("#showComment")) {
            url = "http://" + ip + "//app//showComment.php";
            try {
                urlParameters = "g_id=" + URLEncoder.encode(params[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        }
        else if (params[0].equals("#map")){
            url = "http://" + ip + "//app//map.php";
            urlParameters = "";
        }
        else if (params[0].equals("#check")) {
            url = "http://" + ip + "//app//checkId.php";
            urlParameters = "id=" + params[1];

        }
        else if (params[0].equals("#myPage")) {
            url = "http://" + ip + "//app//myPage.php";
            urlParameters = "id=" + params[1];

        }
        else if (params[0].equals("#join")) {
            // 웹 서버 URL 설정
            url = "http://" + ip + "//app//join.php";
            try {
                urlParameters = "id=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&password=" + URLEncoder.encode(params[2], "UTF-8")
                        + "&carNumber=" + URLEncoder.encode(params[3], "UTF-8")
                        + "&token=" + URLEncoder.encode(params[4], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        else if (params[0].equals("#updateInfo")) {
            url = "http://" + ip + "//app//updateInfo.php";
            urlParameters = "id=" + params[1] + "&curPass=" + params[2] + "&newPass=" + params[3];
        }
        else if (params[0].equals("#updateCarNum")) {
            url = "http://" + ip + "//app//updateCarNum.php";
            try {
                urlParameters = "id=" + URLEncoder.encode(params[1], "UTF-8")
                        + "&carNum=" + URLEncoder.encode(params[2], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }else if (params[0].equals("#deleteReport")) {
            url = "http://" + ip + "//app//deleteReport.php";
            urlParameters = "r_id=" + params[1];
        }
        else if (params[0].equals("#secession")) {
            url = "http://" + ip + "//app//secession.php";
            urlParameters = "id=" + params[1];
        }


        String response = null;
        try {
            initialize(params[0]);
            sendData(params[0]);
            response = readResponse();
        } catch (IOException e) {
            Log.d(TAG, "Error in HTTP POST request", e);
            return null;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // 서버 응답 결과 처리
        if (result.equals("success")) {

        }
        else if (result != null) {
            Log.d(TAG, "HTTP POST request result : " + result);
        }
        else {

        }
    }
    protected void initialize(String param) throws IOException {
        obj = new URL(url);
        con = (HttpURLConnection) obj.openConnection();

        // HTTP POST 요청 설정
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        if (param.equals("#capture")) {
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=boundary");
        }
        else {
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        }

    }

    protected void sendData(String param) throws IOException {
        if (param.equals("#capture")) {
            File file = new File(urlParameters);
            FileInputStream fileInputStream = new FileInputStream(file);

            // 파일 전송을 위한 데이터 쓰기
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes("--boundary\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
            wr.writeBytes("\r\n");

            // 파일 본문 쓰기
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                wr.write(buffer, 0, bytesRead);
            }
            wr.writeBytes("\r\n");
            wr.writeBytes("--boundary--\r\n");

            wr.flush();
            wr.close();
            fileInputStream.close();
        }
        else {
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        }

    }

    protected String readResponse() throws IOException {
        // HTTP 응답 코드 가져오기
        int responseCode = con.getResponseCode();

        Log.d(TAG, "\nSending 'POST' request to URL : " + url);
        Log.d(TAG, "Post parameters : " + urlParameters);
        Log.d(TAG, "Response Code : " + responseCode);

        // HTTP 응답 결과 읽기
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        con.disconnect();
        // HTTP 응답 결과 반환
        return response.toString();
    }
}