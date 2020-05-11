package com.brycekellogg.ingeldop;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/* A custom image button that defines a custom state for an empty deck drawable,
 * handles measuring itself differently based on a default size and a scale
 * parameter, and handles the logic for its own click events.  */
public class DealButton extends AppCompatImageButton implements View.OnClickListener {

    public DealButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnClickListener(this);  // We are our own click listener
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


    /* Calculate deal button size based on the default card sizes for this device
     * and a scale parameter that is controlled by the zoom button. This method
     * gets called each time a new layout is requested.  */
    @Override
    protected void onMeasure(int width, int height) {
        int defaultCardWidth  = this.getResources().getDimensionPixelSize(R.dimen.defaultCardDstWidth);
        int defaultCardHeight = this.getResources().getDimensionPixelSize(R.dimen.defaultCardDstHeight);
        double scale = ((IngeldopActivity) getContext()).getScale();
        setMeasuredDimension((int) (defaultCardWidth*scale), (int) (defaultCardHeight*scale));
    }


    /* Processes a dealButton onClick action. We deal a card and update the button
     * state based on if the deck is empty or if the game is over. If the deck is
     * empty, we set the dealButton's empty state. If the game is over, we set
     * the dealButtons enabled state to false. Finally, we notify the context. */
    @Override
    public void onClick(View v) {
        // Get game and do the deal
        Ingeldop game = ((IngeldopActivity) getContext()).game;
        game.deal();

        // Scroll to the right on deal
        ((HorizontalScrollView) ((IngeldopActivity) getContext()).findViewById(R.id.scrollView))
                                                                 .fullScroll(View.FOCUS_RIGHT);

        // Update activity
        ((IngeldopActivity) getContext()).update();
    }
}
