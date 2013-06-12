package edu.stu.wifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;    

import com.example.test.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;    
import android.content.Intent;     
import android.content.IntentFilter;    
import android.net.wifi.ScanResult;    
import android.net.wifi.WifiManager;    
import android.os.Bundle;    
import android.util.Log;   
import android.view.LayoutInflater;
import android.view.View;    
import android.view.View.OnClickListener;    
import android.view.Window;
import android.widget.Button;    
import android.widget.FrameLayout;
import android.widget.ListView;    
import android.widget.SimpleAdapter;    
import android.widget.TabHost;
import android.widget.TextView;    
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
 {      
    WifiManager wifi;       
    ListView lv;
    TextView textStatus;
    Button buttonScan;
    Button buttonCal;
    Button buttonPos;
    int size = 0;
    List<ScanResult> results;
    List<String> lLocateTEMP= new ArrayList<String>(); 
    List<String> lLocateA= new ArrayList<String>(); 
    List<String> lLocateB= new ArrayList<String>();
    
    
    String[][] saLocateTEMP= new String[2][];
    String[][] saLocateA= new String[2][];
    String[][] saLocateB= new String[2][];
    
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fl);
        
        // 使用LayoutInflater取得tabhost
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.tabhost_template, null);
        
        TabHost tabhost = (TabHost)view.findViewById(R.id.tabhost);
        tabhost.setup();

        TabHost.TabSpec tabcontent1 = tabhost.newTabSpec("tab01");
        tabcontent1.setIndicator("Scan");
        tabcontent1.setContent(R.id.tab1);
        
        
        TabHost.TabSpec tabcontent2 = tabhost.newTabSpec("tab02");
        tabcontent2.setIndicator("Map");
        tabcontent2.setContent(R.id.tab2);
        
        tabhost.addTab(tabcontent1);
        tabhost.addTab(tabcontent2);
        
        FrameLayout baseFL = (FrameLayout)findViewById(R.id.fl);
        baseFL.addView(tabhost);
        
        
        
        

        textStatus = (TextView) findViewById(R.id.textView1);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonCal = (Button) findViewById(R.id.buttonCal);
        buttonPos = (Button) findViewById(R.id.buttonPos);
        buttonScan.setOnClickListener(this);
        buttonCal.setOnClickListener(this);
        buttonPos.setOnClickListener(this);
        
        lv = (ListView)findViewById(R.id.list);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi現在為關閉狀態，重啟動中", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }   
        this.adapter = new SimpleAdapter
        		(MainActivity.this, arraylist, R.layout.row, 
        				new String[] { ITEM_KEY }, new int[] { R.id.list_value });
       
        lv.setAdapter(this.adapter);

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent) 
            {
               results = wifi.getScanResults();
               size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));   
        
        
    }

    
    @Override
	public void onClick(View view) 
    {
    	
    	
    	switch(view.getId())
    	{
    	case R.id.buttonScan:
    		buttonScan.setEnabled(false);
    		lLocateTEMP.clear(); 
    		
            arraylist.clear();          
            wifi.startScan();

            Toast.makeText(this, "Scanning..." + size, Toast.LENGTH_SHORT).show();

            if(size>0)
            {saLocateTEMP=new String[2][size];}
            else {
            	saLocateTEMP=new String[2][1];
            	
			} 
            try 
            {
            	 int i=0;
                size = size - 1;
                
                while (size >= 0) 
                {   
                    HashMap<String, String> item = new HashMap<String, String>();                       
                    item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).level);
                    
                    lLocateTEMP.add(results.get(size).SSID+";"+results.get(size).level);
                   
                    if(results.get(size).level>(-80))
                    {	
                    	saLocateTEMP[0][i]=results.get(size).SSID;
                    	saLocateTEMP[1][i]= String.valueOf(results.get(size).level);
                    }
                    
                    arraylist.add(item);
                    i++;
                    size--;
                    adapter.notifyDataSetChanged();                 
                } 
                
                
            }
            catch (Exception e)
            {Log.d("OOPS", e.toString()); }         
            buttonScan.setEnabled(true);
    		break;
    		
    	case R.id.buttonCal:
    		 
             if(lLocateA.size()==0)
             {
             	lLocateA=lLocateTEMP;	
             	
             	saLocateA=saLocateTEMP;
                Toast.makeText(this, "地點A已儲存", Toast.LENGTH_SHORT).show();
             }   
             else if(lLocateB.size()==0)
             {
             	lLocateB=lLocateTEMP;
             	
             	saLocateB=saLocateTEMP;
             	 Toast.makeText(this, "地點B已儲存", Toast.LENGTH_SHORT).show();
             }
              
    		 
    		break;
    		
    	case R.id.buttonPos:

         	textStatus.setText("定位中");
    		lLocateTEMP.clear();
    		arraylist.clear();
            wifi.startScan();

            Toast.makeText(this, "Pos..." + size, Toast.LENGTH_SHORT).show();
            
            if(size>0)
            {saLocateTEMP=new String[2][size];}
            else {
            	saLocateTEMP=new String[2][1];
            	
			} 
		    try 
		    {
		    	 int i=0;
		        size = size - 1;
        
                while (size >= 0) 
                {   
                	 HashMap<String, String> item = new HashMap<String, String>();                       
                     item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).level);
                     
                     lLocateTEMP.add(results.get(size).SSID+";"+results.get(size).level);
                    
                     if(results.get(size).level>(-80))
                     {	
                     	saLocateTEMP[0][i]=results.get(size).SSID;
                     	saLocateTEMP[1][i]= String.valueOf(results.get(size).level);
                     }
                     
                     arraylist.add(item);
                  
                     i++;
                     size--;   
                     adapter.notifyDataSetChanged();   
                         
                } 
                
                if(!(saLocateA[0]==null)&&!(saLocateTEMP[0]==null))
                {
                	double dfflevel=0;
                	int check=0;
                	
                	for (int k = 0; k < saLocateTEMP.length; k++) {
                		
					for (int j = 0; j < saLocateA.length; j++) {
                		if(saLocateTEMP[0][k].equals(saLocateA[0][j]))
                		{
                			
                			
                			dfflevel=Math.abs(Math.abs(Double.valueOf(saLocateTEMP[1][k]))-
                			Math.abs(Double.valueOf(saLocateA[1][j])));
                			
                			if(dfflevel<=5)
                			{check++;}
                			
                		}
					}
						
					}
                	
                	if(check>=saLocateA[0].length/2)
                	{
                		textStatus.setText("POS A");
                        Toast.makeText(this, "Pos...A!!", Toast.LENGTH_SHORT).show();
                        
                	}
                	/*else {
                		textStatus.setText("你不在a點");
                        Toast.makeText(this, "你不在a點", Toast.LENGTH_SHORT).show();
                        
                		
					}*/
                	
                	
                }

                else if(!(saLocateB[0]==null)&&!(saLocateTEMP[0]==null))
                {
                	double dfflevel=0;
                	int check=0;
                	
                	for (int k = 0; k < saLocateTEMP.length; k++) {
                		
					for (int j = 0; j < saLocateB.length; j++) {
                		if(saLocateTEMP[0][k].equals(saLocateB[0][j]))
                		{
                			
                			
                			dfflevel=Math.abs(Math.abs(Double.valueOf(saLocateTEMP[1][k]))-
                			Math.abs(Double.valueOf(saLocateB[1][j])));
                			
                			if(dfflevel<=5)
                			{check++;}
                			
                		}
					}
						
					}
                	
                	if(check>=saLocateB[0].length/2)
                	{
                		textStatus.setText("POS B");
                        Toast.makeText(this, "Pos...B!!", Toast.LENGTH_SHORT).show();
                        
                	}
                	/*else {
                		textStatus.setText("你不在a點");
                        Toast.makeText(this, "你不在a點", Toast.LENGTH_SHORT).show();
                        
                		
					}*/
                	
                	
                }
                else {
                	textStatus.setText("Unknow pos");
                     
				}
                
                
            }
            catch (Exception e)
            {Log.d("TAG",e.toString()); 
            textStatus.setText(e.toString());}         
   		 
   		break;	
    	
    	}
    	
    }    
}