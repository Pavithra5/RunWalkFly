package com.example.varshini.pedometer;
/* Written by Varshini and Pavithra.*/
/* This is a gateway into all the other screens. It has a menu layout on top that leads to the history, graph and setting
screen respectively.

If the user settings does not exist, it first displays the settings page, and then redirects you to the main page.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;


public class Pedometer_mainscreen extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView count;
    private TextView tipsV;
    /*change later */
    private TextView check;
    private float height, weight;
    private float[] distcal={0.0f,0.0f}; //used for  conversion from steps to distance and calories
    private float[] distcalTemp={0.0f,0.0f};
    private int stepLength;
    public float[] getDistcal(){return distcal;};
    public float steps,prevSteps;
    private boolean gender, system;                     //to calculate the o/p calories/distance/steps
    private boolean caloriesB,distanceB,stepsB=true;   //for enabling and disabling buttons
    boolean activityRunning;
    private Button homeB, graphB, settingsB, historyB;  //to check clicks of button on the screen
    private Button stepsO,caloriesO,distanceO;          //to change the value that you see on the screen
    private Settings currentSettings;

    //written by Pavithra
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private boolean isFirst=false;
    private boolean isReset=false;
    private String[] tips = new String[]
            {
                    "Make sure you always eat a nutritious breakfast.",
                    "Drink plenty of water - do you have your bottle?",
                    "Make sure you get enough sleep – it's an essential part of being healthy.",
                    "Set goals . Aim for four to six walks every week.",
                    "Set boundaries - and set limits on work hours.",
                    "Make a list of the day’s triumphs.",
                    "Create a bedtime ritual.",
                    "Head outside and let your mind get quiet while you engage all your senses.",
                    "Take a 30-minute catnap - it restores wakefulness and attention span",
                    "Figure out what works for you.",
                    "Count backward as a way to relax",
                    "Find a space that’s completely free of stress where you can go to relax.",
                    "Get organized to rid you of stress.",
                    "It doesn't matter if it's a 5 minute mile or a 30 minute mile. A mile is a mile is a mile.",
                    "Most everything is good in moderation."
            };

    /* Written by Varshini and Pavithra. Contains definitions of settings, buttons and so forth*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer_mainscreen);
        count = (TextView) findViewById(R.id.sensorValue);
        //check = (TextView) findViewById(R.id.typeData);
        //isPrev=true;

        /*Written by Pavithra. Checks existence of settings */
        currentSettings= com.example.varshini.pedometer.HandleSettings.LoadSettings();
        if(currentSettings==null)
        {
            Intent intent = new Intent(Pedometer_mainscreen.this,UserSettings.class);
            startActivity(intent);
        }
        isFirst=true;

        //figuring out if user used metric/imperial
        system = currentSettings.getUnit();
        calcDetails(system);    //to calc values based on unit

        tipsV = (TextView) findViewById(R.id.tipsView);
        setTip();
        findButtons();
        homeB.setEnabled(false);
        disableButtons();
        listenToScreenClicks(this, historyB, graphB, settingsB, stepsO, caloriesO, distanceO);

        //Retrieve a PendingIntent that will perform a broadcast - Written by Pavithra



        //Takes values using the database class and displays it// testing for now

        Database db = new Database();

//        steps = prevSteps;

        //check.setText(initialSteps);
//       TreeMap works = db.checkValues(this);

        TreeMap works = db.dbCreate(this);
        // showText(works);
        prevSteps = db.initialSteps(this);
        count.setText(String.valueOf((int)prevSteps)+"\nsteps");

