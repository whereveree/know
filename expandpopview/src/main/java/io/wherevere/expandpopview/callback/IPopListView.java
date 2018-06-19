package io.wherevere.expandpopview.callback;

public interface IPopListView {

    void setDrawable(int popViewTextSize, int popViewTextColor, int popViewTextColorSelected);

    void setPopViewListener(OnPopViewListener listener);
}
