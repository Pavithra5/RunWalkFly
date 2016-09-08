/*Written by Pavithra. This file is used in the settings screen to take in the values, perform validation and
 * save the data in a flat file accordingly. */
package com.example.varshini.pedometer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        Settings currentSetting=HandleSettings.LoadSettings();
        if(currentSetting!=null) {
            EditText text = (EditText) findViewById(R.id.nameText);
            text.setText(currentSetting.getName());
            text = (EditText) findViewById(R.id.DOBText);
            String dateToSet=currentSetting.getDateOfBirth();
            String monthtoSet=dateToSet.substring(0, 2);
            String daytoSet=dateToSet.substring(2, 4);
            String yearToSet=dateToSet.substring(4,8);
            //dateToSet=monthtoSet+"/"+dateToSet+"/"+yearToSet;
            text.setText(dateToSet);
            text = (EditText) findViewById(R.id.heightText);
            text.setText(Float.toString(currentSetting.getHeight()));
            text = (EditText) findViewById(R.id.weightText);
            text.setText(Float.toString(currentSetting.getWeight()));
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGender);
            if (currentSetting.getGender() == true)
                radioGroup.check(R.id.radioFemale);
            else
                radioGroup.check(R.id.radioMale);
            radioGroup = (RadioGroup) findViewById(R.id.radioUnits);
            if (currentSetting.getUnit() == true)
                radioGroup.check(R.id.radioMetric);
            else
                radioGroup.check(R.id.radioImperial);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    //Function called on clicking "Save" button
    public void save(View view){
        int validationResult=isValidData();
        switch(validationResult){
            case 0:
                Settings settingsToSave=new Settings();
                //Name
                EditText editText=(EditText)findViewById(R.id.nameText);
                String name=editText.getText().toString();
                settingsToSave.setName(name);
                //DOB
                editText=(EditText)findViewById(R.id.DOBText);
                settingsToSave.setDateOfBirth(editText.getText().toString());
                //Height
                editText=(EditText)findViewById(R.id.heightText);
                settingsToSave.setHeight(Float.parseFloat(editText.getText().toString()));
                //Weight
                editText=(EditText)findViewById(R.id.weightText);
                settingsToSave.setWeight(Float.parseFloat(editText.getText().toString()));
                //Gender

                RadioGroup radioGroup=(RadioGroup)findViewById((R.id.radioGender));
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);
                int idx = radioGroup.indexOfChild(radioButton);
                if(idx==0)
                    settingsToSave.setGender(false);
                else
                    settingsToSave.setGender(true);

                //Units
                radioGroup=(RadioGroup)findViewById((R.id.radioUnits));
                radioButtonID = radioGroup.getCheckedRadioButtonId();
                radioButton = radioGroup.findViewById(radioButtonID);
                idx = radioGroup.indexOfChild(radioButton);
                if(idx==0)
                    settingsToSave.setUnit(true);
                else
                    settingsToSave.setUnit(false);


               if(HandleSettings.SaveSettings(settingsToSave)) {

                   Toast.makeText(UserSettings.this, "Details saved!", Toast.LENGTH_LONG).show();
                   Intent goHome = new Intent(UserSettings.this, Pedometer_mainscreen.class);
                   UserSettings.this.startActivity(goHome);
                   finish();
               }
                else
                   Toast.makeText(UserSettings.this, "Error in saving settings", Toast.LENGTH_LONG).show();break;
            case 1:
                Toast.makeText(UserSettings.this, "Please enter a name", Toast.LENGTH_LONG).show();break;
            case 2:
                Toast.makeText(UserSettings.this, "Please enter valid Date of birth", Toast.LENGTH_LONG).show();break;
            case 3:
                Toast.makeText(UserSettings.this, "Please enter height", Toast.LENGTH_LONG).show();break;
            case 4:
                Toast.makeText(UserSettings.this, "Please select your gender", Toast.LENGTH_LONG).show();break;
            case 5:
                Toast.makeText(UserSettings.this, "Please select preferred form of unit", Toast.LENGTH_LONG).show();break;
            case 7:
                Toast.makeText(UserSettings.this, "Please enter valid details", Toast.LENGTH_LONG).show();break;
            case 6:
                Toast.makeText(UserSettings.this, "Please enter weight", Toast.LENGTH_LONG).show();break;
            default:
                Toast.makeText(UserSettings.this, "Please enter valid details", Toast.LENGTH_LONG).show();break;
        }




    }

    private int isValidData(){
        int returnValue;
        boolean nameValue,dobValue=false,heightValue,genderValue,unitValue,weightValue;

        //Name validation
        EditText editText=(EditText) findViewById(R.id.nameText);
        String name=editText.getText().toString();

        if(name.isEmpty())
            nameValue=false;
        else
            nameValue=true;

        //DOB validation
        editText=(EditText) findViewById(R.id.DOBText);
        String dateAdded=editText.getText().toString();
        SimpleDateFormat df = new SimpleDateFormat("MMddyyyy");
        Date today= Calendar.getInstance().getTime();
        if(dateAdded==null)
            dobValue=false;
        else {
            if(dateAdded.contains("/"))
                dateAdded=dateAdded.replaceAll("/","");
            else if(dateAdded.contains("-"))
                dateAdded=dateAdded.replaceAll("-","");
            try {
                Date date=df.parse(dateAdded);

                if (date.compareTo(today) > 0) {
                    dobValue = false;
                } else
                    dobValue = true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Height validation
        editText=(EditText) findViewById(R.id.heightText);
        String height=editText.getText().toString();
        if(height.isEmpty())
            heightValue=false;
        else
            heightValue=true;

        //Weight validation
        editText=(EditText) findViewById(R.id.weightText);
        String weight=editText.getText().toString();
        if(weight.isEmpty())
            weightValue=false;
        else
            weightValue=true;

        //Gender validation
        RadioGroup radioGroup=(RadioGroup)findViewById((R.id.radioGender));
        int radioSelection=radioGroup.getCheckedRadioButtonId();
        if(radioSelection==-1)
            genderValue=false;
        else
            genderValue=true;

        //Unit validation
        radioGroup=(RadioGroup)findViewById((R.id.radioUnits));
        radioSelection=radioGroup.getCheckedRadioButtonId();
        if(radioSelection==-1)
            unitValue=false;
        else
            unitValue=true;

        if((!nameValue)&&(dobValue)&&(heightValue)&&(genderValue)&&(unitValue)&&(weightValue))
            returnValue=1;
        else if((nameValue)&&(!dobValue)&&(heightValue)&&(genderValue)&&(unitValue)&&(weightValue))
            returnValue=2;
        else if((nameValue)&&(dobValue)&&(!heightValue)&&(genderValue)&&(unitValue)&&(weightValue))
            returnValue=3;
        else if((nameValue)&&(dobValue)&&(heightValue)&&(!genderValue)&&(unitValue)&&(weightValue))
            returnValue=4;
        else if((nameValue)&&(dobValue)&&(heightValue)&&(genderValue)&&(!unitValue)&&(weightValue))
            returnValue=5;
        else if((nameValue)&&(dobValue)&&(heightValue)&&(genderValue)&&(unitValue)&&(weightValue))
            returnValue=0;
        else if((nameValue)&&(dobValue)&&(heightValue)&&(genderValue)&&(unitValue)&&(!weightValue))
            returnValue=6;
        else
        returnValue=7;

        return returnValue;
    }


}
