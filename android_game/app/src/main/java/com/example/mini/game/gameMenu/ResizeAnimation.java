package com.example.mini.game.gameMenu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * Created by Łukasz on 2015-02-03.
 */
public class ResizeAnimation extends Animation {
    private RelativeLayout relativeLayout;

    private int mToWidth;
    private int mStartWidth;


    public ResizeAnimation(RelativeLayout rl, int toWidth) {
        mToWidth = toWidth;
        relativeLayout = rl;
        mStartWidth = relativeLayout.getWidth();
        setDuration(300);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = mStartWidth + (int) ((mToWidth - mStartWidth) * interpolatedTime);
        relativeLayout.getLayoutParams().width = newWidth;
        relativeLayout.requestLayout();
    }
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}