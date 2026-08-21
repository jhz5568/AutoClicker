package com.autoclicker.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends Activity {
    private static final int PICK_MAIN = 50;
    private Button btnGo;
    private EditText etMax;
    private TextView pendingTX;
    private TextView pendingTY;
    private LinearLayout pointsBox;
    private SharedPreferences prefs;
    private RadioGroup rgMode;
    private LinearLayout root;
    private SeekBar sbMaxInt;
    private SeekBar sbMinInt;
    private BroadcastReceiver stopRcvr;
    private TextView tvMaxInt;
    private TextView tvMinInt;
    private TextView tvSR;
    private TextView tvSX;
    private TextView tvSY;
    private TextView tvStatus;
    private final int C_BG = Color.parseColor("#F0F2F5");
    private final int C_CARD = Color.parseColor("#FFFFFF");
    private final int C_PRI = Color.parseColor("#1A73E8");
    private final int C_GRN = Color.parseColor("#0F9D58");
    private final int C_RED = Color.parseColor("#EA4335");
    private final int C_TXT = Color.parseColor("#202124");
    private final int C_SUB = Color.parseColor("#5F6368");
    private final int C_SURF = Color.parseColor("#E8EAED");

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.prefs = getSharedPreferences("autoclicker", 0);
        loadDefaults();
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(this.C_BG);
        scrollView.setFillViewport(true);
        LinearLayout linearLayout = new LinearLayout(this);
        this.root = linearLayout;
        linearLayout.setOrientation(1);
        this.root.setPadding(dp(18), dp(16), dp(18), dp(32));
        buildUI();
        scrollView.addView(this.root);
        setContentView(scrollView);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.autoclicker.app.MainActivity.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                MainActivity.this.refreshUI();
            }
        };
        this.stopRcvr = broadcastReceiver;
        registerReceiver(broadcastReceiver, new IntentFilter("com.autoclicker.app.STOP"), 4);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(this.stopRcvr);
        } catch (Exception e) {
        }
    }

    private void buildUI() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(0, 0, 0, dp(4));
        linearLayout.addView(t("⚡ 连点器", 26, this.C_PRI, true));
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
        linearLayout.addView(view);
        Button buttonMatBtn = matBtn("悬浮窗", this.C_SURF, this.C_TXT, 12);
        buttonMatBtn.setOnClickListener(new View.OnClickListener() { // from class: com.autoclicker.app.MainActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m9lambda$buildUI$0$comautoclickerappMainActivity(view2);
            }
        });
        linearLayout.addView(buttonMatBtn);
        this.root.addView(linearLayout);
        this.root.addView(space(8));
        LinearLayout linearLayoutCard = card();
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        TextView textViewT = t("○ 就绪", 15, this.C_SUB, false);
        this.tvStatus = textViewT;
        linearLayout2.addView(textViewT);
        View view2 = new View(this);
        view2.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));
        linearLayout2.addView(view2);
        linearLayoutCard.addView(linearLayout2);
        this.root.addView(linearLayoutCard);
        LinearLayout linearLayoutCard2 = card();
        linearLayoutCard2.addView(sec("📋 点击模式"));
        RadioGroup radioGroup = new RadioGroup(this);
        this.rgMode = radioGroup;
        radioGroup.setOrientation(0);
        RadioButton radioButtonRadio = radio("单点 + 半径");
        radioButtonRadio.setId(0);
        RadioButton radioButtonRadio2 = radio("多点排序");
        radioButtonRadio2.setId(1);
        this.rgMode.addView(radioButtonRadio);
        this.rgMode.addView(radioButtonRadio2);
        this.rgMode.check(this.prefs.getInt("mode", 0));
        linearLayoutCard2.addView(this.rgMode);
        this.root.addView(linearLayoutCard2);
        LinearLayout linearLayoutCard3 = card();
        linearLayoutCard3.setTag("single");
        linearLayoutCard3.addView(sec("🎯 单点设置（中心 + 半径）"));
        LinearLayout linearLayoutRow = row();
        this.tvSX = numIn(linearLayoutRow, "X");
        this.tvSY = numIn(linearLayoutRow, "Y");
        this.tvSR = numIn(linearLayoutRow, "R (px)");
        linearLayoutCard3.addView(linearLayoutRow);
        this.tvSX.setText(String.valueOf(this.prefs.getInt("sx", 540)));
        this.tvSY.setText(String.valueOf(this.prefs.getInt("sy", 1000)));
        this.tvSR.setText(String.valueOf(this.prefs.getInt("sr", 40)));
        linearLayoutCard3.addView(matBtn("📌 选取位置", this.C_PRI, -1, 14));
        this.root.addView(linearLayoutCard3);
        LinearLayout linearLayoutCard4 = card();
        linearLayoutCard4.setTag("multi");
        linearLayoutCard4.addView(sec("📍 多点排序"));
        LinearLayout linearLayout3 = new LinearLayout(this);
        this.pointsBox = linearLayout3;
        linearLayout3.setOrientation(1);
        linearLayoutCard4.addView(this.pointsBox);
        linearLayoutCard4.addView(matBtn("＋ 添加位置", this.C_GRN, -1, 13));
        this.root.addView(linearLayoutCard4);
        LinearLayout linearLayoutCard5 = card();
        linearLayoutCard5.addView(sec("⏱ 间隔范围（随机变化）"));
        TextView textViewT2 = t("最小: 100 ms", 11, this.C_SUB, false);
        this.tvMinInt = textViewT2;
        linearLayoutCard5.addView(textViewT2);
        SeekBar seekBar = new SeekBar(this);
        this.sbMinInt = seekBar;
        seekBar.setMax(1900);
        this.sbMinInt.setProgress(this.prefs.getInt("mi", 100) - 10);
        linearLayoutCard5.addView(this.sbMinInt);
        TextView textViewT3 = t("最大: 300 ms", 11, this.C_SUB, false);
        this.tvMaxInt = textViewT3;
        linearLayoutCard5.addView(textViewT3);
        SeekBar seekBar2 = new SeekBar(this);
        this.sbMaxInt = seekBar2;
        seekBar2.setMax(2900);
        this.sbMaxInt.setProgress(this.prefs.getInt("ma", 300) - 100);
        linearLayoutCard5.addView(this.sbMaxInt);
        this.root.addView(linearLayoutCard5);
        LinearLayout linearLayoutCard6 = card();
        linearLayoutCard6.addView(sec("🔢 次数限制（0 = 不限）"));
        EditText editText = new EditText(this);
        this.etMax = editText;
        editText.setTextSize(14.0f);
        this.etMax.setTextColor(this.C_TXT);
        this.etMax.setHint("0");
        this.etMax.setHintTextColor(-7829368);
        this.etMax.setInputType(2);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(8));
        gradientDrawable.setColor(this.C_SURF);
        this.etMax.setBackground(gradientDrawable);
        this.etMax.setPadding(dp(14), dp(10), dp(14), dp(10));
        this.etMax.setText(this.prefs.getInt("mc", 0) == 0 ? "" : String.valueOf(this.prefs.getInt("mc", 0)));
        linearLayoutCard6.addView(this.etMax);
        this.root.addView(linearLayoutCard6);
        Button buttonBigBtn = bigBtn("▶ 开始连点", this.C_GRN);
        this.btnGo = buttonBigBtn;
        this.root.addView(buttonBigBtn);
        LinearLayout linearLayoutCard7 = card();
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.parseColor("#FFF8E1"));
        gradientDrawable2.setCornerRadius(dp(14));
        linearLayoutCard7.setBackground(gradientDrawable2);
        TextView textView = new TextView(this);
        textView.setText("💡 选取坐标使用屏幕绝对位置 | 息屏自动停止 | 圆形范围内随机点击 | 间隔随机变化");
        textView.setTextSize(11.0f);
        textView.setTextColor(Color.parseColor("#8D6E00"));
        linearLayoutCard7.addView(textView);
        this.root.addView(linearLayoutCard7);
        rebuildPoints();
        updateVisibility();
        bindAll();
    }

    /* JADX INFO: renamed from: lambda$buildUI$0$com-autoclicker-app-MainActivity, reason: not valid java name */
    /* synthetic */ void m9lambda$buildUI$0$comautoclickerappMainActivity(View view) {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())));
        } else {
            startService(new Intent(this, (Class<?>) FloatingWidgetService.class));
            Toast.makeText(this, "悬浮窗已启动", 0).show();
        }
    }

    private void bindAll() {
        this.rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.autoclicker.app.MainActivity$$ExternalSyntheticLambda3
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public final void onCheckedChanged(RadioGroup radioGroup, int i) {
                this.f$0.m5lambda$bindAll$1$comautoclickerappMainActivity(radioGroup, i);
            }
        });
        this.sbMinInt.setOnSeekBarChangeListener(sbListener(this.tvMinInt, "最小:", 10, "mi"));
        this.sbMaxInt.setOnSeekBarChangeListener(sbListener(this.tvMaxInt, "最大:", 100, "ma"));
        this.btnGo.setOnClickListener(new View.OnClickListener() { // from class: com.autoclicker.app.MainActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m6lambda$bindAll$2$comautoclickerappMainActivity(view);
            }
        });
        ((Button) ((LinearLayout) findViewByTag("single")).getChildAt(2)).setOnClickListener(new View.OnClickListener() { // from class: com.autoclicker.app.MainActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m7lambda$bindAll$3$comautoclickerappMainActivity(view);
            }
        });
        ((Button) ((LinearLayout) findViewByTag("multi")).getChildAt(2)).setOnClickListener(new View.OnClickListener() { // from class: com.autoclicker.app.MainActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m8lambda$bindAll$4$comautoclickerappMainActivity(view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$bindAll$1$com-autoclicker-app-MainActivity, reason: not valid java name */
    /* synthetic */ void m5lambda$bindAll$1$comautoclickerappMainActivity(RadioGroup radioGroup, int i) {
        this.prefs.edit().putInt("mode", i).apply();
        updateVisibility();
    }

    /* JADX INFO: renamed from: lambda$bindAll$2$com-autoclicker-app-MainActivity, reason: not valid java name */
    /* synthetic */ void m6lambda$bindAll$2$comautoclickerappMainActivity(View view) {
        AutoClickService autoClickService = AutoClickService.getInstance();
        if (autoClickService == null) {
            Toast.makeText(this, "请开启无障碍服务", 1).show();
            startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
        } else if (autoClickService.isRunning()) {
            autoClickService.stop();
            refreshUI();
        } else {
            saveAll();
            applyToService(autoClickService);
            autoClickService.start();
            refreshUI();
        }
    }

    /* JADX INFO: renamed from: lambda$bindAll$3$com-autoclicker-app-MainActivity, reason: not valid java name */
    /* synthetic */ void m7lambda$bindAll$3$comautoclickerappMainActivity(View view) {
        pickForVars(this.tvSX, this.tvSY);
    }

    /* JADX INFO: renamed from: lambda$bindAll$4$com-autoclicker-app-MainActivity, reason: not valid java name */
    /* synthetic */ void m8lambda$bindAll$4$comautoclickerappMainActivity(View view) {
        addPointRow(540, 800, 40);
    }

    private View findViewByTag(String str) {
        for (int i = 0; i < this.root.getChildCount(); i++) {
            View childAt = this.root.getChildAt(i);
            if (str.equals(childAt.getTag())) {
                return childAt;
            }
        }
        return null;
    }

    private void updateVisibility() {
        int checkedRadioButtonId = this.rgMode.getCheckedRadioButtonId();
        View viewFindViewByTag = findViewByTag("single");
        View viewFindViewByTag2 = findViewByTag("multi");
        if (viewFindViewByTag != null) {
            viewFindViewByTag.setVisibility(checkedRadioButtonId == 0 ? 0 : 8);
        }
        if (viewFindViewByTag2 != null) {
            viewFindViewByTag2.setVisibility(checkedRadioButtonId != 1 ? 8 : 0);
        }
    }

    private void updateLive() {
        AutoClickService autoClickService = AutoClickService.getInstance();
        if (autoClickService == null || !autoClickService.isRunning()) {
            return;
        }
        autoClickService.setInterval(this.prefs.getInt("mi", 100), this.prefs.getInt("ma", 300));
    }

    private void saveAll() {
        SharedPreferences.Editor editorEdit = this.prefs.edit();
        try {
            editorEdit.putInt("sx", Integer.parseInt(this.tvSX.getText().toString()));
        } catch (Exception e) {
        }
        try {
            editorEdit.putInt("sy", Integer.parseInt(this.tvSY.getText().toString()));
        } catch (Exception e2) {
        }
        try {
            editorEdit.putInt("sr", Integer.parseInt(this.tvSR.getText().toString()));
        } catch (Exception e3) {
        }
        try {
            editorEdit.putInt("mc", this.etMax.getText().toString().isEmpty() ? 0 : Integer.parseInt(this.etMax.getText().toString()));
        } catch (Exception e4) {
        }
        savePoints();
        editorEdit.apply();
    }

    private void applyToService(AutoClickService autoClickService) {
        autoClickService.setInterval(this.prefs.getInt("mi", 100), this.prefs.getInt("ma", 300));
        autoClickService.setMax(this.prefs.getInt("mc", 0));
        autoClickService.setLoopMode(this.prefs.getInt("lp", 0));
        if (this.rgMode.getCheckedRadioButtonId() == 0) {
            autoClickService.setSingle(this.prefs.getInt("sx", 540), this.prefs.getInt("sy", 1000), this.prefs.getInt("sr", 40));
            return;
        }
        List<AutoClickService.Target> points = parsePoints();
        if (!points.isEmpty()) {
            autoClickService.setMulti(points);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshUI() {
        AutoClickService autoClickService = AutoClickService.getInstance();
        boolean z = autoClickService != null && autoClickService.isRunning();
        this.tvStatus.setText(z ? "● 连点运行中" : "○ 就绪");
        this.tvStatus.setTextColor(z ? this.C_GRN : this.C_SUB);
        this.btnGo.setText(z ? "⏹ 停止连点" : "▶ 开始连点");
        this.btnGo.setBackground(bgBtn(z ? this.C_RED : this.C_GRN));
    }

    private void addPointRow(int i, int i2, int i3) {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(0, dp(3), 0, dp(3));
        linearLayout.addView(t((this.pointsBox.getChildCount() + 1) + ".", 12, this.C_PRI, false));
        final TextView textViewNumIn = numIn(linearLayout, "");
        textViewNumIn.setText(String.valueOf(i));
        textViewNumIn.setMinWidth(dp(48));
        final TextView textViewNumIn2 = numIn(linearLayout, "");
        textViewNumIn2.setText(String.valueOf(i2));
        textViewNumIn2.setMinWidth(dp(48));
        TextView textViewNumIn3 = numIn(linearLayout, "R");
        textViewNumIn3.setText(String.valueOf(i3));
        textViewNumIn3.setMinWidth(dp(36));
        linearLayout.addView(matBtnLite("📌", this.C_PRI, new View.OnClickListener() { // from class: com.autoclicker.app.MainActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m3lambda$addPointRow$5$comautoclickerappMainActivity(textViewNumIn, textViewNumIn2, view);
            }
        }));
        linearLayout.addView(matBtnLite("✕", this.C_RED, new View.OnClickListener() { // from class: com.autoclicker.app.MainActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m4lambda$addPointRow$6$comautoclickerappMainActivity(linearLayout, view);
            }
        }));
        this.pointsBox.addView(linearLayout);
        savePoints();
    }

    /* JADX INFO: renamed from: lambda$addPointRow$5$com-autoclicker-app-MainActivity, reason: not valid java name */
    /* synthetic */ void m3lambda$addPointRow$5$comautoclickerappMainActivity(TextView textView, TextView textView2, View view) {
        pickForVars(textView, textView2);
    }

    /* JADX INFO: renamed from: lambda$addPointRow$6$com-autoclicker-app-MainActivity, reason: not valid java name */
    /* synthetic */ void m4lambda$addPointRow$6$comautoclickerappMainActivity(LinearLayout linearLayout, View view) {
        this.pointsBox.removeView(linearLayout);
        savePoints();
    }

    private void rebuildPoints() {
        this.pointsBox.removeAllViews();
        String string = this.prefs.getString("pts", "");
        if (string.isEmpty()) {
            addPointRow(540, 800, 40);
            return;
        }
        for (String str : string.split("\\|")) {
            String[] strArrSplit = str.split(",");
            if (strArrSplit.length >= 3) {
                try {
                    addPointRow(Integer.parseInt(strArrSplit[0]), Integer.parseInt(strArrSplit[1]), Integer.parseInt(strArrSplit[2]));
                } catch (Exception e) {
                }
            }
        }
        if (this.pointsBox.getChildCount() == 0) {
            addPointRow(540, 800, 40);
        }
    }

    private void savePoints() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.pointsBox.getChildCount(); i++) {
            View childAt = this.pointsBox.getChildAt(i);
            if (childAt instanceof LinearLayout) {
                List<TextView> textViews = getTextViews((LinearLayout) childAt);
                if (textViews.size() >= 5) {
                    if (sb.length() > 0) {
                        sb.append("|");
                    }
                    sb.append(textViews.get(1).getText()).append(",").append(textViews.get(2).getText()).append(",").append(textViews.get(4).getText());
                }
            }
        }
        this.prefs.edit().putString("pts", sb.toString()).apply();
    }

    private List<AutoClickService.Target> parsePoints() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pointsBox.getChildCount(); i++) {
            View childAt = this.pointsBox.getChildAt(i);
            if (childAt instanceof LinearLayout) {
                List<TextView> textViews = getTextViews((LinearLayout) childAt);
                if (textViews.size() >= 5) {
                    try {
                        arrayList.add(new AutoClickService.Target(Integer.parseInt(textViews.get(1).getText().toString()), Integer.parseInt(textViews.get(2).getText().toString()), Integer.parseInt(textViews.get(4).getText().toString())));
                    } catch (Exception e) {
                    }
                }
            }
        }
        return arrayList;
    }

    private List<TextView> getTextViews(LinearLayout linearLayout) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof TextView) {
                arrayList.add((TextView) childAt);
            }
        }
        return arrayList;
    }

    private void pickForVars(TextView textView, TextView textView2) {
        this.pendingTX = textView;
        this.pendingTY = textView2;
        startActivityForResult(new Intent(this, (Class<?>) PositionPickerActivity.class), PICK_MAIN);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        TextView textView;
        super.onActivityResult(i, i2, intent);
        if (i == PICK_MAIN && i2 == -1 && intent != null) {
            float floatExtra = intent.getFloatExtra("x", -1.0f);
            float floatExtra2 = intent.getFloatExtra("y", -1.0f);
            if (floatExtra >= 0.0f && floatExtra2 >= 0.0f && (textView = this.pendingTX) != null && this.pendingTY != null) {
                int i3 = (int) floatExtra;
                textView.setText(String.valueOf(i3));
                int i4 = (int) floatExtra2;
                this.pendingTY.setText(String.valueOf(i4));
                savePoints();
                Toast.makeText(this, "(" + i3 + ", " + i4 + ")", 0).show();
            }
        }
        this.pendingTX = null;
        this.pendingTY = null;
    }

    private LinearLayout card() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(this.C_CARD);
        gradientDrawable.setCornerRadius(dp(14));
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setElevation(dp(2));
        linearLayout.setPadding(dp(16), dp(14), dp(16), dp(14));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(12);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private LinearLayout row() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        return linearLayout;
    }

    private TextView sec(String str) {
        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setTextSize(13.0f);
        textView.setTextColor(this.C_SUB);
        textView.setPadding(0, 0, 0, dp(8));
        return textView;
    }

    private TextView t(String str, int i, int i2, boolean z) {
        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setTextSize(i);
        textView.setTextColor(i2);
        if (z) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return textView;
    }

    private RadioButton radio(String str) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(str);
        radioButton.setTextSize(13.0f);
        radioButton.setTextColor(this.C_TXT);
        return radioButton;
    }

    private Button matBtn(String str, int i, int i2, int i3) {
        Button button = new Button(this);
        button.setText(str);
        button.setTextSize(i3);
        button.setTextColor(i2);
        button.setAllCaps(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(20));
        gradientDrawable.setColor(i);
        if (i == this.C_SURF) {
            gradientDrawable.setStroke(1, Color.parseColor("#DADCE0"));
        }
        button.setBackground(gradientDrawable);
        button.setPadding(dp(14), dp(6), dp(14), dp(6));
        button.setElevation(dp(1));
        return button;
    }

    private Button matBtnLite(String str, int i, View.OnClickListener onClickListener) {
        Button button = new Button(this);
        button.setText(str);
        button.setTextSize(10.0f);
        button.setTextColor(-1);
        button.setAllCaps(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(12));
        gradientDrawable.setColor(i);
        button.setBackground(gradientDrawable);
        button.setPadding(dp(8), dp(4), dp(8), dp(4));
        button.setOnClickListener(onClickListener);
        button.setElevation(dp(1));
        return button;
    }

    private Button bigBtn(String str, int i) {
        Button button = new Button(this);
        button.setText(str);
        button.setTextSize(18.0f);
        button.setTextColor(-1);
        button.setAllCaps(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(24));
        gradientDrawable.setColor(i);
        button.setBackground(gradientDrawable);
        button.setPadding(0, dp(16), 0, dp(16));
        button.setElevation(dp(4));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(8);
        button.setLayoutParams(layoutParams);
        return button;
    }

    private GradientDrawable bgBtn(int i) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(24));
        gradientDrawable.setColor(i);
        return gradientDrawable;
    }

    private TextView numIn(LinearLayout linearLayout, String str) {
        if (!str.isEmpty()) {
            TextView textView = new TextView(this);
            textView.setText(str);
            textView.setTextSize(11.0f);
            textView.setTextColor(this.C_SUB);
            linearLayout.addView(textView);
        }
        TextView textView2 = new TextView(this);
        textView2.setTextSize(13.0f);
        textView2.setTextColor(this.C_TXT);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(8));
        gradientDrawable.setColor(this.C_SURF);
        textView2.setBackground(gradientDrawable);
        textView2.setPadding(dp(8), dp(5), dp(8), dp(5));
        textView2.setGravity(17);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.rightMargin = dp(4);
        textView2.setLayoutParams(layoutParams);
        linearLayout.addView(textView2);
        return textView2;
    }

    private View space(int i) {
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(i)));
        return view;
    }

    private SeekBar.OnSeekBarChangeListener sbListener(final TextView textView, final String str, final int i, final String str2) {
        return new SeekBar.OnSeekBarChangeListener() { // from class: com.autoclicker.app.MainActivity.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i2, boolean z) {
                int i3 = i2 + i;
                textView.setText(str + " " + i3 + " ms");
                MainActivity.this.prefs.edit().putInt(str2, i3).apply();
                AutoClickService autoClickService = AutoClickService.getInstance();
                if (autoClickService != null && autoClickService.isRunning()) {
                    autoClickService.setInterval(MainActivity.this.prefs.getInt("mi", 100), MainActivity.this.prefs.getInt("ma", 300));
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    private int dp(int i) {
        return (int) (i * getResources().getDisplayMetrics().density);
    }

    private void loadDefaults() {
        if (this.prefs.contains("mi")) {
            return;
        }
        this.prefs.edit().putInt("mi", 100).putInt("ma", 300).putInt("sx", 540).putInt("sy", 1000).putInt("sr", 40).putInt("mode", 0).putInt("mc", 0).putInt("lp", 0).putString("pts", "540,800,40").apply();
    }
}
