package gameAccountSimulator;

public class LandPurchaseInfo {

    public LandPurchaseInfo(Land land) {
        this.land = land;
        this.quantity = 1;
        this.totalPrice = land.getCurrentPrice();
        this.totalIncomeIncrease = land.getIncome();
    }

    public void boughtOneMore() {
        quantity++;
        totalPrice += land.getCurrentPrice();
        totalIncomeIncrease += land.getIncome();
    }

    @Override
    public String toString() {
        return String.format("Bought %3d %-17s for %9.0f gold! Income increased by %9.0f golds!", quantity, land.getName(), totalPrice, totalIncomeIncrease);
    }

    private Land land;
    private int quantity;
    private double totalPrice;
    private double totalIncomeIncrease;
}
