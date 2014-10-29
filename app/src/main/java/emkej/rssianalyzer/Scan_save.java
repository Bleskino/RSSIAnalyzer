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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Scan_save extends Activity {
    Button button_stop;
    Button button_stop_save;
    EditText eText_field;
    TextView generalText;
    WifiManager wifi_manager;
    WifiReceiver receiv_Wifi;
    List<ScanResult> wifi_list;
    HashMap<String, String> hash = new HashMap<String, String>();
    StringBuilder str_builder = new StringBuilder();
    StringBuilder str_onscreen = new StringBuilder();
    String file_name;
    BufferedWriter buff;
    int counterOfReceive=1;
    long startTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_save);
        button_stop=(Button)findViewById(R.id.btn_stop);
        button_stop_save=(Button)findViewById(R.id.btn_stop_save);
        eText_field=(EditText)findViewById(R.id.etext);
        generalText=(TextView)findViewById(R.id.text_area);
        wifi_manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiv_Wifi = new WifiReceiver();
        registerReceiver(receiv_Wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        startTime= System.currentTimeMillis();
        wifi_manager.startScan();

        eText_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!eText_field.getText().toString().trim().equals("")) {
                    button_stop_save.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregisterReceiver(receiv_Wifi);
                Toast.makeText(getBaseContext(), "Stopped", Toast.LENGTH_SHORT).show();
                file_name=null;
                finish();
            }
        });

        button_stop_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregisterReceiver(receiv_Wifi);
                saveToFile();
                Toast.makeText(getBaseContext(), "Stopped and Saved", Toast.LENGTH_SHORT).show();
                file_name=null;
                finish();
            }
        });


    }
    public void saveToFile() {
        eText_field=(EditText)findViewById(R.id.etext);
        file_name=eText_field.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + file_name + ".txt");
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



   class WifiReceiver extends BroadcastReceiver {
       @Override
       public void onReceive(Context context, Intent intent) {
            long endTime=System.currentTimeMillis();
           wifi_list = wifi_manager.getScanResults();

           for (int i = 0; i < wifi_list.size(); i++) {
               int channel;
               if ((wifi_list.get(i).frequency) >= 2412 && (wifi_list.get(i).frequency) <= 2484) {
                   channel = ((wifi_list.get(i).frequency) - 2412) / 5 + 1;
               } else if ((wifi_list.get(i).frequency) >= 5170 && (wifi_list.get(i).frequency) <= 5825) {
                   channel = ((wifi_list.get(i).frequency) - 5170) / 5 + 34;
               } else {
                   channel = -999;
               }
               str_builder.append(wifi_list.get(i).SSID);
               str_builder.append(";");
               str_builder.append(wifi_list.get(i).BSSID);
               str_builder.append(";");
               str_builder.append(wifi_list.get(i).capabilities);
               str_builder.append(";");
               str_builder.append(wifi_list.get(i).frequency);
               str_builder.append(";");
               str_builder.append(wifi_list.get(i).level);
               str_builder.append(";");
               str_builder.append(channel);
               str_builder.append(";");
               str_builder.append(endTime - startTime);
               str_builder.append("ms");


               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                    if(!(hash.containsKey(wifi_list.get(i).BSSID))) {
                        hash.put(wifi_list.get(i).BSSID, String.valueOf(wifi_list.get(i).timestamp));

                    }else {
                        str_builder.append(";");
                        str_builder.append((wifi_list.get(i).timestamp - Long.parseLong(String.valueOf(hash.get(wifi_list.get(i).BSSID))))/1000);
                        hash.put(wifi_list.get(i).BSSID, String.valueOf(wifi_list.get(i).timestamp));
                    }

                   str_builder.append(System.getProperty("line.separator"));
               }else{
                   str_builder.append(System.getProperty("line.separator"));
               }

           }
           startTime=endTime;
           str_builder.append(System.getProperty("line.separator"));

           str_onscreen.append(counterOfReceive);
           counterOfReceive += 1;
           str_onscreen.append(System.getProperty("line.separator"));
           generalText.setText(str_onscreen);

           wifi_manager.startScan();
       }
       }

}
