package emkej.rssianalyzer;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Scan_once extends Activity {
    private WifiManager Wifi_man_once;
    private BroadcastReceiver rec_Wifi_once=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long actualTime = System.nanoTime();

            for (ScanResult result : Wifi_man_once.getScanResults()) {
            int channel;
            if ((result.frequency) >= 2412 && (result.frequency) <= 2484) {
                channel = ((result.frequency) - 2412) / 5 + 1;
            } else if ((result.frequency) >= 5170 && (result.frequency) <= 5825) {
                channel = ((result.frequency) - 5170) / 5 + 34;
            } else {
                channel = -999;
            }
                sb.append(actualTime-savedTime).append(";");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    sb.append(result.timestamp).append(";");
                }
                sb.append(result.SSID).append(";").append(result.BSSID).append(";")
                        .append(result.capabilities).append(";").append(result.frequency).append(";")
                        .append(result.level).append(";").append(result.level + 100).append(";").append(channel).append(";");
                sb.append(System.getProperty("line.separator"));


            }
            generalTextOnce.setText(sb);
            unregisterReceiver(rec_Wifi_once);
    }

    };


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



        public static String getDeviceIdentification() {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            return (manufacturer == null ? "unknown" : manufacturer) + "_" + (model == null ? "unknown" : model) + "_" + Build.VERSION.SDK_INT;
        }
        public static String getFullDate(){
            Date cDate = new Date();
            return new SimpleDateFormat("yyyy-MM-dd-kk-mm").format(cDate);
        }

    public void saveToFile() {
        EditText eTextOnce=(EditText)findViewById(R.id.etext_once);
        String filename=eTextOnce.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + getFullDate() + "_scan_once_" + getDeviceIdentification() + "_" + filename + ".txt");
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
        super.onPause();
    }
}

