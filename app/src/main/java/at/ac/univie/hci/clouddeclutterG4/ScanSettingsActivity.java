package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ScanSettingsActivity extends AppCompatActivity {

    private TextView fileTypeSelector;
    private boolean[] selectedFileTypes;
    private final ArrayList<Integer> typeList = new ArrayList<>();
    private String[] fileTypesArray;
    private EditText minNumber;
    private EditText maxNumber;
    private Spinner minUnit;
    private Spinner maxUnit;
    private EditText nameContains;
    private TextView dateRangeSelector;
    private String selectedDateRange = "";
    private CheckBox iCloud;
    private CheckBox googleDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        minNumber = findViewById(R.id.editTextNumber);
        maxNumber = findViewById(R.id.editTextNumber2);
        minUnit = findViewById(R.id.spinner_unit1);
        maxUnit = findViewById(R.id.spinner_unit2);
        nameContains = findViewById(R.id.editTextText);
        dateRangeSelector = findViewById(R.id.dateRangeSelector);
        iCloud = findViewById(R.id.checkBox);
        googleDrive = findViewById(R.id.checkBox2);

        fileTypeSelector = findViewById(R.id.multi_spinner_file_types);
        fileTypesArray = getResources().getStringArray(R.array.file_types);
        selectedFileTypes = new boolean[fileTypesArray.length];

        fileTypeSelector.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ScanSettingsActivity.this);
            builder.setTitle(R.string.select_file_types);
            builder.setCancelable(false);

            builder.setMultiChoiceItems(fileTypesArray, selectedFileTypes, (dialogInterface, i, b) -> {
                if (b) {
                    if (!typeList.contains(i)) {
                        typeList.add(i);
                        Collections.sort(typeList);
                    }
                } else {
                    typeList.remove(Integer.valueOf(i));
                }
            });

            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < typeList.size(); j++) {
                    stringBuilder.append(fileTypesArray[typeList.get(j)]);
                    if (j != typeList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                if (typeList.isEmpty()) {
                    fileTypeSelector.setText(R.string.select_all);
                } else {
                    fileTypeSelector.setText(stringBuilder.toString());
                }
            });

            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());

            builder.setNeutralButton(R.string.clear_all, (dialogInterface, i) -> {
                Arrays.fill(selectedFileTypes, false);
                typeList.clear();
                fileTypeSelector.setText(R.string.select_all);
            });

            builder.show();
        });

        dateRangeSelector.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(R.string.zeitraum_waehlen)
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String startDate = sdf.format(new Date(selection.first));
                String endDate = sdf.format(new Date(selection.second));
                selectedDateRange = startDate + " - " + endDate;
                dateRangeSelector.setText(selectedDateRange);
            });

            picker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
        });
    }

    private ArrayList<String> getClouds(){
        ArrayList<String> toRet = new ArrayList<>();
        if (iCloud.isChecked()){
            toRet.add("iCloud");
        }
        if (googleDrive.isChecked()){
            toRet.add("Google Drive");
        }
        return toRet;
    }

    public void startScan(View v) {
        if (!iCloud.isChecked() && !googleDrive.isChecked()){
            Snackbar.make(v, R.string.error_no_cloud, Snackbar.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, scanningActivity.class);
            intent.putExtra("clouds", getClouds());
            intent.putExtra("minSize", minNumber.getText().toString());
            intent.putExtra("maxSize", maxNumber.getText().toString());
            intent.putExtra("minUnit", minUnit.getSelectedItem().toString());
            intent.putExtra("maxUnit", maxUnit.getSelectedItem().toString());
            intent.putExtra("fileTypes", fileTypeSelector.getText().toString()); //Comma separated list, has to be converted to real list or parsed
            intent.putExtra("nameContains", nameContains.getText().toString()); //Comma separated list, has to be converted to real list or parsed
            intent.putExtra("dateRange", selectedDateRange);

            startActivity(intent);
        }
    }
}
