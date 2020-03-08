package br.com.robsonldo.library.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.FontRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import br.com.robsonldo.library.R;


public class TextViewPlus extends AppCompatTextView {

    private static final String TAG = "TextViewPlus";

    public TextViewPlus(Context context) {
        super(context);
    }

    public TextViewPlus(Context context, String text, @ColorInt int color, float size, @FontRes int font) {
        super(context);

        setText(text);
        setTextColor(color);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        setCustomFont(context, font);
    }

    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        int loadFont = a.getResourceId(R.styleable.TextViewPlus_loadFont,
                ViewFontDefault.getInstance().getFontRes());

        if (loadFont > 0) {
            setCustomFont(ctx, loadFont);
        }

        a.recycle();
    }

    private void setCustomFont(Context context, @FontRes int idFont) {
        Typeface tf = null;
        try {
            tf = ResourcesCompat.getFont(context, idFont);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s: %s", "Problem to load font: ", e.getMessage()));
        }

        setTypeface(tf);
    }

    private void setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s: %s", "Problem to load font: ", e.getMessage()));
        }

        setTypeface(tf);
    }
}