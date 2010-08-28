/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package renderer;

/**
 *
 * @author jmerxbauer
 */
public class Sphere {
    double rad; // radius
    public Vec p, e, c; // position, emission, color
    public Refl_t refl; // reflection type (DIFFuse, SPECular, REFRactive)
    public Sphere(double rad, Vec p, Vec e, Vec c, Refl_t refl) {
        this.rad = rad; this.p = p; this.e = e; this.c = c; this.refl = refl;
    }
    public double intersect(Ray r) { // returns distance, 0 if nohit
        Vec op = p.substract(r.o); // Solve t^2*d.d + 2*t*(o-p).d + (o-p).(o-p)-R^2 = 0
        double t, eps=1e-4, b=op.dot(r.d), det=b*b-op.dot(op)+rad*rad;
        if (det<0) return 0; else det=Math.sqrt(det);
        return (t=b-det)>eps ? t : ((t=b+det)>eps ? t : 0);
    }
}
