package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class scanningActivity extends AppCompatActivity {

    private TextView cloudText;
    private TextView fileText;
    private TextView timeText;
    private Button buttonPause;
    private Button buttonStop;
    private ProgressBar spinner;

    //clouds in scanning queue and files per cloud
    private ArrayList<String> clouds;
    private int[] filesPerCloud;
    private boolean running;
    private int currentFile = 0;
    private int currentCloudIdx = 0;
    private final Random rng = new Random();

    // Use a Handler to schedule UI updates without blocking the thread
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scanning);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonPause = findViewById(R.id.button3);
        buttonStop = findViewById(R.id.button4);
        spinner = findViewById(R.id.progressBar);
        cloudText = findViewById(R.id.textView2);
        fileText = findViewById(R.id.textView3);
        timeText = findViewById(R.id.textView20);

        // Initialize data once in onCreate
        Intent intent = getIntent();
        clouds = intent.getStringArrayListExtra("clouds");

        MockDataManager dm = MockDataManager.getInstance();

        if (clouds == null || clouds.isEmpty()) {
            clouds = new ArrayList<>();
            for (String cloudName : dm.cloudServices.keySet()) {  //new File Naomi
                MockDataManager.CloudService cl = dm.cloudServices.get(cloudName);
                if (cl != null && cl.isConnected && cl.isActive) {
                    clouds.add(cloudName);
                }
            }
        }

        /* no integration for local storage
        if (clouds.isEmpty()) {
            clouds.add("Gerätespeicher");
        }
        */


        filesPerCloud = new int[clouds.size()];
        for (int i = 0; i < clouds.size(); i++) {
            // Count actual items for this cloud if possible, otherwise random
            int count = 0;
            for (FileItem item : dm.cleanupItems) {
                if (item.source.equals(clouds.get(i))) count++;  //new File Naomi
            }
            filesPerCloud[i] = count > 0 ? count : rng.nextInt(5, 15);
        }
        
        currentFile = 0;
        currentCloudIdx = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        updateUI();
        // Start/Resume the scanning process
        scan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Crucial: Stop the "ghost" scan loop when the activity is no longer in the foreground
        running = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void updateUI() {
        if (currentCloudIdx < clouds.size()) {
            cloudText.setText(getString(R.string.wird_gescannt, clouds.get(currentCloudIdx)));
            fileText.setText(getString(R.string.file_progress, currentFile, filesPerCloud[currentCloudIdx]));
            timeText.setText(getString(R.string.time_remaining, calculateRemainingTime()));
        }
    }
    
    private int calculateRemainingTime(){
        int remainingFiles = 0;

        // Files left in the current cloud
        if (currentCloudIdx < filesPerCloud.length) {
            remainingFiles += (filesPerCloud[currentCloudIdx] - currentFile);
        }

        // All files in upcoming clouds
        for (int i = currentCloudIdx + 1; i < filesPerCloud.length; i++) {
            remainingFiles += filesPerCloud[i];
        }

        // Each file takes approx 600ms. Round down to seconds.
        return (remainingFiles * 600) / 1000;
    }

    public void scan() {
        if (!running) return;

        // Schedule the next update after a random delay
        handler.postDelayed(() -> {
            if (!running) return;

            if (currentFile < filesPerCloud[currentCloudIdx]) {
                currentFile++;
            } else {
                if (currentCloudIdx + 1 < clouds.size()) {
                    currentCloudIdx++;
                    currentFile = 0; // Reset for the next cloud
                } else {
                    // Scanning finished
                    running = false;
                    spinner.setVisibility(View.GONE);
                    
                    // Start ReportActivity and finish this one so it doesn't stay in backstack
                    Intent intent = new Intent(this, CleanupActivity.class);
                    // Forward all extras from ScanSettings to CleanupActivity
                    intent.putExtras(getIntent());
                    startActivity(intent);
                    finish(); 
                    return;
                }
            }

            updateUI();
            
            // Recursively call scan to schedule the next "file"
            scan();
        }, rng.nextInt(200, 1000));
    }

    public void pause(View v) {
        if (running) {
            spinner.setVisibility(View.INVISIBLE);
            running = false;
            handler.removeCallbacksAndMessages(null); // Stop scheduled updates
            buttonPause.setText(getString(R.string.resume)); 
        } else {
            spinner.setVisibility(View.VISIBLE);
            running = true;
            buttonPause.setText(getString(R.string.pause));
            scan(); // Resume updates
        }
    }

    public void stop(View v) {
        running = false;
        handler.removeCallbacksAndMessages(null);
        // Go back to main and clear the task so we don't have multiple scan screens
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
