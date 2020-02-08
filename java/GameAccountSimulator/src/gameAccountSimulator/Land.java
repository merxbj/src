package gameAccountSimulator;

public class Land {

    Land() {
        quantity = 0;
        startingPrice = 0;
    }

    Land(String name, double startingPrice, int quantity, double income) {
        this.name = name;
        this.startingPrice = startingPrice;
        this.quantity = quantity;
        this.income = income;
    }

    public double getCurrentPrice() {
        double addition = startingPrice * 0.1 * quantity;
        return startingPrice + addition;
    }

    public double getIncomePerPrice() {
        return (getCurrentPrice() / income);
    }

    public double getTotalIncome() {
        return (income * quantity);
    }

    public double getIncome() {
        return income;
    }

    public void incQuantity() {
        quantity++;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("%-17s  %3d  %9.2f", getName(), getQuantity(), getIncomePerPrice());
    }

    private double startingPrice;
    private int quantity;
    private double income;
    private String name;
}