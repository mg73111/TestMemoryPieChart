package com.moon.piechartone;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TestMemoryPieChartActivity extends Activity {
	ArrayList<String> meminfo;

    long memTotal;
    long memFree;
    long memActive;
    long memInactive;
    
    String strTotal = "Empty";
    String strFree = "Empty";
    String strActive = "Empty";
    String strInactive = "Empty";

	private TextView textAvailableMemory;
	private TextView textUsedMemory;
	private TextView textTotalMemory;
	private TextView textActiveMemory;
	private TextView textInactiveMemory;
    
	//List<PieChartItem> pieChartItemList;
    
    private int maxCount 	= 0;
    private int itemCount 	= 0;
    
    String colorOne = "#FFFF00";
    String colorTwo = "#FFCC00";
    String colorThree = "#FF9900";
    String colorFour = "#FF6600";

	private TextView textAvailableMemoryTitle;
	private TextView textUsedMemoryTitle;
	private TextView textTotalMemoryTitle;
	private TextView textActiveMemoryTitle;
	private TextView textInactiveMemoryTitle;
	private TextView textUsedBattery;
	
	ArrayAdapter<String> adapter;

	private ListView listViewActiveApp;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textAvailableMemory = (TextView) findViewById(R.id.text_available_memory);
        textUsedMemory = (TextView) findViewById(R.id.text_used_memory);
        textTotalMemory = (TextView) findViewById(R.id.text_total_memory);
        textActiveMemory = (TextView) findViewById(R.id.text_active_memory);
        textInactiveMemory = (TextView) findViewById(R.id.text_inactive_memory);
        
        textAvailableMemoryTitle = (TextView) findViewById(R.id.text_available_memory_title);
        textUsedMemoryTitle = (TextView) findViewById(R.id.text_used_memory_title);
        textTotalMemoryTitle = (TextView) findViewById(R.id.text_total_memory_title);
        textActiveMemoryTitle = (TextView) findViewById(R.id.text_active_memory_title);
        textInactiveMemoryTitle = (TextView) findViewById(R.id.text_inactive_memory_title);
        
        textUsedBattery = (TextView) findViewById(R.id.text_use_battery);
        
        listViewActiveApp = (ListView) findViewById(R.id.listview_active_app);
        
        // 메모리 정보 가져오기
        getMemoryInfo();
        
        // 메모리 정보 파이차트 표시
        List<PieChartItem> pieChartItemList = getPieChartItemList();
        
        int size = 200;
        
        makePieChart(pieChartItemList, size);
        
        // 메모리 텍스트 표시
        textAvailableMemory.setText(String.valueOf(memFree/1024) + "MB");
        textAvailableMemoryTitle.setBackgroundColor(Color.parseColor(colorOne));
        
        textUsedMemory.setText(String.valueOf((memTotal - memFree)/1024) + "MB");
        textUsedMemoryTitle.setBackgroundColor(Color.parseColor(colorTwo));
        
        textTotalMemory.setText(String.valueOf(memTotal/1024) + "MB");
        
        textActiveMemory.setText(String.valueOf(memActive/1024) + "MB");
        textActiveMemoryTitle.setBackgroundColor(Color.parseColor(colorThree));
        
        textInactiveMemory.setText(String.valueOf(memInactive/1024) + "MB");
        textInactiveMemoryTitle.setBackgroundColor(Color.parseColor(colorFour));
        
        // 배터리 용량
        this.registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getActiveAppList());
        listViewActiveApp.setAdapter(adapter);
    }
    
    private ArrayList<String> getActiveAppList(){
    	
    	ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
    	List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
    	
    	ArrayList<String> arrayList = new ArrayList<String>();
    	
    	for(RunningAppProcessInfo info : runningAppProcesses){
    		arrayList.add(info.processName);
    	}
    	
    	return arrayList;
    }
    
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			int level = arg1.getIntExtra("level", 0);
			textUsedBattery.setText(String.valueOf(level) + "%");
		}
	};
    
    private void makePieChart(List<PieChartItem> pieChartItemList, int pieChartSize){
    	
        Bitmap mBackgroundImage = Bitmap.createBitmap(pieChartSize, pieChartSize, Bitmap.Config.RGB_565);
        
        PieChartView pieChartView = new PieChartView(this);
        pieChartView.setLayoutParams(new LayoutParams(pieChartSize, pieChartSize));
        pieChartView.setGeometry(pieChartSize, pieChartSize, 10, 10, 10, 10);
        pieChartView.setSkinParams(Color.WHITE);
        pieChartView.setData(pieChartItemList, maxCount);
        pieChartView.invalidate();
        pieChartView.draw(new Canvas(mBackgroundImage));;
        
        ImageView mImageView = new ImageView(this);
	    mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    mImageView.setBackgroundColor(Color.BLACK);
	    mImageView.setImageBitmap( mBackgroundImage );
        
        LinearLayout piechartArea = (LinearLayout) findViewById(R.id.chart_area);
        piechartArea.addView(mImageView);
    }
    
    private List<PieChartItem> getPieChartItemList(){
    	
        List<PieChartItem> pieChartItemList = new ArrayList<PieChartItem>();
        
        PieChartItem item = new PieChartItem();
        item.Count = Integer.parseInt(String.valueOf(memFree));
        item.Label = "FREE";
        item.Color = Color.parseColor(colorOne);
        pieChartItemList.add(item);
        
        maxCount = maxCount + item.Count;
        
        item = new PieChartItem();
        item.Count = Integer.parseInt(String.valueOf(memTotal - memFree));
        item.Label = "USED";
        item.Color = Color.parseColor(colorTwo);
        pieChartItemList.add(item);
        
        maxCount = maxCount + item.Count;
        
        item = new PieChartItem();
        item.Count = Integer.parseInt(String.valueOf(memActive));
        item.Label = "ACTIVE";
        item.Color = Color.parseColor(colorThree);
        pieChartItemList.add(item);
        
        maxCount = maxCount + item.Count;
        
        item = new PieChartItem();
        item.Count = Integer.parseInt(String.valueOf(memInactive));
        item.Label = "INACTIVE";
        item.Color = Color.parseColor(colorFour);
        pieChartItemList.add(item);
        
        maxCount = maxCount + item.Count;
    	
        return pieChartItemList;
    }
    
    private void getMemoryInfo(){
		ProcessBuilder cmd;
		String memList = new String();
		meminfo = new ArrayList<String>();
		
		try{
			String[] args = {"/system/bin/cat", "/proc/meminfo"};			
			
	    	Runtime operator = Runtime.getRuntime();
	    	Process process;
			process = operator.exec(args);
	    	
	    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    	
	    	boolean result = false;
	    	
	    	while(!result){
		    	String readLine = bufferedReader.readLine();
		    	
				String[] temp = readLine.split(":");
				String[] temp2 = temp[1].split("kB");
				String strTemp = temp[0].trim();

				if(strTemp.equals("MemTotal")){
					memTotal = Long.parseLong(temp2[0].trim());
					strTotal = strTemp;
				}
				else if(strTemp.equals("MemFree")){
					memFree = Long.parseLong(temp2[0].trim());
					strFree = strTemp;
				}
				else if(strTemp.equals("Active")){
					memActive = Long.parseLong(temp2[0].trim());
					strActive = strTemp;
				}
				else if(strTemp.equals("Inactive")){
					memInactive = Long.parseLong(temp2[0].trim());
					strInactive = strTemp;
					result = true;
				}	
	    	}
	    	

		} catch(IOException ex){
			ex.printStackTrace();
		}
    }
}