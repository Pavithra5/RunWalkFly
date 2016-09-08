package com.example.varshini.pedometer;
/**
 * Created by Pavi on 4/12/2016.
 * Class : Settings
 * Description : The class that has user settings and preferences
 */

public class Settings {
    private String name;
    private String dateOfBirth;
    private float height;
    private boolean gender;
    private boolean unit;
    private float weight;

    //Getters and Setters
    public String getName(){return name;}

    public void setName(String name){this.name=name;}

    public String getDateOfBirth(){return dateOfBirth;}

    public void setDateOfBirth(String dateOfBirth){this.dateOfBirth=dateOfBirth;}

    public float getHeight(){return height;}

    public void setHeight(float height){this.height=height;}

    public boolean getGender(){return gender;}

    public void setGender(boolean gender){this.gender=gender;}

    public boolean getUnit(){return unit;}

    public void setUnit(boolean unit){this.unit=unit;}

    public float getWeight(){return weight;}

    public void setWeight(float weight){this.weight=weight;}

}
