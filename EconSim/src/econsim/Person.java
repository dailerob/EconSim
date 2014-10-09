/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package econsim;

import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author Roberto
 */
public class Person {
    
    ArrayList<Asset> PersonalItems =  new ArrayList<Asset>();
    private double monetaryValue;// total cash the person has on hand
    private double expectedIncome;// income the person expectedly earns
    private boolean hasIncome; //weather the person is recieveing income
    public double StandardSR; //the ratio the person has to reach the standard doubling value
    public double doubleValue; // the amount of iterations for average units to double in value
    public double standardDB; // the standard amount of time it takes a average units to double value
    public double savingsRatio; // the actual ratio being saved by people 
    public int timeSincePurchase; //iterations since the persons last puchase
    Random rand = new Random(1111);
    
    
    
    public Person()
    {
        double monetaryValue = 10000;
    }
    
    
    
    
    
    public double CalcUnitValue(Asset currentAsset)
    {
        double timeValue = CalculateTimeValue();
        double assetValue = currentAsset.getValue(); 
        double personalAssetValue = rand.nextGaussian()*(assetValue/3);
        
        
        return timeValue*assetValue*personalAssetValue; 
    }
    
    private  double CalculateTimeValue()
    {
        return Math.pow(2,(timeSincePurchase*(1/doubleValue)));
    }
    
    private double CalculateSR()
    {
        return monetaryValue/expectedIncome; 
    }
    
    private double doubleValue()
    {
        return (standardDB*StandardSR/savingsRatio);
    }
    
    public void takeTurn()
    {
        
    }

    public double getMonetaryValue() {
        return monetaryValue;
    }
    
    public void changeMonetaryValue(double change)
    {
        monetaryValue+=change; 
    }
    
}//class person
