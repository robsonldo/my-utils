package br.com.robsonldo.myutils.view.watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class Mask implements TextWatcher {

    private WeakReference<EditText> editTextWeakReference;
    private String mMask;

    private String old;
    private int positionDelete;

    private IStopMask iStopMask;

    public static String unMask(String mask) {
        return mask.replaceAll("[:.\\-\\/)( ]", "");
    }

    public Mask(String mask, EditText editText) {
        this.mMask = mask;
        this.editTextWeakReference = new WeakReference<>(editText);

        old = "";
        positionDelete = 0;
    }

    public static String mask(String mask, String unmask) {
        StringBuilder result = new StringBuilder();

        unmask = unMask(unmask);
        for(int i = 0, j = 0; i < mask.length() && unmask.length() > j; i++){
            if(mask.charAt(i) == '#') {
                result.append(unmask.charAt(j));
                j++;

            }  else {
                result.append(mask.charAt(i));
            }
        }

        return result.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(iStopMask != null && iStopMask.stop(s.toString())) return;

        EditText editText = editTextWeakReference.get();

        String str = unMask(s.toString());
        StringBuilder result = new StringBuilder();

        if(str.length() < 1) return;
        if(s.toString().equals(old)) return;

        if(before == 1 && old.length() > s.toString().length()){
            positionDelete = start;

            if(start > 0 && unMask(old.charAt(start - 1) + "").equals("")){
                positionDelete++;
            }
        }

        boolean exit = false;
        int j = 0;

        for(int i = 0; (i < mMask.length()) && !exit; i++){
            if(unMask(mMask.charAt(i) + "").equals("")){
                result.append(mMask.charAt(i));
                continue;
            }

            for(; str.length() > j;){
                result.append(str.charAt(j));

                if(j + 1 == str.length()) exit = true;

                j++;
                break;
            }
        }

        old = result.toString();
        editText.setText(result.toString());

        if(positionDelete > 0 && editText.getText().toString().length() >= positionDelete){
            editText.setSelection(positionDelete);
            positionDelete = 0;
        }
        else {
            editText.setSelection(result.length());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public IStopMask getiStopMask() {
        return iStopMask;
    }

    public void setiStopMask(IStopMask iStopMask) {
        this.iStopMask = iStopMask;
    }

    public interface IStopMask {
        boolean stop(String text);
    }
}