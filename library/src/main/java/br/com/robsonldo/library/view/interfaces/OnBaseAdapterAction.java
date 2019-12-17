package br.com.robsonldo.library.view.interfaces;


public interface OnBaseAdapterAction<T> {
    void onClick(T object, Integer position);
    void onLongClick(T object, Integer position);
    void onEmpty(boolean isEmpty);
}