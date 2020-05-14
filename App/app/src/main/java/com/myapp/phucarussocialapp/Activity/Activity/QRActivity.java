package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.zxing.Result;
import com.myapp.phucarussocialapp.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView = new ZXingScannerView(QRActivity.this);
        setContentView(zXingScannerView);
    }

    @Override
    public void handleResult(Result result) {
        String url = result.getText().trim();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onResume() {
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
        super.onResume();
    }

    @Override
    protected void onPause() {
        zXingScannerView.stopCamera();
        super.onPause();
    }
}
