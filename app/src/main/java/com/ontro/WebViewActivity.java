package com.ontro;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import static com.ontro.utils.CommonUtils.preferenceHelper;

public class WebViewActivity extends AppCompatActivity {
    private WebView mWebView;
    private Dialog mProgressDialog;
    private MixpanelAPI mMixpanel;
    private int mTournamentId;
    private String mTournamentName;
    private PreferenceHelper mPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        mMixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanel_token));
        mPreferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        mWebView = (WebView) findViewById(R.id.activity_web_view_wv);
        mProgressDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        mProgressDialog.setCancelable(false);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mTournamentId = bundle.getInt(Constants.BundleKeys.TOURNAMENT_ID);
            int formId = bundle.getInt(Constants.BundleKeys.FORM_ID);
            mTournamentName = bundle.getString(Constants.BundleKeys.TOURNAMENT_NAME);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
            mWebView.setWebViewClient(new MyWebViewClient());
            PreferenceHelper preferenceHelper = new PreferenceHelper(WebViewActivity.this, Constants.APP_NAME, 0);
            String userId = preferenceHelper.getString("user_id", "");
            mProgressDialog.show();
            mWebView.loadUrl("https://ideomind.io/demo/ontro/tournament/join/" + mTournamentId + "/" + formId + "/" + userId);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    String successfulUrl = new String("https://ideomind.io/demo/ontro/tournament/paymentSuccess");
                    String failureUrl = new String("https://ideomind.io/demo/ontro/tournament/paymentFailure");

                    String webUrl = new String(mWebView.getUrl());
                    if (successfulUrl.equals(webUrl)) {
                        Log.d("Payment Status", "Payment Successful");
                    } else if(failureUrl.equals(webUrl)) {
                        Log.d("Payment Status", "Payment Failure");
                    } else {
                        if (mWebView.canGoBack()) {
                            mWebView.goBack();
                        } else {
                            // Let the system handle the back button
                            super.onBackPressed();
                        }
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private class MyWebViewClient extends WebViewClient {
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
            // Based on some condition you need to determine if you are going to load the url
            // in your web view itself or in a browser.
            // You can use `host` or `scheme` or any part of the `uri` to decide.
            if (host.equals("ideomind.io") || host.equals("test.payu.in")) {
                // Returning false means that you are going to load this url in the webView itself
                return false;
            } else {
                // Returning true means that you need to handle what to do with the url
                // e.g. open web page in a Browser
                final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            mProgressDialog.dismiss();
        }
    }

    private class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            if (toast.equals(Constants.Messages.PAYMENT_SUCCESSFUL)) {
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("TournamentId", mTournamentId );
                    eventJsonObject.put("UserName",  mPreferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("UserEmail", mPreferenceHelper.getString("user_email", ""));
                    eventJsonObject.put("TournamentName", mTournamentName);
                    mMixpanel.track("TournamentRegistration", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
            }
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void goBack() {
            finish();
        }
    }

}
