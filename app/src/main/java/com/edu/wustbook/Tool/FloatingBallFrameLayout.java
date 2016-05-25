package com.edu.wustbook.Tool;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.edu.wustbook.View.FloatingBall;

import java.util.ArrayList;

public class FloatingBallFrameLayout extends FrameLayout implements FloatingBall.click {
    private ArrayList<FloatingBall> balls;
    private int ballcount;
    private FloatingBall centerBall;

    private enum ballTags {ONE, TWO, THRE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN}

    ;

    private final double FULL_WIDTH = 360.0;
    private final float RADIUS = 70;

    private Context context;

    private FloatingBall.Position screenCenter;

    private CenterBallFunction function;

    public static interface CenterBallFunction {
        public abstract void onClick(View v);
    }

    public FloatingBallFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        ballcount = 0;
        this.context = context;
    }

    public FloatingBallFrameLayout(Context context) {
        this(context, null);
    }


    public void setCenterBall(FloatingBall centerBall) {
        if (centerBall == null) {
            centerBall = new FloatingBall(context);
            centerBall.setBallCenter(getScreenCenter());
            centerBall.setRadius(RADIUS);
            centerBall.setClickListener(this);
            centerBall.setText("换一批");
        }
        this.centerBall = centerBall;
        this.addView(this.centerBall);
    }

    public FloatingBall.Position getScreenCenter() {
        if (screenCenter == null) {
            screenCenter = new FloatingBall.Position();
            screenCenter.setX(ScreenUtils.getScreenWidth(context) / 2);
            screenCenter.setY(ScreenUtils.getScreenHeight(context) / 2 - RADIUS / 2);
        }
        return screenCenter;
    }

    public FloatingBall getCenterBall() {
        return centerBall;
    }


    public void addBall(FloatingBall ball) {
        this.addView(ball);
        ball.setTag(ballcount);
        ballcount++;
    }

    public ArrayList<FloatingBall> getBalls() {
        return balls;
    }

    public void setBalls(ArrayList<FloatingBall> balls) {
        this.balls = balls;
    }

    public void setBallcount(int ballcount) {
        this.ballcount = ballcount;
        if (balls == null)
            balls = new ArrayList<FloatingBall>();
        balls.ensureCapacity(ballcount);

    }

    public void setFunction(CenterBallFunction function) {
        this.function = function;
    }

    @Override
    public void onClick(View v) {
        if (function != null)
            function.onClick(v);
    }
}
