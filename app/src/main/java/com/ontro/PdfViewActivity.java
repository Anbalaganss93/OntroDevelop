package com.ontro;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PdfViewActivity extends AppCompatActivity {
    private WebView mPdfView;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        mPdfView = (WebView) findViewById(R.id.activity_pdf_view_wv);
        mProgressDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        mProgressDialog.setCancelable(false);

        mPdfView.getSettings().setJavaScriptEnabled(true);
        mPdfView.setWebViewClient(new PdfViewClient());
        mPdfView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mPdfView.getSettings().setAllowFileAccess(true);
        String pdf_url = "http://ideomind.in/demo/ontro/public/uploads/tournament/1501163906ideomind_in_demo_ontro_admin_tournament_fixtures_18_1(2).pdf";
        mPdfView.loadUrl("https://docs.google.com/viewer?url=" + pdf_url);
    }

    private class PdfViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Uri uri = Uri.parse(url);
            return handleUri(uri);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            return handleUri(uri);
        }

        private boolean handleUri(final Uri uri) {
            final String host = uri.getHost();
            final String scheme = uri.getScheme();
            return false;
        }
    }
}
