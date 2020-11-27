package com.zzw.hiltdemo;

/**
 * @author Created by lenna on 2020/11/12
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;



public abstract class AbsSafeWebView extends WebView {

    private HashMap viewCache;

    @Nullable
    private Context mContext;

    @Nullable
    private String netUrl;


    public AbsSafeWebView(@NotNull Context paramContext) {
        super(paramContext);
        initNecessaryParams(paramContext);
    }

    public AbsSafeWebView(@NotNull Context paramContext, @Nullable AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initNecessaryParams(paramContext);
    }

    public AbsSafeWebView(@NotNull Context paramContext, @NotNull AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initNecessaryParams(paramContext);
    }

    private String harukiUA() {
        if (TextUtils.isEmpty(this.netUrl)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" haruki/");
            try {
                stringBuilder.append( getContext().getPackageManager().getPackageInfo("", (int) 1f).versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            this.netUrl = stringBuilder.toString();
        }
        return this.netUrl;
    }

    public void clearFindViewByIdCache() {
        if (viewCache != null)
            viewCache.clear();
    }

    public View findCachedViewById(int paramInt) {
        if (viewCache == null)
            viewCache = new HashMap<Integer, View>();
        View view2 = (View) viewCache.get(paramInt);
        View view1 = view2;
        if (view2 == null) {
            view1 = findViewById(paramInt);
            viewCache.put(paramInt, view1);
        }
        return view1;
    }

    @Nullable
    public final Context getMContext() {
        return mContext;
    }

    @Nullable
    public final String getNetUrl() {
        return netUrl;
    }

    public void handleWithWebViewUA() {
        WebSettings webSettings1 = getSettings();

        StringBuilder stringBuilder = new StringBuilder();
        WebSettings webSettings2 = getSettings();
        if (webSettings2 != null) {
            stringBuilder.append(webSettings2.getUserAgentString());
        }
        stringBuilder.append(harukiUA());
        if (webSettings1 != null) {
            webSettings1.setUserAgentString(stringBuilder.toString());
        }
    }

    public abstract void initExtraWebViewParams(@NotNull WebSettings paramWebSettings);

    public final void initNecessaryParams(@NotNull Context paramContext) {
        this.mContext = paramContext;
        if (!isInEditMode())
            initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected final void initWebView() {
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
        if (Build.VERSION.SDK_INT >= 16) {
            WebSettings webSettings1 = getSettings();
            if (webSettings1 != null) {
                webSettings1.setAllowFileAccessFromFileURLs(false);
            }
            webSettings1 = getSettings();
            if (webSettings1 != null) {
                webSettings1.setAllowUniversalAccessFromFileURLs(false);
            }
        }
        WebSettings webSettings = getSettings();

        if (webSettings != null) {
            webSettings.setSavePassword(false);
        }
        webSettings = getSettings();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(true);

        }
        setWebContentDebuggable();
        handleWithWebViewUA();
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings = getSettings();
            if (webSettings != null) {
                webSettings.setMixedContentMode(0);
            }
        }
        webSettings = getSettings();
        if (webSettings != null) {
            initExtraWebViewParams(webSettings);
        }
    }

    @Override
    public void loadUrl(@NotNull String paramString) {
        super.loadUrl(paramString);
    }

    @Override
    public void loadUrl(@NotNull String paramString, @NotNull Map<String, String> paramMap) {
        super.loadUrl(paramString, paramMap);
    }

    public final void setMContext(@Nullable Context paramContext) {
        this.mContext = paramContext;
    }

    public final void setNetUrl(@Nullable String paramString) {
        this.netUrl = paramString;
    }

    public void setWebChromeClient(@Nullable WebChromeClient paramWebChromeClient) {
        if (paramWebChromeClient instanceof HarukiWebChromeClient) {
            super.setWebChromeClient(paramWebChromeClient);
            return;
        }
        throw new IllegalArgumentException("webChromeClient is not instanceof HarukiWebChromeClient");
    }

    public final void setWebClient(@NotNull WebViewClient paramWebViewClient) {
        if (paramWebViewClient instanceof HarukiWebViewClient) {
            setWebViewClient(paramWebViewClient);
            return;
        }
        throw new IllegalArgumentException("webViewClient is not instanceof HarukiWebViewClient");
    }

    protected final void setWebContentDebuggable() {
        if (Build.VERSION.SDK_INT >= 19 &&BuildConfig.DEBUG)
            WebView.setWebContentsDebuggingEnabled(true);
    }

    public static final class a {
        private a() {
        }
    }
}
