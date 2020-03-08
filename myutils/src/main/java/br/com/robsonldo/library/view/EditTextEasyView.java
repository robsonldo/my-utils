package br.com.robsonldo.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FontRes;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

import br.com.robsonldo.library.R;
import br.com.robsonldo.library.view.watcher.Mask;
import br.com.robsonldo.library.view.watcher.MaskMoney;

public class EditTextEasyView extends AppCompatEditText {

    private static final String TAG = "EditTextEasyView";

    public final static int VALIDATION_TYPE_NONE = -1;
    public final static int VALIDATION_TYPE_EMAIL = 0;
    public final static int VALIDATION_TYPE_CPF = 1;
    public final static int VALIDATION_TYPE_CNPJ = 2;

    private String mMask;
    private Boolean mIsEmpty;
    private String mMessageErrorEmpty;
    private String mMessageErrorGeneral;
    private String mValidationTypeEnum;
    private String mRegex;
    private String mEmptyText;

    private Boolean mIsValidateActive;
    private boolean isTrim;

    private ValidationType mValidationType;
    private CustomValidation customValidation;

    private Mask mask;
    private MaskMoney maskMoney;

    private static View mViewFocus;
    private TextInputLayout mTextInputLayout;
    private ActionBinding mActionBinding;

    public EditTextEasyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditTextEasyView);

        mMask = a.getString(R.styleable.EditTextEasyView_mask);
        boolean mMaskMoney = a.getBoolean(R.styleable.EditTextEasyView_maskMoney, false);
        mIsEmpty = a.getBoolean(R.styleable.EditTextEasyView_empty, true);
        mMessageErrorEmpty = a.getString(R.styleable.EditTextEasyView_messageEmpty);
        mMessageErrorGeneral = a.getString(R.styleable.EditTextEasyView_messageGeneral);
        mRegex = a.getString(R.styleable.EditTextEasyView_regex);
        mValidationTypeEnum = a.getString(R.styleable.EditTextEasyView_validationType);
        mEmptyText = a.getString(R.styleable.EditTextEasyView_emptyText);
        isTrim = a.getBoolean(R.styleable.EditTextEasyView_trim, true);
        int loadFont = a.getResourceId(R.styleable.EditTextEasyView_loadFont,
                ViewFontDefault.getInstance().getFontRes());

        a.recycle();

        mValidationType = new ValidationType();
        mIsValidateActive = false;

        mMessageErrorGeneral = mMessageErrorGeneral == null ? "" : mMessageErrorGeneral;

        if(mMaskMoney){
            maskMoney = new MaskMoney(this, new Locale("pt", "br"));
            setInputType(InputType.TYPE_CLASS_NUMBER);
            addTextChangedListener(maskMoney);

        } else if (!"".equals(mMask) && mMask != null) {
            mask = new Mask(mMask, this);
            addTextChangedListener(mask);
        }

        if (loadFont > 0) {
            setCustomFont(context, loadFont);
        }

        onTextChanged();
    }

    public String getTextToString() {
        String text = getText() != null ? getText().toString() : "";
        return isTrim ? text.trim() : text;
    }

    public String unMask() {
        return mask != null ? Mask.unMask(getText() != null ? getText().toString() : "") : "";
    }

    public Double unMaskMoney() {
        return maskMoney != null ? maskMoney.unmask().doubleValue() : null;
    }

    public static boolean validateChields(ViewGroup viewGroup) {
        return validateChields(viewGroup, false);
    }

    public static boolean validateChieldsFocus(ViewGroup viewGroup) {
        mViewFocus = null;
        boolean valid = validateChields(viewGroup, true);

        if (mViewFocus != null) {
            mViewFocus.requestFocus();
        }

        return valid;
    }

    private static boolean validateChields(ViewGroup viewGroup, boolean focus) {
        boolean isValid = true;

        for (int index = 0; index < viewGroup.getChildCount(); ++index) {
            if (viewGroup.getChildAt(index).getVisibility() != VISIBLE) continue;

            EditTextEasyView editTextEasyView = null;

            if (viewGroup.getChildAt(index) instanceof EditTextEasyView) {
                editTextEasyView = (EditTextEasyView) viewGroup.getChildAt(index);

                if (!editTextEasyView.validate(null)) {

                    if(focus && mViewFocus == null) {
                        seViewFocus(editTextEasyView);
                    }

                    isValid = false;
                }

            } else if (viewGroup.getChildAt(index) instanceof TextInputLayout) {
                TextInputLayout textInputLayout = (TextInputLayout) viewGroup.getChildAt(index);

                if (textInputLayout.getChildCount() > 0 && textInputLayout.getEditText()
                        instanceof EditTextEasyView) {

                    editTextEasyView = (EditTextEasyView) textInputLayout.getEditText();

                    if (!editTextEasyView.validate(textInputLayout)) {

                        if(focus && mViewFocus == null) {
                            seViewFocus(textInputLayout);
                        }

                        isValid = false;
                    }
                }

            } else if (viewGroup.getChildAt(index) instanceof ViewGroup) {
                if (!validateChields((ViewGroup) viewGroup.getChildAt(index), focus)) {

                    if(focus && mViewFocus == null) {
                        seViewFocus(returnFirstTextInputLayoutOrEditTextEasyView((ViewGroup) viewGroup.getChildAt(index)));
                    }

                    isValid = false;
                }
            }
        }

        return isValid;
    }

    private static void seViewFocus(View viewFocus) {
        if (viewFocus != null) {
            mViewFocus = viewFocus;
        }
    }

    public static View returnFirstTextInputLayoutOrEditTextEasyView(ViewGroup viewGroup) {
        for (int index = 0; index < viewGroup.getChildCount(); ++index) {

            if (viewGroup.getChildAt(index) instanceof EditTextEasyView) {
                return viewGroup.getChildAt(index);

            } else if (viewGroup.getChildAt(index) instanceof TextInputLayout) {
                return viewGroup.getChildAt(index);
            }
        }

        return null;
    }

    public void validateCustom() {
        mIsEmpty = customValidation.isEmpty();
        mRegex = customValidation.regex();

        if (customValidation.messageErrorValidation() != null && !"".equals(customValidation.messageErrorValidation())) {
            mMessageErrorGeneral = customValidation.messageErrorValidation();
        }

        if (customValidation.validationType() != VALIDATION_TYPE_NONE) {
            mValidationTypeEnum = String.valueOf(customValidation.validationType());
        }
    }

    public boolean validate(TextInputLayout textInputLayout) {
        mTextInputLayout = textInputLayout;
        return validate();
    }

    public boolean validate() {
        mIsValidateActive = true;
        if (mIsEmpty && "".equals(getTextToString()) && customValidation == null) {
            correct(mTextInputLayout);
            return true;
        }

        if(customValidation != null) validateCustom();

        if (!mIsEmpty && "".equals(getTextToString()) || (mEmptyText != null && mEmptyText.equals(getTextToString()))) {
            String msg = mMessageErrorEmpty != null ? mMessageErrorEmpty : mMessageErrorGeneral;
            wrong(msg, mTextInputLayout);

            return false;
        }

        if (mRegex != null && !"".equals(mRegex)) {
            if (!getTextToString().matches(mRegex)) {
                wrong(mMessageErrorGeneral, mTextInputLayout);

                return false;
            }
        }

        if (mValidationTypeEnum != null && !"".equals(mValidationTypeEnum)) {
            if (!mValidationType.validate(getTextToString(), Integer.parseInt
                    (mValidationTypeEnum))) {

                wrong(mMessageErrorGeneral, mTextInputLayout);

                return false;
            }
        }

        if (customValidation != null) {
            boolean isCustomValid = customValidation.custom();

            if (isCustomValid) {
                correct(mTextInputLayout);
                return true;
            }

            wrong(mMessageErrorGeneral, mTextInputLayout);
            return false;

        } else {
            correct(mTextInputLayout);
            return true;
        }
    }

    public void onTextChanged() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsValidateActive) {
                    validate();
                }
                binding(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onBinding(ActionBinding actionBinding) {
        mActionBinding = actionBinding;
    }

    private void binding(String text) {
        if (mActionBinding != null) mActionBinding.onBinding(text);
    }

    public void wrong(String msg, TextInputLayout textInputLayout) {
        if (textInputLayout != null) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(msg);
        } else {
            setError(msg);
        }
    }

    public void correct(TextInputLayout textInputLayout) {
        if (textInputLayout != null) {
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError(null);
        } else {
            setError(null);
        }
    }

    public String getMask() {
        return mMask;
    }

    public Mask getMaskWatcher() {
        return mask;
    }

    public void setMask(String mMask) {
        this.mMask = mMask;

        if (mask != null) {
            removeTextChangedListener(mask);
        }

        mask = new Mask(this.mMask , this);
        addTextChangedListener(mask);
    }

    public Boolean getIsEmpty() {
        return mIsEmpty;
    }

    public void setIsEmpty(Boolean mIsEmpty) {
        this.mIsEmpty = mIsEmpty;
    }

    public String getMessageErrorEmpty() {
        return mMessageErrorEmpty;
    }

    public void setMessageErrorEmpty(String mMessageErrorEmpty) {
        this.mMessageErrorEmpty = mMessageErrorEmpty;
    }

    public String getMessageErrorGeneral() {
        return mMessageErrorGeneral;
    }

    public void setMessageErrorGeneral(String mMessageErrorGeneral) {
        this.mMessageErrorGeneral = mMessageErrorGeneral;
    }

    public String getValidationTypeEnum() {
        return mValidationTypeEnum;
    }

    public void setValidationTypeEnum(String mValidationTypeEnum) {
        this.mValidationTypeEnum = mValidationTypeEnum;
    }

    public String getRegex() {
        return mRegex;
    }

    public void setRegex(String mRegex) {
        this.mRegex = mRegex;
    }

    public class ValidationType {

        private int EMAIL = 0;
        private int CPF = 1;
        private int CNPJ = 2;

        public boolean validate(String text, int flag) {
            if (EMAIL == flag) return validationEmail(text);
            else if (CPF == flag) return validateCpf(text);
            else if (CNPJ == flag) return validateCnpj(text);
            else return false;
        }

        private boolean validationEmail(String email) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        private boolean validateCpf(String cpf) {
            cpf = cpf.replaceAll("[.\\-\\/)( ]", "");
            if (!cpf.matches("\\d+")) return false;

            if (cpf.equals("00000000000") || cpf.equals("11111111111") ||
                    cpf.equals("22222222222") || cpf.equals("33333333333") ||
                    cpf.equals("44444444444") || cpf.equals("55555555555") ||
                    cpf.equals("66666666666") || cpf.equals("77777777777") ||
                    cpf.equals("88888888888") || cpf.equals("99999999999") ||
                    (cpf.length() != 11)) {

                return false;
            }

            char dig10, dig11;
            int sm, i, r, num, peso;

            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char) (r + 48);

            sm = 0;
            peso = 11;

            for (i = 0; i < 10; i++) {
                num = (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) dig11 = '0';
            else dig11 = (char) (r + 48);

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        }

        private boolean validateCnpj(String cnpj) {
            cnpj = cnpj.replaceAll("[.\\-\\/)( ]", "");
            if (!cnpj.matches("\\d+")) return false;

            if (cnpj.equals("00000000000000") || cnpj.equals("11111111111111") ||
                    cnpj.equals("22222222222222") || cnpj.equals("33333333333333") ||
                    cnpj.equals("44444444444444") || cnpj.equals("55555555555555") ||
                    cnpj.equals("66666666666666") || cnpj.equals("77777777777777") ||
                    cnpj.equals("88888888888888") || cnpj.equals("99999999999999") ||
                    (cnpj.length() != 14)) {
                return false;
            }

            char dig13, dig14;
            int sm, i, r, num, peso;

            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {

                num = cnpj.charAt(i) - 48;
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else dig13 = (char) ((11 - r) + 48);

            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else dig14 = (char) ((11 - r) + 48);

            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        }
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

    public void setCustomValidation(CustomValidation customValidation) {
        this.customValidation = customValidation;
    }

    public interface CustomValidation {
        boolean custom();
        boolean isEmpty();
        String regex();
        int validationType();
        String messageErrorValidation();
        String emptyText();
    }

    public interface ActionBinding {
        void onBinding(String text);
    }
}