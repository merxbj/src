/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package renderer;

/**
 *
 * @author jmerxbauer
 */
public class Vec {
    // Usage: time ./smallpt 5000 && xv image.ppm
    public double x, y, z; // position, also color (r,g,b)
    public Vec() { this(0,0,0); }
    public Vec(double x) { this(x,0,0); }
    public Vec(double x, double y) { this(x,y,0); }
    public Vec(double x, double y, double z) { this.x=x; this.y=y; this.z=z; }
    public Vec add(Vec b) { return new Vec(x+b.x,y+b.y,z+b.z); }
    public Vec substract(Vec b) { return new Vec(x-b.x,y-b.y,z-b.z); }
    public Vec multiply(double b) { return new Vec(x*b,y*b,z*b); }
    public Vec mult(Vec b) { return new Vec(x*b.x,y*b.y,z*b.z); }
    public Vec norm() { 
        Vec n = this.multiply(1/Math.sqrt(x*x+y*y+z*z));
        this.x = n.x; this.y=n.y; this.z = n.z;
        return this;
    }
    public double dot(Vec b) { return x*b.x+y*b.y+z*b.z; } // cross:
    public Vec modulo(Vec b) { return new Vec(y*b.z-z*b.y,z*b.x-x*b.z,x*b.y-y*b.x);}
}
