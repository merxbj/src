/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.designPatterns;

/**
 *
 * @author jmerxbauer
 */
public class VisitorExample {

    interface VisitorZajicu {
        void visitBob(Bob b);
        void visitBobek(Bobek b);
    }

    interface Zajic {
        void accept(VisitorZajicu v);
        void vstavej();
        void spi();
    }

    class Bob implements Zajic {
        public void vstavej() { /* kod */ }
        public void spi() { /* kod */ }
        public void accept(VisitorZajicu v) {
            v.visitBob(this);
        }
    }

    class Bobek implements Zajic {
        public void vstavej() { /* kod */ }
        public void spi() { /* kod */ }
        public void accept(VisitorZajicu v) {
            v.visitBobek(this);
        }
    }

    class SpankovyRezim implements VisitorZajicu {
        public void visitBob(Bob b) {
            b.vstavej();
        }
        public void visitBobek(Bobek b) {
            b.spi();
        }
    }

    class Vecernicek {
        void zacni(Zajic z) {
            z.accept(new SpankovyRezim());
        }
    }

}
