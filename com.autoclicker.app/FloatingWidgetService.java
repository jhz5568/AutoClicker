package com.autoclicker.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class FloatingWidgetService extends Service {
    private View ball;
    private TextView ballIcon;
    private WindowManager.LayoutParams ballParams;
    private float currentX;
    private float currentY;
    private boolean isRadialOpen;
    private final List<View> menuItems = new ArrayList();
    private final List<WindowManager.LayoutParams> menuParams = new ArrayList();
    private View modeLabel;
    private WindowManager.LayoutParams modeLabelParams;
    private View pickOverlay;
    private WindowManager.LayoutParams pickOverlayParams;
    private SharedPreferences prefs;
    private BroadcastReceiver stopRcvr;
    private WindowManager wm;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.wm = (WindowManager) getSystemService("window");
        this.prefs = getSharedPreferences("autoclicker", 0);
        loadDefaults();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.autoclicker.app.FloatingWidgetService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                FloatingWidgetService.this.updateBallIcon();
            }
        };
        this.stopRcvr = broadcastReceiver;
        registerReceiver(broadcastReceiver, new IntentFilter("com.autoclicker.app.STOP"), 4);
        showBall();
        showModeLabel();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null || !"hide".equals(intent.getStringExtra("action"))) {
            return 2;
        }
        hideAll();
        stopSelf();
        return 2;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        removePickOverlay();
        hideAll();
        try {
            unregisterReceiver(this.stopRcvr);
        } catch (Exception e) {
        }
    }

    private void showBall() {
        if (this.ball != null) {
            return;
        }
        int i = Build.VERSION.SDK_INT >= 26 ? 2038 : 2002;
        int iDp = dp(60);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(iDp, iDp));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(Color.argb(180, 60, 70, 100));
        gradientDrawable.setStroke(dp(2), Color.argb(100, 255, 255, 255));
        frameLayout.setBackground(gradientDrawable);
        frameLayout.setElevation(dp(8));
        TextView textView = new TextView(this);
        this.ballIcon = textView;
        textView.setText("▶");
        this.ballIcon.setTextSize(22.0f);
        this.ballIcon.setTextColor(-1);
        this.ballIcon.setGravity(17);
        this.ballIcon.setTypeface(Typeface.DEFAULT_BOLD);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(iDp, iDp);
        layoutParams.gravity = 17;
        this.ballIcon.setLayoutParams(layoutParams);
        frameLayout.addView(this.ballIcon);
        this.ball = frameLayout;
        frameLayout.setOnTouchListener(new View.OnTouchListener() { // from class: com.autoclicker.app.FloatingWidgetService.2
            float bx;
            float by;
            float dx;
            float dy;
            float moved;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        this.dx = motionEvent.getRawX();
                        this.dy = motionEvent.getRawY();
                        this.bx = FloatingWidgetService.this.ballParams.x;
                        this.by = FloatingWidgetService.this.ballParams.y;
                        this.moved = 0.0f;
                        return true;
                    case AutoClickService.MODE_MULTI /* 1 */:
                        if (this.moved < FloatingWidgetService.this.dp(6)) {
                            FloatingWidgetService.this.toggleRadialMenu();
                        }
                        return true;
                    case 2:
                        float fAbs = Math.abs(motionEvent.getRawX() - this.dx) + Math.abs(motionEvent.getRawY() - this.dy);
                        this.moved = fAbs;
                        if (fAbs > FloatingWidgetService.this.dp(6)) {
                            FloatingWidgetService.this.ballParams.x = (int) ((this.bx + motionEvent.getRawX()) - this.dx);
                            FloatingWidgetService.this.ballParams.y = (int) ((this.by + motionEvent.getRawY()) - this.dy);
                            try {
                                FloatingWidgetService.this.wm.updateViewLayout(FloatingWidgetService.this.ball, FloatingWidgetService.this.ballParams);
                                break;
                            } catch (Exception e) {
                            }
                            FloatingWidgetService.this.prefs.edit().putInt("bx", FloatingWidgetService.this.ballParams.x).putInt("by", FloatingWidgetService.this.ballParams.y).apply();
                            if (FloatingWidgetService.this.modeLabel != null) {
                                FloatingWidgetService.this.modeLabelParams.x = FloatingWidgetService.this.ballParams.x;
                                FloatingWidgetService.this.modeLabelParams.y = FloatingWidgetService.this.ballParams.y + FloatingWidgetService.this.dp(60) + FloatingWidgetService.this.dp(2);
                                try {
                                    FloatingWidgetService.this.wm.updateViewLayout(FloatingWidgetService.this.modeLabel, FloatingWidgetService.this.modeLabelParams);
                                    break;
                                } catch (Exception e2) {
                                }
                            }
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(iDp, iDp, i, 8, -3);
        this.ballParams = layoutParams2;
        layoutParams2.gravity = 8388659;
        this.ballParams.x = this.prefs.getInt("bx", 100);
        this.ballParams.y = this.prefs.getInt("by", 400);
        this.wm.addView(this.ball, this.ballParams);
        updateBallIcon();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBallIcon() {
        if (this.ballIcon == null) {
            return;
        }
        AutoClickService autoClickService = AutoClickService.getInstance();
        boolean z = autoClickService != null && autoClickService.isRunning();
        this.ballIcon.setText(z ? "⏹" : "▶");
        int i = this.prefs.getInt("mode", 0);
        int i2 = this.prefs.getInt("lp", 0);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setStroke(dp(2), Color.argb(100, 255, 255, 255));
        if (z) {
            gradientDrawable.setColor(Color.argb(200, 220, 60, 60));
        } else if (i == 0) {
            gradientDrawable.setColor(Color.argb(200, 70, 130, 220));
        } else if (i2 == 0) {
            gradientDrawable.setColor(Color.argb(200, 60, 180, 110));
        } else {
            gradientDrawable.setColor(Color.argb(200, 220, 150, 50));
        }
        this.ball.setBackground(gradientDrawable);
        updateModeLabel();
    }

    private void showModeLabel() {
        if (this.modeLabel != null) {
            return;
        }
        int i = Build.VERSION.SDK_INT >= 26 ? 2038 : 2002;
        TextView textView = new TextView(this);
        textView.setTextSize(10.0f);
        textView.setTextColor(-1);
        textView.setGravity(17);
        textView.setPadding(dp(8), dp(3), dp(8), dp(3));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(8));
        gradientDrawable.setColor(Color.argb(170, 30, 30, 40));
        textView.setBackground(gradientDrawable);
        this.modeLabel = textView;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, i, 8, -3);
        this.modeLabelParams = layoutParams;
        layoutParams.gravity = 8388659;
        int iDp = dp(60);
        this.modeLabelParams.x = this.ballParams.x + (iDp / 2);
        this.modeLabelParams.y = this.ballParams.y + iDp + dp(2);
        try {
            this.wm.addView(this.modeLabel, this.modeLabelParams);
        } catch (Exception e) {
        }
        updateModeLabel();
    }

    private void updateModeLabel() {
        if (this.modeLabel == null) {
            return;
        }
        int i = this.prefs.getInt("mode", 0);
        int i2 = this.prefs.getInt("lp", 0);
        TextView textView = (TextView) this.modeLabel;
        if (i == 0) {
            textView.setText("🔵 单点");
        } else if (i2 == 0) {
            textView.setText("🟢 多点·循环");
        } else {
            textView.setText("🟠 多点·单次");
        }
        int iDp = dp(60);
        this.modeLabelParams.x = this.ballParams.x;
        this.modeLabelParams.y = this.ballParams.y + iDp + dp(2);
        try {
            this.wm.updateViewLayout(this.modeLabel, this.modeLabelParams);
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleRadialMenu() {
        if (this.isRadialOpen) {
            closeRadialMenu();
        } else {
            openRadialMenu();
        }
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    private void openRadialMenu() {
        closeRadialMenu();
        int i = 1;
        this.isRadialOpen = true;
        int i2 = Build.VERSION.SDK_INT >= 26 ? 2038 : 2002;
        String[] strArr = {"📍", "▶", "🔄", "🗑"};
        int[] iArr = {Color.argb(200, 70, 130, 220), Color.argb(200, 60, 180, 110), Color.argb(200, 160, 120, 220), Color.argb(200, 200, 80, 80)};
        int iDp = this.ballParams.x + dp(30);
        int iDp2 = this.ballParams.y + dp(30);
        int iDp3 = dp(80);
        final int i3 = 0;
        while (i3 < 4) {
            double radians = Math.toRadians(((i3 * 25) - 90) - 37);
            double d = iDp3;
            double dCos = Math.cos(radians);
            Double.isNaN(d);
            int[] iArr2 = iArr;
            int iDp4 = (((int) (d * dCos)) + iDp) - dp(22);
            double dSin = Math.sin(radians);
            Double.isNaN(d);
            int iDp5 = (((int) (d * dSin)) + iDp2) - dp(22);
            TextView textView = new TextView(this);
            textView.setText(strArr[i3]);
            textView.setTextSize(18.0f);
            textView.setTextColor(-1);
            textView.setGravity(17);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(i);
            gradientDrawable.setColor(iArr2[i3]);
            gradientDrawable.setStroke(dp(i), Color.argb(80, 255, 255, 255));
            textView.setBackground(gradientDrawable);
            textView.setElevation(dp(6));
            textView.setTag(Integer.valueOf(i3));
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.autoclicker.app.FloatingWidgetService$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m0x49948ed9(i3, view);
                }
            });
            this.menuItems.add(textView);
            int i4 = i3;
            int i5 = iDp3;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(dp(44), dp(44), i2, 8, -3);
            layoutParams.gravity = 8388659;
            layoutParams.x = iDp4;
            layoutParams.y = iDp5;
            this.menuParams.add(layoutParams);
            try {
                this.wm.addView(textView, layoutParams);
            } catch (Exception e) {
            }
            i3 = i4 + 1;
            iDp3 = i5;
            iArr = iArr2;
            i = 1;
        }
    }

    /* JADX INFO: renamed from: lambda$openRadialMenu$0$com-autoclicker-app-FloatingWidgetService, reason: not valid java name */
    /* synthetic */ void m0x49948ed9(int i, View view) {
        onMenuItemClick(i);
    }

    private void closeRadialMenu() {
        for (int i = 0; i < this.menuItems.size(); i++) {
            try {
                this.wm.removeView(this.menuItems.get(i));
            } catch (Exception e) {
            }
        }
        this.menuItems.clear();
        this.menuParams.clear();
        this.isRadialOpen = false;
    }

    private void onMenuItemClick(int i) {
        closeRadialMenu();
        switch (i) {
            case 0:
                startPickPosition();
                break;
            case AutoClickService.MODE_MULTI /* 1 */:
                toggleClicking();
                break;
            case 2:
                toggleMode();
                break;
            case 3:
                clearPoints();
                break;
        }
    }

    private void startPickPosition() {
        int i;
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName()));
            intent.addFlags(268435456);
            startActivity(intent);
            return;
        }
        View view = this.ball;
        if (view != null) {
            view.setAlpha(0.4f);
        }
        closeRadialMenu();
        if (Build.VERSION.SDK_INT >= 26) {
            i = 2038;
        } else {
            i = 2002;
        }
        final float[] fArr = {-1.0f};
        final float[] fArr2 = {-1.0f};
        final int[] iArr = {this.prefs.getInt("sr", 40)};
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(Color.argb(80, 0, 0, 0));
        final View view2 = new View(this) { // from class: com.autoclicker.app.FloatingWidgetService.3
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (fArr[0] < 0.0f || fArr2[0] < 0.0f) {
                    return;
                }
                int[] iArr2 = new int[2];
                getLocationOnScreen(iArr2);
                float f = fArr[0] - iArr2[0];
                float f2 = fArr2[0] - iArr2[1];
                float f3 = iArr[0] * getResources().getDisplayMetrics().density;
                Paint paint = new Paint(1);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(30, 255, 180, 120));
                canvas.drawCircle(f, f2, f3, paint);
                Paint paint2 = new Paint(1);
                paint2.setStyle(Paint.Style.STROKE);
                paint2.setStrokeWidth(FloatingWidgetService.this.dp(3));
                paint2.setColor(Color.argb(180, 255, 140, 100));
                canvas.drawCircle(f, f2, f3, paint2);
                Paint paint3 = new Paint(1);
                paint3.setStyle(Paint.Style.STROKE);
                paint3.setStrokeWidth(FloatingWidgetService.this.dp(1));
                paint3.setColor(Color.argb(120, 255, 255, 255));
                float fDp = FloatingWidgetService.this.dp(14);
                canvas.drawLine(f - fDp, f2, f + fDp, f2, paint3);
                canvas.drawLine(f, f2 - fDp, f, f2 + fDp, paint3);
                Paint paint4 = new Paint(1);
                paint4.setStyle(Paint.Style.FILL);
                paint4.setColor(Color.argb(230, 255, 90, 50));
                paint4.setShadowLayer(FloatingWidgetService.this.dp(4), 0.0f, 0.0f, Color.argb(120, 0, 0, 0));
                canvas.drawCircle(f, f2, FloatingWidgetService.this.dp(5), paint4);
                Paint paint5 = new Paint(1);
                paint5.setColor(-1);
                paint5.setTextSize(FloatingWidgetService.this.dp(11));
                paint5.setShadowLayer(FloatingWidgetService.this.dp(2), 0.0f, 0.0f, Color.argb(150, 0, 0, 0));
                String str = "(" + ((int) fArr[0]) + ", " + ((int) fArr2[0]) + ")  R=" + iArr[0];
                float fDp2 = f + f3 + FloatingWidgetService.this.dp(10);
                float fDp3 = (f2 - f3) - FloatingWidgetService.this.dp(6);
                if (fDp3 < FloatingWidgetService.this.dp(60)) {
                    fDp3 = FloatingWidgetService.this.dp(18) + f2 + f3;
                }
                canvas.drawText(str, fDp2, fDp3, paint5);
            }
        };
        frameLayout.addView(view2, new FrameLayout.LayoutParams(-1, -1));
        final TextView textView = new TextView(this);
        textView.setText("👆 点击屏幕任意位置选取\n拖动底部滑块调整半径");
        textView.setTextSize(16.0f);
        textView.setTextColor(Color.argb(200, 255, 255, 255));
        textView.setGravity(17);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        textView.setLayoutParams(layoutParams);
        frameLayout.addView(textView);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(Color.argb(210, 20, 25, 42));
        linearLayout.setPadding(dp(16), dp(10), dp(16), dp(14));
        final TextView textView2 = new TextView(this);
        textView2.setText("点击屏幕选取位置");
        textView2.setTextSize(12.0f);
        textView2.setTextColor(Color.parseColor("#8899BB"));
        textView2.setGravity(17);
        textView2.setPadding(0, 0, 0, dp(6));
        linearLayout.addView(textView2);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        TextView textView3 = new TextView(this);
        textView3.setText("半径:");
        textView3.setTextSize(13.0f);
        textView3.setTextColor(Color.parseColor("#8899BB"));
        linearLayout2.addView(textView3);
        final TextView textView4 = new TextView(this);
        textView4.setText(iArr[0] + "px");
        textView4.setTextSize(13.0f);
        textView4.setTextColor(-1);
        textView4.setPadding(dp(6), 0, dp(8), 0);
        linearLayout2.addView(textView4);
        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(200);
        seekBar.setProgress(iArr[0]);
        int i2 = i;
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.autoclicker.app.FloatingWidgetService.4
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i3, boolean z) {
                textView4.setText(i3 + "px");
                iArr[0] = i3;
                view2.invalidate();
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }
        });
        linearLayout2.addView(seekBar);
        linearLayout.addView(linearLayout2);
        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setOrientation(0);
        linearLayout3.setGravity(17);
        linearLayout3.setPadding(0, dp(8), 0, 0);
        Button button = new Button(this);
        button.setText("取消");
        button.setTextSize(13.0f);
        button.setTextColor(-1);
        button.setAllCaps(false);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dp(18));
        gradientDrawable.setColor(Color.argb(140, 120, 120, 130));
        button.setBackground(gradientDrawable);
        button.setPadding(dp(20), dp(8), dp(20), dp(8));
        button.setOnClickListener(new View.OnClickListener() { // from class: com.autoclicker.app.FloatingWidgetService$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                this.f$0.m1xd9b30464(view3);
            }
        });
        linearLayout3.addView(button);
        View view3 = new View(this);
        view3.setLayoutParams(new LinearLayout.LayoutParams(dp(16), 1));
        linearLayout3.addView(view3);
        Button button2 = new Button(this);
        button2.setText("✓ 确认");
        button2.setTextSize(13.0f);
        button2.setTextColor(-1);
        button2.setAllCaps(false);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setCornerRadius(dp(18));
        gradientDrawable2.setColor(Color.argb(200, 72, 187, 120));
        button2.setBackground(gradientDrawable2);
        button2.setPadding(dp(24), dp(8), dp(24), dp(8));
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.autoclicker.app.FloatingWidgetService$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                this.f$0.m2xbef47325(fArr, fArr2, iArr, view4);
            }
        });
        linearLayout3.addView(button2);
        linearLayout.addView(linearLayout3);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, -2);
        layoutParams2.gravity = 80;
        linearLayout.setLayoutParams(layoutParams2);
        frameLayout.addView(linearLayout);
        frameLayout.setOnTouchListener(new View.OnTouchListener() { // from class: com.autoclicker.app.FloatingWidgetService.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view4, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    fArr[0] = motionEvent.getRawX();
                    fArr2[0] = motionEvent.getRawY();
                    textView.setVisibility(8);
                    textView2.setText("已选取 (" + ((int) fArr[0]) + ", " + ((int) fArr2[0]) + ")  半径: " + iArr[0] + "px");
                    view2.invalidate();
                }
                return true;
            }
        });
        this.pickOverlay = frameLayout;
        WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams(-1, -1, i2, 776, -3);
        this.pickOverlayParams = layoutParams3;
        try {
            this.wm.addView(this.pickOverlay, layoutParams3);
        } catch (Exception e) {
            this.pickOverlay = null;
            View view4 = this.ball;
            if (view4 != null) {
                view4.setAlpha(1.0f);
            }
        }
    }

    /* JADX INFO: renamed from: lambda$startPickPosition$1$com-autoclicker-app-FloatingWidgetService, reason: not valid java name */
    /* synthetic */ void m1xd9b30464(View view) {
        removePickOverlay();
        View view2 = this.ball;
        if (view2 != null) {
            view2.setAlpha(1.0f);
        }
    }

    /* JADX INFO: renamed from: lambda$startPickPosition$2$com-autoclicker-app-FloatingWidgetService, reason: not valid java name */
    /* synthetic */ void m2xbef47325(float[] fArr, float[] fArr2, int[] iArr, View view) {
        if (fArr[0] < 0.0f || fArr2[0] < 0.0f) {
            Toast.makeText(this, "请先点击屏幕选取位置", 0).show();
            return;
        }
        int i = iArr[0];
        this.prefs.edit().putInt("sr", i).apply();
        if (this.prefs.getInt("mode", 0) == 0) {
            this.currentX = fArr[0];
            this.currentY = fArr2[0];
            this.prefs.edit().putInt("sx", (int) fArr[0]).putInt("sy", (int) fArr2[0]).apply();
            Log.e("FixVerify", "单点确认: sx=" + ((int) fArr[0]) + " sy=" + ((int) fArr2[0]) + " sr=" + i);
            Toast.makeText(this, "单点(" + ((int) fArr[0]) + "," + ((int) fArr2[0]) + ") R=" + i, 0).show();
        } else {
            StringBuilder sb = new StringBuilder(this.prefs.getString("pts", ""));
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append((int) fArr[0]).append(",").append((int) fArr2[0]).append(",").append(i);
            this.prefs.edit().putString("pts", sb.toString()).apply();
            int length = sb.toString().split("\\|").length;
            Log.e("FixVerify", "多点确认: (" + ((int) fArr[0]) + "," + ((int) fArr2[0]) + ") R=" + i + " 共" + length + "点");
            Toast.makeText(this, "点" + length + ": (" + ((int) fArr[0]) + "," + ((int) fArr2[0]) + ") R=" + i, 0).show();
        }
        removePickOverlay();
        View view2 = this.ball;
        if (view2 != null) {
            view2.setAlpha(1.0f);
        }
        updateBallIcon();
    }

    private void removePickOverlay() {
        View view = this.pickOverlay;
        if (view != null) {
            try {
                this.wm.removeView(view);
            } catch (Exception e) {
            }
            this.pickOverlay = null;
            this.pickOverlayParams = null;
        }
    }

    private void toggleMode() {
        int i = this.prefs.getInt("mode", 0) == 0 ? 1 : 0;
        this.prefs.edit().putInt("mode", i).apply();
        Toast.makeText(this, "已切换: " + (i == 0 ? "单点+半径" : "多点排序"), 0).show();
        updateModeLabel();
        updateBallIcon();
    }

    private void clearPoints() {
        this.prefs.edit().putString("pts", "").apply();
        this.currentX = this.prefs.getInt("sx", 540);
        this.currentY = this.prefs.getInt("sy", 1000);
        Toast.makeText(this, "点位已清空", 0).show();
    }

    private void toggleClicking() {
        AutoClickService autoClickService = AutoClickService.getInstance();
        if (autoClickService == null) {
            Toast.makeText(this, "请先开启无障碍服务", 0).show();
            return;
        }
        if (autoClickService.isRunning()) {
            autoClickService.stop();
        } else {
            autoClickService.setInterval(this.prefs.getInt("mi", 100), this.prefs.getInt("ma", 300));
            autoClickService.setMax(this.prefs.getInt("mc", 0));
            autoClickService.setLoopMode(this.prefs.getInt("lp", 0));
            if (this.prefs.getInt("mode", 0) == 0) {
                autoClickService.setSingle(this.prefs.getInt("sx", 540), this.prefs.getInt("sy", 1000), this.prefs.getInt("sr", 40));
                Log.e("FixVerify", "悬浮窗启动单点: (" + this.prefs.getInt("sx", 540) + "," + this.prefs.getInt("sy", 1000) + ") R=" + this.prefs.getInt("sr", 40));
            } else {
                List<AutoClickService.Target> points = parsePoints();
                Log.e("FixVerify", "悬浮窗解析多点: pts原始值=" + this.prefs.getString("pts", "") + " 解析到" + points.size() + "个点");
                if (points.isEmpty()) {
                    Toast.makeText(this, "多点模式但无有效点位，请先选取", 0).show();
                    return;
                } else {
                    autoClickService.setMulti(points);
                    Toast.makeText(this, "多点模式: " + points.size() + "个点位", 0).show();
                }
            }
            autoClickService.start();
        }
        updateBallIcon();
    }

    private List<AutoClickService.Target> parsePoints() {
        ArrayList arrayList = new ArrayList();
        String string = this.prefs.getString("pts", "");
        if (!string.isEmpty()) {
            for (String str : string.split("\\|")) {
                String[] strArrSplit = str.split(",");
                if (strArrSplit.length >= 3) {
                    try {
                        arrayList.add(new AutoClickService.Target(Integer.parseInt(strArrSplit[0]), Integer.parseInt(strArrSplit[1]), Integer.parseInt(strArrSplit[2])));
                    } catch (Exception e) {
                    }
                }
            }
        }
        return arrayList;
    }

    private void hideAll() {
        closeRadialMenu();
        View view = this.ball;
        if (view != null) {
            try {
                this.wm.removeView(view);
            } catch (Exception e) {
            }
            this.ball = null;
        }
        View view2 = this.modeLabel;
        if (view2 != null) {
            try {
                this.wm.removeView(view2);
            } catch (Exception e2) {
            }
            this.modeLabel = null;
        }
    }

    private void loadDefaults() {
        if (!this.prefs.contains("mi")) {
            this.prefs.edit().putInt("mi", 100).putInt("ma", 300).putInt("sx", 540).putInt("sy", 1000).putInt("sr", 40).putInt("mode", 0).putInt("mc", 0).putInt("lp", 0).putString("pts", "540,800,40").putInt("bx", 100).putInt("by", 400).apply();
        }
        this.currentX = this.prefs.getInt("sx", 540);
        this.currentY = this.prefs.getInt("sy", 1000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int dp(int i) {
        return (int) (i * getResources().getDisplayMetrics().density);
    }
}
