/*Written by Varshini on 26/4/2016. This is used to create a database if it doesn't already exist.
 * This code reads in the values and displays them on the text for now. It will later be used for the existing values */

/*This also contains most of the functions from the main screen,history screen and graph screen for info from the database */
package com.example.varshini.pedometer;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Varshini on 4/26/2016.
 */
/*This creates a database if it doesn't already exist. Later, will write functions with variable values, so calling
this function would insert a record with those values.

Certain functions would also give back values based on what was asked for. */

public final class Database {

    public void checkDelete(Context context)
    {
        SQLiteDatabase db = null;
        TreeMap values = new TreeMap();
        SQLiteDatabase.CursorFactory cursorEdit;
        db = context.openOrCreateDatabase("StepsDB",Context.MODE_PRIVATE,null);
        db.delete("stepsTable",null,null);
    }
    public TreeMap dbCreate(Context context)
    {
        SQLiteDatabase db = null;
        TreeMap values = new TreeMap();
        SQLiteDatabase.CursorFactory cursorEdit;
        db = context.openOrCreateDatabase("StepsDB",Context.MODE_PRIVATE,null);
        //write if exists only then
        db.execSQL("Drop table if exists stepsTable;");
        db.execSQL("CREATE TABLE IF NOT EXISTS stepsTable(id integer primary key autoincrement,date varchar,period varchar, steps varchar, distance varchar, calories varchar);");
        db.execSQL("Insert into stepsTable Values(1,'2015/04/20','1','350','1000','450');");
        db.execSQL("Insert into stepsTable Values(2,'2015/04/21','1','270','1000','450');");
        db.execSQL("Insert into stepsTable Values(3,'2015/04/22','1','430','1000','450');");
        db.execSQL("Insert into stepsTable Values(4,'2015/04/23','2','540','1000','450');");
        db.execSQL("Insert into stepsTable Values(5,'2015/04/24','2','665','1000','450');");
        db.execSQL("Insert into stepsTable Values(6,'2015/04/25','2','467','1000','450');");
        db.execSQL("Insert into stepsTable Values(7,'2015/04/26','3','655','1000','450');");
        db.execSQL("Insert into stepsTable Values(8,'2015/04/27','3','700','1000','450');");
        db.execSQL("Insert into stepsTable Values(9,'2015/04/28','4','558','1000','450');");
        db.execSQL("Insert into stepsTable Values(10,'2015/04/29','4','424','900','500');");
        db.execSQL("Insert into stepsTable Values(11,'2015/04/30','4','650','1100','600');");
        //this part of code was used to find the number of rows in the table.
//           Cursor checkCount = db.rawQuery("Select count(*) from stepsTable;", null);
//           checkCount.moveToFirst();
//             int count = checkCount.getInt(0);
//            String check = Integer.toString(count);

        //need to remove this later wrt hashmap
        int j=1;
        Cursor allV=db.rawQuery("SELECT date,period FROM stepsTable", null);
        if(allV!=null)
        {
            if (allV.moveToFirst()) {
                while(!allV.isAfterLast()) {

                    values.put(j + "Date: ",allV.getString(allV.getColumnIndex("date")));
                    values.put(j+ "Period: ",allV.getString(allV.getColumnIndex("period")));
//                       values.put("Steps "+j,allV.getString(allV.getColumnIndex("steps")));
//                       values.put("Distance "+j,allV.getString(allV.getColumnIndex("distance")));
//                       values.put("Calories "+j,allV.getString(allV.getColumnIndex("calories")));
                    allV.moveToNext();
                    j++;
                }
            }
        }
        db.close();
        return values;
    }


    /*Written  by Varshini. this finds the last value of the period column so that it can find the existing number of steps in that period.
    * This is done to bridge the gap between when the app starts and when the person starts walking.*/

