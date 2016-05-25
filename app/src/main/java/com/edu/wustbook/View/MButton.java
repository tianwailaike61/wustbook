package com.edu.wustbook.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.wustbook.R;
import com.edu.wustbook.Tool.IMGUtils;
import com.edu.wustbook.Tool.ScreenUtils;

public class MButton extends RelativeLayout {
	private Context context;

	private ImageView imgView;
	private TextView textView;
	private int pictureSize;

	private final int[] StrIds=new int[]{R.string.lookover,R.string.collect,
			R.string.collected,R.string.buy,R.string.delete,R.string.complete};
	private final int[] IMGIds=new int[]{R.drawable.see,R.drawable.uncollected,
			R.drawable.collection,R.drawable.shopping,R.drawable.delete,R.drawable.complete};

	public MButton(Context context) {
		this(context, null);
	}

	public MButton(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.item_mbutton, this, true);
		this.imgView = (ImageView) findViewById(R.id.buttonimg);
		this.textView = (TextView) findViewById(R.id.buttontext);
		this.setClickable(true);
		this.setFocusable(true);
		this.setBackgroundResource(R.drawable.button_bg);
		pictureSize = ScreenUtils.getScreenWidth(context) / 10;
	}

	public void setTextVisibility(int visibility) {
		this.textView.setVisibility(visibility);
	}

	public void setText(String text) {
		this.textView.setText(text);
		getPicture();
	}

	public String getText() {
		return this.textView.getText().toString().trim();
	}

	private int getImgResource() {
		Resources rs=context.getResources();
		String s=textView.getText().toString();
		for(int i=0;i<StrIds.length;i++){
			if(rs.getString(StrIds[i]).equals(s)){
				return IMGIds[i];
			}
		}
		return -1;
	}

	private void getPicture() {
		int resourceID = getImgResource();
		if(resourceID>0) {
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
					resourceID);
			Drawable drawable = IMGUtils.zoomBitmap(bitmap, pictureSize, pictureSize);
			this.imgView.setBackgroundDrawable(drawable);
		}else{
			imgView.setVisibility(View.GONE);
		}
	}
}