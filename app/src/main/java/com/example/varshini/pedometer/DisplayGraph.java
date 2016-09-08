/*This code was written by Varshini on 4/25/2016. This basically gets the values of the number of steps in each day of the period.
 * And displays it in the graph.
  *
  * The AndroidPlot external library was used for the visualization of the graph.*/

package com.example.varshini.pedometer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


/**
 * Created by Varshini on 4/25/2016.
 */
public class DisplayGraph extends Activity{

    public Spinner period;
    public TextView checkSpinn;
    public Button homeB,historyB,graphB,settingsB;
    public XYPlot graphValues;
    public XYSeries series1,series2;
    // create a couple arrays of y-values to plot:
    public Number[] series1Numbers = {1, 4, 2, 8, 4, 17, 8, 32, 16, 64};
    public Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};
    //public Number[] xValues = {5,10,15,20,25,30,35,40,45,50};
    public int[] xValues = {5,10,15,20,25,30,35,40,45,50};
    public LineAndPointFormatter seriesFormat;
    public ArrayList<ArrayList<String>> aarays= new ArrayList<ArrayList<String>>();
    public String[] keyValues;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_graph);

        checkSpinn = (TextView) findViewById(R.id.checkSpin);
        homeB = (Button) findViewById(R.id.homeScreen);
        historyB = (Button) findViewById(R.id.historyScreen);
        graphB = (Button) findViewById(R.id.graphScreen);
        settingsB = (Button) findViewById(R.id.settingsScreen);
        graphB.setEnabled(false);
        listenToScreenClicks(this, homeB, historyB, settingsB);

        graphValues = (XYPlot) findViewById(R.id.plot);
        adjustValues();
        Database db = new Database();
        TreeMap graphD = db.graphDays(this);
        aarays = displayGraph(graphD);
        if(graphD!=null)
        {
            int noSize = aarays.size();
            for(int i=0;i<noSize;i++)
            {
                ArrayList<String> temp = aarays.get(i);

            }
        }

        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());;


        //used to initialize the spinner with dummy data for now. will add sql stuff later
        period = (Spinner) findViewById(R.id.spinnerGraph);
        //Database db = new Database();
        int graph =0;
        TreeMap<String, List<String>> date = db.historyValues(this,0);
        String[] dates = getSpinnerValues(date);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dates);
        period.setAdapter(adapter);

        period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //Written by Varshini. This will have the function for the period value of the data accordingly. just position for now.
            @Override
            public void onItemSelected(AdapterView<?> adapt, View arg1,
                                       int pos, long id) {
                // TODO Auto-generated method stub
                String value = (String)adapt.getItemAtPosition(pos);
                checkSpinn.setText(Integer.toString(pos));

                //based on the position of the arrow, it calculates the arraylist position, gets the dates and steps and puts it in the series array
                int position =aarays.get(pos).size();
                int actualposition = position/2;
                int[] series = new int[actualposition];
                for(int seriesi=0;seriesi<actualposition;seriesi++)
                {
                    series[seriesi]=0;
                    ArrayList<String> values = aarays.get(pos);
                    series[seriesi] = Integer.parseInt(values.get((2*seriesi)+1));

                }
                Number[] xVal = new Number[actualposition];
                Number[] yVal = new Number[actualposition];
                for(int i=0;i<actualposition;i++)
                {
                    yVal[i]=series[i];
                    xVal[i]=i+1;
                }


                series2 = new SimpleXYSeries(Arrays.asList(xVal),Arrays.asList(yVal), "Series"+(pos+1));
                graphValues.clear();
                adjustValues();
                graphValues.addSeries(series2, seriesFormat);
                //graphValues.setDomainValueFormat(new GraphXLabelFormat()); //Uncomment this
                graphValues.setUserRangeOrigin(100);
                graphValues.setRangeValueFormat(new DecimalFormat("#"));
                graphValues.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 100);
                graphValues.setDomainStep(XYStepMode.SUBDIVIDE, 1);
                graphValues.setDomainLabel("Date");
                graphValues.setRangeLabel("Calories");
                graphValues.redraw();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                checkSpinn.setText("Nothing selected, will put latest period value here.");
            }
        });

    }

    /*Written by Varshini. This code extracts the values of first and last dates of each period from the
     * database and displays it in the spinner */
    private String[] getSpinnerValues(TreeMap<String,List<String>> date) {
        int size = date.size();
        Set<String> keys = date.keySet();  //get all keys

        int count = size/2;
        int i=0;
        String[] dates = new String[count];

        List<String> listSplit = new ArrayList<String>();

        for(i=0;i<count;i++)
        {
            listSplit=date.get("Date First "+(i+1));
            String first = listSplit.get(0);
            listSplit=date.get("Date Last "+(i+1));
            String last=listSplit.get(0);
            String together = first+" - "+last;
            dates[i] = together;
        }
        return dates;
    }


    //Written by Varshini. Just accumulated all the customization to the graph view
    private void adjustValues() {
        graphValues.setDomainStep(XYStepMode.SUBDIVIDE, 5);
        graphValues.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 10);
        graphValues.setPlotMargins(10, 10, 0, 10);
        graphValues.setPlotPadding(10, 30, 10, 10);
        graphValues.setMarkupEnabled(false);
    }

    //written by Varshini on 4/29. This is used to get the treemap value from the function, and get the different sets of dates and steps for a certain period.
    //the periods are stored in a string array and the datesteps are stored in an array list of an array list
    private ArrayList<ArrayList<String>> displayGraph(TreeMap graphD)//, ArrayList<ArrayList<String>> aarays) {
    { // TreeMap dateSteps = new TreeMap<String,String>();
        int size = graphD.size();

        //check to see sizes directly with respect to hard written values
        keyValues = new String[size];
        //String[] stepValues = new String[size];
        Set<String> keys = graphD.keySet();
        int i=0;
        for(String key: keys)
        {
            keyValues[i]=key;
            //Log.i("Key "+i,keyValues[i]);
            i++;
        }
        for(int count=0;count<i;count++)
        {
            ArrayList<String> dateSteps = new ArrayList<String>();
            //after clearing , initializing didn't happen again
            String keyTemp = keyValues[count];
            dateSteps = (ArrayList) graphD.get(keyTemp);
            Log.i("Key "+count,keyValues[count]);
            Log.i("Database " + count, Integer.toString(dateSteps.size()));
            aarays.add(dateSteps);
        }
        return aarays;
    }

    /*Written by Varshini. Basically accumulates all the onClickListeners for the various buttons */
    private void listenToScreenClicks(DisplayGraph displayGraph, Button homeB, Button historyB, Button settingsB) {

        historyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayGraph.this,History.class);
                startActivity(intent);
            }
        });
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayGraph.this,Pedometer_mainscreen.class);
                startActivity(intent);
            }
        });
        settingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayGraph.this,UserSettings.class);
                startActivity(intent);
            }
        });
    }


}


//Uncomment this
 class GraphXLabelFormat extends Format {

    private static String LABELS[] = {"Label 1", "Label 2", "Label 3"};

    @Override
    public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
        int parsedInt = Math.round(Float.parseFloat(object.toString()));
        String labelString = LABELS[parsedInt];

        buffer.append(labelString);
        return buffer;
    }

    @Override
    public Object parseObject(String string, ParsePosition position) {
        return java.util.Arrays.asList(LABELS).indexOf(string);
    }
}