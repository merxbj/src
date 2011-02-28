/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.core;

import java.util.List;

/**
 *
 * @author jmerxbauer
 */
public class GameProgress {

    private List<TableauTransfer> movements;
    
    public void print() {
        for (TableauTransfer tableauTransfer : movements) {
            System.out.println(tableauTransfer);
        }
    }
    
    public void onFlip(Pile<?> onPile) {
        movements.add(new TableauTransfer(onPile.getNumber(), onPile.getNumber(), 0, Reason.Flip));
    }
    
    public void onTransfer(Pile<?> from, Pile<?> to, Pile<?> transfer) {
        movements.add(new TableauTransfer(from.getNumber(), to.getNumber(), transfer.size(), Reason.General));
    }
    
    public void onFinalize(Pile<?> from, Pile<?> to, Pile<?> transfer) {
        movements.add(new TableauTransfer(from.getNumber(), to.getNumber(), transfer.size(), Reason.Finalization));
    }
    
    public void onFound(Pile<?> founded, int foundationPileNumber) {
        movements.add(new TableauTransfer(founded.getNumber(), foundationPileNumber, Card.Rank.values().length, Reason.Found));
    }
    
    public class TableauTransfer {
        private int from;
        private int to;
        private int size;
        private Reason reason;

        public TableauTransfer(int from, int to, int size, Reason reason) {
            this.from = from;
            this.to = to;
            this.size = size;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return "TableauTransfer{" + "from=" + from + ", to=" + to + ", size=" + size + ", reason=" + reason + '}';
        }
        
    }
    
    public enum Reason {
        Flip, General, Finalization, Found
    }
    
}
