 package com.example.penpapercoder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;

 public class ViewPDFActivity extends AppCompatActivity {
    PDFView pdfView;
    String  path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_p_d_f);
        pdfView =findViewById(R.id.pdfView);
        path= getIntent().getExtras().getString("path");
        File file = new File(path);

        pdfView.fromFile(file)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(false)
                .defaultPage(0)
                .password(null)
                .scrollHandle(null)
                .load();

    }
}