package com.onetap.fullbrowser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    private LinearLayout rootLayout;
    private LinearLayout topBar;
    private LinearLayout bottomBar;
    private EditText urlInput;
    private WebView webView;
    private Button fullscreenButton;
    private boolean isFullscreen = false;

    // dp 값을 실제 픽셀 값으로 변환합니다.
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 전체 화면을 세로 방향으로 쌓는 루트 레이아웃입니다.
        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.WHITE);

        // 최상단 URL 입력바입니다.
        topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setPadding(dp(6), dp(5), dp(6), dp(5));
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        rootLayout.addView(topBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
        ));

        urlInput = new EditText(this);
        urlInput.setSingleLine(true);
        urlInput.setText("https://google.com");
        urlInput.setTextSize(15);
        urlInput.setSelectAllOnFocus(false);
        topBar.addView(urlInput, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
        ));

        // 실제 안드로이드 시스템 WebView입니다. iframe이 아닙니다.
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            // 앱 밖 브라우저로 튀지 않고, 앱 내부 WebView에서 계속 열리게 합니다.
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        rootLayout.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        // 하단 버튼바입니다.
        bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setPadding(dp(6), dp(6), dp(6), dp(6));
        rootLayout.addView(bottomBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(60)
        ));

        Button goButton = new Button(this);
        goButton.setText("이동");
        goButton.setTextSize(15);
        goButton.setOnClickListener(v -> loadInputUrl());
        bottomBar.addView(goButton, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.28f
        ));

        fullscreenButton = new Button(this);
        fullscreenButton.setText("전체화면 ON");
        fullscreenButton.setTextSize(17);
        fullscreenButton.setTextColor(Color.WHITE);
        fullscreenButton.setBackgroundColor(Color.rgb(33, 150, 243));
        fullscreenButton.setOnClickListener(v -> toggleFullscreen());
        bottomBar.addView(fullscreenButton, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.72f
        ));

        setContentView(rootLayout);
        webView.loadUrl("https://google.com");
    }

    // 입력한 주소로 이동합니다. http/https가 없으면 https를 자동으로 붙입니다.
    private void loadInputUrl() {
        String url = urlInput.getText().toString().trim();
        if (url.length() == 0) return;

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
            urlInput.setText(url);
        }
        webView.loadUrl(url);
    }

    // 전체화면 모드를 토글합니다.
    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    // 상태표시줄, 내비게이션 바, URL바, 버튼바를 숨겨 웹페이지만 100% 보이게 합니다.
    private void enterFullscreen() {
        isFullscreen = true;
        topBar.setVisibility(View.GONE);
        bottomBar.setVisibility(View.GONE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    // 원래 UI로 복귀합니다.
    private void exitFullscreen() {
        isFullscreen = false;
        topBar.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.VISIBLE);
        fullscreenButton.setText("전체화면 ON");
        fullscreenButton.setBackgroundColor(Color.rgb(33, 150, 243));

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    // 몰입형 전체화면은 포커스를 다시 얻을 때 풀릴 수 있어서 재적용합니다.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    // 전체화면 중에는 뒤로가기로 복귀, 일반 상태에서는 웹 뒤로가기, 더 이상 없으면 앱 종료입니다.
    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            exitFullscreen();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
