
package common;

public class BaseExectuableClass implements Executable {

    public BaseExectuableClass(boolean wantRun) {
        this.wantRun = wantRun;
    }

    public boolean ReadyForExecution() {
        return this.wantRun;
    }

    public void Execute() {
        ;
    }

    protected boolean wantRun = false;
}
