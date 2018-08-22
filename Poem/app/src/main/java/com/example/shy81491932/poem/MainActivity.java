package com.example.shy81491932.poem;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        //添加按钮监听
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toast
                Toast toast = Toast.makeText(getApplicationContext(),"...",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                //读取输入的关键字
                EditText editText = findViewById(R.id.editText);
                String word = editText.getText().toString();
                String title= "",writer= "",res="";

                int num = 0;
                try {
                    TextView textView = findViewById(R.id.textView);
                    Scanner scan = new Scanner(getResources().getAssets().open("诗"));
                    utter:while(scan.hasNext()) {
                        String line = scan.nextLine();
                        for (int ii = 0; ii < line.length(); ii++) {
                            if (line.charAt(ii) == '<') {
                                title = line;
                                continue utter;
                            }
                            if (line.charAt(ii) == '(') {
                                writer = line;
                                continue utter;
                            }
                            if (line.indexOf(word) == ii) {
                                num++;
                                res += num + "." + line + "\n————" + title + " " + writer + "\n";
                                textView.setText(res);

                            }
                            else if (word == "。" || word == "？" || word == "，" || word == "!")
                                break utter;
                            textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                        }


                        //字体
                        int k = 0;
                        SpannableStringBuilder hl = new SpannableStringBuilder(res);
                        while (k >= 0) {
                            int l = res.indexOf(word, k);

                            if (l == -1)
                                break;
                            k = l + 1;

                            hl.setSpan(new ForegroundColorSpan(Color.BLUE), l, l + word.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                        textView.setText(hl);
                    }
                        if (num==0)
                        textView.setText("抱歉，您所输入的字不在所收录的诗中");
                }
                catch (IOException e){
                    TextView textView = findViewById(R.id.textView);
                    res = "抱歉，您所输入的字不在所收录的诗中";
                    textView.setText(res);

                }
            }
        });


    }
}


