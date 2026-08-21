package com.autoclicker.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class PositionPickerActivity extends Activity {
    private LinearLayout bar;
    private View dotView;
    private View ringView;
    private FrameLayout root;
    private SeekBar sbRadius;
    private TextView tvCoord;
    private TextView tvRadius;
    private float cx = -1.0f;
    private float cy = -1.0f;
    private float radius = 40.0f;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.radius = getIntent().getIntExtra("radius", 40);
        getWindow().addFlags(256);
        getWindow().addFlags(2);
        getWindow().getAttributes().dimAmount = 0.5f;
        FrameLayout frameLayout = new FrameLayout(this);
        this.root = frameLayout;
        frameLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));
        TextView textView = new TextView(this);
        textView.setText("👆 点击目标位置\n拖动滑块调整半径");
        textView.setTextSize(14.0f);
        textView.setTextColor(Color.argb(220, 255, 255, 255));
        textView.setGravity(17);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 49;
        layoutParams.topMargin = dp(80);
        textView.setLayoutParams(layoutParams);
        this.root.addView(textView);
        LinearLayout linearLayout = new LinearLayout(this);
        this.bar = linearLayout;
        linearLayout.setOrientation(1);
        this.bar.setBackgroundColor(Color.argb(220, 22, 27, 44));
        this.bar.setPadding(dp(18), dp(12), dp(18), dp(18));
        this.bar.setVisibility(8);
        TextView textView2 = new TextView(this);
        this.tvCoord = textView2;
        textView2.setTextSize(14.0f);
        this.tvCoord.setTextColor(Color.parseColor("#BCC8E0"));
        this.tvCoord.setGravity(17);
        this.tvCoord.setPadding(0, 0, 0, dp(6));
        this.bar.addView(this.tvCoord);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        linearLayout2.setPadding(0, dp(4), 0, dp(8));
        TextView textView3 = new TextView(this);
        textView3.setText("半径:");
        textView3.setTextSize(12.0f);
        textView3.setTextColor(Color.parseColor("#8899BB"));
        linearLayout2.addView(textView3);
        TextView textView4 = new TextView(this);
        this.tvRadius = textView4;
        textView4.setText("40px");
        this.tvRadius.setTextSize(12.0f);
        this.tvRadius.setTextColor(-1);
        this.tvRadius.setPadding(dp(6), 0, dp(10), 0);
        linearLayout2.addView(this.tvRadius);
        SeekBar seekBar = new SeekBar(this);
        this.sbRadius = seekBar;
        seekBar.setMax(200);
        this.sbRadius.setProgress(40);
        this.sbRadius.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout2.addView(this.sbRadius);
        this.bar.addView(linearLayout2);
        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setOrientation(0);
        linearLayout3.setGravity(17);
        linearLayout3.addView(mkBtn("重选", Color.argb(160, 130, 130, 130), new View.OnClickListener() { // from class: com.autoclicker.app.PositionPickerActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m10lambda$onCreate$0$comautoclickerappPositionPickerActivity(view);
            }
        }));
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(dp(14), 1));
        linearLayout3.addView(view);
        linearLayout3.addView(mkBtn("✓ 确认", Color.argb(200, 72, 187, 120), new View.OnClickListener() { // from class: com.autoclicker.app.PositionPickerActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m11lambda$onCreate$1$comautoclickerappPositionPickerActivity(view2);
            }
        }));
        this.bar.addView(linearLayout3);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, -2);
        layoutParams2.gravity = 80;
        this.bar.setLayoutParams(layoutParams2);
        this.root.addView(this.bar);
        setContentView(this.root);
        this.sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.autoclicker.app.PositionPickerActivity.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                PositionPickerActivity.this.radius = i;
                PositionPickerActivity.this.tvRadius.setText(i + "px");
                if (PositionPickerActivity.this.cx >= 0.0f) {
                    PositionPickerActivity.this.drawRing();
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onCreate$0$com-autoclicker-app-PositionPickerActivity, reason: not valid java name */
    /* synthetic */ void m10lambda$onCreate$0$comautoclickerappPositionPickerActivity(View view) {
        reset();
    }

    /* JADX INFO: renamed from: lambda$onCreate$1$com-autoclicker-app-PositionPickerActivity, reason: not valid java name */
    /* synthetic */ void m11lambda$onCreate$1$comautoclickerappPositionPickerActivity(View view) {
        confirm();
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.cx = motionEvent.getRawX();
            this.cy = motionEvent.getRawY();
            this.tvCoord.setText("屏幕坐标 (" + ((int) this.cx) + ", " + ((int) this.cy) + ")");
            this.bar.setVisibility(0);
            drawRing();
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawRing() {
        View view = this.ringView;
        if (view != null) {
            this.root.removeView(view);
        }
        View view2 = this.dotView;
        if (view2 != null) {
            this.root.removeView(view2);
        }
        int[] iArr = new int[2];
        this.root.getLocationOnScreen(iArr);
        float f = this.cx - iArr[0];
        float f2 = this.cy - iArr[1];
        int i = (int) (this.radius * getResources().getDisplayMetrics().density * 2.0f);
        View view3 = new View(this);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(Color.argb(25, 255, 180, 120));
        gradientDrawable.setStroke(dp(2), Color.argb(160, 255, 140, 100));
        view3.setBackground(gradientDrawable);
        this.ringView = view3;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i, i);
        int i2 = (int) f;
        int i3 = i / 2;
        layoutParams.leftMargin = i2 - i3;
        int i4 = (int) f2;
        layoutParams.topMargin = i4 - i3;
        view3.setLayoutParams(layoutParams);
        this.root.addView(view3, 0);
        View view4 = new View(this);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(1);
        gradientDrawable2.setColor(Color.argb(220, 255, 110, 70));
        gradientDrawable2.setStroke(1, -1);
        view4.setBackground(gradientDrawable2);
        this.dotView = view4;
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(dp(10), dp(10));
        layoutParams2.leftMargin = i2 - dp(5);
        layoutParams2.topMargin = i4 - dp(5);
        view4.setLayoutParams(layoutParams2);
        this.root.addView(view4);
    }

    private void reset() {
        this.cx = -1.0f;
        this.cy = -1.0f;
        View view = this.ringView;
        if (view != null) {
            this.root.removeView(view);
            this.ringView = null;
        }
        View view2 = this.dotView;
        if (view2 != null) {
            this.root.removeView(view2);
            this.dotView = null;
        }
        this.bar.setVisibility(8);
    }

    private void confirm() {
        if (this.cx < 0.0f) {
            return;
        }
        getSharedPreferences("autoclicker", 0).edit().putFloat("pick_x", this.cx).putFloat("pick_y", this.cy).putInt("pick_radius", (int) this.radius).apply();
        sendBroadcast(new Intent("com.autoclicker.app.PICK_DONE"));
        Intent intent = new Intent();
        intent.putExtra("x", this.cx);
        intent.putExtra("y", this.cy);
        intent.putExtra("radius", (int) this.radius);
        setResult(-1, intent);
        finish();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        setResult(0);
        finish();
    }

    private Button mkBtn(String str, int i, View.OnClickListener onClickListener) {
        Button button = new Button(this);
        button.setText(str);
        button.setTextColor(-1);
        button.setAllCaps(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(20));
        gradientDrawable.setColor(i);
        button.setBackground(gradientDrawable);
        button.setTextSize(13.0f);
        button.setOnClickListener(onClickListener);
        button.setPadding(dp(20), dp(8), dp(20), dp(8));
        return button;
    }

    private int dp(int i) {
        return (int) (i * getResources().getDisplayMetrics().density);
    }
}
