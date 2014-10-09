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

    Random rand = new Random(3333);
    int firmNum;
    ArrayList<Double> employeeList = new ArrayList<Double>();
    ArrayList<Asset> capital = new ArrayList<Asset>();
    ArrayList<Asset> products = new ArrayList<Asset>();
    double liquidity = 0.0;
    double productPrice = 0.0;
    double productValue; //average standard value seen in the market
    double MC = 0.0;
    double UMC; //Unit Material Cost
    double PVratio; //price to value ratio
    int availableUnitsProduced; //total in storage
    int unitsProduced; //units produced in the currect cycle 
    int unitsRequested; //per cycle
    int requestDeficit; //per cycle (unitsProduced-unitsRequested
    int producingUnits; //used to check how many units to produce before MC greater than marketPrice. 
    double employeeSalary; //per cycle

    //worker production variables
    // output = employeeList.size()*(-1* Math.abs(deltaE*(employeeList.size()-maxEsize))+maxE) = x(-|b(x-a)|+c)
    double deltaE = 0;//b
    double maxEsize = 0;//a
    double maxE = 0;//c
    //employeeList.size() -->> x number of employes

    public Firm(int firmNum) {
        productPrice = 100;
        liquidity = 10000;
        productValue = 100 + rand.nextGaussian()*33;
        this.firmNum = firmNum;
        availableUnitsProduced = 0;
        for (int a = 0; a < 200; a++) {
            products.add(new Asset(firmNum, productValue));
            availableUnitsProduced++;
        }
        maxE = 5;
        deltaE = .8;
        maxEsize = .9;
        employeeSalary = 100; 
        
    }

    public void takeTurn() {
        requestDeficit = unitsRequested - unitsProduced;
        unitsRequested = 0;
        unitsProduced = 0;//set the initial units produced for the cycle 
        convertCapital();//add to the product what was created the previous time. 

        //Price adjustment
        if (requestDeficit > 0) {
            decreasePrice();
        } else {
            if (requestDeficit < 0) {
                increasePrice();
            }
        }//end of price adjustment

        makeRequests();

    }

    public int unitsToProduce() {
        producingUnits = (int)(calcCapitalValue()/UMC);
        boolean exitFlag = false;
        while (producingUnits < 10000 && exitFlag== false) {
            if (MC < productPrice) {
                producingUnits++;
            } else {
                if (productPrice > UMC*lowestMarketUnitCost()) {
                    if(employeeSalary /((maxE- Math.abs(deltaE*(employeeList.size()+1- maxEsize)))*100/productValue) + UMC * lowestMarketUnitCost() < productPrice){
                        employeeList.add(rand.nextDouble()*(SimRunner.people.size()-1));
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
        
        return producingUnits;
    }

    private void calculateMC() {
        MC = UMC * lowestMarketUnitCost();
        if (calcEmployeeOutput() <= producingUnits) {
            MC += employeeSalary;
        }
    }
    
    private double calcEmployeeOutput(){
        double employeeOutput = 0;
        for(int x = 0; x< employeeList.size(); x++)
        {
            employeeOutput+= (-1 * Math.abs(deltaE * (x - maxEsize)) + maxE)*100/productValue;
        }
        return employeeOutput; 
    }

    private void increasePrice() {
        productPrice *= 1.25;
    }

    private void decreasePrice() {
        productPrice *= .975;
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
        double availibleLiquid = liquidity;
        for (int x = 0; x < unitsToProduce(); x++) {
            availibleLiquid -= (UMC * lowestMarketUnitCost());
        }

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

    public int lowesMUCfirm() {
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
        return products.get(1);
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
}//class Firm
