package com.example.varshini.pedometer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Varshini on 4/14/2016.
 */
/* This method was written by Varshini Ramaraj. For now, it contains dummy values. In the actual app, it will retrieve
* values from the SQLite database and display them.*/
public class History extends Activity {

    public ExpandableListView expand;
    ExpandableListAdapter listAdapter;
    List<String> header;
    TreeMap<String,List<String>> child;
    private Button homeB, graphB, settingsB, historyB;
    private TextView test;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        expand = (ExpandableListView) findViewById(R.id.expandView);


        Database db = new Database();
        TreeMap<String,List<String>> period = db.historyValues(this,1);
        test = (TextView) findViewById(R.id.historyData);
//        Set<String> keys = period.keySet();  //get all keys
//        for(String i: keys)
//        {
//            List<String> val = new ArrayList<String>();
//            val = period.get(i);
//            int value = val.size();
//            for(int ji=0;ji<value;ji++)
//            {
//                test.append(i + ": " + val.get(ji) + "\n");
//            }
//            test.append("One done");
//        }

        putData(period);
        listAdapter = new com.example.varshini.pedometer.ExpandableListAdapter(this,header,child);
        expand.setAdapter(listAdapter);

        homeB = (Button) findViewById(R.id.homeScreen);
        historyB = (Button) findViewById(R.id.historyScreen);
        graphB = (Button) findViewById(R.id.graphScreen);
        settingsB = (Button) findViewById(R.id.settingsScreen);
        historyB.setEnabled(false);
        listenToScreenClicks(homeB, graphB, settingsB);

    }

    /*Written by Varshini. Basically accumulates all the onClickListeners for the various buttons */
    public void listenToScreenClicks(Button homeB,Button graphB, Button settingsB)
    {
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this,Pedometer_mainscreen.class);
                startActivity(intent);
            }
        });
        graphB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this,DisplayGraph.class);
                startActivity(intent);
            }
        });
        settingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this,UserSettings.class);
                startActivity(intent);
            }
        });
    }

    /* Written by Varshini. This has dummy values for now, and is the expandable list view for History. */
    public void putData(TreeMap<String, List<String>> period)
    {
        header = new ArrayList<String>();
        child = new TreeMap<String, List<String>>();

        int size = period.size();
        Set<String> keys = period.keySet();  //get all keys
        List<String> stdical = new ArrayList<String>();

        int count = size/3;
        int i=0;
        String[] headern = new String[count*2];
        List<String> listSplit = new ArrayList<String>();
//        listSplit=period.get("Date 1");
//        int j=0;
//        j=listSplit.size();
//        Log.e("My value: ",Integer.toString(j));
        for(i=0;i<count;i++)
        {
            listSplit=period.get("Date First "+(i+1));
            headern[i] = listSplit.get(0);
            listSplit=period.get("Date Last "+(i+1));
            headern[i+3]=listSplit.get(0);
                //Log.i("Value of element " + j, listSplit.get(j));
        }
       // i=0;
        // Adding child data
        for(i=0;i<count;i++)
        {

            header.add(headern[i]+" - "+headern[i+3]);
            //test.append(headern[i]);
            stdical = period.get(Integer.toString(i+1));
            child.put(header.get(i),stdical);
        }

//        //header.add("Coming Soon..");
//
//        // Adding child data
//        List<String> first = new ArrayList<String>();
//        first.add("Steps taken: 75");
//        first.add("Calories burnt: 10");
//
//        List<String> second = new ArrayList<String>();
//        second.add("Steps taken: 1175");
//        second.add("Calories burnt: 400");
//
//        child.put(header.get(0), first); // Header, Child data
//        child.put(header.get(1), second);

    }
}
