package kr.ac.mmu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class UserInfoSetActivity extends AppCompatActivity {
    private String id;
    private TextView textViewId, textViewCheck;
    private EditText editTextCurPass, editTextNewPass;
    private Button buttonMod;
    private ImageButton imageButtonUpdateInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_set);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        // 뒤로가기 버튼
        imageButtonUpdateInfo = findViewById(R.id.imageButton_updateInfo_back);
        imageButtonUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textViewId = findViewById(R.id.textView_id);
        textViewId.setText(id);
        textViewCheck = findViewById(R.id.textView_check);
        editTextCurPass = findViewById(R.id.editText_curpass);
        editTextNewPass = findViewById(R.id.editText_newpass);

        editTextCurPass.requestFocus();
        editTextCurPass.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9!-/:-@\\[-`{-~]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                textViewCheck.setText("영문, 숫자, 특수문자만 입력 가능합니다.");
                textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                editTextCurPass.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                return "";
            }
        }});

        editTextCurPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textViewCheck.setText(" ");
                editTextCurPass.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() < 8){
                    textViewCheck.setText("아직 8자리가 아니에요");
                    textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                    editTextCurPass.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                }
                else {
                    textViewCheck.setText("알맞은 비밀번호입니다 :)");
                    textViewCheck.setTextColor(Color.parseColor("#00FF00"));
                    editTextCurPass.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                }
            }
        });

        editTextNewPass.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9!-/:-@\\[-`{-~]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                textViewCheck.setText("영문, 숫자, 특수문자만 입력 가능합니다.");
                textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                editTextNewPass.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                return "";
            }
        }});

        editTextNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textViewCheck.setText(" ");
                editTextNewPass.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() < 8){
                    textViewCheck.setText("아직 8자리가 아니에요");
                    textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                    editTextNewPass.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                }
                else {
                    textViewCheck.setText("알맞은 비밀번호입니다 :)");
                    textViewCheck.setTextColor(Color.parseColor("#00FF00"));
                    editTextNewPass.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                }
            }
        });


        buttonMod = findViewById(R.id.button_mod);
        buttonMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextNewPass.getText().length() < 8) {
                    textViewCheck.setText("8글자 이상 입력해 주세요.");
                    textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    try {
                        String curPass = editTextCurPass.getText().toString();
                        String newPass = editTextNewPass.getText().toString();
                        String message = new HttpTask().execute("#updateInfo", id, curPass, newPass).get();

                        if (message.equals("false")) {
                            textViewCheck.setText("현재 비밀번호가 맞지않습니다.");
                            textViewCheck.setTextColor(Color.parseColor("#FF0000"));
                            editTextCurPass.requestFocus();
                            editTextCurPass.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            finish();
                        }


                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}