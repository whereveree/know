package io.wherevere.coordinatortablayout;

import android.support.design.widget.TabLayout;

public interface OnTabSelectedListener {
    void onTabSelected(TabLayout.Tab tab);

    void onTabUnselected(TabLayout.Tab tab);

    void onTabReselected(TabLayout.Tab tab);
}
