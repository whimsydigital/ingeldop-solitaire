package com.brycekellogg.ingeldop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * A View for a hand of cards that does not collapse
 *
 * This class handles laying out the cards in the hand
 * in a game of Ingeldop. It draws them as overlapping
 * cards that continue to the right indefinitely. There
 * is no action taken to constrain hand view width.
 */
public class HandViewExpanded extends View {
    private Bitmap spritesheet;
    private Rect srcRect;
    private Rect dstRect;

    private double overlap = 0.8;

    public HandViewExpanded(Context context, AttributeSet attrs) {
        super(context, attrs);
        dstRect = new Rect();
        srcRect = new Rect();

        // Import card images spritesheet from resources
        spritesheet = BitmapFactory.decodeResource(this.getResources(), R.drawable.carddeck);
    }

    /* Calculate source rect for extracting the corresponding card from the spritesheet.
     * It is assumed that the spritesheet is organized in a series of rows and columns,
     * where each suit has its own column and cards are organized in ascending order from
     * left to right with ace being the lowest (left most) card. Which row corresponds to
     * which suit is defined in the resources. The height and width of each card in the
     * sprite sheet is also defined in the resources. */
    void setSrcRect(Card card) {
        // Read in resources
        int cardHeight  = this.getResources().getDimensionPixelSize(R.dimen.cardSrcHeight);
        int cardWidth   = this.getResources().getDimensionPixelSize(R.dimen.cardSrcWidth);
        int rowHearts   = this.getResources().getInteger(R.integer.rowHearts);
        int rowDiamonds = this.getResources().getInteger(R.integer.rowDiamonds);
        int rowClubs    = this.getResources().getInteger(R.integer.rowClubs);
        int rowSpades   = this.getResources().getInteger(R.integer.rowSpades);

        // Set row & column
        int row = 0;
        int col = card.rank-1;
        switch (card.suit) {
            case HEARTS:   row = rowHearts;   break;
            case DIAMONDS: row = rowDiamonds; break;
            case CLUBS:    row = rowClubs;    break;
            case SPADES:   row = rowSpades;   break;
        }

        // Set rect boundaries
        srcRect.top    = cardHeight*row;
        srcRect.bottom = cardHeight*(row+1);
        srcRect.left   = cardWidth*col;
        srcRect.right  = cardWidth*(col+1);
    }

    /* Calculate the destination rect for where a card in the hand should be placed. This is
       dependent on the natural aspect ratio of a playing card, the current zoom ratio, the
       index of the card in the hand, the size of overlap, and whether or not the card is selected.  */
    void setDstRect(int i, boolean sel) {
        // Read in resources
        int topMargin       = this.getResources().getDimensionPixelSize(R.dimen.topMargin);
        int topScrollMargin = this.getResources().getDimensionPixelSize(R.dimen.topScrollMargin);
        int cardHeight = this.getResources().getDimensionPixelSize(R.dimen.defaultCardDstHeight);
        int cardWidth  = this.getResources().getDimensionPixelSize(R.dimen.defaultCardDstWidth);
        double zoomPercent = ((IngeldopActivity)getContext()).zoomPercent / 100.0;

        // Calculate rect boundaries
        int selMargin  = topMargin - topScrollMargin;
        int topPadding = topMargin + topScrollMargin;

        dstRect.top    = topPadding - (sel ? selMargin : 0);
        dstRect.bottom = topPadding - (sel ? selMargin : 0) + (int) (cardHeight*zoomPercent);
        dstRect.left  = (int) ((i+0)*cardWidth*zoomPercent - i*cardWidth*zoomPercent*overlap);
        dstRect.right = (int) ((i+1)*cardWidth*zoomPercent - i*cardWidth*zoomPercent*overlap);
    }


    @Override
    protected void onMeasure(int width, int height) {
        // Get reference to current game
        Ingeldop game = ((IngeldopActivity)getContext()).game;

        // Calculate bottom & left bounds
        setDstRect(0, false);
        int bottom = dstRect.bottom;
        int left   = dstRect.left;

        // Calculate top & right bounds
        setDstRect(game.handSize()-1, true);
        int top = dstRect.top;
        int right = dstRect.right;

        // Calculate measurements
        int measuredWidth  = right - left;
        int measuredHeight = bottom - top;

        // Set measurements
        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Can't do anything with a null canvas
        if (canvas == null) return;

        // Clear canvas with background color
        canvas.drawColor(this.getResources().getColor(R.color.background));

        // Get reference to current game
        Ingeldop game = ((IngeldopActivity)getContext()).game;

        // Iterate through hand and draw each card
        for (int i = 0; i < game.handSize(); i++) {
            Card c = game.getCard(i);
            setSrcRect(c);                          // Get card location in spritesheet
            setDstRect(i, game.isCardSelected(i));  // Get location to place card
            canvas.drawBitmap(spritesheet, srcRect, dstRect, null);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_UP ||
            e.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            Ingeldop game = ((IngeldopActivity) getContext()).game;

            // Check if we clicked on any of the cards in the hand
            for (int i = game.handSize() - 1; i >= 0; i--) {
                setDstRect(i, game.isCardSelected(i));
                if (dstRect.contains(x, y)) {
                    if (e.getAction() == MotionEvent.ACTION_UP) {
                        game.selectCard(i, !game.isCardSelected(i));
                        this.invalidate();
                    }
                    return true;
                }
            }
        }

        return false;
    }
}
