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
public class Firm {

    Random rand = new Random();
    private int firmNum;
    ArrayList<Integer> employeeList = new ArrayList<Integer>();
    ArrayList<Asset> capital = new ArrayList<Asset>();
    ArrayList<Asset> products = new ArrayList<Asset>();
    private double liquidity = 0.0;
    private double productPrice = 0.0;
    private double productValue; //average standard value seen in the market
    private double MC = 0.0;
    private double UMC; //Unit Material Cost
    //private double PVratio; //price to value ratio
    private int availableUnitsProduced; //total in storage
    private int unitsProduced; //units produced in the currect cycle 
    private int unitsRequested; //per cycle
    private int requestDeficit; //per cycle (unitsProduced-unitsRequested
    private int producingUnits; //used to check how many units to produce before MC greater than marketPrice. 
    private double employeeSalary; //per cycle
    private double LMUC; //lowestmarketUnitCost

    //worker production variables
    // output = employeeList.size()*(-1* Math.abs(deltaE*(employeeList.size()-maxEsize))+maxE) = x(-|b(x-a)|+c)
    double deltaE = 0;//b
    double maxEsize = 0;//a
    double maxE = 0;//c
    //employeeList.size() -->> x number of employes

    public Firm(int firmNum) {
        productPrice = 100;
        liquidity = 100000;
        productValue = Math.abs(100 + rand.nextGaussian()*33);
        this.firmNum = firmNum;
        availableUnitsProduced = 0;
        for (int a = 0; a < 200; a++) {
            products.add(new Asset(firmNum, productValue));
            availableUnitsProduced++;
        }
        maxE = 2;
        deltaE = .01;
        maxEsize = 10;
        employeeSalary = 100; 
        UMC = .5;
        
    }

    public void takeTurn() {
        requestDeficit = unitsRequested - (int)((double)calcCapitalValue()/(double)UMC);
        //requestDeficit = unitsRequested - products.size();
        unitsRequested = 0;
        unitsProduced = 0;//set the initial units produced for the cycle 
        for(int currentEmployee: employeeList)
        {
            if(liquidity>= employeeSalary){
                liquidity-=employeeSalary;
                SimRunner.people.get(currentEmployee).changeMonetaryValue(employeeSalary);
            }
        }
        convertCapital();//add to the product what was created the previous time. 

        //Price adjustment
        if (requestDeficit < 0) {
            decreasePrice();
        } else {
            if (requestDeficit > 0) {
                increasePrice();
            }
        }//end of price adjustment

        makeRequests();

    }

    public int unitsToProduce() {
        producingUnits = (int)(calcCapitalValue()/UMC);
        boolean exitFlag = false;
        while (producingUnits < 10000 && exitFlag== false) {
            calculateMC();
            if (MC < productPrice) {
                producingUnits++;
            } else {
                if (productPrice > UMC*LMUC) {
                    double bradleyint = ((maxE- Math.abs(deltaE*(employeeList.size()+1- maxEsize)))*100/productValue) + UMC * LMUC;
                    if(bradleyint > 0 && employeeSalary /bradleyint < productPrice ){
                        employeeList.add((int)(rand.nextDouble()*(SimRunner.people.size()-1)));
                    }
                    else
                    {
                        exitFlag = true;
                    }
                    //check if adding worker would increase profit
                } else {
                    //produce less units
                    exitFlag = true; 
                }
            }
        }
        
        if(employeeList.size()>0)
            if(calcEmployeeOutput(employeeList.size()-1)>(producingUnits+(int)(calcCapitalValue()/UMC))){
                employeeList.remove(0);
            }
        return producingUnits - (int)(calcCapitalValue()/UMC);
    }

    private void calculateMC() {
        MC = UMC * LMUC;
        if (calcEmployeeOutput(employeeList.size()) <= (producingUnits+(int)(calcCapitalValue()/UMC))) {
            MC += employeeSalary;
        }
    }
    
    private double calcEmployeeOutput( int numEmployees){
        double employeeOutput = 0;
        for(int x = 0; x< numEmployees; x++)
        {
            employeeOutput+= (-1 * Math.abs(deltaE * (x - maxEsize)) + maxE)*100/productValue;
        }
        return employeeOutput; 
    }

    private void increasePrice() {
        productPrice *= 1.05;
    }

    private void decreasePrice() {
        productPrice *= .95;
    }
    
    
    private double calcCapitalValue()
    {
        double capitalValue = 0.0;
        for (Asset capitalItem : capital) {
            capitalValue += capitalItem.getValue();
        }
        return capitalValue; 
    }

    private void convertCapital()// turns all the firm's capital into the product it produces
    {
        double capitalValue = calcCapitalValue();

        capital.clear();

        while (UMC * productValue <= capitalValue) {
            capitalValue -= (UMC * productValue);
            products.add(new Asset(firmNum,productValue));
            unitsProduced++;
            availableUnitsProduced++;
        }
    }

    public void makeRequests() {
        LMUC = lowestMarketUnitCost();
        double availibleLiquid = liquidity;
        int requestSize = 0;
        int unitsToRequest = unitsToProduce();
        while(availibleLiquid > (UMC * LMUC) && requestSize < unitsToRequest)
        {
            requestSize++;
            availibleLiquid -= (UMC * LMUC);
        }

        SimRunner.reqList.add(new ReqTransfer(true,firmNum,true,lowestMUCfirm(),requestSize));
        
    }

    public double calcPV() {
        return productPrice / productValue;
    }

    public double lowestMarketUnitCost() {
        double lowestValue = 100000;
        for (Firm index : SimRunner.firms) {
            if (index.calcPV() < lowestValue && index.getAvailableUnitsProduced() > 0) {
                lowestValue = index.calcPV();
            }
        }
        return lowestValue;
    }

    public int lowestMUCfirm() {
        double lowestValue = 100000;
        int firmNumber = 0;
        for (Firm index : SimRunner.firms) {
            if (index.calcPV() < lowestValue && index.getAvailableUnitsProduced() > 0) {
                lowestValue = index.calcPV();
                firmNumber = index.getFirmNum();
            }
        }
        return firmNumber;
    }

    public int getFirmNum() {
        return firmNum;
    }

    public int getAvailableUnitsProduced() {
        return availableUnitsProduced;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void incRequested(int inc) {
        unitsRequested+=inc;
    }

    public void changeNumAssets(int change) {

        while (change > 0) {
            if (availableUnitsProduced > 0) {
                products.remove(products.size() - 1);
            }
            availableUnitsProduced--;
            change--;
        }

    }
    
    public Asset viewAsset()
    {
        return new Asset(firmNum, productValue);
    }
    
    public Asset sellAsset ()
    {
        liquidity+=productPrice;
        availableUnitsProduced--; 
        return products.remove(products.size()-1);
    }
    
    public void buyCapital (Asset cap)
    {
        liquidity-=cap.getValue();
        capital.add(cap);   
    }

    public void changeLiquidity(double change) {
        liquidity += change;
    }

    public void addCapital(Asset cap) {
        capital.add(cap);
    }

    public double getLiquidity() {
        return liquidity;
    }

    public int getUnitsRequested() {
        return unitsRequested;
    }
    
    
}//class Firm
