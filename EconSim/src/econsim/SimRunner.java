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
        
        int totalCycles = 1000;
        
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
            //System.out.println("test");
            if(cycleIndex%10 == 1)
            {
               // System.out.print("");
            }
            //complete all takeTurns
            fillMarketMap();
            //System.out.println("test");
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
            
            //complete all request
            int originalRequestSize = reqList.size();
            int requestIndex = 0;
            
            while(requestIndex < reqList.size()&& requestIndex < 10*originalRequestSize)    
            {
                
                ReqTransfer currentRequest = reqList.get(requestIndex);
                
                if(!currentRequest.isInitFirm())
                {
                    if(people.get(currentRequest.getInitNum()).getMonetaryValue()>firms.get(currentRequest.getFinNum()).getProductPrice())
                    {
                        firms.get(currentRequest.getFinNum()).incRequested(1);
                        if(firms.get(currentRequest.getFinNum()).getAvailableUnitsProduced()>0)
                        {
                            people.get(currentRequest.getInitNum()).changeMonetaryValue(-1* firms.get(currentRequest.getFinNum()).getProductPrice());
                            firms.get(currentRequest.getFinNum()).sellAsset();
                            
                        }
                    }
                }
                else
                {
                    for (int currentAsset = currentRequest.getNumMoved(); currentAsset >0; currentAsset--) {
                        firms.get(currentRequest.getFinNum()).incRequested(1);
                        if(firms.get(currentRequest.getFinNum()).getAvailableUnitsProduced()>0)
                        {
                            firms.get(currentRequest.getInitNum()).buyCapital(firms.get(currentRequest.getFinNum()).sellAsset());
                        }
                        else
                        {
                            firms.get(currentRequest.getFinNum()).incRequested(currentAsset-1);
                            firms.get(currentRequest.getInitNum()).makeRequests();
                            currentAsset = -1;//breaks the forLoop
                        }
                    }
                }
                requestIndex++;
                if(requestIndex%1000==0){
                  //System.out.print(".");   
                }
            }//end of the requests
            double avgprice = 0.0;
            double totalSavings = 0.0;
            for(Firm cfirm: firms){
                //totalSavings += cfirm.getLiquidity();
                avgprice+=cfirm.getProductPrice();
            }
                
            
            avgprice/= firms.size();
            //System.out.println("");
            //System.out.println("request size: " + reqList.size()+" price: " + firms.get(10).getProductPrice() + " unitsAvailible: " + firms.get(10).getAvailableUnitsProduced() + " liquidity: " + firms.get(10).getLiquidity() + " unitsRequested: " + firms.get(10).getUnitsRequested());
            System.out.println("Request Size: " + reqList.size() + " total savings: " + (int)totalSavings + " average price : " + avgprice);
            //System.out.println("");
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
}//simRunner
