package com.edu.wustbook.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.edu.wustbook.R;


public class FloatingBall extends View implements OnClickListener{

	private Context context;

	private int color;
	private Style style;

	private Position ballCenter;
	private String text;
	private int textSize;


	private float radius;
	private float strokeWidth;

	private StaticLayout layout;
	
	public click clickListener;
	
	public interface click{
		public void onClick(View v);
	}

	public static class Position{
		private float x, y;
		public Position(){x=y=0;}
		public Position(float x,float y){
			this.x=x;
			this.y=y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public void setX(float x) {
			this.x = x;
		}

		public void setY(float y) {
			this.y = y;
		}

		public Position newOtherPostion(int xDistance,int yDistance){
			if(xDistance==0&&yDistance==0)
				return null;
			else{
				Position newPosition =new Position();
				newPosition.setX(this.x+xDistance);
				newPosition.setY(this.y+yDistance);
				return newPosition;
			}
		}
	}


	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public FloatingBall(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr, 0);
		this.context=context;
		radius=50;
		color = Color.BLACK;
		style = Style.STROKE;
		strokeWidth = 5;
		textSize=30;
		ballCenter=new Position();
	}

	public FloatingBall(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.FloatingBall);
	}

	public FloatingBall(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(style);
		canvas.drawCircle(ballCenter.getX(), ballCenter.getY(), radius, paint);
		TextPaint textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
		int width= (int) (radius*Math.sqrt(2.0));
		layout=new StaticLayout(text, textPaint, width, StaticLayout.Alignment.ALIGN_NORMAL, 1, 0, false);
//		StaticLayout.Builder builder=StaticLayout.Builder.obtain(text,0,text.length(),textPaint,width);
//		builder.setEllipsizedWidth(16);
//		layout=builder.build();
		canvas.translate(ballCenter.getX()-layout.getWidth()/2, ballCenter.getY()-layout.getHeight()/2);
		layout.draw(canvas);
		//canvas.drawText(text, ballCenter.getX(), ballCenter.getY(), paint);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	public void setClickListener(click clickListener) {
		this.clickListener = clickListener;
	}

	public void setBallCenter(Position ballCenter) {
		this.ballCenter = ballCenter;
	}

	public void setBallCenter(float cx,float cy){
		ballCenter=new Position(cx,cy);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	@Override
	public void onClick(View v) {
		v.setBackgroundColor(Color.BLUE);
		clickListener.onClick(v);
	}

}
