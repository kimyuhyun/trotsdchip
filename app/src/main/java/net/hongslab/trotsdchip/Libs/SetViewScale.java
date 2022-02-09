package net.hongslab.trotsdchip.Libs;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

public class SetViewScale {
	private static final String TAG = "SetViewScale";
	public static int height, width;
	public final static int Full_w = 800;
	public SetViewScale(Activity a) {
		DisplayMetrics dm = a.getResources().getDisplayMetrics();
		SetViewScale.width = dm.widthPixels;
		SetViewScale.height = dm.heightPixels;
	}

	public SetViewScale(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		SetViewScale.width = dm.widthPixels;
		SetViewScale.height = dm.heightPixels;
	}

	public LinearLayout setCaseLineVertical(LinearLayout view, int w, int h, int dh) {
		float setW = 0, setH = 0;
		if(w != h){
			int x = width * 800;
			float ContentW = x/Full_w;
			float y =  1114*ContentW;
			float ContentH = (int)(y/800);

			float ratio = (float)w/(float)h;

			setW = width * 9f/ 10f;
			setH = setW * (float)h / (float)w;
			Log.d("setW , setH", "setW ; " + Math.round(setW) +"setH :"+ Math.round(setH));
			if(ContentH <= setH){
				setH = ContentH * 9f/ 10f;
				setW = setH *(float) w /(float)h;
			}


			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Math.round(setW), Math.round(setH));
			view.setLayoutParams(lp);
		}else{
			setW = width * 9f/ 10f;
			setH = setW;


			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Math.round(setW), Math.round(setH));
			view.setLayoutParams(lp);
		}
		return view;
	}

	public LinearLayout setCaseLineHorizontal(LinearLayout view, int w, int h, int dh) {
		float setW = 0, setH = 0;

		int x = width * 800;
		float ContentW = x/Full_w;
		float y =  1114*ContentW;
		float ContentH = (int)(y/800);

		float ratio = (float)w/(float)h;


		setW = width*9f/10f;
		setH = setW * (float) w / (float)h;
		if(setH >= dh){
			setH = dh * 9f/10f;
			setW = setH * (float)h / (float)w;
		}

		RelativeLayout.LayoutParams lp;
		Log.d("dh", "dh : " + dh);
		Log.d("width", "width : " + width);
		Log.d("ContentH", "ContentH : " + ContentH);
		Log.d("setW , setH", "setW ; " + Math.round(setW) +"setH :"+ Math.round(setH));
		if(setH >= ContentH){
			setH = ContentH*99f/100f;
			setW = setH * (float)w / (float)h;
		}

		lp = new RelativeLayout.LayoutParams(Math.round(setW), Math.round(setH));
		view.setLayoutParams(lp);

		return view;
	}

	public LinearLayout setBackImage(LinearLayout view, int w, int h) {
		float setW = 0, setH = 0;

		Log.d("setW , setH", "setW ; " + Math.round(w) +"setH :"+ Math.round(h));

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
		view.setLayoutParams(lp);
		return view;
	}

	public LinearLayout setViewScale(LinearLayout view, int w, int h) {
		int setW = 0, setH = 0;
		view = view;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

//		LayoutParams lp = new LayoutParams(setW, setH);
		
		ViewGroup.LayoutParams lp = null;
		if(view.getLayoutParams() instanceof LayoutParams)
		{
			lp = new LayoutParams(setW, setH);
		} else if(view.getLayoutParams() instanceof RelativeLayout.LayoutParams)
		{
			lp = new RelativeLayout.LayoutParams(setW, setH);
		} else if(view.getLayoutParams() instanceof FrameLayout.LayoutParams)
		{
			lp = new FrameLayout.LayoutParams(setW, setH);
		} else if(view.getLayoutParams() instanceof ScrollView.LayoutParams)
		{
			lp = new ScrollView.LayoutParams(setW, setH);
		} else
		{
			lp = new LayoutParams(setW, setH);
		}
		
		try {
			view.setLayoutParams(lp);
		} catch (NullPointerException e) {
			Log.e(TAG , Log.getStackTraceString(e));
		}
		return view;
	}

	public TextView setViewScale(TextView view, int w, int h) {
		//int tmp = h / 2;
		//h = h + tmp;
		int setW = 0, setH = 0;
		ViewGroup.LayoutParams lp;
		if(w == 0 && h == 0) {
			if ( view.getLayoutParams() instanceof FrameLayout.LayoutParams ) {
				lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			} else {
				lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
		} else {
			int x = width * w;
			setW = x/Full_w;
			int y = h*setW;
			setH = y/w;
			if(setH == 0) {
				setH = 1;
			}
			if ( view.getLayoutParams() instanceof FrameLayout.LayoutParams ) {
				lp = new FrameLayout.LayoutParams(setW, setH);
			} else {
				lp = new LayoutParams(setW, setH);
			}
		}
		view.setLayoutParams(lp);
		return view;
	}


	public View setViewScale(View view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public Spinner setViewScale(Spinner view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public LinearLayout setViewScale(LinearLayout view, int padding) {
		int setW = 0, setH = 0;

		LayoutParams lp  = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.setMargins(padding, padding/2, 0, 0);
		view.setLayoutParams(lp);
		return view;
	}

	public LinearLayout setViewScale(LinearLayout view, int w, int h, int weight) {
		int setW = 0, setH = 0;
		view = view;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH, weight);
		view.setLayoutParams(lp);
		return view;
	}

	public LinearLayout setViewScale(LinearLayout view, int w, int h, int weight, int magine) {
		int setW = 0, setH = 0;
		view = view;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH, weight);
		lp.setMargins(magine, magine, magine, magine);
		view.setLayoutParams(lp);
		return view;
	}

	public RelativeLayout setViewScale(RelativeLayout view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public RelativeLayout setViewScale(RelativeLayout view, int w, int h, int weight) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH, weight);
		view.setLayoutParams(lp);
		return view;
	}

	public RelativeLayout setViewScale(RelativeLayout view, int w, int h, int weight, int magine) {
		int setW = 0, setH = 0;
		view = view;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(setW, setH);
		lp.setMargins(magine, magine, magine, magine);
		view.setLayoutParams(lp);
		return view;
	}

	public RelativeLayout setEditScale(RelativeLayout view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public HorizontalScrollView setViewScale(HorizontalScrollView view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		Log.d("setW", Integer.toString(setW));

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}
	public HorizontalScrollView setViewScale(HorizontalScrollView view, int w, int h, int weight) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		Log.d("setW", Integer.toString(setW));

		LayoutParams lp = new LayoutParams(setW, setH, weight);
		view.setLayoutParams(lp);
		return view;
	}

	public ImageView setViewScale(ImageView view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public ImageButton setViewScale(ImageButton view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public ImageView setViewScale(ImageView view, int w, int h, int weight) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH, weight);
		view.setLayoutParams(lp);
		return view;
	}
	public ImageView setViewScale(ImageView view, int w, int h, int weight, int magin) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH, weight);
		lp.setMargins(magin, magin, magin, magin);
		view.setLayoutParams(lp);
		return view;
	}

	public ViewPager setViewScale(ViewPager view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}
		View v = (View)view.getParent();

		if(v instanceof RelativeLayout){
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(setW, setH);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			view.setLayoutParams(lp);
		}else{
			LayoutParams lp= new LayoutParams(setW, setH);
			view.setLayoutParams(lp);
		}

		return view;
	}


	public CheckBox setViewScale(CheckBox view, int w, int h) {
		int setW = 0, setH = 0;
		LayoutParams lp;
		if(w == 0 && h == 0)
			lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		else{
			int x = width * w;
			setW = x/Full_w;
			int y = h*setW;
			setH = y/w;
			if(setH == 0) {
				setH = 1;
			}
			lp = new LayoutParams(setW, setH);
		}
		view.setLayoutParams(lp);
		return view;
	}



	public ScrollView setViewScale(ScrollView view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}



	public ScrollView setViewScale(ScrollView view, int w, int h, int weight) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH, weight);
		view.setLayoutParams(lp);
		return view;
	}

	public FrameLayout setViewScale(FrameLayout view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public EditText setViewScale(EditText view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public ListView setViewScale(ListView view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public int getH(int w, int h){
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		return setH;
	}
	public int getW(int w, int h){
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;

		return setW;
	}
	public int getW(int w){
		//int tmp = w / 2;
		//w = w + tmp;
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;

		return setW;
	}


	public SeekBar setViewScale(SeekBar view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}


		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}


	public WebView setViewScale(WebView view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);

		return view;
	}

	public Button setViewScale(Button view, int w, int h) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}


		LayoutParams lp = new LayoutParams(setW, setH);
		view.setLayoutParams(lp);
		return view;
	}

	public View setPadding(View v, int i)
	{
		v.setPadding(getW(i),getW(i),getW(i),getW(i));

		return v;
	}
	public View setPadding(View v, int i, int j, int k, int l)
	{
		v.setPadding(getW(i),getW(j),getW(k),getW(l));

		return v;
	}
	public View setPaddingL(View v, int i)
	{
		v.setPadding(getW(i),0,0,0);

		return v;
	}
	public View setPaddingR(View v, int i)
	{
		v.setPadding(0,0,getW(i),0);

		return v;
	}
	public View setPaddingU(View v, int i)
	{
		v.setPadding(0,getW(i),0,0);

		return v;
	}
	public View setPaddingD(View v, int i)
	{
		v.setPadding(0,0,0,getW(i));

		return v;
	}

	public TextView setTextSize(TextView v, int i) {
		v.setTextSize(TypedValue.COMPLEX_UNIT_PX, getW(i));

		return  v;
	}

	public View setLayoutMargin(View v, int w, int h, int i, int j, int k, int l) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		lp.setMargins(getW(i),getW(j),getW(k),getW(l));
		v.setLayoutParams(lp);
		return v;
	}

	public View setLayoutMargin(View v, int w, int h, int i) {
		int setW = 0, setH = 0;
		int x = width * w;
		setW = x/Full_w;
		int y = h*setW;
		setH = y/w;
		if(setH == 0) {
			setH = 1;
		}

		LayoutParams lp = new LayoutParams(setW, setH);
		lp.setMargins(getW(i),getW(i),getW(i),getW(i));
		v.setLayoutParams(lp);
		return v;
	}
}
