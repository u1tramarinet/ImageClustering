package com.u1tramarinet.imageclustering.model;

import javafx.application.Platform;
import javafx.concurrent.Task;

public abstract class AsyncTask<In, Out> {
    public final void execute(In input) {
        Task<Void> backgroundTask = new Task<Void>() {
            @Override
            protected Void call() {
                Out output = doInBackground(input);
                Platform.runLater(() -> onPostExecute(output));
                return null;
            }
        };
        new Thread(backgroundTask).start();
    }

    public abstract Out doInBackground(In input);

    public abstract void onPostExecute(Out output);
}
