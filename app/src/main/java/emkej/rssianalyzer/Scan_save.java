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
import java.util.HashMap;
import java.util.Map;


public class Scan_save extends Activity {
    private WifiManager wifi_manager;
    private BroadcastReceiver receiv_Wifi=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long endTime=System.nanoTime();
            str_onscreen.setLength(0);
            wifi_manager.startScan();
            counterOfReceive++;
            str_onscreen.append(counterOfReceive).append(";").append(endTime- startTime).append(";");


                for (ScanResult result : wifi_manager.getScanResults()) {

                    str_builder.append(counterOfReceive).append(";").append(endTime- startTime).append(";");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        Long prevTimeStamp = hash.get(result.BSSID);
                        hash.put(result.BSSID, result.timestamp);
                        str_onscreen.append(result.BSSID).append("=").append(result.timestamp).append("=");
                        str_builder.append(result.timestamp).append(";");
                        if (prevTimeStamp == null) {
                            str_onscreen.append("0");
                            str_builder.append("0");
                        } else {
                            str_onscreen.append(result.timestamp - prevTimeStamp);
                            str_builder.append(result.timestamp - prevTimeStamp);
                        }
                        str_onscreen.append(";");
                        str_builder.append(";");
                    }

                    generalText.setText(str_onscreen);


                    int channel;
                    if ((result.frequency) >= 2412 && (result.frequency) <= 2484) {
                        channel = ((result.frequency) - 2412) / 5 + 1;
                    } else if ((result.frequency) >= 5170 && (result.frequency) <= 5825) {
                        channel = ((result.frequency) - 5170) / 5 + 34;
                    } else {
                        channel = -999;
                    }
                    str_builder.append(result.SSID).append(";").append(result.BSSID).append(";")
                            .append(result.capabilities).append(";").append(result.frequency).append(";")
                            .append(result.level).append(";").append(result.level + 100).append(";").append(channel).append(";");
                    str_builder.append(System.getProperty("line.separator"));

                }
            startTime = endTime;
                str_builder.append(System.getProperty("line.separator"));
                str_onscreen.append(System.getProperty("line.separator"));
            }

    };
    private TextView generalText;
    private Map<String, Long> hash = new HashMap<String, Long>();
    private StringBuilder str_builder = new StringBuilder();
    private StringBuilder str_onscreen = new StringBuilder();
    private BufferedWriter buff;
    private int counterOfReceive=0;
    private long startTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_save);

        final Button button_stop=(Button)findViewById(R.id.btn_stop);
        final Button button_stop_save=(Button)findViewById(R.id.btn_stop_save);
        generalText=(TextView)findViewById(R.id.text_area);

        wifi_manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(receiv_Wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi_manager.startScan();
        startTime= System.nanoTime();

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_stop_save.setOnClickListener(new View.OnClickListener() {
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
        EditText eText_field=(EditText)findViewById(R.id.etext);
        String file_name=eText_field.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + getFullDate() + "_scan_all_" + getDeviceIdentification() + "_" + file_name + ".txt");
        try {
            file.createNewFile();
            buff = new BufferedWriter(new FileWriter(file));
            buff.write(String.valueOf(str_builder));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                buff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void onPause() {
        unregisterReceiver(receiv_Wifi);
        super.onPause();
    }
    protected void onResume() {
        super.onResume();
    }


}
