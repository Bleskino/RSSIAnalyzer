package emkej.rssianalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.sql.Timestamp;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Analyzer extends Activity {
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    StringBuilder sbToScreen = new StringBuilder();
    Button btn1,btn2,btn3,btn4;
    EditText edittxt;
    String filename;
    BufferedWriter buf;
    int counterOfReceive=1;
    long startTime,firstScanTime,ts;
    Timestamp tmstmp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzer);
        mainText = (TextView) findViewById(R.id.text_field);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        edittxt=(EditText)findViewById(R.id.editText1);
        btn1=(Button)findViewById(R.id.button1);
        btn2=(Button)findViewById(R.id.button2);
        btn3=(Button)findViewById(R.id.button3);
        btn4=(Button)findViewById(R.id.button4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterReceiver(receiverWifi);
                saveToFile();
                Toast.makeText(getBaseContext(), "zastavene ulozene", Toast.LENGTH_SHORT).show();
                filename=null;
            }
        });
        edittxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!edittxt.getText().toString().trim().equals("")) {
                    btn1.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                sb.setLength(0);
                mainWifi.startScan();
                firstScanTime=System.currentTimeMillis();
                Toast.makeText(getBaseContext(), "Skenujem...", Toast.LENGTH_SHORT).show();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
        }});
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Useless");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mainWifi.startScan();
        return super.onMenuItemSelected(featureId, item);
    }

    public void saveToFile() {
        edittxt=(EditText)findViewById(R.id.editText1);
        filename=edittxt.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + filename + ".odt");
        try {
            file.createNewFile();
            buf = new BufferedWriter(new FileWriter(file));
            buf.write(String.valueOf(sb));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            wifiList = mainWifi.getScanResults();
            long prevTimestamp[] = new long[wifiList.size()];
            Arrays.fill(prevTimestamp,System.currentTimeMillis());
                if (counterOfReceive==1){
                    startTime=firstScanTime;

                }


                for (int i = 0; i < wifiList.size(); i++) {
                    int channel;
                    if ((wifiList.get(i).frequency) >= 2412 && (wifiList.get(i).frequency) <= 2484) {
                        channel = ((wifiList.get(i).frequency) - 2412) / 5 + 1;
                    } else if ((wifiList.get(i).frequency) >= 5170 && (wifiList.get(i).frequency) <= 5825) {
                        channel = ((wifiList.get(i).frequency) - 5170) / 5 + 34;
                    } else {
                        channel = -999;
                    }
                    sb.append(wifiList.get(i).SSID);
                    sb.append(";");
                    sb.append(wifiList.get(i).BSSID);
                    sb.append(";");
                    sb.append(wifiList.get(i).capabilities);
                    sb.append(";");
                    sb.append(wifiList.get(i).frequency);
                    sb.append(";");
                    sb.append(wifiList.get(i).level);
                    sb.append(";");
                    sb.append(channel);
                    sb.append(";");
                    ts = System.currentTimeMillis() - startTime;
                    sb.append(ts);
                    sb.append("ms");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        sb.append(";");
                        sb.append(wifiList.get(i).timestamp);
                        sb.append(";");
                        tmstmp=new Timestamp(wifiList.get(i).timestamp);
                        sb.append(tmstmp.toString());
                        sb.append(System.getProperty("line.separator"));
                    }else{
                        sb.append(System.getProperty("line.separator"));
                    }

                }


            sb.append(System.getProperty("line.separator"));

                //on screen text
                sbToScreen.append(counterOfReceive);
                counterOfReceive += 1;
                sbToScreen.append(System.getProperty("line.separator"));
                mainText.setText(sbToScreen);

            startTime=System.currentTimeMillis();
            mainWifi.startScan();
        }
    }
}
