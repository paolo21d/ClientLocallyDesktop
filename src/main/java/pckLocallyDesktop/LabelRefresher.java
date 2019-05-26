package pckLocallyDesktop;

import javafx.concurrent.Task;

public class LabelRefresher extends Task<String> {
    private String title;
    @Override
    protected String call() throws Exception {
        synchronized (this){
            wait();
            while(true){
                updateMessage(title);
                wait();
            }
        }
    }

    public void refresh(String t){
        synchronized (this){
            title = t;
            notify();
        }
    }
}
