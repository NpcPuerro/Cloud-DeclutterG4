package at.ac.univie.hci.clouddeclutterG4;

import android.app.Application;
import android.util.Log;

public class SetupApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LoginData.addLogin("rainer.winkler@gmail.com", "unbesigt");
        LoginData.addLogin("hcim3.t412@clouddeclutter.at", "hci");
    }
}
