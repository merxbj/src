/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

/**
 *
 * @author merxbj
 */
class Plan {
    private int acresToBuy;
    private int acresToSell;
    private int bushelsToFeed;
    private int acresToSeed;

    public Plan(int acresToBuy, int acresToSell, int bushelsToFeed, int acresToSeed) {
        this.acresToBuy = acresToBuy;
        this.acresToSell = acresToSell;
        this.bushelsToFeed = bushelsToFeed;
        this.acresToSeed = acresToSeed;
    }
    
    public int getAcresToBuy() {
        return acresToBuy;
    }

    public void setAcresToBuy(int acresToBuy) {
        this.acresToBuy = acresToBuy;
    }

    public int getAcresToSeed() {
        return acresToSeed;
    }

    public void setAcresToSeed(int acresToSeed) {
        this.acresToSeed = acresToSeed;
    }

    public int getAcresToSell() {
        return acresToSell;
    }

    public void setAcresToSell(int acresToSell) {
        this.acresToSell = acresToSell;
    }

    public int getBushelsToFeed() {
        return bushelsToFeed;
    }

    public void setBushelsToFeed(int bushelsToFeed) {
        this.bushelsToFeed = bushelsToFeed;
    }
}
