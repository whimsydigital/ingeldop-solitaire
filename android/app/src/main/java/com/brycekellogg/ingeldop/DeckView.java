package com.brycekellogg.ingeldop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class DeckView  extends View {
    public DeckView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Can't do anything with a null canvas
        if (canvas == null) return;

        // Clear canvas with white background
        canvas.drawColor(Color.GREEN);
    }
}