//        db.checkDelete(this);

    }



    /*Written by Varshini. This code just accumulates all the different button definitions to make it easier.*/
    private void findButtons() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        homeB = (Button) findViewById(R.id.homeScreen);
        historyB = (Button) findViewById(R.id.historyScreen);
        graphB = (Button) findViewById(R.id.graphScreen);
        settingsB = (Button) findViewById(R.id.settingsScreen);
        stepsO = (Button) findViewById(R.id.stepsOption);
        caloriesO = (Button) findViewById(R.id.caloriesOption);
        distanceO =(Button) findViewById(R.id.distanceOption);
    }

    /*Written by Varshini. This takes the hashmap values and displays it on the screen by appending text */
    private void showText(TreeMap works) {
        Set<String> keys = works.keySet();  //get all keys
        for(String i: keys)
        {
            check.append((String)works.get(i)+"\n");
        }
    }

    /*Written by Varshini. This retrieves the user details from the settings file, and creates the options for the units
     * based on the options given y the user */
    /*all of the values below are sort of unit-ambiguous. After these values, the rest of the values are calc
    separately based on unit*/
    private void calcDetails(boolean system) {

        gender = currentSettings.getGender();   //used to calculate stepLength
        height = currentSettings.getHeight();   //used to calculate stepLength
        weight = currentSettings.getWeight();
        stepLength = calculateLength(gender, height);    //to be used later in the screen when choosing distance instead of steps

    }

    /*Written by Varshini. This uses the gender of the person and the height to calculate their step length,
     * which in turn can be used to calculate the distance walked.
      *
      * The formula is height in cm * 0.413 for females, and the height in cm * 0.415 for males.*/
    private int calculateLength(boolean gender, float height)
    {
        double calLength;

        if(gender==true)
        {
            calLength = height*0.413;
        }
        else
        {
            calLength = height *0.415;
        }
        calLength = Math.ceil(calLength);
        int stepL = (int)calLength;
        return stepL;
    }



    /*Written by Varshini. This code generates a random number between 1 and 15. This is then used in the array
     * tips to generate a pseudo-random tip on the mainScreen */
    public void setTip()
    {
        Random rInt = new Random();
        int tipno = rInt.nextInt(14)+1;
        tipsV.setText(tips[tipno]);
    }

    /*Written by Varshini. Basically accumulates all the onClickListeners for the various buttons */
    public void listenToScreenClicks(Context context, Button historyB,Button graphB, Button settingsB, Button stepsO,Button caloriesO, Button distanceO)
    {
        historyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pedometer_mainscreen.this,History.class);
                startActivity(intent);
            }
        });
        graphB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pedometer_mainscreen.this,DisplayGraph.class);
                startActivity(intent);
            }
        });
        settingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pedometer_mainscreen.this,UserSettings.class);
                startActivity(intent);
            }
        });
        stepsO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsB = true;
                caloriesB = false;
                distanceB = false;
                disableButtons();
          //      if(isPrev)
                    SetValue();
            }
        });
        distanceO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsB = false;
                caloriesB = false;
                distanceB = true;
                disableButtons();
        //        if(isPrev)
                    SetValue();
            }
        });
        caloriesO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsB = false;
                caloriesB = true;
                distanceB = false;
                disableButtons();
      //          if (isPrev)
                    SetValue();
            }
        });
    }

        private void SetValue() {
            if ((steps != 0)||(isReset) ){
                if (stepsB)
                    count.setText(String.valueOf((int) steps) + "\nsteps");
                else {
                    float[] tempSteps = calcDistAndCals(currentSettings.getUnit(), steps);
                    if (distanceB) {
                        if (currentSettings.getUnit())
                            count.setText(String.valueOf(tempSteps[0]) + "\nkms");
                        else
                            count.setText(String.valueOf(tempSteps[0]) + "\n miles");

                    } else if (caloriesB)
                        count.setText(String.valueOf((int)tempSteps[1]) + "\ncalories");

                }
            } else

            {
                if (stepsB)
                    count.setText(String.valueOf((int) prevSteps) + "\nsteps");
                else {
                    float[] tempSteps = calcDistAndCals(currentSettings.getUnit(), prevSteps);
                    if (distanceB) {
                        if (currentSettings.getUnit())
                            count.setText(String.valueOf(tempSteps[0]) + "\nkms");
                        else
                            count.setText(String.valueOf(tempSteps[0]) + "\n miles");

                    } else if (caloriesB)
                        count.setText(String.valueOf((int)tempSteps[1]) + "\ncalories");

                }
                //isPrev=false;

            }
        }
    /*Written by Varshini. This is an implementation of the toggle button values on the app main screen. Based on the button that has been clicked,
     * that button is disabled and the boolean is set to true */
    private void disableButtons()
    {
        if(stepsB)
        {
            stepsO.setEnabled(false);
            caloriesO.setEnabled(true);
            distanceO.setEnabled(true);
        }
        if(caloriesB)
        {
            stepsO.setEnabled(true);
            caloriesO.setEnabled(false);
            distanceO.setEnabled(true);
        }
        if(distanceB)
        {
            stepsO.setEnabled(true);
            caloriesO.setEnabled(true);
            distanceO.setEnabled(false);
        }
    }

    /*Written by Varshini. It checks that the sensor is actually available, if the app was paused before.*/
    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    //functions inside written by Pavithra
    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;

        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
        if(steps!=0) {
            SQLiteDatabase db = this.openOrCreateDatabase("StepsDB", Context.MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * FROM stepsTable", null);
            c.moveToLast();
            Cursor sumS = db.rawQuery("select sum(steps) as sumS from stepsTable;", null);
            sumS.moveToLast();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();

            //Convert steps into distance and calories
            distcalTemp = calcDistAndCals(system, steps - sumS.getFloat(sumS.getColumnIndex("sumS")));

            String query = "Insert into stepsTable Values(" + (c.getInt(c.getColumnIndex("id")) + 1) + ",'" + dateFormat.format(date) + "','" + c.getString(c.getColumnIndex("period")) + "','" + (steps - sumS.getFloat(sumS.getColumnIndex("sumS"))) + "','" + String.valueOf(distcalTemp[0]) + "','" + String.valueOf(distcalTemp[1]) + "');";
            db.execSQL(query);
        }
    }


    /*Written by Varshini. This method uses the Step_counter that exists in the Android phone.
    It starts counting onSensorChanged and shows the value */
    @Override
    public void onSensorChanged(SensorEvent event) {
        isReset=false;
        steps = event.values[0];
        float currentSteps=CurrentSteps(steps);
        if (activityRunning) {
            distcal = calcDistAndCals(system,currentSteps);
            if(stepsB)
            {
                count.setText(String.valueOf(currentSteps)+ "\nsteps");
            }
            if(distanceB)
            {
                if(currentSettings.getUnit())
                    count.setText(String.valueOf(distcal[0])+"\nkms" );
                else
                    count.setText(String.valueOf(distcal[0])+ "\n miles" );
                // count.setText(String.valueOf(steps));

            }
            if(caloriesB)
            {
                //count.setText(String.valueOf(steps));

                count.setText(String.valueOf((int)distcal[1])+ " calories");
            }

        }

        //Written by Pavithra. To insert into database
        if(isFirst)
        {
            SQLiteDatabase db= this.openOrCreateDatabase("StepsDB", Context.MODE_PRIVATE, null);
            Cursor c=db.rawQuery("SELECT * FROM stepsTable", null);
            c.moveToLast();
            Cursor sumS=db.rawQuery("select sum(steps) as sumS from stepsTable;", null);
            sumS.moveToLast();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();

            //Convert steps into distance and calories
            distcalTemp = calcDistAndCals(system, steps - sumS.getFloat(sumS.getColumnIndex("sumS")));

            String query="Insert into stepsTable Values("+(c.getInt(c.getColumnIndex("id"))+1)+",'"+dateFormat.format(date)+"','" +c.getString(c.getColumnIndex("period"))+"','"+(steps - sumS.getFloat(sumS.getColumnIndex("sumS")))+"','"+String.valueOf(distcalTemp[0])+"','"+String.valueOf(distcalTemp[1])+"');";
            db.execSQL(query);
            isFirst=false;
        }



    }

    /*Written by Varshini. This checks the unit chosen by the user and calculates the distance and calories accordingly */
    private float[] calcDistAndCals(boolean system, float steps) {
        float distance;
        float calories;
        float[] distCal = new float[2];
        if(system==true)
        {
            distance = (steps*height)/100000.0f;
            calories = 0.57f * weight * 0.453f * distance;
            //check.setText(String.valueOf(calories));
        }
        else
        {
            distance = (steps*height)/160000.0f;
            calories = 0.57f * weight * distance;
            //check.setText(String.valueOf(calories));
        }
        distCal[0]=distance;
        distCal[1] = calories;
        return distCal;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void ResetPeriod(View view)
    {
        Toast.makeText(this, "Values reset!", Toast.LENGTH_LONG).show();
        steps=0;
        isReset=true;
        if(stepsB)
        count.setText("  0 steps");

        else
        {
            if(distanceB) {
                if(currentSettings.getUnit())
                    count.setText(String.valueOf("  0 kms" ));
                else
                    count.setText(String.valueOf("  0 miles" ));

            }
            else if(caloriesB)
                count.setText(String.valueOf("  0 calories" ));

        }
        SQLiteDatabase db = this.openOrCreateDatabase("StepsDB", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM stepsTable", null);
        c.moveToLast();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();

        String query = "Insert into stepsTable Values(" + (c.getInt(c.getColumnIndex("id")) + 1) + ",'" + dateFormat.format(date) + "','" + (c.getString(c.getColumnIndex("period")) + 1) + "','0','0','0');";
        db.execSQL(query);

    }

    public float CurrentSteps(float steps)
    {
        SQLiteDatabase db = this.openOrCreateDatabase("StepsDB", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM stepsTable", null);
        c.moveToLast();
        if(c.getFloat(c.getColumnIndex("period"))==1)
            return steps;
        else {
            Cursor sumS=db.rawQuery("select sum(steps) as sumS from stepsTable where period !="+c.getInt(c.getColumnIndex("period"))+";", null);
            sumS.moveToLast();
            return (steps-sumS.getFloat(sumS.getColumnIndex("sumS")));
        }

    }


}
