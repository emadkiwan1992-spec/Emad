package com.example.app;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner assetSpinner;
    Spinner timeframeSpinner;
    Button scanButton;
    TextView resultText;
    TextView strengthText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("OTC SIGNAL SCANNER");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        assetSpinner = new Spinner(this);

        String[] assets = {
                "EUR/USD OTC",
                "GBP/USD OTC",
                "USD/JPY OTC",
                "EUR/JPY OTC",
                "GBP/JPY OTC"
        };

        ArrayAdapter<String> assetAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        assets
                );

        assetSpinner.setAdapter(assetAdapter);
        layout.addView(assetSpinner);

        timeframeSpinner = new Spinner(this);

        String[] timeframes = {
                "1 Minute",
                "5 Minutes",
                "15 Minutes",
                "1 Hour"
        };

        ArrayAdapter<String> timeframeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        timeframes
                );

        timeframeSpinner.setAdapter(timeframeAdapter);
        layout.addView(timeframeSpinner);

        scanButton = new Button(this);
        scanButton.setText("SCAN");
        layout.addView(scanButton);

        resultText = new TextView(this);
        resultText.setText("WAIT");
        resultText.setTextSize(32);
        resultText.setGravity(Gravity.CENTER);
        resultText.setPadding(0, 50, 0, 20);

        layout.addView(resultText);

        strengthText = new TextView(this);
        strengthText.setText("Waiting for scan...");
        strengthText.setTextSize(18);
        strengthText.setGravity(Gravity.CENTER);

        layout.addView(strengthText);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String asset =
                        assetSpinner.getSelectedItem().toString();

                String timeframe =
                        timeframeSpinner.getSelectedItem().toString();

                resultText.setText("ANALYZING...");
                resultText.setTextColor(Color.BLUE);

                strengthText.setText(
                        asset + "\n" +
                        timeframe + "\n\n" +
                        "Indicators: RSI | MACD | EMA | BB | Stochastic"
                );
            }
        });

        setContentView(layout);
    }
}
