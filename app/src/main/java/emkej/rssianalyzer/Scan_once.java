package emkej.rssianalyzer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Scan_once extends Activity {
    private WifiManager Wifi_man_once;
    private BroadcastReceiver rec_Wifi_once=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long actualTime = System.nanoTime();
            sb.append("1;").append(actualTime-savedTime).append(";");
            for (ScanResult result : Wifi_man_once.getScanResults()) {
            int channel;
            if ((result.frequency) >= 2412 && (result.frequency) <= 2484) {
                channel = ((result.frequency) - 2412) / 5 + 1;
            } else if ((result.frequency) >= 5170 && (result.frequency) <= 5825) {
                channel = ((result.frequency) - 5170) / 5 + 34;
            } else {
                channel = -999;
            }
                sb.append(result.SSID).append(";").append(result.BSSID).append(";")
                        .append(result.capabilities).append(";").append(result.frequency).append(";")
                        .append(result.level).append(";").append(channel).append(";");
                sb.append(System.getProperty("line.separator"));


            }
            generalTextOnce.setText(sb);
    }};


    private TextView generalTextOnce;
    private BufferedWriter buf;
    private StringBuilder sb=new StringBuilder();
    private long savedTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_once);

        generalTextOnce=(TextView)findViewById(R.id.text_field_once);
        final Button btn_once=(Button)findViewById(R.id.button_save_once);

        Wifi_man_once = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(rec_Wifi_once, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Wifi_man_once.startScan();
        savedTime=System.nanoTime();

        btn_once.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToFile();
                finish();

            }
        });
    }
    public void saveToFile() {
        EditText eTextOnce=(EditText)findViewById(R.id.etext_once);
        String filename=eTextOnce.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + filename + "_once.txt");
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

    protected void onResume() {
        super.onResume();
    }
    protected void onPause() {
        unregisterReceiver(rec_Wifi_once);
        super.onPause();
    }
}
