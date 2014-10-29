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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class Scanner extends Activity {
    WifiManager Wifi_man;
    WifiReceiver rec_Wifi;
    List<ScanResult> wifi_result;
    Button button;
    TextView txt;
    long lastTime;
    StringBuilder string_b = new StringBuilder();
    int receive_count = 1;
    HashMap<String, String> hashmap = new HashMap<String, String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        button = (Button) findViewById(R.id.btn_scan_stop);
        txt = (TextView) findViewById(R.id.txtview_scan);
        Wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        rec_Wifi = new WifiReceiver();
        registerReceiver(rec_Wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Wifi_man.startScan();
        lastTime = System.currentTimeMillis();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregisterReceiver(rec_Wifi);
                finish();
            }
        });

    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long nowTime = System.currentTimeMillis();
            string_b.append(receive_count);
            receive_count += 1;
            string_b.append(";");
            string_b.append(nowTime - lastTime);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wifi_result=Wifi_man.getScanResults();
                for (int i = 0; i < wifi_result.size(); i++) {
                    if (!(hashmap.containsKey(wifi_result.get(i).BSSID))) {
                        hashmap.put(wifi_result.get(i).BSSID, String.valueOf(wifi_result.get(i).timestamp));

                    } else {
                        string_b.append(";");
                        string_b.append((wifi_result.get(i).timestamp - Long.parseLong(String.valueOf(hashmap.get(wifi_result.get(i).BSSID)))) / 1000);
                        hashmap.put(wifi_result.get(i).BSSID, String.valueOf(wifi_result.get(i).timestamp));
                    }

                }


            }else {
                string_b.append(System.getProperty("line.separator"));
            }

            string_b.append(System.getProperty("line.separator"));
            txt.setText(string_b);
            lastTime = nowTime;
            Wifi_man.startScan();
        }
    }

}
