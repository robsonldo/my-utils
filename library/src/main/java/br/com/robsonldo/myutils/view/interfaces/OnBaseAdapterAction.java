package br.com.robsonldo.myutils.view.interfaces;


public interface OnBaseAdapterAction<T> {
    void onClick(T object, Integer position);
    void onLongClick(T object, Integer position);
    void onEmpty(boolean isEmpty);
}