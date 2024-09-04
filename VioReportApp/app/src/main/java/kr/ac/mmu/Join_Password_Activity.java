package kr.ac.mmu;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.regex.Pattern;

public class Join_Password_Activity extends AppCompatActivity {
    // 상수 선언
    private final int DYNAMIC_EDITTEXT_ID = 0X8000;

    // 변수 선언
    private int count = 0;
    private TextView textViewMain, textViewToast, textViewLength;
    private EditText editTextPassword,  dynamicEditText;

    private Space space1;
    private LinearLayout dynamicLayout, mainLayout, bottomLayout;
    private Button buttonContinue2;
    private ImageButton imageButtonPasswordBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_password);
        // UI 위젯과 연결
        textViewMain = findViewById(R.id.textView_password_main);
        textViewToast = findViewById(R.id.textView_password_toast);
        textViewLength = findViewById(R.id.textView_password_length);
        dynamicLayout = findViewById(R.id.dynamicArea);
        mainLayout = findViewById(R.id.mainLayout);
        bottomLayout = findViewById(R.id.bottomLayout);
        editTextPassword = findViewById(R.id.editText_password);
        space1 = findViewById(R.id.space1);
        buttonContinue2 = findViewById(R.id.button_countinue2);
        imageButtonPasswordBack = findViewById(R.id.imageButton_password_back);

        // focus 설정
        editTextPassword.requestFocus();

        // 필터 설정
        editTextPassword.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9!-/:-@\\[-`{-~]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                textViewLength.setText("영문, 숫자, 특수문자만 입력 가능합니다.");
                textViewLength.setTextColor(Color.parseColor("#FF0000"));
                editTextPassword.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                return "";
            }
        }});

        // 키보드 완료(엔터) 버튼 이벤트
        editTextPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT)
                {
                    String password = editTextPassword.getText().toString().trim();

                    // 입력 값 유효성 검사
                    if (password.isEmpty()) {
                        textViewToast.setText("비밀번호를 입력해 주세요.");
                        editTextPassword.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    } else if(password.length() >= 8 && count == 0){
                        addPasswordAgain();
                    }
                    return true;
                }
                return false;
            }
        });

        //
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editTextPassword.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 8){
                    textViewLength.setText("아직 8자리가 아니에요");
                    textViewLength.setTextColor(Color.parseColor("#FF0000"));
                    editTextPassword.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                }
                else {
                    textViewLength.setText("알맞은 비밀번호 입니다 :)");
                    textViewLength.setTextColor(Color.parseColor("#00FF00"));
                    editTextPassword.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                }
            }
        });

        // 계속하기 버튼 리스너
        buttonContinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString().trim();

                // 입력 값 유효성 검사
                if (password.isEmpty()) {
                    textViewToast.setText("비밀번호를 입력해 주세요.");
                } else if(password.length() >= 8 && count == 0) {
                    addPasswordAgain();
                }else if(count > 0){
                    nextActivity();
                }
            }
        });

        // 뒤로가기 버튼 리스너
        imageButtonPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
   public void addPasswordAgain(){
//        // 추가할 TextView 설정
//        TextView dynamicTextView = new TextView(this);
//        dynamicTextView.setText("비밀번호 확인");
//        dynamicTextView.setTextSize(Dimension.SP, 44);
//        dynamicTextView.setGravity(Gravity.FILL);
//        dynamicTextView.setTypeface(Typeface.DEFAULT_BOLD);


        // password_textMain 축소
        textViewMain.setText("좋습니다. 비밀번호 확인을 위해 \n 다시 한 번 입력해 주세요.");
        textViewMain.setTextSize(Dimension.SP, 20);
        textViewMain.setGravity(Gravity.FILL);
        mainLayout.removeView(space1);
        //bottomLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60));


        // 추가할 EditText 설정
        dynamicEditText = new EditText(this);
        dynamicEditText.setId(DYNAMIC_EDITTEXT_ID+count++);
        dynamicEditText.setHint("다시 한 번 입력해 주세요.");
        dynamicEditText.setTextSize(Dimension.SP, 18);
        dynamicEditText.setTextColor(Color.BLACK);
        dynamicEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // 필터 설정
        dynamicEditText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9!-/:-@\\[-`{-~]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                textViewToast.setText("영문, 숫자, 특수문자만 입력 가능합니다.");
                textViewToast.setTextColor(Color.parseColor("#FF0000"));
                dynamicEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                return "";
           }
        }});
        // 키보드 완료(엔터) 버튼 이벤트
        dynamicEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dynamicEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        // dynamicLayout 설정
        //dynamicLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));

        // 추가
        //dynamicLayout.addView(dynamicTextView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        dynamicLayout.addView(dynamicEditText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        dynamicEditText.requestFocus();

        dynamicEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textViewToast.setText(" ");
                dynamicEditText.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(editTextPassword.getText().toString())) {
                    textViewToast.setText("비밀번호가 일치해요 :)");
                    textViewToast.setTextColor(Color.parseColor("#00FF00"));
                    dynamicEditText.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                } else {
                    textViewToast.setText("비밀번호가 일치하지 않아요.");
                    textViewToast.setTextColor(Color.parseColor("#FF0000"));
                    dynamicEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                }
            }
        });
   }
   private void nextActivity(){
       String password = editTextPassword.getText().toString().trim();
       String passwordAgain = dynamicEditText.getText().toString().trim();

       // 입력 값 유효성 검사
       if (password.isEmpty() || passwordAgain.isEmpty()) {
           textViewToast.setText("모두 입력해 주세요.");
           textViewToast.setTextColor(Color.parseColor("#FF0000"));
       } else if(password.equals(passwordAgain)){
           // id 가져오기
           Intent intent = getIntent();
           String id = intent.getStringExtra("id");

           // id , pw 전달
           intent = new Intent(getApplicationContext(), Join_Identity_Activity.class);
           intent.putExtra("id", id);
           intent.putExtra("password", password);
           startActivity(intent);
       } else {
           textViewToast.setText("비밀번호가 일치하지 않아요.");
           textViewToast.setTextColor(Color.parseColor("#FF0000"));
           dynamicEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
       }
   }

}