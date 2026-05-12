package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private TextView deletionData;
    private Button doneButton;
    private HashMap<String,Double> chartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        deletionData = findViewById(R.id.textView4);
        doneButton = findViewById(R.id.button2);

        //replace with intent from file deletion:
        deletionData.setText(getString(R.string.files_deleted,4,1.2,"GB"));
        chartData = new HashMap<String,Double>();
        chartData.put("Bilder",0.2);
        chartData.put("Videos",0.7);
        chartData.put("Textdateien",0.2);
        chartData.put("Sonstige",0.1);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        for (String key : chartData.keySet()) {
            data.add(new ValueDataEntry(key, chartData.get(key)));
        }

        pie.data(data);
        anyChartView.setChart(pie);

    }

    public void done(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}