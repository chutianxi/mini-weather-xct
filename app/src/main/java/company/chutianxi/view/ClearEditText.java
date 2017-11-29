package company.chutianxi.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import company.chutianxi.miniweather.R;

/**
 * Created by Administrator on 2017\11\15 0015.
 */

public class ClearEditText extends EditText implements OnFocusChangeListener,TextWatcher {
    /*
    删除按钮的引用
     */
    private Drawable mClearDrawable;
    public ClearEditText(Context context) {
        this(context,null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs,android.R.attr.editTextStyle);//错误点
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /*
        获取是一个数组，数组0,1,2,3,对应着左，上，右，下 这4个位置的图片，如果没有就为null
         */
    mClearDrawable = getCompoundDrawables()[2];
    if(mClearDrawable==null)
        {
            mClearDrawable=getResources().getDrawable(R.drawable.magnifying_glass);
        }
    Log.d("mClearDrawable",""+mClearDrawable);
    mClearDrawable.setBounds(0,0,mClearDrawable.getIntrinsicWidth(),mClearDrawable.getIntrinsicHeight());
    setClearIconvisible(false);
    setOnFocusChangeListener(this);
    addTextChangedListener(this);
    }

    protected void setClearIconvisible(boolean visible) {
        Drawable right = visible?mClearDrawable:null;
        Log.d("success",""+right);
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],right,getCompoundDrawables()[3]);
        Log.d("success","set success!!!");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
/*
当输入框中内容发生变化时，回调
 */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("onchanged","onchanged!!"+charSequence.length());
    setClearIconvisible(charSequence.length()>0);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    /**
     * 设置晃动动画
     */
    public void setShakeAnimation(){
        this.setAnimation(shakeAnimation(5));
    }
    public static Animation shakeAnimation(int counts){
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(getCompoundDrawables()[2]!=null){
        if(event.getAction() == MotionEvent.ACTION_UP)
            {
            boolean touchable = event.getX()>(getWidth()-mClearDrawable.getIntrinsicWidth()-getPaddingRight())&&event.getX()<(getWidth()-getPaddingRight());
                    if(touchable)
                        setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    /*
    根据焦点的变化，来确定是否隐藏cancelicon
     */
    @Override
    public void onFocusChange(View view, boolean hasfocus) {
        Log.d("onfocus","onfocus!!");
    if(hasfocus)
    {
        setClearIconvisible(getText().length()>0);
    }
    else
        setClearIconvisible(false);
    }
}