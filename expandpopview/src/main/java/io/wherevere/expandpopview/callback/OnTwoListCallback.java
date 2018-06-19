package io.wherevere.expandpopview.callback;

import io.wherevere.expandpopview.entity.KeyValue;

public interface OnTwoListCallback {
    void returnParentKeyValue(int pos, KeyValue keyValue);

    void returnChildKeyValue(int pos, KeyValue keyValue);
}
