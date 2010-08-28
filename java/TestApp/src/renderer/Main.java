/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package renderer;

import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author jmerxbauer
 */
public class Main {

    public static Sphere spheres[] = {//Scene: radius, position, emission, color, material
        new Sphere(1e5, new Vec( 1e5+1,40.8,81.6), new Vec(),new Vec(.75,.25,.25),Refl_t.DIFF),//Left
        new Sphere(1e5, new Vec(-1e5+99,40.8,81.6),new Vec(),new Vec(.25,.25,.75),Refl_t.DIFF),//Rght
        new Sphere(1e5, new Vec(50,40.8, 1e5), new Vec(),new Vec(.75,.75,.75),Refl_t.DIFF),//Back
        new Sphere(1e5, new Vec(50,40.8,-1e5+170), new Vec(),new Vec(), Refl_t.DIFF),//Frnt
        new Sphere(1e5, new Vec(50, 1e5, 81.6), new Vec(),new Vec(.75,.75,.75),Refl_t.DIFF),//Botm
        new Sphere(1e5, new Vec(50,-1e5+81.6,81.6),new Vec(),new Vec(.75,.75,.75),Refl_t.DIFF),//Top
        new Sphere(16.5,new Vec(27,16.5,47), new Vec(),new Vec(1,1,1).multiply(.999), Refl_t.SPEC),//Mirr
        new Sphere(16.5,new Vec(73,16.5,78), new Vec(),new Vec(1,1,1).multiply(.999), Refl_t.REFR),//Glas
        new Sphere(600, new Vec(50,681.6-.27,81.6),new Vec(12,12,12), new Vec(), Refl_t.DIFF) //Lite
    };

    public static double clamp(double x){ return x<0 ? 0 : x>1 ? 1 : x; }

    public static int toInt(double x){ return (int)((Math.pow(clamp(x),1/2.2)*255+.5)); }

    public static boolean intersect(Ray r, double[] t, int[] id) {
        int n = spheres.length - 1;
        double d, inf=t[0]=1e20;
        for (int j = n; j >= 0; j--) {
            d = spheres[j].intersect(r);
            if((d != 0) && (d < t[0])) {
                t[0] = d;
                id[0] = j;
            }
        }
        return t[0]<inf;
    }

    public static double erand48(int[] Xi) {
        return Math.random();
    }

    public static Vec radiance(Ray r, int depth, int[] Xi){
        double t[] = {0.0}; // distance to intersection
        int id[] = {0}; // id of intersected object
        if (!intersect(r, t, id)) return new Vec(); // if miss, return black
        Sphere obj = spheres[id[0]]; // the hit object
        Vec x = r.o.add(r.d.multiply(t[0]));
        Vec n = (x.substract(obj.p)).norm();
        Vec nl = n.dot(r.d) < 0 ? n : n.multiply(-1);
        Vec f= obj.c;
        double p = f.x>f.y && f.x>f.z ? f.x : f.y>f.z ? f.y : f.z; // max refl
        if (++depth>5) if (erand48(Xi)<p) f = f.multiply(1/p); else return obj.e; //R.R.
        if (obj.refl == Refl_t.DIFF) { // Ideal DIFFUSE reflection
            double r1=2*Math.PI*erand48(Xi), r2=erand48(Xi), r2s=Math.sqrt(r2);
            Vec w = nl;
            Vec u =((Math.abs(w.x)>.1 ? new Vec(0,1) : new Vec(1)).modulo(w)).norm();
            Vec v = w.modulo(u);
            Vec d = (u.multiply(Math.cos(r1)).multiply(r2s).add(v.multiply(Math.sin(r1)).multiply(r2s)).add(w.multiply(Math.sqrt(1-r2)))).norm();
            return obj.e.add(f.mult(radiance(new Ray(x,d),depth,Xi)));
        } else if (obj.refl == Refl_t.SPEC) {// Ideal SPECULAR reflection
            return obj.e.add(f.mult(radiance(new Ray(x, r.d.substract(n.multiply(2*n.dot(r.d)))), depth, Xi)));
        }
        Ray reflRay = new Ray(x, r.d.substract(n.multiply(2*n.dot(r.d)))); // Ideal dielectric REFRACTION
        boolean into = n.dot(nl)>0; // Ray from outside going in?
        double nc=1, nt=1.5, nnt=into?nc/nt:nt/nc, ddn=r.d.dot(nl), cos2t;
        if ((cos2t=1-nnt*nnt*(1-ddn*ddn))<0) // Total internal reflection
            return obj.e.add(f.mult(radiance(reflRay,depth,Xi)));
        Vec tdir = (r.d.multiply(nnt).substract(n.multiply((into?1:-1)*(ddn*nnt+Math.sqrt(cos2t))))).norm();
        double a=nt-nc, b=nt+nc, R0=a*a/(b*b), c = 1-(into?-ddn:tdir.dot(n));
        double Re=R0+(1-R0)*c*c*c*c*c,Tr=1-Re,P=.25+.5*Re,RP=Re/P,TP=Tr/(1-P);
        return obj.e.add(f.mult(depth>2 ? (erand48(Xi)<P ? // Russian roulette
            radiance(reflRay,depth,Xi).multiply(RP) : radiance(new Ray(x,tdir),depth,Xi).multiply(TP)) :
            radiance(reflRay,depth,Xi).multiply(Re).add(radiance(new Ray(x,tdir),depth,Xi).multiply(Tr))));
    }