    public int initialSteps(Context context)
    {
        SQLiteDatabase db = null;
        // HashMap values = new HashMap();
        SQLiteDatabase.CursorFactory cursorEdit;
        int  countCol;
        int count=0;
        String values="a";
        db = context.openOrCreateDatabase("StepsDB",Context.MODE_PRIVATE,null);
        //comment the table below later and see if it crashes.
        db.execSQL("CREATE TABLE IF NOT EXISTS stepsTable(id integer primary key autoincrement,date varchar,period varchar, steps varchar, distance varchar, calories varchar);");

        Cursor lastR = db.rawQuery("select * from stepsTable", null);
        //this query is used to get the period number of the last value to see what is ongoing
        if(lastR!=null)
        {
            lastR.moveToPosition(lastR.getCount() - 1);
            countCol = lastR.getInt(lastR.getColumnIndex("period"));
//          String col = Integer.toString(countCol);

            //the period number is then used to count the number of initial steps
            Cursor sumS = db.rawQuery("select sum(steps) as sumS,period from stepsTable group by period having period='" + countCol + "';", null);
            if(sumS!=null)
            {
                sumS.moveToFirst(); //remember always to moveToFirst then do other things
                count = sumS.getInt(sumS.getColumnIndex("sumS"));
                //values = Integer.toString(count);
            }
        }
        db.close();
        return count;
    }

