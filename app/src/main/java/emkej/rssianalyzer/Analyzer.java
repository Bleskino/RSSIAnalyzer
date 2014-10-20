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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class Analyzer extends Activity {
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    Button btn1,btn2;
    EditText edittxt;
    String nazov;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzer);
        mainText = (TextView) findViewById(R.id.text_field);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //mainWifi.startScan();

        btn2=(Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 mainWifi.startScan(); //spustacie skenovanie, najprv som chcel nastavit nazov ukladacieho suboru
                 }
                 });


        edittxt=(EditText)findViewById(R.id.editText1);
        nazov=edittxt.getText().toString();

        /*btn1= (Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        File myFile = new File(Environment.getExternalStorageDirectory().getPath() + "/vysledky.txt");
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
            });*/
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Useless");
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
    public static int convertFrequencyToChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {

            //sb = new StringBuilder(); //ak chcem uchovat predch. zapis
            wifiList = mainWifi.getScanResults();

            for(int i = 0; i < wifiList.size(); i++){
                int channel;
                if ((wifiList.get(i).frequency) >= 2412 && (wifiList.get(i).frequency) <= 2484) {
                    channel= ((wifiList.get(i).frequency) - 2412) / 5 + 1;
                } else if ((wifiList.get(i).frequency) >= 5170 && (wifiList.get(i).frequency) <= 5825) {
                    channel= ((wifiList.get(i).frequency) - 5170) / 5 + 34;
                } else {
                    channel = -1;
                }

                sb.append("SSID: " + (wifiList.get(i).SSID) + "\n");
                sb.append("AP MAC: " + (wifiList.get(i).BSSID) + "\n");
                sb.append("ZABEZPEČENIE: " + (wifiList.get(i).capabilities) + "\n");
                sb.append("FREKVENCIA: " + (wifiList.get(i).frequency) + " Hz \n");
                sb.append("RSSI: " + (wifiList.get(i).level) + "dBm \n");
                sb.append("KANÁL: " + channel + "\n");

                //timestamp je až od API 17
                sb.append("\n");
            }
            sb.append("--------------------------------\n");
            mainText.setText(sb);
            try {
                File myFile = new File(Environment.getExternalStorageDirectory().getPath() + nazov +"_data.txt"); //nefunguje z editboxu zmena nazvu suboru
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(sb);
                myOutWriter.close();
                fOut.close();
                Toast.makeText(getBaseContext(), "Dáta uložené", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
            mainWifi.startScan();


        }

    }


}
