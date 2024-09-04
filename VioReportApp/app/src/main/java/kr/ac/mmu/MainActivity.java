package kr.ac.mmu;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.getSystemService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Button buttonLogin, buttonJoin;
    private EditText editTextId, editTextPassword;
    private TextView textViewCheck;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String id;
    private boolean isLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("Main", "Start");
        sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
        editor = sp.edit();
        id = sp.getString("userId", null);
        isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
        // UI 위젯과 연결
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonJoin = findViewById(R.id.buttonJoin);
        textViewCheck = findViewById(R.id.textView_main_check);

        // 회원가입 버튼 리스너
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Join_Id_Activity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 리스너
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = String.valueOf(editTextId.getText());
                String pass = String.valueOf(editTextPassword.getText());

                try {
                    String message = new HttpTask().execute("#login", id, pass).get();
                    if (message.equals("true")) {
                        SharedPreferences sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        // 로그인 정보 저장
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userId", id);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);

                        finish();
                    }
                    else if (message.equals("idError")) {
                        editTextId.requestFocus();
                        editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        textViewCheck.setText("아이디가 존재하지않습니다.");
                        textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                    }
                    else if (message.equals("passError")) {
                        editTextPassword.requestFocus();
                        editTextPassword.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        textViewCheck.setText("비밀번호가 일치하지 않습니다.");
                        textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

        createNotificationChannel();
        MyWorker.startPeriodicWork(this);

    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            CharSequence channelName = "Channel Name";
            String channelDescription = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
