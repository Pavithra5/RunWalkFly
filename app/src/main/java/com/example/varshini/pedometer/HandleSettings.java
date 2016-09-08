package com.example.varshini.pedometer;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Pavi on 4/12/2016.
 * Class : HandleSettings
 * Description : The class that handles changes to user settings or fetches the current settings for display
 */

public class HandleSettings {
    private static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/UserSettings.txt";


    //Method: LoadSettings
    //Created by Pavithra Sridaran
    //Description : To load the existing user settings

    public static Settings LoadSettings(){

        Settings currentSetting=new Settings();
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        int lineCount = 0;
        String line1;

        int i = 0;
        try {
            if((line1=br.readLine())==null)
                currentSetting=null;
            else {
                String splitString[] = line1.split("~");
                currentSetting.setName(splitString[0]);
                currentSetting.setDateOfBirth(splitString[1]);
                currentSetting.setHeight(Float.parseFloat(splitString[2]));
                currentSetting.setWeight(Float.parseFloat(splitString[3]));
                currentSetting.setGender(Boolean.parseBoolean((splitString[4])));
                currentSetting.setUnit(Boolean.parseBoolean((splitString[5])));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentSetting;

    }

    //Method : SaveSettings
    //Created by : Pavithra Sridharan
    //Description : To save the user settings to the file

    public static boolean SaveSettings(Settings settingToSave)
    {
        boolean saveResult=false;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fos);
        Writer w = new BufferedWriter(osw);
        String setting = null;
        setting=settingToSave.getName()+"~"+settingToSave.getDateOfBirth()+"~"+String.valueOf(settingToSave.getHeight())+"~"+String.valueOf(settingToSave.getWeight())+"~"+String.valueOf(settingToSave.getGender())+"~"+String.valueOf(settingToSave.getUnit());
        try {
            w.write(setting);
            saveResult=true;
        } catch (IOException e) {
            e.printStackTrace();
        }



    try {
        w.close();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    return saveResult;
    }


}
