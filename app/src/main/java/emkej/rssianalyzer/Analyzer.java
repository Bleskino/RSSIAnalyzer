package emkej.rssianalyzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Analyzer extends Activity {
    TextView mainText;
    Button btn1,btn2,btn3,btn4;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzer);
        mainText = (TextView) findViewById(R.id.text_field);
        btn1=(Button)findViewById(R.id.button1);
        btn2=(Button)findViewById(R.id.button2);
        btn3=(Button)findViewById(R.id.button3);
        btn4=(Button)findViewById(R.id.button4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Scan_save.class));
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(),Scanner.class));
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());

            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Scan_once.class));

        }});
    }

}
