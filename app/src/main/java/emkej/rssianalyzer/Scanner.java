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
import java.util.HashMap;
import java.util.Map;


public class Scanner extends Activity {
    private WifiManager wifiMan;
    private BroadcastReceiver recWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long nowTime = System.nanoTime();
            builder.append(receiveCount);
            wifiMan.startScan();
            receiveCount++;
            builder.append(";").append(nowTime- lastTime).append(";");
            lastTime = nowTime;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                for (ScanResult result : wifiMan.getScanResults()) {
                    Long prevTimeStamp = hashMap.get(result.BSSID);
                    hashMap.put(result.BSSID, result.timestamp);
                    builder.append(result.BSSID).append("=");
                    if (prevTimeStamp == null) {
                        builder.append("0");
                    } else {
                        builder.append(result.timestamp - prevTimeStamp);
                    }
                    builder.append(";");
                }
            }
            builder.append(System.getProperty("line.separator"));
            txt.setText(builder);
        }
    };
    private TextView txt;
    private long lastTime;
    private StringBuilder builder = new StringBuilder();
    private int receiveCount = 1;
    private Map<String, Long> hashMap = new HashMap<String, Long>();
    private BufferedWriter bw;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Button button = (Button) findViewById(R.id.btn_scan_stop);
        txt = (TextView) findViewById(R.id.txtview_scan);
        wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(recWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiMan.startScan();
        lastTime = System.nanoTime();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToFile();
                finish();
            }
        });

    }
    public void saveToFile() {
        EditText edittxt=(EditText)findViewById(R.id.etext_scanner);
        String name=edittxt.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + name + "_quick.txt");
        try {
            file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(String.valueOf(builder));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void onPause() {
        unregisterReceiver(recWifi);
        super.onPause();
    }
    protected void onResume() {
        registerReceiver(recWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
}
