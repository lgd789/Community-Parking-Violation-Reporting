package kr.ac.mmu;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Join_Id_Activity extends AppCompatActivity {
    private EditText editTextId;
    private Button buttonContinue;
    private ImageButton imageButtonIdBack;
    private TextView textViewToast, textViewLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_id);

        // UI 위젯과 연결
        editTextId = findViewById(R.id.editText_id);
        buttonContinue = findViewById(R.id.button_countinue);
        imageButtonIdBack = findViewById(R.id.imageButton_id_back);
        textViewToast = findViewById(R.id.textView_id_toast);
        textViewLength = findViewById(R.id.textView_id_length);

        // focus 설정
        editTextId.requestFocus();

        // 필터 적용
        editTextId.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                textViewLength.setText("영문, 숫자만 입력 가능합니다.");
                textViewLength.setTextColor(Color.parseColor("#FF0000"));
                editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                return "";
            }
        }});

        // 키보드 완료(엔터) 버튼 이벤트
        editTextId.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        editTextId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textViewLength.setText(" ");
                editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() < 4){
                    textViewLength.setText("아직 4자리가 아니에요");
                    textViewLength.setTextColor(Color.parseColor("#FF0000"));
                    editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                }
                else {
                    textViewLength.setText("알맞은 아이디 입니다 :)");
                    textViewLength.setTextColor(Color.parseColor("#00FF00"));
                    editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                }
            }
        });

        // 계속하기 버튼 리스너
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        // 뒤로가기 버튼 리스너
        imageButtonIdBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void nextActivity(){
        String id = editTextId.getText().toString().trim();

        // 입력 값 유효성 검사
        if (id.isEmpty()) {
            textViewLength.setText("아이디를 입력해 주세요.");
            textViewLength.setTextColor(ColorStateList.valueOf(Color.RED));
            editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }
        else if (editTextId.getText().length() >= 4){ // id 전달
            String doubleCheckId = null;
            try {
                doubleCheckId = new HttpTask().execute("#check", id).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Log.e(TAG, "DOUBLECHECKID" + doubleCheckId);
            if (doubleCheckId.equals("true")) {
                textViewLength.setText("사용중인 아이디입니다.");
                editTextId.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                textViewLength.setTextColor(Color.parseColor("#FF0000"));

            }
            else {
                Intent intent = new Intent(getApplicationContext(), Join_Password_Activity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }
    }
}