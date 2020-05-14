package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phucarussocialapp.R;

public class ReadNewsActivity extends AppCompatActivity {

    WebView webView;
    ImageView back;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news);
        back = (ImageView) findViewById(R.id.r_news_back);
        name = (TextView)findViewById(R.id.r_news_type);
        webView = (WebView)findViewById(R.id.wv);
        webView.getSettings().setJavaScriptEnabled(true);

        String type = getIntent().getStringExtra("type");
        if(type.equals("k14") == true){
            webView.loadUrl("https://kenh14.vn/");
            name.setText("Kênh 14");
        }else if(type.equals("baomoi") == true){
            webView.loadUrl("https://baomoi.com/");
            name.setText("Baomoi");
        }else if(type.equals("vnexpress") == true){
            webView.loadUrl("http://express.vn/");
            name.setText("VN EXPRESS");
        }else if(type.equals("tuoitre") == true){
            webView.loadUrl("https://tuoitre.vn/");
            name.setText("Tuổi Trẻ");
        }else {
            webView.loadUrl("https://zingnews.vn/");
            name.setText("Zing News");
        }

        webView.setWebViewClient(new Client());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.canGoBack()) {
                    webView.goBack();
                }else finish();
            }
        });

    }

    private class Client extends WebViewClient{
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else super.onBackPressed();
    }
}
