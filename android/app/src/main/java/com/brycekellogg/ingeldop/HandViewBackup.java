//package com.brycekellogg.ingeldop;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//
//import java.util.HashMap;
//
//// TODO: REPEATED DISCARDS WORK!!!!!!!! Make it so they don't
//
///**
// * A View for a hand of cards.
// *
// * This class handles laying out the cards in the hand
// * in a game of Ingeldop.
// *
// *
// *   +------+      +---+---+
// *   |      |      |   |   |
// *   |      |      |   |   |
// *   |      |      |   |   |
// *   |      |      |   |   |
// *   +------+      +---+---+
// *
// *   +------+
// *   |      |
// *   +------+
// */
//public class HandViewBackup extends View {
//    private double overlapBig = 0.25;
//    private double overlapSmall = 0.1;
//    private double cardWidth = 72;
//    private double cardHeight = 100;
//    private double scale = 5;
//
//    private int leftPadding = 20;
//    private int topPadding  = 20;
//
//    private Bitmap deck  = BitmapFactory.decodeResource(this.getResources(), R.drawable.deck0);
//    private Bitmap cardBackSrcImage     = BitmapFactory.decodeResource(this.getResources(), R.drawable.deal0);
//    private Bitmap greenCircleSrcImage  = BitmapFactory.decodeResource(this.getResources(), R.drawable.deal1);
//    private Bitmap redCrossSrcImage     = BitmapFactory.decodeResource(this.getResources(), R.drawable.deal2);
//
//
//
//    Rect dealButtonSrcRect = new Rect(0, 0, 72, 100);
//    Rect dealButtonDstRect = new Rect((int) (0*scale   + leftPadding),
//            (int) (0*scale   + topPadding),
//            (int) (72*scale  + leftPadding),
//            (int) (100*scale + topPadding));
//
//    int discardButtonPadding = 20;
//    int discarButtonHeight = 100;
//
//    RectF discardButtonRect = new RectF(
//            dealButtonDstRect.left,
//            dealButtonDstRect.bottom + discardButtonPadding,
//            dealButtonDstRect.right,
//            dealButtonDstRect.bottom + discardButtonPadding + discarButtonHeight);
//    int discardButtonRy = 15;
//    int discardButtonRx = 15;
//
//    Rect[] handDstRects = new Rect[52];
//
//
//    HashMap<String, Rect> map = new HashMap<>();
//
//    public HandViewBackup(Context context, AttributeSet attr) {
//        super(context, attr);
//
//
//        map.put("SPADE_A",    new Rect(72*0,  100*3, 72*1,  100*4));
//        map.put("SPADE_2",    new Rect(72*1,  100*3, 72*2,  100*4));
//        map.put("SPADE_3",    new Rect(72*2,  100*3, 72*3,  100*4));
//        map.put("SPADE_4",    new Rect(72*3,  100*3, 72*4,  100*4));
//        map.put("SPADE_5",    new Rect(72*4,  100*3, 72*5,  100*4));
//        map.put("SPADE_6",    new Rect(72*5,  100*3, 72*6,  100*4));
//        map.put("SPADE_7",    new Rect(72*6,  100*3, 72*7,  100*4));
//        map.put("SPADE_8",    new Rect(72*7,  100*3, 72*8,  100*4));
//        map.put("SPADE_9",    new Rect(72*8,  100*3, 72*9,  100*4));
//        map.put("SPADE_10",   new Rect(72*9,  100*3, 72*10, 100*4));
//        map.put("SPADE_J",    new Rect(72*10, 100*3, 72*11, 100*4));
//        map.put("SPADE_Q",    new Rect(72*11, 100*3, 72*12, 100*4));
//        map.put("SPADE_K",    new Rect(72*12, 100*3, 72*13, 100*4));
//        map.put("CLUB_A",     new Rect(72*0,  100*2, 72*1,  100*3));
//        map.put("CLUB_2",     new Rect(72*1,  100*2, 72*2,  100*3));
//        map.put("CLUB_3",     new Rect(72*2,  100*2, 72*3,  100*3));
//        map.put("CLUB_4",     new Rect(72*3,  100*2, 72*4,  100*3));
//        map.put("CLUB_5",     new Rect(72*4,  100*2, 72*5,  100*3));
//        map.put("CLUB_6",     new Rect(72*5,  100*2, 72*6,  100*3));
//        map.put("CLUB_7",     new Rect(72*6,  100*2, 72*7,  100*3));
//        map.put("CLUB_8",     new Rect(72*7,  100*2, 72*8,  100*3));
//        map.put("CLUB_9",     new Rect(72*8,  100*2, 72*9,  100*3));
//        map.put("CLUB_10",    new Rect(72*9,  100*2, 72*10, 100*3));
//        map.put("CLUB_J",     new Rect(72*10, 100*2, 72*11, 100*3));
//        map.put("CLUB_Q",     new Rect(72*11, 100*2, 72*12, 100*3));
//        map.put("CLUB_K",     new Rect(72*12, 100*2, 72*13, 100*3));
//        map.put("DIAMOND_A",  new Rect(72*0,  100*1, 72*1,  100*2));
//        map.put("DIAMOND_2",  new Rect(72*1,  100*1, 72*2,  100*2));
//        map.put("DIAMOND_3",  new Rect(72*2,  100*1, 72*3,  100*2));
//        map.put("DIAMOND_4",  new Rect(72*3,  100*1, 72*4,  100*2));
//        map.put("DIAMOND_5",  new Rect(72*4,  100*1, 72*5,  100*2));
//        map.put("DIAMOND_6",  new Rect(72*5,  100*1, 72*6,  100*2));
//        map.put("DIAMOND_7",  new Rect(72*6,  100*1, 72*7,  100*2));
//        map.put("DIAMOND_8",  new Rect(72*7,  100*1, 72*8,  100*2));
//        map.put("DIAMOND_9",  new Rect(72*8,  100*1, 72*9,  100*2));
//        map.put("DIAMOND_10", new Rect(72*9,  100*1, 72*10, 100*2));
//        map.put("DIAMOND_J",  new Rect(72*10, 100*1, 72*11, 100*2));
//        map.put("DIAMOND_Q",  new Rect(72*11, 100*1, 72*12, 100*2));
//        map.put("DIAMOND_K",  new Rect(72*12, 100*1, 72*13, 100*2));
//        map.put("HEART_A",    new Rect(72*0,  100*0, 72*1,  100*1));
//        map.put("HEART_2",    new Rect(72*1,  100*0, 72*2,  100*1));
//        map.put("HEART_3",    new Rect(72*2,  100*0, 72*3,  100*1));
//        map.put("HEART_4",    new Rect(72*3,  100*0, 72*4,  100*1));
//        map.put("HEART_5",    new Rect(72*4,  100*0, 72*5,  100*1));
//        map.put("HEART_6",    new Rect(72*5,  100*0, 72*6,  100*1));
//        map.put("HEART_7",    new Rect(72*6,  100*0, 72*7,  100*1));
//        map.put("HEART_8",    new Rect(72*7,  100*0, 72*8,  100*1));
//        map.put("HEART_9",    new Rect(72*8,  100*0, 72*9,  100*1));
//        map.put("HEART_10",   new Rect(72*9,  100*0, 72*10, 100*1));
//        map.put("HEART_J",    new Rect(72*10, 100*0, 72*11, 100*1));
//        map.put("HEART_Q",    new Rect(72*11, 100*0, 72*12, 100*1));
//        map.put("HEART_K",    new Rect(72*12, 100*0, 72*13, 100*1));
//
//    }
//
//    private Rect calcCardDst(int prev, boolean isSel, double overlap) {
//        int left  = prev + (int)(cardWidth*scale*overlap);
//        int right = prev + (int)(cardWidth*scale*overlap) + (int)(cardWidth*scale);
//
//        int top = (isSel ? 0 : 30);
//        int bot = (isSel ? 0 : 30) + (int) (cardHeight*scale);
//
//        return new Rect(left, top, right, bot);
//    }
//
//    private void updateHandDstRects() {
//        Ingeldop game = ((MainActivity) getContext()).game;
//        int prev = dealButtonDstRect.right;
//        for (int i = 0; i < game.handSize(); i++) {
//
//            double overlap;
//            if (game.handSize() > 8 && i < game.handSize()-4) {
//                overlap = overlapSmall;
//            } else {
//                overlap = overlapBig;
//            }
//
//            handDstRects[i] = calcCardDst(prev, game.isCardSelected(i), overlap);
//            prev = handDstRects[i].left;
//        }
//    }
//
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        // Can't do anything with a null canvas
//        if (canvas == null) return;
//
//        // Clear canvas with white background
//        canvas.drawColor(Color.WHITE);
//
//        Ingeldop game = ((MainActivity)getContext()).game;
//
//        // Draw Deal Button
//        Bitmap dealButtonSrcImage = cardBackSrcImage;
//        if (game.deckSize() == 0) dealButtonSrcImage = greenCircleSrcImage;
//        if (game.gameOver()) dealButtonSrcImage = redCrossSrcImage;
//        canvas.drawBitmap(dealButtonSrcImage, dealButtonSrcRect, dealButtonDstRect, null);
//
//        // Draw Discard Button
//        Paint discardPaint = new Paint();
//        discardPaint.setColor(Color.GREEN);
//        canvas.drawRoundRect(discardButtonRect, discardButtonRx, discardButtonRy, discardPaint);   // Draw Discard Button
//
//        Paint textPaint = new Paint();
//        textPaint.setTextSize(80);
//        canvas.drawText("Discard", discardButtonRect.left + 5, discardButtonRect.bottom - he 5, textPaint);
//
//        // Iterate through hand and draw each card
//        for (int i = 0; i < game.handSize(); i++) {
//            Card c = game.getCard(i);
//            Rect srcRect = map.get(c.toString());
//            Rect dstRect = handDstRects[i];
//            canvas.drawBitmap(deck, srcRect, dstRect, null);
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        if (e.getAction() == MotionEvent.ACTION_DOWN) {
//            int x = (int) e.getX();
//            int y = (int) e.getY();
//            Ingeldop game = ((MainActivity) getContext()).game;
//
//            // Check if we clicked on the deal button
//            if (dealButtonDstRect.contains(x, y)) {
//                game.deal();
//                updateHandDstRects();
//                this.invalidate();
//                return true;
//            }
//
//            // Check if we clicked on the discard button
//            if (discardButtonRect.contains(x, y)) {
//                try {
//                    game.discard();
//                    updateHandDstRects();
//                    this.invalidate();
//                } catch (DiscardException ex) {
//                    Log.w("INGELDOP", ex.getMessage());
//                }
//                return true;
//            }
//
//            // Check if we clicked on any of the cards in the hand
//            for (int i = game.handSize() - 1; i >= 0; i--) {
//                if (handDstRects[i].contains(x, y)) {
//                    game.selectCard(i, !game.isCardSelected(i));
//                    updateHandDstRects();
//                    this.invalidate();
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//}
