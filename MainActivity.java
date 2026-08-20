package com.example.pocketoptionscanner;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    ImageView preview;
    TextView result, details;
    Spinner duration;
    Bitmap chart;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        buildUI();
    }

    TextView tv(String s, int sp) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(sp); t.setTextColor(Color.WHITE);
        t.setPadding(16,10,16,10); return t;
    }

    void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18,18,18,18);
        root.setBackgroundColor(Color.rgb(16,20,28));

        TextView title = tv("POCKET OPTION SCANNER", 22);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView note = tv("تحليل يدوي للصورة — الإشارة ليست ضمانًا للربح", 13);
        note.setGravity(Gravity.CENTER);
        root.addView(note);

        duration = new Spinner(this);
        String[] d = {"1 دقيقة","3 دقائق","5 دقائق","15 دقيقة"};
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, d);
        duration.setAdapter(ad);
        root.addView(duration);

        Button pick = new Button(this);
        pick.setText("اختيار Screenshot");
        root.addView(pick);

        preview = new ImageView(this);
        preview.setAdjustViewBounds(true);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(preview, new LinearLayout.LayoutParams(-1, 0, 1));

        Button scan = new Button(this);
        scan.setText("SCAN");
        root.addView(scan);

        result = tv("NO SIGNAL", 28);
        result.setGravity(Gravity.CENTER);
        root.addView(result);

        details = tv("اختر Screenshot للشارت ثم اضغط SCAN.", 14);
        details.setGravity(Gravity.CENTER);
        root.addView(details);

        setContentView(root);

        pick.setOnClickListener(v -> chooseImage());
        scan.setOnClickListener(v -> analyze());
    }

    void chooseImage() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, 10);
    }

    @Override protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req,res,data);
        if (req==10 && res==RESULT_OK && data!=null) {
            try {
                Uri u=data.getData();
                chart=MediaStore.Images.Media.getBitmap(getContentResolver(),u);
                preview.setImageBitmap(chart);
                result.setText("READY");
                details.setText("الصورة جاهزة للتحليل.");
            } catch(IOException e) {
                result.setText("NO SIGNAL");
                details.setText("تعذر قراءة الصورة.");
            }
        }
    }

    void analyze() {
        if (chart==null) {
            result.setText("NO SIGNAL");
            details.setText("اختر Screenshot أولًا.");
            return;
        }

        // Lightweight candle-color/edge heuristic for an offline MVP.
        // It is intentionally conservative: weak evidence => NO SIGNAL.
        int w=chart.getWidth(), h=chart.getHeight();
        int x0=(int)(w*0.10), x1=(int)(w*0.90);
        int y0=(int)(h*0.15), y1=(int)(h*0.85);
        int up=0, down=0, samples=0;

        for(int y=y0; y<y1; y+=Math.max(2,h/120)) {
            for(int x=x0; x<x1; x+=Math.max(2,w/160)) {
                int c=chart.getPixel(x,y);
                int r=Color.red(c), g=Color.green(c), b=Color.blue(c);
                // Common chart conventions: green/teal bullish, red bearish.
                if(g > r*1.18 && g > b*1.05 && g > 90) up++;
                if(r > g*1.18 && r > b*1.05 && r > 90) down++;
                samples++;
            }
        }

        double upRatio = samples==0 ? 0 : (double)up/samples;
        double downRatio = samples==0 ? 0 : (double)down/samples;
        String tf = duration.getSelectedItem().toString();

        if (Math.abs(upRatio-downRatio) < 0.008) {
            result.setText("NO SIGNAL");
            details.setText("الإشارة ضعيفة على هذا Screenshot ("+tf+").");
        } else if (upRatio > downRatio * 1.35) {
            result.setText("UP");
            details.setText("ميل صاعد ظاهر في ألوان الشموع. مدة التحليل: "+tf+
                    ". استخدمها كإشارة مساعدة فقط.");
        } else if (downRatio > upRatio * 1.35) {
            result.setText("DOWN");
            details.setText("ميل هابط ظاهر في ألوان الشموع. مدة التحليل: "+tf+
                    ". استخدمها كإشارة مساعدة فقط.");
        } else {
            result.setText("NO SIGNAL");
            details.setText("لا توجد أفضلية واضحة. لا تدخل الصفقة.");
        }
    }
}
