package com.xiupitter.task;

import org.jivesoftware.multiplexer.ClientSession;
import org.jivesoftware.multiplexer.ConnectionWorkerThread;
import org.jivesoftware.multiplexer.task.ClientTask;

public class NewSessionExTask extends ClientTask{

    private String addressStr = null;
    private String hostname;

    public NewSessionExTask(String streamID, String addressStr, String hostname) {
        super(streamID);
        this.addressStr = addressStr;
        this.hostname = hostname;
    }
    public void run() {
        ConnectionWorkerThread workerThread = (ConnectionWorkerThread) Thread.currentThread();
        workerThread.clientSessionCreated(streamID, addressStr,hostname);
    }

    public void serverNotAvailable() {
        // Close client session indicating that the server is not available
        ClientSession.getSession(streamID).close(true);
    }
}
