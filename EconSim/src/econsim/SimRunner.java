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
public class SimRunner {

    /**
     * @param args the command line arguments
     */
    static Random rand = new Random(2222);
    static ArrayList<ReqTransfer> reqList = new ArrayList<ReqTransfer>();
    static ArrayList<Firm> firms = new ArrayList<Firm>();
    static ArrayList<Person> people = new ArrayList<Person>();
    private static ArrayList<Integer> assetList = new ArrayList<Integer>();
    private static ArrayList<Integer> marketMap = new ArrayList<Integer>();
    
    
    public static void main(String[] args) {
        
        int totalCycles = 10000;
        
        for(int x = 0; x < 100; x++)
        {
           firms.add(new Firm(x));
        }
        
        for(int x = 0; x< 10000; x++)
        {
            people.add(new Person(x));
        }
        
        
        for (int cycleIndex  = 0;cycleIndex < totalCycles; cycleIndex++)
        {
            //complete all takeTurns
            fillMarketMap();//decides the random order for taking turns
            takeTurns();//allows firms & people to convert their capital, change pricing, pay employees, and make initial requests
            makeTransferRequests();//carries out the transfer requests and creates
            printData();
            
            reqList = new ArrayList<ReqTransfer>();
            marketMap = new ArrayList<Integer>();
        }//end of the cycle 
    }//main
    
    
    public static void fillMarketMap() {
        int currentIndex = 0;
        for (Person peopleIndex : people) {
            marketMap.add(currentIndex);
            currentIndex++;
        }
        for (Firm firmIndex : firms) {     
            marketMap.add(currentIndex);
            currentIndex++;
        }
    }
    
    public static void makeTransferRequests()
    {
        int originalRequestSize = reqList.size();
        int requestIndex = 0;
        
        while (requestIndex < reqList.size() && requestIndex < 10 * originalRequestSize) {

            ReqTransfer currentRequest = reqList.get(requestIndex);
            currentRequest.carryOutRequest();
            requestIndex++;

        }//end of the requests
    }
    
    public static void printData()
    {
        double avgprice = 0.0;
            double totalLiquidity = 0.0;
            double personalMonetary = 0.0;
            double totalCapital = 0;
            double totalSavings = 0;
            for(Firm cfirm: firms){
                totalLiquidity += cfirm.getLiquidity();
                avgprice+=cfirm.getProductPrice();
                totalCapital += (double)cfirm.unitsAvailible();
                
            }
            for(Person cperson: people)
            {
                personalMonetary+= cperson.getMonetaryValue();
            }
                
            totalSavings = totalLiquidity + personalMonetary;
            
            avgprice/= firms.size();
            //System.out.println("");
            //System.out.println("request size: " + reqList.size()+" price: " + firms.get(10).getProductPrice() + " unitsAvailible: " + firms.get(10).getAvailableUnitsProduced() + " liquidity: " + firms.get(10).getLiquidity() + " unitsRequested: " + firms.get(10).getUnitsRequested());
            System.out.println("Request Size:    " + reqList.size() + "    total savings:   " + (int)totalSavings + "    totalLiquidity:    " + (int)totalLiquidity +"    totalPersonalsavings:    " + (int) personalMonetary + "    total capital:    " + (int)totalCapital + "    average price :    " + avgprice);
            //System.out.println("");
    }
    
    public static void takeTurns()
    {
        while(marketMap.size()>0)
            {
                int turnIndex = (int)(rand.nextDouble()*(marketMap.size()-1));
                if(marketMap.get(turnIndex) < people.size())
                {
                    people.get(marketMap.get(turnIndex)).takeTurn();
                }
                else
                {
                    firms.get(marketMap.get(turnIndex)-(people.size())).takeTurn();
                }
                marketMap.remove(turnIndex);
                //System.out.println("maketMap size: " + marketMap.size());
            }//end of taking turns
    }
}//simRunner
