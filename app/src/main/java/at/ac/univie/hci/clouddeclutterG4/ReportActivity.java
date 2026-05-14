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
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private TextView deletionData;
    private Button doneButton;

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

        List<FileItem> trashItems = MockDataManager.getInstance().trashItems;
        int count = trashItems.size();
        long totalBytes = 0;
        Map<String, Long> typeBytesMap = new HashMap<>();

        for (FileItem item : trashItems) {
            totalBytes += item.sizeBytes;
            String type = (item.type != null && !item.type.isEmpty()) ? item.type : "Sonstige";
            typeBytesMap.merge(type, item.sizeBytes, Long::sum);
        }

        double sizeToShow;
        String unit;
        if (totalBytes >= 1024L * 1024L * 1024L) {
            sizeToShow = totalBytes / (1024.0 * 1024.0 * 1024.0);
            unit = "GB";
        } else {
            sizeToShow = totalBytes / (1024.0 * 1024.0);
            unit = "MB";
        }

        deletionData.setText(getString(R.string.files_deleted, count, sizeToShow, unit));

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        for (Map.Entry<String, Long> entry : typeBytesMap.entrySet()) {
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
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