    public static final int width = 1024, height = 768;
    public static Vec[] scene = new Vec[width*height];
    public static int progress = 0;
    public static final Object[] locks = {new Object(), new Object()};
    public static JoinableThreadGroup jtg = new JoinableThreadGroup("Threads");
    public static int threadCount = 0;
    public static final Object signal = new Object();

    public static synchronized Vec[] acquireScene() {
        return scene;
    }

    public static void main(String[] args) {
        final int samps = args.length == 1 ? Integer.parseInt(args[0])/4 : 1; // # samples
        final Ray cam = new Ray(new Vec(50,52,295.6), new Vec(0,-0.042612,-1).norm()); // cam pos, dir
        final Vec cx = new Vec(width*.5135/height);
        final Vec cy = (cx.modulo(cam.d)).norm().multiply(.5135);
        for (int p = 0; p < scene.length; p++) {
            scene[p] = new Vec();
        }
        for (int y=0; y<height; y++){ // Loop over image rows
            final int cury = y;
            Thread t = new Thread(jtg, new Runnable() {
                public void run() {
                    int Xi[] = {0,0,cury*cury*cury};
                    for (int x = 0; x<width; x++) { // Loop cols
                        for (int sy=0, i=(height-cury-1)*width+x; sy<2; sy++) { // 2x2 subpixel rows
                            Vec r = new Vec();
                            for (int sx=0; sx<2; sx++, r = new Vec()){ // 2x2 subpixel cols
                                for (int s=0; s<samps; s++){
                                    double r1=2*erand48(Xi), dx=r1<1 ? Math.sqrt(r1)-1: 1-Math.sqrt(2-r1);
                                    double r2=2*erand48(Xi), dy=r2<1 ? Math.sqrt(r2)-1: 1-Math.sqrt(2-r2);
                                    Vec d = cx.multiply( ( (sx+.5 + dx)/2 + x)/width - .5).add(
                                    cy.multiply( ( (sy+.5 + dy)/2 + cury)/height - .5).add(cam.d));
                                    r = r.add(radiance(new Ray(cam.o.add(d.multiply(140)),d.norm()),0,Xi).multiply(1./samps));
                                } // Camera rays are pushed ^^^^^ forward to start in interior
                                acquireScene()[i] = acquireScene()[i].add(new Vec(clamp(r.x),clamp(r.y),clamp(r.z)).multiply(.25));
                            }
                        }
                    }
                    synchronized (signal) {
                        System.out.println(String.format("\rRendered %5.2f%% | %d threads on duty | %d rows remaining", 100.*progress++/(height), threadCount--, height-progress));
                        signal.notify(); // let the waitee know that we have finished to eventually spawn another thread
                    }
                }
            });
            
            t.start();
            synchronized (signal) {
                /*
                 * Make sure that we are not spawning more threads than 
                 * availabke processors unless we will generate unnecessary overhead.
                 */
                if (++threadCount >= Runtime.getRuntime().availableProcessors()) {
                    try {
                        signal.wait();
                    } catch (InterruptedException iex) {
                        // don't care
                    }
                }
            }
        }

        // wait for all threads to get the job done!
        jtg.join();

        File f = new File("image.ppm");
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(String.format("P3\n%d %d\n%d\n", width, height, 255));
            for (int i=0; i<width*height; i++) {
                fw.write(String.format("%d %d %d ", toInt(scene[i].x), toInt(scene[i].y), toInt(scene[i].z)));
            }
        } catch (Exception ex) {
            
        }
    }
}
