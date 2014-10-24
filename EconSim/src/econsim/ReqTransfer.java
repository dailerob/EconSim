/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package econsim;

/**
 *
 * @author Roberto
 */
public class ReqTransfer {

    
    public ReqTransfer(boolean initFirm, int initNum, boolean finfirm, int finNum, int numMoved) {
        this.initFirm = initFirm;
        this.initNum = initNum;
        this.finfirm = finfirm;
        this.finNum = finNum;
        this.numMoved = numMoved;
    }
    
    boolean initFirm; 
    int initNum; 
    boolean finfirm; 
    int finNum; 
    int numMoved; 
    
    

    public boolean isInitFirm() {
        return initFirm;
    }

    public int getInitNum() {
        return initNum;
    }

    public boolean isFinfirm() {
        return finfirm;
    }

    public int getFinNum() {
        return finNum;
    }

    public int getNumMoved() {
        return numMoved;
    }
    
    
    
    public void carryOutRequest()
    {
        Firm finalFirm = SimRunner.firms.get(finNum);
        if(!initFirm)
        {
            Person initPerson  = SimRunner.people.get(initNum);
            if(initPerson.getMonetaryValue()>finalFirm.getProductPrice()){
                finalFirm.incRequested(1);
                if(finalFirm.getAvailableUnitsProduced()>0)
                {
                    initPerson.buyAsset(finalFirm.sellAsset());
                }
            }
            
        }
        else {
            Firm initFirm = SimRunner.firms.get(initNum);
            for (int currentAsset = 0; currentAsset < numMoved; currentAsset++)
            {
                if (initFirm.getLiquidity() > finalFirm.getProductPrice()) 
                {
                    finalFirm.incRequested(1);
                    if (finalFirm.getAvailableUnitsProduced() > 0) {
                        initFirm.buyCapital(finalFirm.sellAsset());
                    }
                    else
                    {
                        finalFirm.incRequested(numMoved-(currentAsset+1));
                        currentAsset = numMoved;//exits the forloop  
                    }
                }
            }
        }
        
    }
}//Reqtransfer-- end of class
