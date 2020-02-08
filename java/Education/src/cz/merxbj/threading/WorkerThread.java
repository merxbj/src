package cz.merxbj.threading;

/*
 * This class simulates a times expansive action which would block the framw
 * unless runing in a separate thread
 */
public class WorkerThread implements Runnable {

    public WorkerThread(MainFrame mf) {
        this.mf = mf;
        this.stop = false;
    }

    public void run() {
        int cycles = 0;
        mf.getjProgressBar1().setMinimum(0);
        mf.getjProgressBar1().setMaximum(60);

        while (cycles < 60 && !stop) {
            try {
                Thread.sleep(1000); // very useful work here! :D

                mf.getLog().append("I'm sleeping, dude! Hard work here!\n");
                mf.getjProgressBar1().setValue(cycles);

                cycles++;
            } catch (InterruptedException  iex) {
                // we are already sleeping and someone else is trying to sleep us
                // again - dont care about that
            }
        }
    }

    public synchronized void stopThread() {
        this.stop = true;
    }

    private MainFrame mf;
    private boolean stop;
}
