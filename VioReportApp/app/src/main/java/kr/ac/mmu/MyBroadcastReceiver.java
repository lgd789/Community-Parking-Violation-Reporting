//package kr.ac.mmu;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import androidx.work.Constraints;
//import androidx.work.NetworkType;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;
//import androidx.work.WorkRequest;
//
//import java.util.concurrent.TimeUnit;
//
//public class MyBroadcastReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // 작업 제약 조건 설정
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresBatteryNotLow(true)
//                .build();
//
//        // 작업 요청 생성
//        PeriodicWorkRequest workRequest =
//                new PeriodicWorkRequest.Builder(MyWorker.class, 30, TimeUnit.MINUTES)
//                        .setConstraints(constraints)
//                        .build();
//
//        // 작업 스케줄링
//        WorkManager.getInstance(context).enqueue(workRequest);
//    }
//}
