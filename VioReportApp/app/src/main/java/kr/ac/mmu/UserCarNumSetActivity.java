package kr.ac.mmu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class UserCarNumSetActivity extends AppCompatActivity {
    private ImageButton imageButtonUpdateCarNumBack;
    private EditText editTextCarNumber;
    private Button buttonMod;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_num_set);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        imageButtonUpdateCarNumBack = findViewById(R.id.imageButton_updateCarNum_back);
        editTextCarNumber = findViewById(R.id.editText_carnumber_update);

        // 뒤로가기 버튼 리스너
        imageButtonUpdateCarNumBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        buttonMod = findViewById(R.id.button_car_mod);
        buttonMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String carNum = editTextCarNumber.getText().toString();
                    String message = new HttpTask().execute("#updateCarNum", id, carNum).get();

                    Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();

                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }
}