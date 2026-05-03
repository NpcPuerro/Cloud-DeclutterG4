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

import java.util.Random;

public class scanningActivity extends AppCompatActivity {

    private TextView cloudText;
    private TextView fileText;
    private Button buttonPause;
    private Button buttonStop;
    private ProgressBar spinner;

    //clouds in scanning queue and files per cloud
    private String[] clouds;
    private int[] filesPerCloud;
    private boolean running;
    private int currentFile = 0;
    private int currentCloudIdx = 0;
    private Random rng = new Random();

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
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Initialize data if needed
        if (clouds == null) {
            clouds = new String[]{"iCloud", "Google Drive"};
            filesPerCloud = new int[]{5, 7};
        }

        running = true;
        cloudText.setText(getString(R.string.wird_gescannt, clouds[currentCloudIdx]));
        fileText.setText(getString(R.string.file_progress, currentFile, filesPerCloud[currentCloudIdx]));

        // Start the scanning process
        scan();
    }

    public void scan() {
        if (!running) return;

        // Schedule the next update after a random delay
        handler.postDelayed(() -> {
            if (!running) return;

            if (currentFile < filesPerCloud[currentCloudIdx]) {
                currentFile++;
            } else {
                if (currentCloudIdx + 1 < clouds.length) {
                    currentCloudIdx++;
                    currentFile = 0; // Reset for the next cloud
                    cloudText.setText(getString(R.string.wird_gescannt, clouds[currentCloudIdx]));
                } else {
                    // Scanning finished
                    running = false;
                    spinner.setVisibility(View.GONE);
                    //REPLACE: file deletion is missing in between.
                    Intent intent = new Intent(this, ReportActivity.class);
                    startActivity(intent);
                    return;
                }
            }

            fileText.setText(getString(R.string.file_progress, currentFile, filesPerCloud[currentCloudIdx]));

            // Recursively call scan to schedule the next "file"
            scan();
        }, rng.nextInt(200, 1000));
    }

    public void pause(View v) {
        if (running) {
            spinner.setVisibility(View.INVISIBLE);
            running = false;
            handler.removeCallbacksAndMessages(null); // Stop scheduled updates
        } else {
            spinner.setVisibility(View.VISIBLE);
            running = true;
            scan(); // Resume updates
        }
    }

    public void stop(View v) {
        running = false;
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}