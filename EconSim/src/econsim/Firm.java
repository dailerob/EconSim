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
    private int unitsRequested; //per cycle
    private int requestDeficit; //per cycle (unitsProduced-unitsRequested
    private int producingUnits; //used to check how many units to produce before MC greater than marketPrice. 
    private double employeeSalary; //per cycle
    private double LMUC; //lowestmarketUnitCost
    private double capitalValue = 0;

    //worker production variables
    // output = employeeList.size()*(-1* Math.abs(deltaE*(employeeList.size()-maxEsize))+maxE) = x(-|b(x-a)|+c)
    double deltaE = 0;//b
    double maxEsize = 0;//a
    double maxE = 0;//c
    //employeeList.size() -->> x number of employes

    public Firm(int firmNum) {
        
        liquidity = 100000;
        productValue = Math.abs(90 + rand.nextGaussian()*33);
        productPrice = productValue;
        this.firmNum = firmNum;
        availableUnitsProduced = 0;
        for (int a = 0; a < 200; a++) {
            products.add(new Asset(firmNum, productValue));
            availableUnitsProduced++;
        }
        unitsRequested = 200;
        maxE = 2;
        deltaE = .01;
        maxEsize = 10;
        employeeSalary = 100; 
        UMC = .5;
        
    }

    public void takeTurn() {       
        payEmployees();
        convertCapital();//add to the product what was created the previous time. 
        makeRequests();

    }
    
    public void alterPrice()
    {
        int productsInCapital = (int)(capitalValue/UMC) +(int)((double)calcCapitalValue()/(double)UMC);
        requestDeficit = unitsRequested - productsInCapital;
        
        //Price adjustment
        if (requestDeficit < 0 || unitsRequested == 0) {
            decreasePrice();
        } else {
            if (requestDeficit > 0) {
                increasePrice();
            }
        }//end of price adjustment
        
        unitsRequested = 0;
    }
    
    public void makeRequests() {
        LMUC = lowestMarketUnitCost();
        double availibleLiquid = liquidity;
        int requestSize = 0;
        int unitsToRequest = unitsToProduce();
        fireEmployees();
        while(availibleLiquid > (UMC * LMUC) && requestSize < unitsToRequest)
        {
            requestSize++;
            availibleLiquid -= (UMC * LMUC);
        }

        if(requestSize>0)
            SimRunner.reqList.add(new ReqTransfer(true,firmNum,true,lowestMUCfirm(),requestSize));   
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
                    if(calcEmployeeOutput(employeeList.size()+1)>0&&employeeSalary/calcEmployeeOutput(employeeList.size()+1)+UMC*LMUC<productPrice)
                        employeeList.add((int)(rand.nextDouble()*(SimRunner.people.size()-1)));
                    else{
                        exitFlag = true; 
                    }
                } else {
                    //produce less units
                    exitFlag = true; 
                }
            }
        }
        return producingUnits - (int)(calcCapitalValue()/UMC);
    }
    
    public void fireEmployees()
    {
        if(employeeList.size()>0)
            if(calcLaborOutput(employeeList.size()-1)>(producingUnits+(int)(calcCapitalValue()/UMC))){
                employeeList.remove(0);
            }
        
    }
    
    private double calcEmployeeOutput (int numEmployees)
    {
        return (-1 * Math.abs(deltaE * ((double)numEmployees - maxEsize)) + maxE)/productValue*100;
    }
    
    private double calcLaborOutput( int numEmployees){
        double employeeOutput = 0;
        for(int x = 0; x< numEmployees; x++)
        {
            employeeOutput+= (-1 * Math.abs(deltaE * ((double)x - maxEsize)) + maxE)/productValue*100;
        }
        return employeeOutput; 
    }
    
    private void convertCapital()// turns all the firm's capital into the product it produces
    {
        capitalValue += calcCapitalValue();
        double employeeOutput = calcEmployeeOutput(employeeList.size());
        int producingUnits = 0; 

        capital.clear();

        
        while (UMC * productValue <= capitalValue && producingUnits < employeeOutput) {
            capitalValue -= (UMC * productValue);
            products.add(new Asset(firmNum,productValue,productPrice));
            availableUnitsProduced++;
            producingUnits++;
        }
    }

    private void calculateMC() {
        MC = UMC * LMUC;
        if (calcLaborOutput(employeeList.size()) <= (producingUnits+(int)(calcCapitalValue()/UMC))) {
            MC += employeeSalary;
        }
    }

    private void increasePrice() {
        productPrice = Math.abs(productPrice * 1.025);
    }

    private void decreasePrice() {
        productPrice = Math.abs(productPrice * .975);
    }
    
    
    private double calcCapitalValue()
    {
        double capitalValue = 0.0;
        for (Asset capitalItem : capital) {
            capitalValue += capitalItem.getValue();
        }
        return capitalValue; 
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
    
    public Asset viewAsset()
    {
        return new Asset(firmNum, productValue);
    }
    
    public Asset sellAsset ()
    {
        liquidity+=productPrice;
        availableUnitsProduced--; 
        products.get(products.size()-1).setSellPrice(productPrice);
        return products.remove(products.size()-1);
    }
    
    public void buyCapital (Asset cap)
    {
        liquidity-=cap.getSellPrice();
        capital.add(cap);   
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
    
    public int unitsAvailible()
    {
        return products.size() + (int)(calcCapitalValue()/productPrice + capitalValue/UMC);
    }
    
    public String toString()
    {
        return " product price: " + productPrice + " producingUnits " + producingUnits + " employees " + employeeList.size();
    }
    
    private void payEmployees()
    {
        for(int currentEmployee = 0; currentEmployee < employeeList.size(); currentEmployee++)
        {
            
            if(liquidity>= employeeSalary){
                liquidity-=employeeSalary;
                SimRunner.people.get(employeeList.get(currentEmployee)).changeMonetaryValue(employeeSalary);
            }
            else
            {
                employeeList.remove(currentEmployee);
            }
        }
    }
    
}//class Firm
