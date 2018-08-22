package com.example.shy81491932.gpsclock;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;


public class AddClockActivity extends AppCompatActivity {
    private String dis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clock);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Button backButtom = findViewById(R.id.backButtom);
        backButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //setText
        final EditText editText = findViewById(R.id.setText);

        //setButtom
        Button setButtom = findViewById(R.id.setButton);
        setButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText() != null) {
                    dis = editText.getText().toString();
                }
                Toast.makeText(AddClockActivity.this, "距离设置成功", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent();
                intent2.putExtra("distance", dis);
                setResult(RESULT_OK, intent2);
                finish();

            }
        });


    }
}

