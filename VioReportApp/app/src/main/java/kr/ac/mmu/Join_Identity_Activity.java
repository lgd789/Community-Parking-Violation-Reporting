package kr.ac.mmu;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.regex.Pattern;

public class Join_Identity_Activity extends AppCompatActivity {
    private Button buttonSignup, buttonSkip;
    private ImageButton imageButtonIdentityBack;
    private EditText editTextCarNumber;
    private TextView textViewToast;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_identity);

        //firebase 토큰값
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        token = task.getResult();
                    }
                });

        // UI 위젯과 연결
        buttonSignup = findViewById(R.id.button_signup);
        buttonSkip = findViewById(R.id.button_skip);
        imageButtonIdentityBack = findViewById(R.id.imageButton_identity_back);
        editTextCarNumber = findViewById(R.id.editText_carnumber);
        textViewToast = findViewById(R.id.textView_identity_toast);

        // focus 설정
        editTextCarNumber.requestFocus();

        // 필터 설정
        editTextCarNumber.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                return "";
            }
        }});


        // 키보드 완료(엔터) 버튼 이벤트
        editTextCarNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextCarNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    nextActivity();
                    return true;
                }
                return false;
            }
        });

        // 회원가입 버튼 클릭 이벤트 처리
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        // 건너뛰기 버튼 리스너
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // id, pw 가져오기
                Intent intent = getIntent();
                String id = intent.getStringExtra("id");
                String password = intent.getStringExtra("password");

                // 회원 등록
                new HttpTask().execute("#join", id, password, "", token);

                //id , password, carNum 전달
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // 뒤로가기 버튼 리스너
        imageButtonIdentityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void nextActivity(){
        String carNumber = editTextCarNumber.getText().toString().trim();

        // 입력 값 유효성 검사
        if (carNumber.isEmpty()) {
            textViewToast.setText("차량번호를 입력해 주세요.");
        } else {
            // id, pw 가져오기
            Intent intent = getIntent();
            String id = intent.getStringExtra("id");
            String password = intent.getStringExtra("password");

            new HttpTask().execute("#join", id, password, carNumber, token);

            //id , password, phone 전달
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);


        }
    }
}