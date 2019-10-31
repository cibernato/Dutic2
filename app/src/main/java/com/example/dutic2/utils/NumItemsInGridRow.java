package com.example.dutic2.utils;

import android.content.Context;

import com.airbnb.epoxy.EpoxyModel;

import static java.lang.Math.round;

public class NumItemsInGridRow implements EpoxyModel.SpanSizeOverrideCallback {

    public final int numItemsForCurrentScreen;

    /**
     * Shows one item per row on phone and two for all tablet sizes.
     */
    public static NumItemsInGridRow oneColumnPhoneTwoColumnTablet(Context context) {
        return new NumItemsInGridRow(context, 2, 2, 2);
    }

    /**
     * Specify how many items to show per grid row on phone. Tablet will show more items per row according to a default ratio.
     */
    public static NumItemsInGridRow forPhoneWithDefaultScaling(Context context, int numItemsPerRowOnPhone) {
        return new NumItemsInGridRow(context, numItemsPerRowOnPhone, round(numItemsPerRowOnPhone * 1.5f), numItemsPerRowOnPhone * 2);
    }

    public NumItemsInGridRow(Context context, int forPhone, int forTablet, int forWideTablet) {
        numItemsForCurrentScreen =forPhone;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        if (totalSpanCount % numItemsForCurrentScreen != 0) {
            throw new IllegalStateException(
                    "Total Span Count of : " + totalSpanCount + " can not evenly fit: " + numItemsForCurrentScreen + " cards per row");
        }

        return totalSpanCount / numItemsForCurrentScreen;
    }
}