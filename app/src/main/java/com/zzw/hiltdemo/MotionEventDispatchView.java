package com.zzw.hiltdemo;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Created by lenna on 2020/9/22
 */
public class MotionEventDispatchView extends View {
    private Map<View, Boolean> viewMap = new HashMap();

    private Rect rect = new Rect();

    public MotionEventDispatchView(Context paramContext) {
        this(paramContext, null);
    }

    public MotionEventDispatchView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    private void onViewTouchEvent(MotionEvent paramMotionEvent) {
        for (View view : viewMap.keySet()) {
            view.getHitRect(rect);
            if (rect.contains(Math.round(paramMotionEvent.getX()), Math.round(paramMotionEvent.getY()))) {
                viewMap.put(view, true);
                MotionEvent motionEvent = MotionEvent.obtain(paramMotionEvent);
                motionEvent.setLocation(paramMotionEvent.getX() - rect.left, paramMotionEvent.getY() - rect.top);
                view.onTouchEvent(motionEvent);
                continue;
            }
            viewMap.put(view, false);
            view.onTouchEvent(paramMotionEvent);
        }
    }

    private void shouldEvent(MotionEvent event) {
        for (View view : viewMap.keySet()) {
            view.getHitRect(rect);
            if (viewMap.containsKey(view)) {
                MotionEvent motionEvent = MotionEvent.obtain(event);
                motionEvent.setLocation(event.getX() - rect.left, event.getY() - rect.top);
                view.onTouchEvent(motionEvent);
                continue;
            }
            view.onTouchEvent(event);
        }
    }

    /***
     *  是否在矩形区域
     */
    private boolean isRectArea(MotionEvent paramMotionEvent) {
        Iterator<View> iterator = viewMap.keySet().iterator();
        while (iterator.hasNext()) {
            ((View) iterator.next()).getHitRect(rect);
            if (rect.contains(Math.round(paramMotionEvent.getX()), Math.round(paramMotionEvent.getY())))
                return true;
        }
        return false;
    }

    public void addView(View paramView) {
        viewMap.put(paramView, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_DOWN) {
            shouldEvent(e);
            return true;
        }
        if (isRectArea(e)) {
            onViewTouchEvent(e);
            return true;
        }
        return false;
    }
}

