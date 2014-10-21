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
    
    int personNum = 0;
    ArrayList<Firm> firmsViewed = new ArrayList<Firm>();
    ArrayList<Asset> PersonalItems =  new ArrayList<Asset>();
    private double monetaryValue;// total cash the person has on hand
    private double expectedIncome;// income the person expectedly earns
    private boolean hasIncome; //weather the person is recieveing income
    public double standardSR; //the ratio the person has to reach the standard doubling value
    public double doubleValue; // the amount of iterations for average units to double in value
    public double standardDB; // the standard amount of time it takes a average units to double value
    public double savingsRatio; // the actual ratio being saved by people 
    public int timeSincePurchase; //iterations since the persons last puchase
    Random rand = new Random();
    
    
    
    public Person(int personNum)
    {
        this.personNum = personNum;
        monetaryValue = 10000;
        expectedIncome  = 100;
        standardSR = 10; 
        standardDB = 100;
        timeSincePurchase = 0;
    }
    
    
    
    
    
    public double calcUnitValue(Asset currentAsset)
    {
        double timeValue = CalculateTimeValue();
        double assetValue = currentAsset.getValue(); 
        double personalAssetValue = rand.nextGaussian()*(assetValue/3);
        
        
        return timeValue*Math.abs(assetValue+personalAssetValue); 
    }
    
    private  double CalculateTimeValue()
    {
        return Math.pow(2,(timeSincePurchase*(1/doubleValue())));
    }
    
    private double CalculateSR()
    {
        return monetaryValue/expectedIncome; 
    }
    
    private double doubleValue()
    {
        return (standardDB*standardSR/savingsRatio);
    }
    
    public void takeTurn()
    {
        savingsRatio = CalculateSR();
        double lowestPV = 1; 
        int firmNum = 0; 
        firmsViewed.clear();
        for(int currentFirm = 0; currentFirm < 10; currentFirm++)
        {
            firmsViewed.add(SimRunner.firms.get((int)(rand.nextDouble() * (SimRunner.firms.size()-1))));
        }
        
        for (Firm currentFirm : firmsViewed) 
        {
            double valued = calcUnitValue(currentFirm.viewAsset());
            
            if (currentFirm.getProductPrice() / valued < lowestPV) 
            {
                if (currentFirm.getProductPrice() < monetaryValue) 
                {
                    lowestPV = currentFirm.getProductPrice() / valued;
                    firmNum = currentFirm.getFirmNum();
                }

            }
        }//looks through the current set of firms
        
        if(lowestPV < 1)
        {
            timeSincePurchase = 0;
            SimRunner.reqList.add(new ReqTransfer(false,personNum, true, firmNum, 1));
        }
        else{
            timeSincePurchase++;
        }
    }

    public double getMonetaryValue() {
        return monetaryValue;
    }
    
    public void changeMonetaryValue(double change)
    {
        monetaryValue+=change; 
    }
    
}//class person
