package simpleFinancialJournal;

/**
 *
 * @author eTeR
 */
public class SortOrder {

    public SortOrder(Order order) {
        this.order = order;
    }

    public SortOrder(String order) {
        this.order = parse(order);
    }

    public Order parse(String rawOrder){
        if (rawOrder.compareTo("asc") == 0) {
            return Order.ASC;
        } else if (rawOrder.compareTo("desc") == 0) {
            return Order.DESC;
        } else {
            return Order.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return order.toString();
    }

    public Order getOrder() {
        return order;
    }

    Order order;

    public enum Order {
        ASC, DESC, UNKNOWN
    }
}
