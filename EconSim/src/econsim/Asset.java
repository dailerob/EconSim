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
public class Asset {
    private int assetNum; 
    public double sellPrice; 
    public double value; 
    
    public Asset (int typenum)
    {
        assetNum = typenum; 
    }
    
    public Asset (int typenum, double value)
    {
        assetNum = typenum;
        this.value = value; 
    }

    public int getAssetNum() {
        return assetNum;
    }

    public void setAssetNum(int assetNum) {
        this.assetNum = assetNum;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
}//class asset
