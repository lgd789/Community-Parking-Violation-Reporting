//package kr.ac.mmu;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//public class LoginActivity extends AppCompatActivity {
//    private Button buttonLogin, buttonJoin;
//    private EditText editTextId, editTextPassword;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        // UI 위젯과 연결
//        editTextId = (EditText) findViewById(R.id.editTextId);
//        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
//        buttonLogin = (Button) findViewById(R.id.buttonLogin);
//        buttonJoin = findViewById(R.id.buttonJoin);
//
//        // 회원가입 버튼 리스너
//        buttonJoin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Join_Id_Activity.class);
//                startActivity(intent);
//            }
//        });
//
//        // 로그인 버튼 리스너
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String id = String.valueOf(editTextId.getText());
//                String pass = String.valueOf(editTextPassword.getText());
//
//                try {
//                    String message = new HttpTask().execute("#login", id, pass).get();
//
//                    if (message.equals("true")) {
//                        SharedPreferences sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sp.edit();
//
//                        // 로그인 정보 저장
//                        editor.putBoolean("isLoggedIn", true);
//                        editor.putString("userId", id);
//                        editor.apply();
//
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//
//                        finish();
//                    }
//                    else {
//                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        });
//    }
//
//}