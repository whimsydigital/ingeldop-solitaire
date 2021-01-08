/* Ingeldop
 *
 * Author: Bryce Kellogg
 * Copyright: 2019 Bryce Kellogg
 * License: Proprietary
 */
package com.whimsydigital.ingeldop;

import java.util.*;

public class Ingeldop {
    /**
     *
     */
    private final List<Card> deck;
    private final List<Card> hand;
    private final List<Boolean> sel;
    private boolean dealt;


    /**
     * Creates a new Ingeldop game
     *
     * This will initialize a new game from the passed
     * arguments. Note that the seedHand and seedSel
     * must be the same size. There is no limit on
     * duplicate cards or number of decks used. We mostly
     * use this for unit tests and loading saved states. */
    Ingeldop(Card[] seedDeck, Card[] seedHand, Boolean[] seedSel, boolean seedDealt) {
        this.deck = new ArrayList<>(Arrays.asList(seedDeck));
        this.hand = new ArrayList<>(Arrays.asList(seedHand));
        this.sel = new ArrayList<>(Arrays.asList(seedSel));
        this.dealt = seedDealt;
    }


    /**
     * Creates a new random Ingeldop game
     *
     * This will initialize a new game with an
     * empty hand and a randomly shuffled deck. */
    Ingeldop() {
        this.deck = new ArrayList<>(Arrays.asList(Card.values()));  // Default un-shuffled deck
        this.hand = new ArrayList<>(52);
        this.sel = new ArrayList<>(52);
        this.dealt = false;
        Collections.shuffle(this.deck); // Shuffle deck
    }


    /**
     * Deals a card into the hand.
     *
     * Takes the card from the top of the deck and put
     * it on the top of the hand. If there are no
     * cards left in the deck, instead take the card
     * from the bottom of the hand and put is on the
     * top of the hand. */
    void deal() {
        if (0 != deckSize()) {
            Card c = deck.remove(deck.size()-1);
            hand.add(c);
            sel.add(false);
            dealt = true;
        } else if (0 != handSize()) {
            Card c = hand.remove(0);
            boolean s = sel.remove(0);
            hand.add(c);
            sel.add(s);
            dealt = true;
        }
    }


    /**
     * The number of cards in the deck.
     */
    int deckSize() {
        return deck.size();
    }


    /**
     * The number of cards in the hand.
     */
    int handSize() {
        return hand.size();
    }


