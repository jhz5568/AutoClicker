package com.autoclicker.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class AutoClickService extends AccessibilityService {
    public static final int MODE_MULTI = 1;
    public static final int MODE_SINGLE = 0;
    private static AutoClickService instance;
    private int count;
    private int idx;
    private View indicator;
    private int maxClicks;
    private int roundCount;
    private boolean running;
    private BroadcastReceiver scrOff;
    private WindowManager wm;
    private final Handler h = new Handler(Looper.getMainLooper());
    private final Random rnd = new Random();
    private int mode = 0;
    private float sX = 540.0f;
    private float sY = 1000.0f;
    private float sR = 30.0f;
    private final List<Target> targets = new ArrayList();
    private int loopMode = 0;
    private long minI = 80;
    private long maxI = 200;
    private final Runnable loop = new Runnable() { // from class: com.autoclicker.app.AutoClickService.2
        @Override // java.lang.Runnable
        public void run() {
            if (AutoClickService.this.running) {
                try {
                    AutoClickService.this.click();
                } catch (Exception e) {
                    Log.e("FixVerify", "click()异常: " + e.getMessage());
                }
                AutoClickService.access$208(AutoClickService.this);
                if (AutoClickService.this.mode == 1 && !AutoClickService.this.targets.isEmpty() && AutoClickService.this.idx == 0) {
                    AutoClickService.access$608(AutoClickService.this);
                    Log.e("FixVerify", "多点一轮完成 round=" + AutoClickService.this.roundCount + " 总点击=" + AutoClickService.this.count);
                    if (AutoClickService.this.loopMode == 1) {
                        Log.e("FixVerify", "单次模式，停止");
                        AutoClickService.this.stop();
                        AutoClickService.this.sendSb();
                        return;
                    }
                }
                if (AutoClickService.this.maxClicks <= 0 || AutoClickService.this.count < AutoClickService.this.maxClicks) {
                    long jAbs = AutoClickService.this.minI;
                    if (AutoClickService.this.maxI > AutoClickService.this.minI) {
                        jAbs += Math.abs(AutoClickService.this.rnd.nextLong() % (AutoClickService.this.maxI - AutoClickService.this.minI));
                    }
                    AutoClickService.this.h.postDelayed(this, jAbs);
                    return;
                }
                AutoClickService.this.stop();
                AutoClickService.this.sendSb();
            }
        }
    };

    static /* synthetic */ int access$208(AutoClickService autoClickService) {
        int i = autoClickService.count;
        autoClickService.count = i + 1;
        return i;
    }

    static /* synthetic */ int access$608(AutoClickService autoClickService) {
        int i = autoClickService.roundCount;
        autoClickService.roundCount = i + 1;
        return i;
    }

    public static AutoClickService getInstance() {
        return instance;
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
        stop();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        stop();
        removeDot();
        try {
            BroadcastReceiver broadcastReceiver = this.scrOff;
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
        }
        instance = null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.wm = (WindowManager) getSystemService("window");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.autoclicker.app.AutoClickService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                AutoClickService.this.stop();
            }
        };
        this.scrOff = broadcastReceiver;
        registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
    }

    public void setSingle(float f, float f2, float f3) {
        this.mode = 0;
        this.sX = f;
        this.sY = f2;
        this.sR = f3;
    }

    public void setMulti(List<Target> list) {
        this.mode = 1;
        this.targets.clear();
        if (list != null) {
            this.targets.addAll(list);
        }
        this.idx = 0;
    }

    public void setInterval(long j, long j2) {
        long jMax = Math.max(j, 10L);
        this.minI = jMax;
        this.maxI = Math.max(j2, jMax);
    }

    public void setMax(int i) {
        this.maxClicks = i;
    }

    public void setLoopMode(int i) {
        this.loopMode = i;
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getCount() {
        return this.count;
    }

    public int getRoundCount() {
        return this.roundCount;
    }

    public int getMode() {
        return this.mode;
    }

    public float getSR() {
        return this.sR;
    }

    public float getSX() {
        return this.sX;
    }

    public float getSY() {
        return this.sY;
    }

    public long getMaxI() {
        return this.maxI;
    }

    public long getMinI() {
        return this.minI;
    }

    public void start() {
        if (this.running) {
            stop();
        }
        this.count = 0;
        this.idx = 0;
        this.roundCount = 0;
        this.running = true;
        Log.e("FixVerify", "===== 开始连点 mode=" + this.mode + " loopMode=" + this.loopMode + " targets=" + this.targets.size() + " =====");
        this.h.post(this.loop);
    }

    public void stop() {
        this.running = false;
        this.h.removeCallbacks(this.loop);
        removeDot();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void click() {
        float f;
        float f2;
        if (this.mode == 1 && !this.targets.isEmpty()) {
            Target target = this.targets.get(this.idx);
            double dNextFloat = this.rnd.nextFloat() * 2.0f;
            Double.isNaN(dNextFloat);
            double d = dNextFloat * 3.141592653589793d;
            double d2 = target.r;
            double dSqrt = Math.sqrt(this.rnd.nextFloat());
            Double.isNaN(d2);
            double d3 = d2 * dSqrt;
            double d4 = target.x;
            double dCos = Math.cos(d) * d3;
            Double.isNaN(d4);
            f = (float) (d4 + dCos);
            double d5 = target.y;
            double dSin = d3 * Math.sin(d);
            Double.isNaN(d5);
            f2 = (float) (d5 + dSin);
            Log.e("FixVerify", "多点点击[" + this.idx + "/" + this.targets.size() + "]: 中心=(" + ((int) target.x) + "," + ((int) target.y) + ") 实际=(" + ((int) f) + "," + ((int) f2) + ") R=" + ((int) target.r));
            this.idx = (this.idx + 1) % this.targets.size();
        } else {
            double dNextFloat2 = this.rnd.nextFloat() * 2.0f;
            Double.isNaN(dNextFloat2);
            double d6 = dNextFloat2 * 3.141592653589793d;
            double d7 = this.sR;
            double dSqrt2 = Math.sqrt(this.rnd.nextFloat());
            Double.isNaN(d7);
            double d8 = d7 * dSqrt2;
            double d9 = this.sX;
            double dCos2 = Math.cos(d6) * d8;
            Double.isNaN(d9);
            f = (float) (d9 + dCos2);
            double d10 = this.sY;
            double dSin2 = d8 * Math.sin(d6);
            Double.isNaN(d10);
            f2 = (float) (d10 + dSin2);
            Log.e("FixVerify", "单点点击: 中心=(" + ((int) this.sX) + "," + ((int) this.sY) + ") 实际=(" + ((int) f) + "," + ((int) f2) + ") 半径=" + ((int) this.sR));
        }
        showDot(f, f2);
        Path path = new Path();
        path.moveTo(f, f2);
        path.lineTo(f + 0.5f, f2 + 0.5f);
        dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path, 0L, 20L)).build(), null, null);
    }

    private void showDot(float f, float f2) {
        try {
            removeDot();
            View view = new View(this);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(1);
            gradientDrawable.setColor(Color.argb(100, 255, 130, 130));
            gradientDrawable.setStroke(2, Color.argb(180, 255, 255, 255));
            view.setBackground(gradientDrawable);
            this.indicator = view;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(36, 36, ((int) f) - 18, ((int) f2) - 18, Build.VERSION.SDK_INT >= 26 ? 2032 : 2006, 280, -3);
            layoutParams.gravity = 8388659;
            this.wm.addView(this.indicator, layoutParams);
            this.h.postDelayed(new Runnable() { // from class: com.autoclicker.app.AutoClickService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.removeDot();
                }
            }, 200L);
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeDot() {
        WindowManager windowManager;
        try {
            View view = this.indicator;
            if (view == null || (windowManager = this.wm) == null) {
                return;
            }
            windowManager.removeView(view);
            this.indicator = null;
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSb() {
        Intent intent = new Intent("com.autoclicker.app.STOP");
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    public static class Target {
        public float r;
        public float x;
        public float y;

        public Target(float f, float f2, float f3) {
            this.x = f;
            this.y = f2;
            this.r = f3;
        }
    }
}
