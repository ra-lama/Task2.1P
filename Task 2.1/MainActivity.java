package com.serasonproject.task21p;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.sql.Array;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Spinner spType;
    Spinner spFrom;
    Spinner spTo;
    ArrayAdapter arrAdapterToFrom;
    ArrayList<String> strType;
    TextView tvCommunication;
    EditText etAmt;
    Double tmpVal;
    Button bConvert;

    String strLength[] = {
        "--Select a measure--",
        // Length 1-7
        "Centimetre",
        "Inch",
        "Foot",
        "Yard",
        "Metre",
        "Kilometre",
        "Mile"
    };

    String strWeight[] = {
        "--Select a measure--",
        // Weight 1-5
        "Gram",
        "Pound",
        "Ounce",
        "Kilogram",
        "Ton"
    };

    String strTemp[] = {
        "--Select a measure--",
        // Temperature 1-3
        "Celsius",
        "Fahrenheit",
        "Kelvin"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCommunication = findViewById(R.id.tvComms); // for result and messages
        etAmt = findViewById(R.id.etAmount);

        // Button
        bConvert = findViewById(R.id.btnConvert);
        bConvert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tvCommunication.setTextColor(Color.RED);
                tmpVal = 0.0;
                Object spTypeName = spType.getSelectedItem(); // spType.getSelectedItemPosition();
                Object spFromName = spFrom.getSelectedItem();
                Object spToName = spTo.getSelectedItem();

                if(spType.getSelectedItemPosition() == 0) {
                    tvCommunication.setText("Select conversion type");
                }
                else if(spFrom.getSelectedItemPosition() == 0 || spTo.getSelectedItemPosition() == 0) {
                    tvCommunication.setText("Select both convert from/to measure");
                }
                else {
                    if(etAmt.getText().toString().isEmpty()) {
                        tvCommunication.setText("Enter a whole or decimal number");
                    }
                    else {
                        // also used EditText's "hint" to exclude anything but numbers
                        try {
                            tmpVal = Double.parseDouble(etAmt.getText().toString());
                            tvCommunication.setTextColor(Color.BLUE);
                            tvCommunication.setText(String.format("%.4f", conversionCalc(spTypeName, spFromName, spToName, tmpVal)) + " " + spToName.toString().toLowerCase());
                        } catch (Exception e) {
                            tvCommunication.setText("Enter a whole or decimal value");
                        }
                    }
                }
            }
        });

        // First selection, the Conversion Type dropdown
        spType = findViewById(R.id.spinnerType);
        strType = new ArrayList<String>();
        strType.add("---Conversion Type---");
        strType.add("Length");
        strType.add("Weight");
        strType.add("Temperature");
        spType.setAdapter(new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, strType));

        // event listener for Conversion Type dropdown, so the two selected measures are compatible
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i) {
                    case 0:
                        String j[] = { "--Select Conversion Type--" };
                        dynamicSpinners(j);
                        break;
                    case 1:
                        dynamicSpinners(strLength);
                        break;
                    case 2:
                        dynamicSpinners(strWeight);
                        break;
                    case 3:
                        dynamicSpinners(strTemp);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Nothing selected", "Nothing selected");
            }
        });
    }

    // one method for producing dynamic measures based on user selection in Conversion Type dropdown
    private void dynamicSpinners(String[] convTypeSelected) {
        spFrom = findViewById(R.id.spinnerFrom);
        spTo = findViewById(R.id.spinnerTo);
        arrAdapterToFrom = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, convTypeSelected);
        spFrom.setAdapter(arrAdapterToFrom);
        spTo.setAdapter(arrAdapterToFrom);
    }

    // calculated result
    private Double conversionCalc(Object conType, Object select1, Object select2, Double a) {
        Double tmpCalc = 0.0;
        String cType = conType.toString();
        Log.d("Conversion selected", cType);

        HashMap<String, Double> hmLookup = new HashMap<>();
        hmLookup.put("Centimetre", 1.0);
        hmLookup.put("Inch", 2.54);
        hmLookup.put("Foot", 30.48);
        hmLookup.put("Yard", 91.44);
        hmLookup.put("Metre", 100.0);
        hmLookup.put("Kilometre", 100000.0);
        hmLookup.put("Mile", 160934.0);
        hmLookup.put("Gram", 1.0);
        hmLookup.put("Ounce", 28.3495);
        hmLookup.put("Pound", 453.592);
        hmLookup.put("Kilogram", 1000.0);
        hmLookup.put("Ton", 907185.0);

        Double s1 = hmLookup.get(select1);
        Double s2 = hmLookup.get(select2);

        // selected same
        if(select1.toString() == select2.toString()) {
            return a;
        }
        else if(cType == "Temperature") {
            if(select1.toString() == "Celsius") {
                if(select2.toString() == "Fahrenheit") {
                    return ((a * 1.8) + 32.0);
                }
                else if(select2.toString() == "Kelvin") {
                    return (a + 273.15);
                }
            }
            else if(select1.toString() == "Fahrenheit") {
                if(select2.toString() == "Celsius") {
                    return ((a - 32.0) / 1.8);
                }
                else if(select2.toString() == "Kelvin") {
                    return (((a - 32.0) / 1.8) + 273.15);
                }
            }
            else if(select1.toString() == "Kelvin") {
                if(select2.toString() == "Celsius") {
                    return (a - 273.15);
                }
                else if(select2.toString() == "Fahrenheit") {
                    return (((a - 273.15) * 1.8) + 32.0);
                }
            }
        }
        else {
            if(select1.toString() == "Centimetre" || select1.toString() == "Gram") {
                return a / s2;
            }
            else if(select2.toString() == "Centimetre" || select2.toString() == "Gram") {
                return s1 * a;
            }
            else {
                return ((s1 * a) / s2);
            }
        }
        return 0.0;
    }
}