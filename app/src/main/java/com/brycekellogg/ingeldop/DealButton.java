package com.brycekellogg.ingeldop;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageButton;

/* A custom image button that defines a custom state for an empty deck drawable.  */
public class DealButton extends AppCompatImageButton {

    public DealButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /* Create a custom button state for when the deck is empty. This allows us
     * to set custom drawables for this button depending on an empty state.
     * This logic doesn't directly deal with the deck itself, instead just
     * creates a state and a setter function for notifying us of emptiness.  */
    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.isEmpty) mergeDrawableStates(drawableState, STATE_EMPTY);
        return drawableState;
    }
    private boolean isEmpty = false;
    private static final int[] STATE_EMPTY = {R.attr.state_empty};
    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
        refreshDrawableState();
    }
}