    /*written on 27/4  by Varshini. This code gets in the date and period values and puts them into arrays so that the
    * history screen can access them and display it in the expandable list accordingly.*/
    public TreeMap<String,List<String>> historyValues(Context context,int func)
    {
        SQLiteDatabase db = null;
        // HashMap values = new HashMap();
        //SQLiteDatabase.CursorFactory cursorEdit;
        int countCol;
        db = context.openOrCreateDatabase("StepsDB",Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS stepsTable(date varchar,period varchar, steps varchar, distance varchar, calories varchar);");
        ArrayList<Integer> periodValues =new ArrayList<Integer>();
        TreeMap<String,List<String>> historyV= new TreeMap<String, List<String>>();
        Cursor period = db.rawQuery("select distinct period from stepsTable", null);
        period.moveToFirst();
        if(period!=null)
        {
            if (period.moveToFirst()) {
                while(!period.isAfterLast())
                {
                    String value = period.getString(period.getColumnIndex("period"));
                    Integer per = Integer.parseInt(value);
                    periodValues.add(per);
                    period.moveToNext();
                }
            }
        }
        //return periodValues;
        if(!periodValues.isEmpty())
        {
            int size = periodValues.size();
            int dateCount=0;
            for(int count = 0;count<size;count++)
            {
                int periodN = periodValues.get(count);

                //get the first date of the period
                Cursor data = db.rawQuery("select date from stepsTable where id = (select min(id) from stepsTable group by period having period='" + periodN + "');", null);
                if(data!=null)
                {
                    if(data.moveToFirst())
                    {
                        while ((!data.isAfterLast()))
                        {
                            String date = data.getString(data.getColumnIndex("date"));
                            List<String> name=new ArrayList<String>();
                            name.add(date);
                            historyV.put("Date First "+Integer.toString(periodN),name);
                            data.moveToNext();
                        }
                    }
                }

                //get the last date of the period
                Cursor data2 = db.rawQuery("select date from stepsTable where id = (select max(id) from stepsTable group by period having period='"+periodN+"');",null);
                if(data2!=null)
                {
                    if(data2.moveToFirst())
                    {
                        while ((!data2.isAfterLast()))
                        {
                            String date = data2.getString(data2.getColumnIndex("date"));
                            List<String> name=new ArrayList<String>();
                            name.add(date);
                            historyV.put("Date Last "+Integer.toString(periodN),name);
                            data2.moveToNext();
                        }
                    }
                }

                if(func==1)
                {
                    //getting the total sum of calories,distance and so on.
                    Cursor data1 = db.rawQuery("select sum(steps) as sumS,sum(distance) as sumD,sum(calories) as sumC from stepsTable group by period having period = '"+periodN+"';",null);
                    if(data1!=null)
                    {
                        if (data1.moveToFirst()) {
                            while(!data1.isAfterLast())
                            {
                                int stepsI = data1.getInt(data1.getColumnIndex("sumS"));
                                int distanceI = data1.getInt(data1.getColumnIndex("sumD"));
                                int caloriesI = data1.getInt(data1.getColumnIndex("sumC"));
                                String stepsS = Integer.toString(stepsI);
                                String distanceS = Integer.toString(distanceI);
                                String caloriesS = Integer.toString(caloriesI);
                                List<String> name = new ArrayList<String>();
                                name.add("Steps: "+stepsS);
                                name.add("Distance: "+distanceS);
                                name.add("Calories: "+caloriesS);
                                historyV.put(Integer.toString(periodN),name);
                                data1.moveToNext();
                            }
                        }
                    }
                }

            }
        }
        db.close();
        return historyV;
    }


    //Written by Varshini on 4/29. This is used to get all the day steps values to display it in the graph screen
    public TreeMap graphDays(Context context)
    {
        int countP=0;
        String periodd="";
        SQLiteDatabase db = null;
        //  TreeMap values = new TreeMap();
        SQLiteDatabase.CursorFactory cursorEdit;
        db = context.openOrCreateDatabase("StepsDB",Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS stepsTable(date varchar,period varchar, steps varchar, distance varchar, calories varchar);");
        ArrayList<Integer> periodValues =new ArrayList<Integer>();
        TreeMap<String,ArrayList<String>> graph= new TreeMap<String, ArrayList<String>>();   //bigger treemap
        Cursor period = db.rawQuery("select distinct period from stepsTable", null);
        period.moveToFirst();
        if(period!=null)
        {
            if (period.moveToFirst()) {
                while(!period.isAfterLast())
                {
                    String value = period.getString(period.getColumnIndex("period"));
                    Integer per = Integer.parseInt(value);
                    periodValues.add(per);
                    period.moveToNext();
                }
            }
        }
        if(!periodValues.isEmpty()) {
            int size = periodValues.size();
            int dateCount = 0;
            for (int count = 0; count < size; count++)
            {
                ArrayList<String> dates = new ArrayList<>();

                //dates.clear();

                int periodN = periodValues.get(count);
                Log.i("Integer ",Integer.toString(periodN));
               /* if(count>0)
                {
                    ArrayList<String> temp = graph.get("1");
                    //Log.i("Database after " + periodN-1, Integer.toString(temp.size()));
                    Log.i("Database "+(periodN-1),Integer.toString(temp.size()));
                }*/
                Cursor data = db.rawQuery("select count(*) as CountP from stepsTable group by period having period='" + periodN + "';", null);
                if(data!=null)
                {
                    if(data.moveToFirst())
                    {
                        while ((!data.isAfterLast()))
                        {
                            countP = data.getInt(data.getColumnIndex("CountP"));
                            data.moveToNext();
                        }
                    }
                }
                // for(int co=0;co<countP;co++)
                // {
                data = db.rawQuery("select date,steps from stepsTable where period='" + periodN + "';", null);
                int j=0;
                //dates.clear();
                if(data!=null)
                {
                    if(data.moveToFirst())
                    {
                        while ((!data.isAfterLast()))
                        {
                            String date = data.getString(data.getColumnIndex("date"));
                            String steps = data.getString(data.getColumnIndex("steps"));
                            dates.add(date);
                            dates.add(steps);
                            Log.d("Date "+j,date);
                            Log.d("Steps " + j, steps);
                            //dates.put(date,steps);
                            j++;
                            data.moveToNext();
                        }
                    }
                }
                //Log.i("Database before " + periodN, Integer.toString(dates.size()));
                //Log.i("Database before " + periodN, periodN);
                periodd = Integer.toString(periodN);
                graph.put(periodd, dates);
                ArrayList<String> temp = graph.get(periodd);
                Log.i("Database after " + periodN, Integer.toString(temp.size()));
            }
        }
        db.close();
        return graph; //more code to be written//breakpoint there
    }

    //Written by Varshini. This code was used to verify the number of steps in the period vs the sum
    public TreeMap checkValues(Context context)
    {
        SQLiteDatabase db = null;
        TreeMap values = new TreeMap();
        SQLiteDatabase.CursorFactory cursorEdit;
        db = context.openOrCreateDatabase("StepsDB",Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS stepsTable(date varchar,period varchar, steps varchar, distance varchar, calories varchar);");
        Cursor c = db.rawQuery("select steps from stepsTable;",null);
        //Cursor c = db.rawQuery("Select count(*) as count from stepsTable;",null);
        int j=1;
        if (c.moveToFirst()) {
            while(!c.isAfterLast()) {

                values.put("Date "+j,c.getString(c.getColumnIndex("steps")));
                c.moveToNext();
                j++;
            }
        }
        db.close();
        return values;
    }

}
