package com.zzw.hiltdemo;

/**
 * @author Created by lenna on 2020/11/12
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorV19WebView extends AbsSafeWebView {
    private static final boolean DEBUG = false;

    public static final long SHOW_EDITOR_DELAY = 300L;

    public static final String UPLOAD_IMAGE_HOST = "upload-images.jianshu.io";

    public static final String UPLOAD_IMAGE_HOST_TEST = "sg-upload-images.jianshu.io";

    private final String TAG = EditorV19WebView.class.getSimpleName();

    private String articleContent = "";

    private String articleTitle = "";

    private int articleWordage = 0;

    private Context context;

    private String currentElementID = "";

    private EditorDelegate delegate;

    private String[] failedImages;

    private Runnable fireOnEditorReady = new Runnable() {
        public void run() {
            onEditorReady();
        }
    };

    private Runnable fireOnGetContent = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onGetContent(articleContent);
        }
    };

    private Runnable fireOnGetImageCounts = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onGetImagesCount(uploadedImageCounts);
        }
    };

    private Runnable fireOnGetImageStatus = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onGetImageStatus(loadedImages, loadingImages, failedImages);
        }
    };

    private Runnable fireOnGetSelectedText = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onGetSelectionText(selectedText);
        }
    };

    private Runnable fireOnGetSelectionInfo = new Runnable() {
        public void run() {
            onEditorGetSelectionInfo();
        }
    };

    private Runnable fireOnGetSelectionStyles = new Runnable() {
        public void run() {
            onEditorGetSelectionStyles();
        }
    };

    private Runnable fireOnGetTitle = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onGetTitle(articleTitle);
        }
    };

    private Runnable fireOnGetWordage = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onGetWordage(articleWordage);
        }
    };

    private Runnable fireOnInput = new Runnable() {
        public void run() {
            onInput();
        }
    };

    private Runnable fireOnLog = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onLog(mLastLog);
        }
    };

    private Runnable fireOnPaste = new Runnable() {
        public void run() {
            onPaste();
        }
    };

    private Runnable fireOnTap = new Runnable() {
        public void run() {
            onTap();
        }
    };

    private Runnable fireOnTapImage = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onTapImage(mLastTapImageId, mLastTapImageUrl);
        }
    };

    private Runnable fireOnTapLink = new Runnable() {
        public void run() {
            if (delegate != null)
                delegate.onTapLink(mLastTapLinkUrl, mLastTapLinkName);
        }
    };

    private List<String> hasLogedOnUrls;

    private boolean hasSelection = false;

    private boolean isEditorReady = false;

    private boolean isMarkdown = false;

    private boolean isNightMode = false;

    private boolean isPreview = false;

    private boolean isUsingActiveMonitor = false;

    private int lineHeight = 0;

    private String[] loadedImages;

    private String[] loadingImages;

    private String mLastLog;

    private String mLastTapImageId;

    private String mLastTapImageUrl;

    private String mLastTapLinkName;

    private String mLastTapLinkUrl;

    private WebSettings mSetting;

    private String selectedText;

    private int uploadedImageCounts;

    private int yOffset = 0;

    public EditorV19WebView(Context paramContext) {
        super(paramContext);
    }

    public EditorV19WebView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public EditorV19WebView(@NotNull Context paramContext1, @Nullable AttributeSet paramAttributeSet, int paramInt, Context paramContext2) {
        super(paramContext1, paramAttributeSet, paramInt);
    }

    @TargetApi(19)
    private void callEvaluateJavascript(String paramString, ValueCallback<String> paramValueCallback) {
        try {
            return;
        } finally {
            paramString = null;
        }
    }

    private void callJS(String paramString) {
        callEvaluateJavascript(paramString, null);
    }

    private void callJS(String paramString, ValueCallback<String> paramValueCallback) {
        callEvaluateJavascript(paramString, paramValueCallback);
    }

    private void callScript(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ZSSEditor.");
        stringBuilder.append(paramString);
        stringBuilder.append("();");
        callEvaluateJavascript(stringBuilder.toString(), null);
    }

    private void callScript(String paramString1, String paramString2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ZSSEditor.");
        stringBuilder.append(paramString1);
        stringBuilder.append("(");
        stringBuilder.append(paramString2);
        stringBuilder.append(");");
        callEvaluateJavascript(stringBuilder.toString(), null);
    }

    private void logHttpError(String paramString1, String paramString2) {
        if (!TextUtils.isEmpty(paramString1)) {
            if (this.hasLogedOnUrls == null) {
                ArrayList<String> arrayList = new ArrayList();
                this.hasLogedOnUrls = arrayList;
                arrayList.add(paramString1);
            }
            if (!this.hasLogedOnUrls.contains(paramString1)) {
                this.hasLogedOnUrls.add(paramString1);
            }
        }
    }

    private void onEditorGetSelectionInfo() {
        EditorDelegate editorDelegate = this.delegate;
        if (editorDelegate != null)
            editorDelegate.onGetSelectionInfo(this.currentElementID, this.yOffset, this.lineHeight, this.hasSelection);
    }

    private void onEditorGetSelectionStyles() {
        EditorDelegate editorDelegate = this.delegate;
        if (editorDelegate != null)
            editorDelegate.onGetSelectionStyles(CurrentStyles.toStyle());
    }

    private void onEditorReady() {
        this.isEditorReady = true;
        EditorDelegate editorDelegate = this.delegate;
        if (editorDelegate != null)
            editorDelegate.onInit();
        postDelayed(new Runnable() {
            public void run() {
                setVisibility(View.VISIBLE);
            }
        }, 300L);
    }

    private void onInput() {
        EditorDelegate editorDelegate = this.delegate;
        if (editorDelegate != null)
            editorDelegate.onInput();
    }

    private void onPaste() {
        String str1;
        ClipData clipData = ((ClipboardManager) this.context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
        final String finalContent = "";
        if (clipData != null && clipData.getItemAt(0) != null && clipData.getItemAt(0).getText() != null) {
            str1 = clipData.getItemAt(0).getText().toString();
        } else {
            str1 = "";
        }
        String str2 = "";
        if (!TextUtils.isEmpty(str1))
            str2 = regularizeString(str1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Maleskine.getPaste(\"");
        stringBuilder.append(str2);
        stringBuilder.append("\");");
        callJS(stringBuilder.toString(), new ValueCallback<String>() {
            public void onReceiveValue(String param1String) {
                if ("null".equalsIgnoreCase(param1String)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("getPaste onReceive ");
                    stringBuilder.append(param1String);
                    stringBuilder.append(" finalContent ");
                    stringBuilder.append(finalContent);
                    Log.i("OnPaste", stringBuilder.toString());
                }
            }
        });
    }

    private void onTap() {
        EditorDelegate editorDelegate = this.delegate;
        if (editorDelegate != null)
            editorDelegate.onTap();
    }

    private static String regularizeString(String paramString) {
        if (TextUtils.isEmpty(paramString))
            return "";
        paramString = paramString.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
        Pattern pattern2 = Pattern.compile("\\s");
        Pattern pattern1 = Pattern.compile("[ \\n\\t\\r]");
        Matcher matcher = pattern2.matcher(paramString);
        while (matcher.find()) {
            String str = matcher.group();
            if (!pattern1.matcher(str).find())
                paramString = paramString.replaceAll(str, "");
        }
        return paramString;
    }

    private static String regularizeStringPassEmptyChars(String paramString) {
        return TextUtils.isEmpty(paramString) ? "" : paramString.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
    }

    public void callCancelRemoveCurrentImage() {
        callScript("cancelRemoveCurrentImage");
    }

    public void callGetArticleContent() {
        callJS("Maleskine.getContent(true);");
    }

    public void callGetArticleTitle() {
        callJS("Maleskine.getTitle();");
    }

    public void callGetCurrentImageStatus() {
        callJS("Maleskine.getImageStates();");
    }

    public void callGetImagesCount() {
        callJS("ZSSEditor.getImagesCount();");
    }

    public void callGetSelectText() {
        callScript("getSelectedText");
    }

    public void callGetWordage() {
        callJS("Maleskine.getWordage();");
    }

    public void callInsertHtml(String paramString) {
        callScript("insertHTML", paramString);
    }

    public void callInsertImage(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2) {
        String str = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString1));
        stringBuilder.append("\",\"");
        stringBuilder.append(regularizeString(paramString2));
        stringBuilder.append("\"");
        paramString2 = stringBuilder.toString();
        paramString1 = paramString2;
        if (paramString3 != null) {
            paramString1 = paramString2;
            if (paramString3.length() > 0) {
                StringBuilder stringBuilder1 = new StringBuilder();
                stringBuilder1.append(paramString2);
                stringBuilder1.append(",\"");
                stringBuilder1.append(regularizeString(paramString3));
                stringBuilder1.append("\"");
                str = stringBuilder1.toString();
                if (paramString4 != null && paramString4.length() > 0) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(",\"");
                    stringBuilder2.append(regularizeString(paramString4));
                    stringBuilder2.append("\"");
                    String str1 = stringBuilder2.toString();
                    str = str1;
                    if (paramInt1 > 0) {
                        str = str1;
                        if (paramInt2 > 0) {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(str1);
                            stringBuilder3.append(",");
                            stringBuilder3.append(paramInt1);
                            stringBuilder3.append(",");
                            stringBuilder3.append(paramInt2);
                            str = stringBuilder3.toString();
                        }
                    }
                }
            }
        }
        callScript("insertImage", str);
    }

    public void callInsertLink(String paramString1, String paramString2, boolean paramBoolean) {
        String str = paramString2;
        if (paramString2.length() == 0)
            str = paramString1;
        if (paramString1.length() == 0) {
            callScript("unlink");
            return;
        }
        if (paramBoolean) {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append("\"");
            stringBuilder1.append(regularizeString(paramString1));
            stringBuilder1.append("\",\"");
            stringBuilder1.append(regularizeString(str));
            stringBuilder1.append("\"");
            callScript("updateLink", stringBuilder1.toString());
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString1));
        stringBuilder.append("\",\"");
        stringBuilder.append(regularizeString(str));
        stringBuilder.append("\"");
        callScript("insertLink", stringBuilder.toString());
    }

    public void callInsertQuickLink() {
        callScript("quickLink");
    }

    public void callInsertRuleLine() {
        callScript("setHorizontalRule");
    }

    public void callInsertUploadingImagePlaceholder(String paramString1, String paramString2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString1));
        stringBuilder.append("\",\"");
        stringBuilder.append(regularizeString(paramString2));
        stringBuilder.append("\"");
        callScript("insertLocalImage", stringBuilder.toString());
    }

    public void callInsertUploadingImagePlaceholders(String paramString1, String paramString2) {
        if (paramString1 != null && paramString1.length() > 0 && paramString2 != null && paramString2.length() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\"");
            stringBuilder.append(regularizeString(paramString1));
            stringBuilder.append("\",\"");
            stringBuilder.append(regularizeString(paramString2));
            stringBuilder.append("\"");
            callScript("insertLocalImages", stringBuilder.toString());
        }
    }

    public void callMarkImageUploadFailed(String paramString1, String paramString2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString1));
        stringBuilder.append("\",\"");
        stringBuilder.append(regularizeString(paramString2));
        stringBuilder.append("\"");
        callScript("markImageUploadFailed", stringBuilder.toString());
    }

    public void callRedo() {
        callScript("redo");
    }

    public void callRemoveCurrentImage() {
        callScript("removeCurrentImage");
    }

    public void callRemoveImageWithID(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString));
        stringBuilder.append("\"");
        callScript("removeImage", stringBuilder.toString());
    }

    public void callRemoveParagraphFormat() {
        callScript("removeFormating");
    }

    public void callSetBackgroundColor(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString));
        stringBuilder.append("\"");
        callScript("setBackgroundColor", stringBuilder.toString());
    }

    public void callSetBlockquote() {
        callScript("setBlockquote");
    }

    public void callSetFontBold() {
        callScript("setBold");
    }

    public void callSetFontItalic() {
        callScript("setItalic");
    }

    public void callSetFontStrikethrough() {
        callScript("setStrikeThrough");
    }

    public void callSetFontSubscript() {
        callScript("setSubscript");
    }

    public void callSetFontSuperscript() {
        callScript("setSuperscript");
    }

    public void callSetFontUnderline() {
        callScript("setUnderline");
    }

    public void callSetHeader(int paramInt) {
        int i;
        if (paramInt < 1) {
            i = 1;
        } else {
            i = paramInt;
            if (paramInt > 6)
                i = 6;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"h");
        stringBuilder.append(i);
        stringBuilder.append("\"");
        callScript("setHeading", stringBuilder.toString());
    }

    public void callSetIndent() {
        callScript("setIndent");
    }

    public void callSetJustifyCenter() {
        callScript("setJustifyCenter");
    }

    public void callSetJustifyFull() {
        callScript("setJustifyFull");
    }

    public void callSetJustifyLeft() {
        callScript("setJustifyLeft");
    }

    public void callSetJustifyRight() {
        callScript("setJustifyRight");
    }

    public void callSetNormalParagraph() {
        callScript("setParagraph");
    }

    public void callSetOrderedList() {
        callScript("setOrderedList");
    }

    public void callSetOutdent() {
        callScript("setOutdent");
    }

    public void callSetTextColor(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString));
        stringBuilder.append("\"");
        callScript("setTextColor", stringBuilder.toString());
    }

    public void callSetUnorderedList() {
        callScript("setUnorderedList");
    }

    public void callSetUploadingProgressOfImage(String paramString, int paramInt) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString));
        stringBuilder.append("\",\"");
        stringBuilder.append(paramInt);
        stringBuilder.append("\"");
        callScript("setProgressOnImage", stringBuilder.toString());
    }

    public void callUndo() {
        callScript("undo");
    }

    public void callUnmarkImageUploadFailed(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString));
        stringBuilder.append("\"");
        callScript("unmarkImageUploadFailed", stringBuilder.toString());
    }

    public void callUpdateUploadingImagePlaceholder(String paramString1, String paramString2, String paramString3, String paramString4) {
        String str = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(regularizeString(paramString1));
        stringBuilder.append("\",\"");
        stringBuilder.append(regularizeString(paramString2));
        stringBuilder.append("\"");
        paramString2 = stringBuilder.toString();
        paramString1 = paramString2;
        if (paramString3 != null) {
            paramString1 = paramString2;
            if (paramString3.length() > 0) {
                StringBuilder stringBuilder1 = new StringBuilder();
                stringBuilder1.append(paramString2);
                stringBuilder1.append(",\"");
                stringBuilder1.append(regularizeString(paramString3));
                stringBuilder1.append("\"");
                str = stringBuilder1.toString();
                if (paramString4 != null && paramString4.length() > 0) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(",\"");
                    stringBuilder2.append(regularizeString(paramString4));
                    stringBuilder2.append("\"");
                    str = stringBuilder2.toString();
                }
            }
        }
        callScript("replaceLocalImageWithRemoteImage", str);
    }

    public boolean didHasSelection() {
        return this.hasSelection;
    }

    public void doListenBlur(boolean paramBoolean) {
        EditorSettings.listenBlur = paramBoolean;
    }

    public void doListenFocus(boolean paramBoolean) {
        EditorSettings.listenFocus = paramBoolean;
    }

    public void doListenInput(boolean paramBoolean) {
        EditorSettings.listenInput = paramBoolean;
    }

    public void doListenKeyDown(boolean paramBoolean) {
        EditorSettings.listenKeyDown = paramBoolean;
    }

    public void doListenKeyUp(boolean paramBoolean) {
        EditorSettings.listenKeyUp = (paramBoolean);
    }

    public void doListenLog(boolean paramBoolean) {
        EditorSettings.listenLog = (paramBoolean);
    }

    public void doListenSelectionChanged(boolean paramBoolean) {
        EditorSettings.listenSelectionChanged = (paramBoolean);
    }

    public void doListenTap(boolean paramBoolean) {
        EditorSettings.listenTap = (paramBoolean);
    }

    public void focusOnContent() {
        callJS("showKeyboard();");
        requestFocus();
        postDelayed(new Runnable() {
            public void run() {
                showKeyboard();
            }
        }, 100L);
    }

    public void focusOnTitle() {
        callJS("showKeyboardInTitle();");
        requestFocus();
        postDelayed(new Runnable() {
            public void run() {
                showKeyboard();
            }
        }, 100L);
    }

    public String getCurrentElementID() {
        return this.currentElementID;
    }

    public int getLineHeight() {
        return this.lineHeight;
    }

    public String getSelectedText() {
        return this.selectedText;
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public void hideKeyboard() {
        try {
            ((InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindowToken(), 0);
            return;
        } catch (Exception exception) {
            return;
        }
    }

    public String imageCaption() {
        return CurrentStyles.imageCaption;
    }

    public String imageID() {
        return CurrentStyles.imageID;
    }

    public int imageStatus() {
        return CurrentStyles.imageStatus;
    }

    public String imageURL() {
        return CurrentStyles.imageURL;
    }

    public void init(Context paramContext) {
        init(paramContext, null);
    }

    public void init(Context paramContext, EditorDelegate paramEditorDelegate) {
        Boolean bool = Boolean.valueOf(false);
        init(paramContext, paramEditorDelegate, bool, bool, bool);
    }

    @TargetApi(19)
    public void init(Context paramContext, EditorDelegate paramEditorDelegate, Boolean paramBoolean1, Boolean paramBoolean2, Boolean paramBoolean3) {
        this.context = paramContext;
        this.delegate = paramEditorDelegate;
        this.isNightMode = paramBoolean1.booleanValue();
        this.isMarkdown = paramBoolean2.booleanValue();
        this.isPreview = paramBoolean3.booleanValue();
        String str1 = getSettings().getUserAgentString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str1);
        stringBuilder.append(" HighLevel");
        String str2 = stringBuilder.toString();
        str1 = str2;
        if (paramBoolean1.booleanValue()) {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(str2);
            stringBuilder1.append(" NightMode");
            str1 = stringBuilder1.toString();
        }
        getSettings().setUserAgentString(str1);
        loadUrl("file:///android_asset/editor_v19/editorv19.html");
    }

    @SuppressLint({"AddJavascriptInterface", "JavascriptInterface"})
    public void initExtraWebViewParams(@NotNull WebSettings paramWebSettings) {
        paramWebSettings.setAllowContentAccess(true);
        paramWebSettings.setAllowFileAccess(true);
        paramWebSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= 16)
            paramWebSettings.setAllowUniversalAccessFromFileURLs(true);
        paramWebSettings.setBlockNetworkImage(false);
        paramWebSettings.setLoadsImagesAutomatically(true);
        paramWebSettings.setDefaultTextEncodingName("UTF-8");
        paramWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        addJavascriptInterface(new EditorWebViewJSBridge(), "Android");
        setHorizontalScrollBarEnabled(false);
        setWebViewClient((WebViewClient) new HarukiWebViewClient() {
            private WebResourceResponse getInterceptResponse(String param1String) {
                if (TextUtils.isEmpty(param1String))
                    return null;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("__");
                stringBuilder.append(param1String);
                Log.i("shouldInterceptRequest", stringBuilder.toString());
                try {
                    Uri uri = Uri.parse(param1String);
                    String str1 = uri.getScheme();
                    String str2 = uri.getHost();
                    if (!"https".equals(str1) && ("upload-images.jianshu.io".equals(str2) || "sg-upload-images.jianshu.io".equals(str2))) {
                        Uri.Builder builder = uri.buildUpon().scheme("https");
                        URL uRL = new URL(builder.toString());
                        StringBuilder stringBuilder1 = new StringBuilder();
                        stringBuilder1.append("___");
                        stringBuilder1.append(builder.toString());
                        Log.i("should_url", stringBuilder1.toString());
                        return new WebResourceResponse("text/html", "UTF-8", ((HttpURLConnection) uRL.openConnection()).getInputStream());
                    }
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
                return null;
            }

            public void onPageFinished(WebView param1WebView, String param1String) {
                super.onPageFinished(param1WebView, param1String);
                if (isEditorReady)
                    return;
                StringBuilder stringBuilder = new StringBuilder();
                if (EditorV19WebView.EditorSettings.listenLog) {
                    stringBuilder.append("ZSSEditor.eventListeners.log=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.log=false;");
                }
                if (EditorV19WebView.EditorSettings.listenSelectionChanged) {
                    stringBuilder.append("ZSSEditor.eventListeners.selectionChanged=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.selectionChanged=false;");
                }
                if (EditorV19WebView.EditorSettings.listenInput) {
                    stringBuilder.append("ZSSEditor.eventListeners.input=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.input=false;");
                }
                if (EditorV19WebView.EditorSettings.listenKeyDown) {
                    stringBuilder.append("ZSSEditor.eventListeners.keydown=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.keydown=false;");
                }
                if (EditorV19WebView.EditorSettings.listenKeyUp) {
                    stringBuilder.append("ZSSEditor.eventListeners.keyup=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.keyup=false;");
                }
                if (EditorV19WebView.EditorSettings.listenTap) {
                    stringBuilder.append("ZSSEditor.eventListeners.tap=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.tap=false;");
                }
                if (EditorV19WebView.EditorSettings.listenFocus) {
                    stringBuilder.append("ZSSEditor.eventListeners.focus=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.focus=false;");
                }
                if (EditorV19WebView.EditorSettings.listenBlur) {
                    stringBuilder.append("ZSSEditor.eventListeners.blur=true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.blur=false;");
                }
                if (EditorV19WebView.EditorSettings.sendWordage) {
                    stringBuilder.append("ZSSEditor.eventListeners.sendWordage = true;");
                } else {
                    stringBuilder.append("ZSSEditor.eventListeners.sendWordage = false;");
                }
                if (isNightMode) {
                    stringBuilder.append("Maleskine.setEditorAsNightMode();");
                } else {
                    stringBuilder.append("Maleskine.setEditorAsDayMode();");
                }
                if (EditorV19WebView.EditorSettings.listenLog)
                    stringBuilder.append("AndroidDelegate.log=function(msg){Android.log(msg);};");
                stringBuilder.append("AndroidDelegate.init=function(){Android.init();};");
                stringBuilder.append("AndroidDelegate.getTitle=function(title){Android.getTitle(title);};");
                stringBuilder.append("AndroidDelegate.getContent=function(content){Android.getContent(content);};");
                stringBuilder.append("AndroidDelegate.onGetImagesCount=function(imagesCount){Android.onGetImagesCount(imagesCount);};");
                stringBuilder.append("AndroidDelegate.onGetWordage=function(wordage){Android.getWordage(wordage);};");
                if (EditorV19WebView.EditorSettings.listenSelectionChanged)
                    stringBuilder.append("AndroidDelegate.onSelectionChanged=function(params){Android.onSelectionChanged(params);};");
                stringBuilder.append("AndroidDelegate.onSelectionStyles=function(params){Android.onSelectionStyles(params);};");
                if (EditorV19WebView.EditorSettings.listenInput)
                    stringBuilder.append("AndroidDelegate.onInput=function(){Android.onInput();};");
                if (EditorV19WebView.EditorSettings.listenTap)
                    stringBuilder.append("AndroidDelegate.onTap=function(){Android.onTap();};");
                if (EditorV19WebView.EditorSettings.listenSelectionChanged)
                    stringBuilder.append("AndroidDelegate.onTapImage=function(param){Android.onTapImage(param);};");
                stringBuilder.append("AndroidDelegate.onTapLink=function(param){Android.onTapLink(param);};");
                stringBuilder.append("AndroidDelegate.onPaste=function(){Android.onPaste();};");
                stringBuilder.append("AndroidDelegate.onGetImageStatus=function(loaded,loading,failed){Android.onGetImageStatus(loaded,loading,failed);};");
                stringBuilder.append("AndroidDelegate.showKeyboard=function(){Android.showKeyboard();};");
                stringBuilder.append("AndroidDelegate.hideKeyboard=function(){Android.hideKeyboard();};");
                stringBuilder.append("Maleskine.loadStatus.interface=true;Maleskine.loadStatus.check();");
                stringBuilder.append("AndroidDelegate.onGetSelectedText=function(param){Android.getSelectedText(param);};");
                callJS(stringBuilder.toString());
                if (delegate != null)
                    delegate.onPageLoaded(param1String);
                setVisibility(VISIBLE);
            }

            public void onReceivedHttpError(WebView param1WebView, WebResourceRequest param1WebResourceRequest, WebResourceResponse param1WebResourceResponse) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    EditorV19WebView editorV19WebView = EditorV19WebView.this;
                    String str = param1WebResourceRequest.getUrl().toString();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(param1WebResourceResponse.getStatusCode());
                    stringBuilder.append(",");
                    stringBuilder.append(param1WebResourceResponse.getReasonPhrase());
                    editorV19WebView.logHttpError(str, stringBuilder.toString());
                }
                super.onReceivedHttpError(param1WebView, param1WebResourceRequest, param1WebResourceResponse);
            }

            @TargetApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView param1WebView, WebResourceRequest param1WebResourceRequest) {
                WebResourceResponse webResourceResponse = getInterceptResponse(param1WebResourceRequest.getUrl().toString());
                return (webResourceResponse != null) ? webResourceResponse : super.shouldInterceptRequest(param1WebView, param1WebResourceRequest);
            }

            public WebResourceResponse shouldInterceptRequest(WebView param1WebView, String param1String) {
                WebResourceResponse webResourceResponse = getInterceptResponse(param1String);
                return (webResourceResponse != null) ? webResourceResponse : super.shouldInterceptRequest(param1WebView, param1String);
            }

            public boolean shouldOverrideUrlLoading(WebView param1WebView, WebResourceRequest param1WebResourceRequest) {
                if (isMarkdown) {
                    Log.i("shouldOUrlLoading_21", "____");
                    return true;
                }
                return super.shouldOverrideUrlLoading(param1WebView, param1WebResourceRequest);
            }

            public boolean shouldOverrideUrlLoading(WebView param1WebView, String param1String) {
                return isMarkdown ? true : super.shouldOverrideUrlLoading(param1WebView, param1String);
            }
        });
    }

    public boolean isBlockQuote() {
        return CurrentStyles.isBlockquote;
    }

    public boolean isBold() {
        return CurrentStyles.isBold;
    }

    public boolean isCodeQuote() {
        return (CurrentStyles.isPre || CurrentStyles.isCode);
    }

    public boolean isCurrentElementIdEqContent() {
        return (!TextUtils.isEmpty(this.currentElementID) && (this.currentElementID.equals("zss_field_content") || this.currentElementID.equals("zss_field_markdown")));
    }

    public boolean isCurrentElementIdEqTitle() {
        return (!TextUtils.isEmpty(this.currentElementID) && "zss_field_title".equals(this.currentElementID));
    }

    public boolean isHead1() {
        return CurrentStyles.isHead1;
    }

    public boolean isHead2() {
        return CurrentStyles.isHead2;
    }

    public boolean isHead3() {
        return CurrentStyles.isHead3;
    }

    public boolean isHead4() {
        return CurrentStyles.isHead4;
    }

    public boolean isHead5() {
        return CurrentStyles.isHead5;
    }

    public boolean isHead6() {
        return CurrentStyles.isHead6;
    }

    public boolean isImage() {
        return CurrentStyles.isImage;
    }

    public boolean isItalic() {
        return CurrentStyles.isItalic;
    }

    public boolean isLine() {
        return CurrentStyles.isLine;
    }

    public boolean isLink() {
        return CurrentStyles.isLink;
    }

    public boolean isMarkdownMode() {
        return this.isMarkdown;
    }

    public boolean isNightMode() {
        return this.isNightMode;
    }

    public boolean isNormal() {
        return ((CurrentStyles.isP || CurrentStyles.isDiv) && !CurrentStyles.isBlockquote && !CurrentStyles.isPre && !CurrentStyles.isCode && !CurrentStyles.isOrderedList && !CurrentStyles.isUnorderedList);
    }

    public boolean isOrderedList() {
        return CurrentStyles.isOrderedList;
    }

    public boolean isPreviewMode() {
        return this.isPreview;
    }

    public boolean isStrikethrough() {
        return CurrentStyles.isStrikethrough;
    }

    public boolean isSubscript() {
        return CurrentStyles.isSubscript;
    }

    public boolean isSupscript() {
        return CurrentStyles.isSupscript;
    }

    public boolean isUnderline() {
        return CurrentStyles.isUnderline;
    }

    public boolean isUnorderedList() {
        return CurrentStyles.isUnorderedList;
    }

    public boolean isUsingActiveSelectionMonitor() {
        return this.isUsingActiveMonitor;
    }

    public String linkTitle() {
        return CurrentStyles.linkTitle;
    }

    public String linkURL() {
        return CurrentStyles.linkURL;
    }

    public void pause() {
        if (this.isEditorReady)
            setVisibility(INVISIBLE);
        pauseTimers();
    }

    public void resume() {
        if (this.isEditorReady)
            setVisibility(VISIBLE);
        resumeTimers();
    }

    public void setArticleContent(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Maleskine.setContent(\"");
        stringBuilder.append(regularizeStringPassEmptyChars(paramString));
        stringBuilder.append("\");");
        callJS(stringBuilder.toString());
    }

    public void setArticleTitle(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Maleskine.setTitle(\"");
        stringBuilder.append(regularizeString(paramString));
        stringBuilder.append("\");");
        callJS(stringBuilder.toString());
    }

    public void setMarkdownMode(boolean paramBoolean) {
        this.isMarkdown = paramBoolean;
        this.isPreview = false;
        if (paramBoolean) {
            callJS("Maleskine.setMarkdownMode();");
            return;
        }
        callJS("Maleskine.setRichTextMode();");
    }

    public void setNightMode(boolean paramBoolean) {
        this.isNightMode = paramBoolean;
        if (paramBoolean) {
            callJS("Maleskine.setEditorAsNightMode();");
            return;
        }
        callJS("Maleskine.setEditorAsDayMode();");
    }

    public void setPreviewState(boolean paramBoolean) {
        this.isPreview = paramBoolean;
        if (paramBoolean) {
            callJS("Maleskine.enterPreviewMode();");
            return;
        }
        callJS("Maleskine.exitPreviewMode();");
    }

    public void showKeyboard() {
        try {
            requestFocus();
            ((InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput((View) this, 0);
            return;
        } catch (Exception exception) {
            return;
        }
    }

    public void turnActiveSelectionMonitorOff() {
        this.isUsingActiveMonitor = false;
        callJS("ZSSEditor.usingActiveMonitor=false;");
    }

    public void turnActiveSelectionMonitorOn() {
        this.isUsingActiveMonitor = true;
        callJS("ZSSEditor.usingActiveMonitor=true;");
    }

    public static class ContentStyle {
        public String imageCaption = "";

        public String imageID = "";

        public int imageStatus = 0;

        public String imageURL = "";

        public boolean isBlockquote = false;

        public boolean isBold = false;

        public boolean isCode = false;

        public boolean isDiv = false;

        public boolean isHead1 = false;

        public boolean isHead2 = false;

        public boolean isHead3 = false;

        public boolean isHead4 = false;

        public boolean isHead5 = false;

        public boolean isHead6 = false;

        public boolean isImage = false;

        public boolean isItalic = false;

        public boolean isLine = false;

        public boolean isLink = false;

        public boolean isOrderedList = false;

        public boolean isP = false;

        public boolean isPre = false;

        public boolean isStrikethrough = false;

        public boolean isSubscript = false;

        public boolean isSupscript = false;

        public boolean isUnderline = false;

        public boolean isUnorderedList = false;

        public String linkTitle = "";

        public String linkURL = "";
    }

    private static class CurrentStyles {
        public static String imageCaption = "";

        public static String imageID = "";

        public static int imageStatus = 0;

        public static String imageURL = "";

        public static boolean isBlockquote = false;

        public static boolean isBold = false;

        public static boolean isCode = false;

        public static boolean isDiv = false;

        public static boolean isHead1 = false;

        public static boolean isHead2 = false;

        public static boolean isHead3 = false;

        public static boolean isHead4 = false;

        public static boolean isHead5 = false;

        public static boolean isHead6 = false;

        public static boolean isImage = false;

        public static boolean isItalic = false;

        public static boolean isLine = false;

        public static boolean isLink = false;

        public static boolean isOrderedList = false;

        public static boolean isP = false;

        public static boolean isPre = false;

        public static boolean isStrikethrough = false;

        public static boolean isSubscript = false;

        public static boolean isSupscript = false;

        public static boolean isUnderline = false;

        public static boolean isUnorderedList = false;

        public static String linkTitle = "";

        public static String linkURL = "";

        private static final String tagBlockquote;

        private static final String tagBold = "bold".intern();

        private static final String tagCode;

        private static final String tagDiv;

        private static final String tagHRule;

        private static final String tagHead1;

        private static final String tagHead2;

        private static final String tagHead3;

        private static final String tagHead4;

        private static final String tagHead5;

        private static final String tagHead6;

        private static final String tagImage;

        private static final String tagImageCaptionPrefix;

        private static final String tagImageIDPrefix;

        private static final String tagImageStatusPrefix;

        private static final String tagImageURLPrefix;

        private static final String tagItalic = "italic".intern();

        private static final String tagLink;

        private static final String tagLinkTitlePrefix;

        private static final String tagLinkURLPrefix;

        private static final String tagOrderedList;

        private static final String tagOrderedListPrime;

        private static final String tagP;

        private static final String tagPre;

        private static final String tagStrikeThrough = "strikethrough".intern();

        private static final String tagSubscript = "subscript".intern();

        private static final String tagSupscript = "superscript".intern();

        private static final String tagUnderline = "underline".intern();

        private static final String tagUnorderedList;

        private static final String tagUnorderedListPrime;

        static {
            tagHead1 = "h1".intern();
            tagHead2 = "h2".intern();
            tagHead3 = "h3".intern();
            tagHead4 = "h4".intern();
            tagHead5 = "h5".intern();
            tagHead6 = "h6".intern();
            tagP = "p".intern();
            tagDiv = "div".intern();
            tagBlockquote = "blockquote".intern();
            tagPre = "pre".intern();
            tagCode = "code".intern();
            tagOrderedList = "orderedlist".intern();
            tagOrderedListPrime = "ol".intern();
            tagUnorderedList = "unorderedlist".intern();
            tagUnorderedListPrime = "ul".intern();
            tagHRule = "hrule".intern();
            tagLink = "islink".intern();
            tagImage = "isimage".intern();
            tagLinkURLPrefix = "link:".intern();
            tagLinkTitlePrefix = "link-title:".intern();
            tagImageURLPrefix = "image:".intern();
            tagImageCaptionPrefix = "image-alt:".intern();
            tagImageIDPrefix = "image-id:".intern();
            tagImageStatusPrefix = "image-status:".intern();
        }

        private static void reset() {
            isBold = false;
            isItalic = false;
            isUnderline = false;
            isStrikethrough = false;
            isSubscript = false;
            isSupscript = false;
            isHead1 = false;
            isHead2 = false;
            isHead3 = false;
            isHead4 = false;
            isHead5 = false;
            isHead6 = false;
            isDiv = false;
            isP = false;
            isPre = false;
            isCode = false;
            isBlockquote = false;
            isOrderedList = false;
            isUnorderedList = false;
            isLine = false;
            isLink = false;
            isImage = false;
            linkURL = "";
            linkTitle = "";
            imageURL = "";
            imageCaption = "";
            imageID = "";
            imageStatus = 0;
        }

        private static void setStyles(String[] param1ArrayOfString) {
            int j = param1ArrayOfString.length;
            for (int i = 0; i < j; i++) {
                String str = param1ArrayOfString[i].intern();
                if (str.equals(tagBold)) {
                    isBold = true;
                } else if (str.equals(tagItalic)) {
                    isItalic = true;
                } else if (str.equals(tagUnderline)) {
                    isUnderline = true;
                } else if (str.equals(tagStrikeThrough)) {
                    isStrikethrough = true;
                } else if (str.equals(tagSubscript)) {
                    isSubscript = true;
                } else if (str.equals(tagSupscript)) {
                    isSupscript = true;
                } else if (str.equals(tagHead1)) {
                    isHead1 = true;
                } else if (str.equals(tagHead2)) {
                    isHead2 = true;
                } else if (str.equals(tagHead3)) {
                    isHead3 = true;
                } else if (str.equals(tagHead4)) {
                    isHead4 = true;
                } else if (str.equals(tagHead5)) {
                    isHead5 = true;
                } else if (str.equals(tagHead6)) {
                    isHead6 = true;
                } else if (str.equals(tagP)) {
                    isP = true;
                } else if (str.equals(tagDiv)) {
                    isDiv = true;
                } else if (str.equals(tagBlockquote)) {
                    isBlockquote = true;
                } else if (str.equals(tagPre)) {
                    isPre = true;
                } else if (str.equals(tagCode)) {
                    isCode = true;
                } else if (str.equals(tagOrderedList)) {
                    isOrderedList = true;
                } else if (str.equals(tagOrderedListPrime)) {
                    isOrderedList = true;
                } else if (str.equals(tagUnorderedList)) {
                    isUnorderedList = true;
                } else if (str.equals(tagUnorderedListPrime)) {
                    isUnorderedList = true;
                } else if (str.equals(tagHRule)) {
                    isLine = true;
                } else if (str.equals(tagLink)) {
                    isLink = true;
                } else if (str.equals(tagImage)) {
                    isImage = true;
                } else if (str.indexOf(tagLinkURLPrefix) == 0) {
                    linkURL = str.replaceFirst(tagLinkURLPrefix, "");
                } else if (str.indexOf(tagLinkTitlePrefix) == 0) {
                    linkTitle = str.replaceFirst(tagLinkTitlePrefix, "");
                } else if (str.indexOf(tagImageURLPrefix) == 0) {
                    imageURL = str.replaceFirst(tagImageURLPrefix, "");
                } else if (str.indexOf(tagImageCaptionPrefix) == 0) {
                    imageCaption = str.replaceFirst(tagImageCaptionPrefix, "");
                } else if (str.indexOf(tagImageIDPrefix) == 0) {
                    imageID = str.replaceFirst(tagImageIDPrefix, "");
                } else if (str.indexOf(tagImageStatusPrefix) == 0) {
                    imageStatus = Integer.parseInt(str.replaceFirst(tagImageStatusPrefix, ""));
                }
            }
        }

        public static EditorV19WebView.ContentStyle toStyle() {
            EditorV19WebView.ContentStyle contentStyle = new EditorV19WebView.ContentStyle();
            if (isBold)
                contentStyle.isBold = true;
            if (isItalic)
                contentStyle.isItalic = true;
            if (isUnderline)
                contentStyle.isUnderline = true;
            if (isStrikethrough)
                contentStyle.isStrikethrough = true;
            if (isSubscript)
                contentStyle.isSubscript = true;
            if (isSupscript)
                contentStyle.isSupscript = true;
            if (isHead1)
                contentStyle.isHead1 = true;
            if (isHead2)
                contentStyle.isHead2 = true;
            if (isHead3)
                contentStyle.isHead3 = true;
            if (isHead4)
                contentStyle.isHead4 = true;
            if (isHead5)
                contentStyle.isHead5 = true;
            if (isHead6)
                contentStyle.isHead6 = true;
            if (isDiv)
                contentStyle.isDiv = true;
            if (isP)
                contentStyle.isP = true;
            if (isPre)
                contentStyle.isPre = true;
            if (isCode)
                contentStyle.isCode = true;
            if (isBlockquote)
                contentStyle.isBlockquote = true;
            if (isOrderedList)
                contentStyle.isOrderedList = true;
            if (isUnorderedList)
                contentStyle.isUnorderedList = true;
            if (isLine)
                contentStyle.isLine = true;
            if (isLink)
                contentStyle.isLink = true;
            if (isImage)
                contentStyle.isImage = true;
            contentStyle.linkURL = linkURL;
            contentStyle.linkTitle = linkTitle;
            contentStyle.imageURL = imageURL;
            contentStyle.imageCaption = imageCaption;
            contentStyle.imageID = imageID;
            contentStyle.imageStatus = imageStatus;
            return contentStyle;
        }
    }

    public static interface EditorDelegate {
        void onGetContent(String param1String);

        void onGetImageStatus(String[] param1ArrayOfString1, String[] param1ArrayOfString2, String[] param1ArrayOfString3);

        void onGetImagesCount(int param1Int);

        void onGetSelectionInfo(String param1String, int param1Int1, int param1Int2, boolean param1Boolean);

        void onGetSelectionStyles(EditorV19WebView.ContentStyle param1ContentStyle);

        void onGetSelectionText(String param1String);

        void onGetTitle(String param1String);

        void onGetWordage(int param1Int);

        void onInit();

        void onInput();

        void onLog(String param1String);

        void onPageLoaded(String param1String);

        void onTap();

        void onTapImage(String param1String1, String param1String2);

        void onTapLink(String param1String1, String param1String2);
    }

    private static class EditorSettings {
        private static boolean listenBlur = true;

        private static boolean listenFocus = false;

        private static boolean listenInput = true;

        private static boolean listenKeyDown = false;

        private static boolean listenKeyUp = false;

        private static boolean listenLog = true;

        private static boolean listenSelectionChanged = true;

        private static boolean listenTap = true;

        private static boolean sendWordage = true;
    }

    private class EditorWebViewJSBridge {
        private final String strParamsEqual = "=";

        private final String strParamsSplit = "~";

        private final String tagH = "height".intern();

        private final String tagID = "id".intern();

        private final String tagS = "hasSelection".intern();

        private final String tagY = "yOffset".intern();

        private EditorWebViewJSBridge() {
        }

        @JavascriptInterface
        public void getContent(String param1String) {
            articleContent = param1String;
            post(fireOnGetContent);
        }

        @JavascriptInterface
        public void getSelectedText(String param1String) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onGetSelectTExt ");
            stringBuilder.append(param1String);
            Log.i(str, stringBuilder.toString());
            selectedText = param1String;
            post(fireOnGetSelectedText);
        }

        @JavascriptInterface
        public void getTitle(String param1String) {
            articleTitle = param1String;
            post(fireOnGetTitle);
        }

        @JavascriptInterface
        public void getWordage(int param1Int) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getWordage ");
            stringBuilder.append(param1Int);
            Log.i(str, stringBuilder.toString());
            try {
                articleWordage = param1Int;
            } catch (Exception exception) {
                articleWordage = 0;
            }
            post(fireOnGetWordage);
        }

        @JavascriptInterface
        public void hideKeyboard() {
            hideKeyboard();
        }

        @JavascriptInterface
        public void init() {
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnEditorReady);
        }

        @JavascriptInterface
        public void log(String param1String) {
            mLastLog = param1String;
            post(fireOnLog);

        }

        @JavascriptInterface
        public void onGetImageStatus(String param1String1, String param1String2, String param1String3) {
            if (param1String1.length() > 4) {
                param1String1 = param1String1.substring(2, param1String1.length() - 2);
//                EditorV19WebView.access$2202(EditorV19WebView.this, param1String1.split("\",\""));
            } else {
//                EditorV19WebView.access$2202(EditorV19WebView.this, new String[]{""});
            }
            if (param1String2.length() > 4) {
                param1String1 = param1String2.substring(2, param1String2.length() - 2);
//                EditorV19WebView.access$2302(EditorV19WebView.this, param1String1.split("\",\""));
            } else {
//                EditorV19WebView.access$2302(EditorV19WebView.this, new String[]{""});
            }
            if (param1String3.length() > 4) {
                param1String1 = param1String3.substring(2, param1String3.length() - 2);
//                EditorV19WebView.access$2402(EditorV19WebView.this, param1String1.split("\",\""));
            } else {
//                EditorV19WebView.access$2402(EditorV19WebView.this, new String[]{""});
            }
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnGetImageStatus);
        }

        @JavascriptInterface
        public void onGetImagesCount(int param1Int) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("_____");
            stringBuilder.append(param1Int);
            Log.i("onGetImagesCount", stringBuilder.toString());
            uploadedImageCounts = param1Int;
            post(fireOnGetImageCounts);
        }

        @JavascriptInterface
        public void onInput() {
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnInput);
        }

        @JavascriptInterface
        public void onPaste() {
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnPaste);
        }

        @JavascriptInterface
        public void onSelectionChanged(String param1String) {
            String[] arrayOfString = param1String.split("~", -1);
            int k = arrayOfString.length;
            boolean bool3 = false;
            param1String = "";
            int j = 0;
            boolean bool2 = false;
            boolean bool1 = false;
            int i = 0;
            while (j < k) {
                boolean bool4;
                boolean bool5;
                String str1;
                String[] arrayOfString1 = arrayOfString[j].split("=");
                if (arrayOfString1.length < 2)
                    return;
                String str2 = arrayOfString1[0].intern();
                if (str2.equals(this.tagID)) {
                    str1 = arrayOfString1[1];
                    bool4 = bool2;
                    bool5 = bool1;
                } else if (str2.equals(this.tagY)) {
                    bool4 = true;
                    str1 = param1String;
                    bool5 = bool1;
                } else if (str2.equals(this.tagH)) {
                    bool5 = true;
                    str1 = param1String;
                    bool4 = bool2;
                } else {
                    str1 = param1String;
                    bool4 = bool2;
                    bool5 = bool1;
                    if (str2.equals(this.tagS)) {
                        i = Integer.parseInt(arrayOfString1[1]);
                        bool5 = bool1;
                        bool4 = bool2;
                        str1 = param1String;
                    }
                }
                j++;
                param1String = str1;
                bool2 = bool4;
                bool1 = bool5;
            }
            if (param1String.length() > 0) {
//                EditorV19WebView.access$3702(EditorV19WebView.this, param1String);
//                EditorV19WebView.access$3802(EditorV19WebView.this, bool2);
//                EditorV19WebView.access$3902(EditorV19WebView.this, bool1);
                EditorV19WebView editorV19WebView1 = EditorV19WebView.this;
                if (i != 0)
                    bool3 = true;
//                EditorV19WebView.access$4002(editorV19WebView1, bool3);
            }
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnGetSelectionInfo);
        }

        @JavascriptInterface
        public void onSelectionStyles(String param1String) {
            String[] arrayOfString = param1String.split("~", -1);
            EditorV19WebView.CurrentStyles.reset();
            EditorV19WebView.CurrentStyles.setStyles(arrayOfString);
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnGetSelectionStyles);
        }

        @JavascriptInterface
        public void onTap() {
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnTap);
        }

        @JavascriptInterface
        public void onTapImage(String param1String) {
            try {
                String[] arrayOfString = param1String.split("~");
                if (arrayOfString.length >= 3) {
//                    EditorV19WebView.access$2902(EditorV19WebView.this, arrayOfString[1].split("=")[1]);
//                    EditorV19WebView.access$3002(EditorV19WebView.this, arrayOfString[2].split("=")[1]);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnTapImage);
        }

        @JavascriptInterface
        public void onTapLink(String param1String) {
            try {
                String[] arrayOfString = param1String.split("~");
                if (arrayOfString.length >= 3) {
//                    EditorV19WebView.accezss$3102(EditorV19WebView.this, arrayOfString[1].split("=")[1]);
//                    EditorV19WebView.access$3202(EditorV19WebView.this, arrayOfString[2].split("=")[1]);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            EditorV19WebView editorV19WebView = EditorV19WebView.this;
            editorV19WebView.post(editorV19WebView.fireOnTapLink);
        }

        @JavascriptInterface
        public void showKeyboard() {
            showKeyboard();
        }
    }
}

