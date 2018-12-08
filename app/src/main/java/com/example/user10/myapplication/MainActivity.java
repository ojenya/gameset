package com.example.user10.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private EditText editText;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editLogin);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.btnReg);
    }


    public void onclick(View v) throws InterruptedException {
        String name = editText.getText().toString();
        MyThread myThread = new MyThread(name);
        myThread.start();
        myThread.join();


        if (myThread.token != -1) {
            //textView.setText("token: " + myThread.token);
            Intent intent = new Intent(this, Game.class);
            //System.out.println(myThread.token);
            intent.putExtra("token", myThread.token);
            startActivity(intent);
        } else {
            textView.setText("Такой логин уже существует! Пожалуйста введите другой.");
            textView.setTextColor(Color.RED);
        }

    }

}