package emkej.rssianalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.lang.Object;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class Analyzer extends Activity {
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    Button btn1,btn2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzer);
        mainText = (TextView) findViewById(R.id.text_field);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();

        btn2=(Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 mainWifi.startScan();
                 }
                 });

        btn1= (Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        File myFile = new File(Environment.getExternalStorageDirectory().getPath() + "/merania.txt");
                        myFile.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(sb);
                        myOutWriter.close();
                        fOut.close();
                            Toast.makeText(getBaseContext(), "Dáta uložené", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Obnoviť");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mainWifi.startScan();
        return super.onMenuItemSelected(featureId, item);
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

            //sb = new StringBuilder(); //ak chcem uchovat predch. zapis
            wifiList = mainWifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++){
                sb.append("SSID: " + (wifiList.get(i).SSID) + "\n");
                sb.append("AP MAC: " + (wifiList.get(i).BSSID) + "\n");
                sb.append("ZABEZPEČENIE: " + (wifiList.get(i).capabilities) + "\n");
                sb.append("FREKVENCIA: " + (wifiList.get(i).frequency) + " Hz \n");
                sb.append("RSSI: " + (wifiList.get(i).level) + "dBm \n");
                //timestamp je až od API 17
                sb.append("\n\n");
            }
            mainText.setText(sb);

        }

    }


}
