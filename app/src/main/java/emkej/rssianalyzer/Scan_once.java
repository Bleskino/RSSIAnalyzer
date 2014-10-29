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
import java.util.List;


public class Scan_once extends Activity {
    EditText eTextOnce;
    Button btn_once;
    TextView generalTextOnce;
    WifiManager Wifi_man_once;
    WifiReceiver rec_Wifi_once;
    List<ScanResult> wifi_result_once;
    String filename;
    BufferedWriter buf;
    StringBuilder sb=new StringBuilder();
    long savedTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_once);
        eTextOnce=(EditText)findViewById(R.id.etext_once);
        generalTextOnce=(TextView)findViewById(R.id.text_field_once);
        btn_once=(Button)findViewById(R.id.button_save_once);
        Wifi_man_once = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        rec_Wifi_once = new WifiReceiver();
        registerReceiver(rec_Wifi_once, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        savedTime=System.currentTimeMillis();
        Wifi_man_once.startScan();

        btn_once.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregisterReceiver(rec_Wifi_once);
                saveToFile();
                Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_SHORT).show();
                filename=null;
                finish();

            }
        });

        eTextOnce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!eTextOnce.getText().toString().trim().equals("")) {
                    btn_once.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    public void saveToFile() {
        eTextOnce=(EditText)findViewById(R.id.etext_once);
        filename=eTextOnce.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + filename + ".txt");
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

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifi_result_once = Wifi_man_once.getScanResults();

            for (int i = 0; i < wifi_result_once.size(); i++) {
                int channel;
                if ((wifi_result_once.get(i).frequency) >= 2412 && (wifi_result_once.get(i).frequency) <= 2484) {
                    channel = ((wifi_result_once.get(i).frequency) - 2412) / 5 + 1;
                } else if ((wifi_result_once.get(i).frequency) >= 5170 && (wifi_result_once.get(i).frequency) <= 5825) {
                    channel = ((wifi_result_once.get(i).frequency) - 5170) / 5 + 34;
                } else {
                    channel = -999;
                }
                sb.append(wifi_result_once.get(i).SSID);
                sb.append(";");
                sb.append(wifi_result_once.get(i).BSSID);
                sb.append(";");
                sb.append(wifi_result_once.get(i).capabilities);
                sb.append(";");
                sb.append(wifi_result_once.get(i).frequency);
                sb.append(";");
                sb.append(wifi_result_once.get(i).level);
                sb.append(";");
                sb.append(channel);
                sb.append(";");
                sb.append(savedTime - System.currentTimeMillis());
                sb.append("ms");
                sb.append(System.getProperty("line.separator"));
                sb.append(System.getProperty("line.separator"));
            }

            generalTextOnce.setText(sb);

        }
        }
}
