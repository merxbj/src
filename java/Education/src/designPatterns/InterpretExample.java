/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package designPatterns;

/**
 * We want to be able to interpret this:
 * a = 10;
   b = a;
   c = b + 1;
   print c;
 * @author jmerxbauer
 */
public class InterpretExample {
    public interface Expression {
        int eval();
    }

    public interface TerminatingExpression {
        void eval();
    }

    public class Number implements Expression {
        public int val;
        public int eval() {
            return val;
        }

        public Number(int value) {
            this.val = value;
        }

        public Number(Expression value) {
            this.val = value.eval();
        }
    }

    public class Plus implements Expression {
        public Expression left;
        public Expression right;
        public int eval() {
            return left.eval() + right.eval();
        }

        public Plus(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
    }

    public class Print implements TerminatingExpression {
        public Expression toPrint;
        public void eval() {
            System.out.println(toPrint.eval());
        }

        public Print(Expression toPrint) {
            this.toPrint = toPrint;
        }
    }

    /**
     * a = 10;
       b = a;
       c = b + 1;
       print c;
     */ 
    public void use() {
        Number a = new Number(10);
        Number b = new Number(a);
        Number c = new Number(new Plus(b, new Number(1)));
        Print print = new Print(c);
        print.eval();
    }
}
