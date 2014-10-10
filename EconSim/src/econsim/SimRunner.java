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
        System.out.println("test");
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
            //complete all takeTurns
            fillMarketMap();
            while(marketMap.size()>0)
            {
                int turnIndex = (int)(rand.nextDouble()*(marketMap.size()-1));
                marketMap.remove(turnIndex);
                if(turnIndex < people.size())
                {
                    people.get(marketMap.get(turnIndex)).takeTurn();
                }
                else
                {
                    firms.get(marketMap.get(turnIndex)).takeTurn();
                }
            }//end of taking turns
             
            System.out.println("test2");
            
            //complete all request
            int originalRequestSize = reqList.size();
            int requestIndex = 0;
            
            for(ReqTransfer currentRequest: reqList)
            {
                if(requestIndex >=  10 * originalRequestSize)
                    break;
                
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
                        }
                    }
                }
                requestIndex++;
            }//end of the requests
            reqList.clear();
            marketMap.clear();
            
            System.out.println("firm 10's product price: " + firms.get(10).getProductPrice());
        }//end of the cycle 
    }//main
    
    
    public static void fillMarketMap() {
        int currentIndex = 0;
        for (Person peopleIndex : people) {
            marketMap.add(currentIndex);
            currentIndex++;
        }

        currentIndex = 0;
        for (Firm firmIndex : firms) {     
            marketMap.add(currentIndex);
            currentIndex++;
        }
    }
}//simRunner
