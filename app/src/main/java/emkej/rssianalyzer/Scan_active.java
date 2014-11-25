package emkej.rssianalyzer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Scan_active extends Activity{
    private WifiManager wifiMan;
    private BroadcastReceiver recWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long nowTime = System.nanoTime();
            startScanActive();
                for (ScanResult result : wifiMan.getScanResults()) {
                    int channel;
                    if ((result.frequency) >= 2412 && (result.frequency) <= 2484) {
                        channel = ((result.frequency) - 2412) / 5 + 1;
                    } else if ((result.frequency) >= 5170 && (result.frequency) <= 5825) {
                        channel = ((result.frequency) - 5170) / 5 + 34;
                    } else {
                        channel = -999;
                    }
                    builder.append(receiveCount).append(";").append(nowTime- lastTime).append(";").append(result.SSID).append(";").append(result.BSSID).append(";")
                            .append(result.capabilities).append(";").append(result.frequency).append(";")
                            .append(result.level).append(";").append(result.level + 100).append(";").append(channel).append(";");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    Long prevTimeStamp = hashMap.get(result.BSSID);
                    hashMap.put(result.BSSID, result.timestamp);
                    builder.append(result.timestamp).append(";");
                    if (prevTimeStamp == null) {
                        builder.append("0");
                    } else {
                        builder.append(result.timestamp - prevTimeStamp);
                    }
                    builder.append(";");
                        builder.append(System.getProperty("line.separator"));
                }else{
                        builder.append(System.getProperty("line.separator"));
                    }
            }
            lastTime = nowTime;
            receiveCount++;



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
        setContentView(R.layout.activity_scan_active);

        Button button = (Button) findViewById(R.id.button_active);
        txt = (TextView) findViewById(R.id.textView_active);
        wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(recWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        startScanActive();


        lastTime = System.nanoTime();

        button.setOnClickListener(new View.OnClickListener() {
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
    public void startScanActive()
    {
        try {
            Method method=WifiManager.class.getMethod("startScanActive");
            method.invoke(wifiMan);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() {
        EditText edittxt=(EditText)findViewById(R.id.etext_active);
        String name=edittxt.getText().toString();
        File file= new File(Environment.getExternalStorageDirectory().getPath() + "/" + getFullDate() + "_scan_active_" + getDeviceIdentification() + "_" + name + ".txt");
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
        super.onResume();
    }
}

