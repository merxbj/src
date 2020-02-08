package cz.merxbj.unip.application;

import cz.merxbj.unip.common.CommonStatics;
import cz.merxbj.unip.common.MainTaskObserverResponse;
import cz.merxbj.unip.core.LogEvent;
import cz.merxbj.unip.core.LogReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author mrneo
 */
public class MainTask extends Observable implements Runnable {

    private boolean running;
    private boolean stop;
    private LogReader reader = new LogReader();
    private File file;
    private LogEvent root;

    public MainTask(File file) {
        this.file = file;
    }

    public void setRoot(LogEvent root) {
        this.root = root;
    }

    @Override
    public void addObserver(Observer o) {
        super.deleteObservers();
        super.addObserver(o);
    }

    private void readFile() {
        try {
            InputStream is = new FileInputStream(file);
            reader.read(is, root);
        } catch (Exception e) {
            CommonStatics.invokeErrorDialog(e.getMessage());
        }

        stop = true;
    }

    @Override
    public void run() {
        reader.registerObserver(this);

        stop = false;
        running = true;
        invokeUpdateState();

        while (!stop) {
            this.readFile();
        }

        running = false;
        invokeUpdateState();
    }

    public boolean isRunning() {
        return this.running;
    }

    public void fireStop() {
        stop = true;
        reader.fireStop();
        invokeUpdateState();
    }

    public LogReader getReader() {
        return this.reader;
    }

    public void invokeUpdateState() {
        this.setChanged();
        this.notifyObservers(MainTaskObserverResponse.UPDATESTATE);
    }

    public void invokeUpdateProgress() {
        this.setChanged();
        this.notifyObservers(MainTaskObserverResponse.UPDATEPROGRESS);
    }
}
