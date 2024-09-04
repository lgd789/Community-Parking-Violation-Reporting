package kr.ac.mmu;

import android.content.Context;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker {
    private static final String TAG = "MyWorker";
    private Context context;


    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (hasNotificationPermission()) {
            String ip = "218.157.77.240:51212";
            String url = "http://"+ ip + "/app/reportRequest.php";
            LocationHelper location = new LocationHelper(context);
            String urlParameters = "latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude();

            String responseData = null;
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 응답이 성공적으로 받아졌을 경우
                    InputStream inputStream = con.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    bufferedReader.close();
                    inputStream.close();

                    responseData = response.toString();

                    Log.d(TAG, responseData);
                } else {
                    // 응답이 실패한 경우
                    Log.d(TAG, String.valueOf(responseCode));
                }

                con.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Integer.valueOf(responseData) > 0) {
                String message = "주변에 신고된 차량이 " + responseData + "대 있습니다";
                showNotification("신고 요망", message);
            }

            return Result.success();
        }
        return null;
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(123, builder.build());
    }

    private boolean hasNotificationPermission() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        return notificationManager.areNotificationsEnabled();
    }

    public static void startPeriodicWork(Context context) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                MyWorker.class,
                30, TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("mywork", ExistingPeriodicWorkPolicy.KEEP, workRequest);

    }
//    public static void getWorkStatusByName(Context context, String workName) {
//        WorkManager workManager = WorkManager.getInstance(context);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            workManager.getWorkInfosForUniqueWork(workName).addListener(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        List<WorkInfo> workInfos = workManager.getWorkInfosForUniqueWork(workName).get();
//                        for (WorkInfo workInfo : workInfos) {
//                            Log.d(TAG, "WorkInfo: " + workInfo.getState().name());
//                        }
//                    } catch (ExecutionException | InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, context.getMainExecutor());
//        }
//    }
}