    /**
     * Get a card from the hand.
     *
     * Returns the value (suit and rank) of the card
     * at the specified index in the hand. If the index
     * is outside the range of number of cards in the hand
     * throw an error.
     */
    Card getCard(int idx) {
        if (0 <= idx && idx < handSize()) {
            return hand.get(idx);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }


    /**
     * Select or deselect a card in the hand.
     *
     * Mark the card at the soecified index in the hand as
     * either selected or not selected. If the index is
     * outside the range of number of cards in the hand
     * throw an error.
     */
    void selectCard(int idx, boolean s) {
        if (0 <= idx && idx < handSize()) {
            sel.set(idx, s);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }


    /**
     * Test if a card is selected.
     *
     * Test if the card at the specified index in the hand
     * is selected or not. Returns true if the card is selected.
     * Throws an error if the index is outside the range of
     * the number of cards in the hand.
     */
    boolean isCardSelected(int idx) {
        if (0 <= idx && idx < handSize()) {
            return sel.get(idx);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }


    /**
     * Attempts to discard cards from the hand.
     *
     * Cards can be discarded from the hand if they
     * match certain criteria. If these criteria are
     * met, the selected cards are removed from the hand.
     * If the criteria are not met, an error is thrown.
     *
     * Discard Rules:
     *   - Cards must be selected before they can be discarded
     *   - Only 2 or 4 cards can be discarded at a time
     *   - Only cards in the top 4 of the hand can be discarded
     *   - Selected cards must match a valid discard pattern:
     *       a) top 4 cards flush
     *       b) top 2 cards pair
     *       c) any two cards (in positions 2 and 3) separating a pair
     *       d) any two cards (in positions 2 and 3) separating same suit
     *
     * A consecutive discard without an intervening deal is only
     * allowed once after a discard of types c & d, but only if
     * the second discard is of type a or b.  */
    void discard() throws DiscardException {

        // Get num selected
        int numSel = 0;
        for (int i = 0; i < handSize(); i++) {
            if (isCardSelected(i)) numSel++;
        }

        // Get indices of top four cards
        int i0 = handSize() - 1;
        int i1 = handSize() - 2;
        int i2 = handSize() - 3;
        int i3 = handSize() - 4;
        
        // Trying to discard 4
        if (numSel == 4) {

            // Get discard condition
            boolean sameSuit = getCard(i0).suit == getCard(i1).suit &&
                               getCard(i0).suit == getCard(i2).suit &&
                               getCard(i0).suit == getCard(i3).suit;

            boolean topSel = isCardSelected(i0) &&
                             isCardSelected(i1) &&
                             isCardSelected(i2) &&
                             isCardSelected(i3);

            // Discard
            if (topSel && sameSuit) {
                hand.remove(i0);
                hand.remove(i1);
                hand.remove(i2);
                hand.remove(i3);
                sel.remove(i0);
                sel.remove(i1);
                sel.remove(i2);
                sel.remove(i3);
                dealt = false;
            } else {
                throw new DiscardException();
            }

        // Trying to discard 2 (only 2 left)
        } else if (numSel == 2 && (handSize() == 2 || handSize() == 3)) {

            // Get discard condition
            boolean sameRank = getCard(i0).rank == getCard(i1).rank;
            boolean topSel = isCardSelected(i0) && isCardSelected(i1);

            // Discard
            if (topSel && sameRank) {
                hand.remove(i0);
                hand.remove(i1);
                sel.remove(i0);
                sel.remove(i1);
                dealt = false;
            } else {
                throw new DiscardException();
            }
        
        // Trying to discard 2
        } else if (numSel == 2) {

            // Get discard conditions
            boolean sameRank = getCard(i0).rank == getCard(i1).rank;
            boolean topSel = isCardSelected(i0) && isCardSelected(i1);
            boolean outsideSameRank = getCard(i0).rank == getCard(i3).rank;
            boolean outsideSameSuit = getCard(i0).suit == getCard(i3).suit;
            boolean middleSel = isCardSelected(i1) && isCardSelected(i2);

            if (topSel && sameRank) {
                hand.remove(i0);
                hand.remove(i1);
                sel.remove(i0);
                sel.remove(i1);
                dealt = false;
            } else if (middleSel && (outsideSameRank || outsideSameSuit) && dealt) {
                hand.remove(i1);
                hand.remove(i2);
                sel.remove(i1);
                sel.remove(i2);
                dealt = false;
            } else {
                throw new DiscardException();
            }

        // Trying to discard not 2 or 4
        } else {
            throw new DiscardException();
        }
    }


    /**
     * Test if there are any more possible moves.
     *
     * A game counts as over if there are no cards
     * remaining in the deck and if it is not
     * possible to make a valid call to discard()
     * after any number of deals.  */
    boolean gameOver() {
        // Never over if still cards in deck
        if (deckSize() != 0) return false;

        for (int i = 0; i < handSize(); i++) {
            int i0 = (i+0) % handSize();
            int i1 = (i+1) % handSize();
            int i2 = (i+2) % handSize();
            int i3 = (i+3) % handSize();

            Card c0 = getCard(i0);
            Card c1 = getCard(i1);
            Card c2 = getCard(i2);
            Card c3 = getCard(i3);

            if (handSize() >= 2 && c0.rank == c1.rank) return false; // Check for pairs
            if (handSize() >= 4 && c0.rank == c3.rank) return false; // Check between pairs
            if (handSize() >= 4 && c0.suit == c3.suit) return false; // Check between suits
        }

        // No possible discards left
        return true;
    }


    /**
     * Obtain an Interable of Cards in the deck
     *
     * Provides a read only view of the Cards in the
     * deck by returning an Iterable<Card>.
     */
    public Iterable<Card> deck() {
        return Collections.unmodifiableList(this.deck);
    }


    /**
     * Obtain an Interable of Cards in the hand
     *
     * Provides a read only view of the Cards in the
     * hand by returning an Iterable<Card>.
     */
    public Iterable<Card> hand() {
        return Collections.unmodifiableList(this.hand);
    }


    /**
     * Obtain an Interable of Card selections
     *
     * Provides a read only view of the Cards in the
     * hand that are selected by returning an
     * Iterable<Boolean> where each element signals
     * if a Card in the hand is selected or not.
     */
    public Iterable<Boolean> sel() {
        return Collections.unmodifiableList(this.sel);
    }


    /**
     * Query if a card has been dealt to the hand.
     *
     * Returns a Boolean flag representing if a card
     * has been dealt from the deck into the hand
     * since the last discard.  */
    public Boolean dealt() {
        return dealt;
    }


    /**
     * Test for equality with another Object.
     *
     * An Ingeldop game counts as equal if the deck and hand
     * are identical and if the same cards are selected.  */
    public boolean equals(Object o) {
        // Easy cases
        if (o == this) return true;
        if (!(o instanceof Ingeldop)) return false;

        // Equal if deck, hand, and sel are
        Ingeldop oi = (Ingeldop) o;
        if (!oi.deck.equals(deck)) return false;
        if (!oi.hand.equals(hand)) return false;
        if (!oi.sel.equals(sel))   return false;
        return true;
    }
}


/**
 * Exceptions for indicating an incorrect or illegal discard.
 *
 * All errors when discarding result in a DiscardException instead
 * of having individual exception types or exception messages. This
 * means that if the client wants to know the reason for an exception,
 * they should inspect the hand themselves.  */
class DiscardException extends Exception { }


/**
 * Representations of Cards and Suits.
 *
 * These are represented as Java enums, with a Card consisting
 * of a suit and a rank. A rank is an integer that maps ace = 1
 * to king = 13, while a suit is an enum consisting of the
 * four suit values. These representations can be converted
 * to a from strings using the enum toString() and valueOf(). */
enum Suit {CLUBS, DIAMONDS, HEARTS, SPADES}
enum Card {
    CLUB_A   (Suit.CLUBS, 1),   DIAMOND_A  (Suit.DIAMONDS, 1),
    CLUB_2   (Suit.CLUBS, 2),   DIAMOND_2  (Suit.DIAMONDS, 2),
    CLUB_3   (Suit.CLUBS, 3),   DIAMOND_3  (Suit.DIAMONDS, 3),
    CLUB_4   (Suit.CLUBS, 4),   DIAMOND_4  (Suit.DIAMONDS, 4),
    CLUB_5   (Suit.CLUBS, 5),   DIAMOND_5  (Suit.DIAMONDS, 5),
    CLUB_6   (Suit.CLUBS, 6),   DIAMOND_6  (Suit.DIAMONDS, 6),
    CLUB_7   (Suit.CLUBS, 7),   DIAMOND_7  (Suit.DIAMONDS, 7),
    CLUB_8   (Suit.CLUBS, 8),   DIAMOND_8  (Suit.DIAMONDS, 8),
    CLUB_9   (Suit.CLUBS, 9),   DIAMOND_9  (Suit.DIAMONDS, 9),
    CLUB_10  (Suit.CLUBS, 10),  DIAMOND_10 (Suit.DIAMONDS, 10),
    CLUB_J   (Suit.CLUBS, 11),  DIAMOND_J  (Suit.DIAMONDS, 11),
    CLUB_Q   (Suit.CLUBS, 12),  DIAMOND_Q  (Suit.DIAMONDS, 12),
    CLUB_K   (Suit.CLUBS, 13),  DIAMOND_K  (Suit.DIAMONDS, 13),
    HEART_A  (Suit.HEARTS, 1),  SPADE_A    (Suit.SPADES, 1),
    HEART_2  (Suit.HEARTS, 2),  SPADE_2    (Suit.SPADES, 2),
    HEART_3  (Suit.HEARTS, 3),  SPADE_3    (Suit.SPADES, 3),
    HEART_4  (Suit.HEARTS, 4),  SPADE_4    (Suit.SPADES, 4),
    HEART_5  (Suit.HEARTS, 5),  SPADE_5    (Suit.SPADES, 5),
    HEART_6  (Suit.HEARTS, 6),  SPADE_6    (Suit.SPADES, 6),
    HEART_7  (Suit.HEARTS, 7),  SPADE_7    (Suit.SPADES, 7),
    HEART_8  (Suit.HEARTS, 8),  SPADE_8    (Suit.SPADES, 8),
    HEART_9  (Suit.HEARTS, 9),  SPADE_9    (Suit.SPADES, 9),
    HEART_10 (Suit.HEARTS, 10), SPADE_10   (Suit.SPADES, 10),
    HEART_J  (Suit.HEARTS, 11), SPADE_J    (Suit.SPADES, 11),
    HEART_Q  (Suit.HEARTS, 12), SPADE_Q    (Suit.SPADES, 12),
    HEART_K  (Suit.HEARTS, 13), SPADE_K    (Suit.SPADES, 13);

    public final Suit suit;
    public final int  rank;
    Card(Suit s, int r) {
        this.suit = s;
        this.rank = r;
    }
}
