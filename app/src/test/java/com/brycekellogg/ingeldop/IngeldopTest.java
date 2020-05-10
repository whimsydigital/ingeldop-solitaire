package com.brycekellogg.ingeldop;

import static org.junit.Assert.*;

import org.junit.Test;

public class IngeldopTest {

    @Test
    public void dealing() {
        Card[] deck = {Card.HEART_5, Card.DIAMOND_5, Card.SPADE_A, Card.HEART_2};  // deal from end
        Card[] hand = {};
        Boolean[] sel = {};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // No deals yet
        assertEquals(game.handSize(), 0);
        assertEquals(game.deckSize(), 4);

        // Deal first card
        game.deal();
        assertEquals(game.handSize(), 1);
        assertEquals(game.deckSize(), 3);
        assertEquals(game.getCard(0), Card.HEART_2);
        
        // Deal second card
        game.deal();
        assertEquals(game.handSize(), 2);
        assertEquals(game.deckSize(), 2);
        assertEquals(game.getCard(0), Card.HEART_2);
        assertEquals(game.getCard(1), Card.SPADE_A);
        
        // Deal third card
        game.deal();
        assertEquals(game.handSize(), 3);
        assertEquals(game.deckSize(), 1);
        assertEquals(game.getCard(0), Card.HEART_2);
        assertEquals(game.getCard(1), Card.SPADE_A);
        assertEquals(game.getCard(2), Card.DIAMOND_5);
        
        // Deal fourth card
        game.deal();
        assertEquals(game.handSize(), 4);
        assertEquals(game.deckSize(), 0);
        assertEquals(game.getCard(0), Card.HEART_2);
        assertEquals(game.getCard(1), Card.SPADE_A);
        assertEquals(game.getCard(2), Card.DIAMOND_5);
        assertEquals(game.getCard(3), Card.HEART_5);

        // Deal from hand
        game.deal();
        assertEquals(game.handSize(), 4);
        assertEquals(game.deckSize(), 0);
        assertEquals(game.getCard(0), Card.SPADE_A);
        assertEquals(game.getCard(1), Card.DIAMOND_5);
        assertEquals(game.getCard(2), Card.HEART_5);
        assertEquals(game.getCard(3), Card.HEART_2);

        // Deal from hand
        game.deal();
        assertEquals(game.handSize(), 4);
        assertEquals(game.deckSize(), 0);
        assertEquals(game.getCard(0), Card.DIAMOND_5);
        assertEquals(game.getCard(1), Card.HEART_5);
        assertEquals(game.getCard(2), Card.HEART_2);
        assertEquals(game.getCard(3), Card.SPADE_A);
        
        // Deal from hand
        game.deal();
        assertEquals(game.handSize(), 4);
        assertEquals(game.deckSize(), 0);
        assertEquals(game.getCard(0), Card.HEART_5);
        assertEquals(game.getCard(1), Card.HEART_2);
        assertEquals(game.getCard(2), Card.SPADE_A);
        assertEquals(game.getCard(3), Card.DIAMOND_5);
        
        // Deal from hand
        game.deal();
        assertEquals(game.handSize(), 4);
        assertEquals(game.deckSize(), 0);
        assertEquals(game.getCard(0), Card.HEART_2);
        assertEquals(game.getCard(1), Card.SPADE_A);
        assertEquals(game.getCard(2), Card.DIAMOND_5);
        assertEquals(game.getCard(3), Card.HEART_5);
    }

    @Test
    public void selecting() {
        Card[] deck = {Card.HEART_5, Card.DIAMOND_5, Card.SPADE_A, Card.HEART_2};
        Card[] hand = {};
        Boolean[] sel = {};
        Ingeldop game = new Ingeldop(deck, hand, sel);
       
        // Deal first card
        game.deal();
        assertFalse(game.isCardSelected(0));
        
        // Select first card
        game.selectCard(0, true);
        assertTrue(game.isCardSelected(0));
        
        // Deselect first card
        game.selectCard(0, false);
        assertFalse(game.isCardSelected(0));
       
        // Deal second card
        game.deal();
        assertFalse(game.isCardSelected(0));
        assertFalse(game.isCardSelected(1));
        
        // Select first and second card
        game.selectCard(0, true);
        game.selectCard(1, true);
        assertTrue(game.isCardSelected(0));
        assertTrue(game.isCardSelected(1));
        
        // Deselect first card
        game.selectCard(0, false);
        assertFalse(game.isCardSelected(0));
        assertTrue(game.isCardSelected(1));
       
        // Deal third and fourth cards
        game.deal();
        game.deal();
        assertFalse(game.isCardSelected(0));
        assertTrue(game.isCardSelected(1));
        assertFalse(game.isCardSelected(2));
        assertFalse(game.isCardSelected(3));
        
        // Deal from hand
        game.deal();
        assertTrue(game.isCardSelected(0));
        assertFalse(game.isCardSelected(1));
        assertFalse(game.isCardSelected(2));
        assertFalse(game.isCardSelected(3));
        game.deal();
        assertFalse(game.isCardSelected(0));
        assertFalse(game.isCardSelected(1));
        assertFalse(game.isCardSelected(2));
        assertTrue(game.isCardSelected(3));
        game.deal();
        assertFalse(game.isCardSelected(0));
        assertFalse(game.isCardSelected(1));
        assertTrue(game.isCardSelected(2));
        assertFalse(game.isCardSelected(3));
        game.deal();
        assertFalse(game.isCardSelected(0));
        assertTrue(game.isCardSelected(1));
        assertFalse(game.isCardSelected(2));
        assertFalse(game.isCardSelected(3));
    }

    /**
     * Discarding with nothing in the hand.
     * This tests trying to discard either before
     * you have dealt any cards, or after already
     * discarding everything in the hand. The discard
     * should fail and throw an exception. **/
    @Test
    public void discardingEmpty() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {};
        Boolean[] sel = {};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Try discarding
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }
    }

    /** 
     * Discarding a pair when only 2 cards remain.
     * This test simulates discarding a pair when those
     * are the only cards remaining in the hand. We should
     * be able to successfully discard. **/
    @Test
    public void discardingPair0() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_5, Card.DIAMOND_5};
        Boolean[] sel = {false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and discard
        game.selectCard(0, true);
        game.selectCard(1, true);
        game.discard();

        // Check result
        assertEquals(0, game.handSize());
    }

    /**
     * Discarding a pair at top of hand.
     * This test simulates discarding a pair when there
     * are other cards in the deck. The pair is at the top
     * of the hand, and should successfully discard. **/
    @Test
    public void discardingPair1() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_A, Card.CLUB_2, Card.HEART_5, Card.DIAMOND_5};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and discard
        game.selectCard(2, true);
        game.selectCard(3, true);
        game.discard();

        // Check result
        assertEquals(2, game.handSize());
        assertEquals(Card.SPADE_A, game.getCard(0));
        assertEquals(Card.CLUB_2,  game.getCard(1));
    }
    
    /**
     * Discarding a pair at top of hand (3 cards).
     * This test simulates discarding a pair when there
     * are other cards in the deck. The pair is at the top
     * of the hand, and should successfully discard. **/
    @Test
    public void discardingPair2() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.CLUB_2, Card.HEART_5, Card.DIAMOND_5};
        Boolean[] sel = {false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and discard
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.discard();

        // Check result
        assertEquals(1, game.handSize());
        assertEquals(Card.CLUB_2,  game.getCard(0));
    }

    /**
     * Try discarding two non-pair.
     * This tests trying to discard two cards that are not a pair.
     * Discard should not succeed and should throw and exception. **/
    @Test
    public void discardingPair3() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_5, Card.HEART_2};
        Boolean[] sel = {false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(0, true);
        game.selectCard(1, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }

        //  Check result
        assertEquals(2, game.handSize());
        assertEquals(Card.HEART_5, game.getCard(0));
        assertEquals(Card.HEART_2, game.getCard(1));
    }

    /**
     * Try discarding two non-pair at top of hand.
     * This tests trying to discard two cards that are not a pair.
     * The two cards are at the top of the hand, and there are other
     * cards in the hand. Discard should not succeed and should throw
     * an exception. **/
    @Test
    public void discardingPair4() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_A, Card.HEART_3, Card.HEART_5, Card.HEART_2};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(2, true);
        game.selectCard(3, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }

        // Check result
        assertEquals(4, game.handSize());
        assertEquals(Card.SPADE_A, game.getCard(0));
        assertEquals(Card.HEART_3, game.getCard(1));
        assertEquals(Card.HEART_5, game.getCard(2));
        assertEquals(Card.HEART_2, game.getCard(3));
    }

    /**
     * Try discarding a pair not at top of hand.
     * This tests trying to discard a pair that is in the middle of
     * the hand instead of at the top of the hand. This is not allowed,
     * and should throw an exception. **/
    @Test
    public void discardingPair5() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_5, Card.HEART_A, Card.SPADE_A, Card.SPADE_3};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(1, true);
        game.selectCard(2, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }

        // Check result
        assertEquals(4, game.handSize());
        assertEquals(Card.HEART_5, game.getCard(0));
        assertEquals(Card.HEART_A, game.getCard(1));
        assertEquals(Card.SPADE_A, game.getCard(2));
        assertEquals(Card.SPADE_3, game.getCard(3));
    }

    /**
     * Try discarding a non-adjacent pair.
     * This tests trying to discard a pair that are not adjacent
     * cards. This is not allowed and should throw an exception. **/
    @Test
    public void discardingPair6() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.HEART_5, Card.SPADE_A, Card.SPADE_3};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and try discarding
        game.selectCard(0, true);
        game.selectCard(2, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }
        
        // Check result
        assertEquals(4, game.handSize());
        assertEquals(Card.HEART_A, game.getCard(0));
        assertEquals(Card.HEART_5, game.getCard(1));
        assertEquals(Card.SPADE_A, game.getCard(2));
        assertEquals(Card.SPADE_3, game.getCard(3));
    }

    /**
     * Try discard a pair outside of top 4 cards.
     * This simulatest trying to discard a pair in the middle of the hand.
     * This is not allowed and should throw an exception. **/
    @Test
    public void discardingpair6() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.SPADE_2, Card.CLUB_2, Card.HEART_5, Card.SPADE_A, Card.DIAMOND_9};
        Boolean[] sel = {false, false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and try discarding
        game.selectCard(1, true);
        game.selectCard(2, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }
        
        // Check result
        assertEquals(6, game.handSize());
        assertEquals(Card.HEART_A,   game.getCard(0));
        assertEquals(Card.SPADE_2,   game.getCard(1));
        assertEquals(Card.CLUB_2,    game.getCard(2));
        assertEquals(Card.HEART_5,   game.getCard(3));
        assertEquals(Card.SPADE_A,   game.getCard(4));
        assertEquals(Card.DIAMOND_9, game.getCard(5));
    }

    /**
     * Discard four of a suit when only those remain.
     * We're allowed to discard four cards when the top 4 are
     * all the same suit. Perform this discard when those
     * 4 are the only ones that remain. **/
    @Test
    public void discardingFour0() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.HEART_5, Card.HEART_7, Card.HEART_3};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and discard
        game.selectCard(0, true);
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.selectCard(3, true);
        game.discard();

        // Check result
        assertEquals(0, game.handSize());
    }

    /**
     * Discard four of a suit after some deals.
     * Try discarding four of a suit when they are the top four
     * cards and there are other cards under them in the hand.  **/
    @Test
    public void discardingFour1() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_A, Card.CLUB_4, Card.HEART_A, Card.HEART_5, Card.HEART_7, Card.HEART_3};
        Boolean[] sel = {false, false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and discard
        game.selectCard(2, true);
        game.selectCard(3, true);
        game.selectCard(4, true);
        game.selectCard(5, true);
        game.discard();
        
        // Check result
        assertEquals(2, game.handSize());
        assertEquals(Card.SPADE_A, game.getCard(0));
        assertEquals(Card.CLUB_4,  game.getCard(1));
    }

    /**
     * Try discarding four cards that do not match in suit.
     * Tests trying to discard 4 cards, but one of them has
     * a suit that does not match the others. This is not
     * allowed and should throw an exception.  **/
    @Test
    public void discardingFour2() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.SPADE_5, Card.HEART_7, Card.HEART_3};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and try discarding
        game.selectCard(0, true);
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.selectCard(3, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }

        // Check result
        assertEquals(4, game.handSize());
        assertEquals(Card.HEART_A, game.getCard(0));
        assertEquals(Card.SPADE_5, game.getCard(1));
        assertEquals(Card.HEART_7, game.getCard(2));
        assertEquals(Card.HEART_3, game.getCard(3));
    }

    /**
     * Try discarding four of a suit when not top 4 selected.
     * This test simulates trying to discard 4 of a suit where
     * all the suits match, but the four selected 
     **/
    @Test
    public void discardingFour3() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.HEART_5, Card.HEART_7, Card.HEART_3, Card.CLUB_4};
        Boolean[] sel = {false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and try discarding
        game.selectCard(0, true);
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.selectCard(3, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }
        
        // Check result
        assertEquals(5, game.handSize());
        assertEquals(Card.HEART_A, game.getCard(0));
        assertEquals(Card.HEART_5, game.getCard(1));
        assertEquals(Card.HEART_7, game.getCard(2));
        assertEquals(Card.HEART_3, game.getCard(3));
        assertEquals(Card.CLUB_4,  game.getCard(4));
    }

    /**
     * Try discarding four cards with a pair in the middle.
     * This tests if we can discard four cards if they contain a pair.
     * This is not allowed and sould throw an exception. **/
    @Test
    public void discardingFour4() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.SPADE_5, Card.HEART_5, Card.HEART_3};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and try discarding
        game.selectCard(0, true);
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.selectCard(3, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }
        
        // Check result
        assertEquals(4, game.handSize());
        assertEquals(Card.HEART_A, game.getCard(0));
        assertEquals(Card.SPADE_5, game.getCard(1));
        assertEquals(Card.HEART_5, game.getCard(2));
        assertEquals(Card.HEART_3, game.getCard(3));
    }

    /**
     * Discard inbetween to form a pair.
     * This test simulates discarding cards to form a pair. This
     * is one of the inbetween test cases and should pass. **/
    @Test
    public void discardingInbetween0() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.DIAMOND_4, Card.CLUB_5, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);
        
        // Select and discard
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.discard();

        // Check result
        assertEquals(2, game.handSize());
        assertEquals(Card.HEART_A, game.getCard(0));
        assertEquals(Card.SPADE_A, game.getCard(1));
    }

    /**
     * Discard in between same suit.
     * This test simulates moving suits closer together as
     * a part of the process of forming four of a suit. **/
    @Test
    public void discardingInbetween1() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_3, Card.CLUB_10, Card.HEART_5, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and discard
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.discard();
        
        // Check result
        assertEquals(2, game.handSize());
        assertEquals(Card.SPADE_3, game.getCard(0));
        assertEquals(Card.SPADE_A, game.getCard(1));
    }

    /**
     * Try discarding inbetween incorrect cards.
     * This tests that inbetween discarding cannot be used
     * between random cards. This is not allowed and should
     * throw an exception. **/
    @Test
    public void discardingInbetween2() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.DIAMOND_3, Card.CLUB_10, Card.HEART_5, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(1, true);
        game.selectCard(2, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }

        // Check result
        assertEquals(game.handSize(), 4);
        assertEquals(game.getCard(0), Card.DIAMOND_3);    // Check Card value
        assertEquals(game.getCard(1), Card.CLUB_10);    // Check Card value
        assertEquals(game.getCard(2), Card.HEART_5);    // Check Card value
        assertEquals(game.getCard(3), Card.SPADE_A);    // Check Card value
    }

    /**
     * Try discarding inbetween pair not top 4 cards.
     * This tests trying to discard between a pair when
     * the pair is not in the top 4 cards. This is not
     * allowed and should throw an exception.  **/
    @Test
    public void discardingInbetween3() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.DIAMOND_A, Card.CLUB_10, Card.HEART_5, Card.SPADE_A, Card.SPADE_2};
        Boolean[] sel = {false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(1, true);
        game.selectCard(2, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }

        // Check result
        assertEquals(5, game.handSize());
        assertEquals(Card.DIAMOND_A, game.getCard(0));
        assertEquals(Card.CLUB_10, game.getCard(1));
        assertEquals(Card.HEART_5, game.getCard(2));
        assertEquals(Card.SPADE_A, game.getCard(3));
        assertEquals(Card.SPADE_2, game.getCard(4));
    }

    /**
     * Try discarding inbetween suit not top 4 cards.
     * This tests trying to discard between cards with the
     * same suit when they are not in the top 4 cards. This is
     * not allowed and should throw an exception.  **/
    @Test
    public void discardingInbetween4() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_3, Card.CLUB_10, Card.HEART_5, Card.SPADE_A, Card.SPADE_2};
        Boolean[] sel = {false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(1, true);
        game.selectCard(2, true);
        try {
            game.discard();
            fail("Expected DiscardException");
        } catch (DiscardException e) { }

        // Check result
        assertEquals(5, game.handSize());
        assertEquals(Card.SPADE_3, game.getCard(0));
        assertEquals(Card.CLUB_10, game.getCard(1));
        assertEquals(Card.HEART_5, game.getCard(2));
        assertEquals(Card.SPADE_A, game.getCard(3));
        assertEquals(Card.SPADE_2, game.getCard(4));
    }
    
    /**
     * Discard inbetween as part of four of a suit.
     * We're allowed to discard in between two of the same
     * suit, even if the two inbetween cards are part of a
     * four of a suit. This tests that. **/
    @Test
    public void discardingInbetween5() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_3, Card.SPADE_10, Card.SPADE_5, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.discard();

        // Check result
        assertEquals(2, game.handSize());
        assertEquals(Card.SPADE_3,  game.getCard(0));
        assertEquals(Card.SPADE_A,  game.getCard(1));
    }
    
    /**
     * Discard a pair inbetween matching suits.
     * Test discarding a pair inbetween matching suits. Normally
     * we can't discard a pair that is not the top two cards, but
     * in this case we are using the inbetween rule instead. **/
    @Test
    public void discardingInbetween6() throws DiscardException {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.CLUB_3, Card.HEART_A, Card.SPADE_5, Card.CLUB_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Select and try discarding
        game.selectCard(1, true);
        game.selectCard(2, true);
        game.discard();

        // Check result
        assertEquals(2, game.handSize());
        assertEquals(Card.CLUB_3,  game.getCard(0));
        assertEquals(Card.CLUB_A,  game.getCard(1));
    }

    /**
     * Game not over while there are cards in the deck.
     * Tests to make sure we don't report game over until
     * there are no longer any cards in the deck. The deck
     * is set up such that there are no possiblemoves.  **/
    @Test
    public void gameover0() {
        // Setup game
        Card[] deck = {Card.DIAMOND_3, Card.CLUB_10, Card.HEART_5, Card.SPADE_A};
        Card[] hand = {};
        Boolean[] sel = {};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Deal all the cards in the deck
        while (game.deckSize() != 0) {
            assertFalse(game.gameOver());
            game.deal();
        }

        // No possible discards
        assertTrue(game.gameOver());
    }

    /**
     * Game not over while only pair left.
     * Check that gameOver() returns false while
     * there is only a pair left.  **/
    @Test
    public void gameover1() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.CLUB_5, Card.HEART_5};
        Boolean[] sel = {false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Check game over
        assertFalse(game.gameOver());
    }
    
    /**
     * Game not over while only four of a suit left.
     * Check that gameOver() returns false while
     * there is only a four of a suit left.  **/
    @Test
    public void gameover2() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.CLUB_5, Card.CLUB_4, Card.CLUB_9, Card.CLUB_7};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Check game over
        assertFalse(game.gameOver());
    }
    
    /**
     * Game not over while a pair exists.
     * Check that gameOver() returns false while
     * a pair exists in the hand.  **/
    @Test
    public void gameover3() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.DIAMOND_3, Card.CLUB_5, Card.HEART_5, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Check game over
        assertFalse(game.gameOver());
    }

    /**
     * Game not over while pair spans boundaries.
     * Check that the game is ont reported as over if
     * there is a pair that spands the edges of the hand. **/
    @Test
    public void gameover4() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.DIAMOND_A, Card.CLUB_2, Card.CLUB_5, Card.HEART_6, Card.HEART_5, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertFalse(game.gameOver());
    }
    
    /**
     * Game not over while four of a suit spans boundaries.
     * Check that the game is ont reported as over if
     * there is a four of a suit that spans the edges of the hand. **/
    @Test
    public void gameover5() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_2, Card.CLUB_3, Card.CLUB_2, Card.CLUB_5, Card.SPADE_6, Card.SPADE_5, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertFalse(game.gameOver());
    }
    
    /**
     * Game not over while an inbetween suits exists.
     * Check that the game is ont reported as over if
     * there is are two cards inbetween a common suit. **/
    @Test
    public void gameover6() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_2, Card.CLUB_3, Card.CLUB_2, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertFalse(game.gameOver());
    }

    /**
     * Game not over while an inbetween pair exists.
     * Check that the game is ont reported as over if
     * there is are two cards inbetween a pair. **/
    @Test
    public void gameover7() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.CLUB_3, Card.CLUB_2, Card.SPADE_A};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertFalse(game.gameOver());
    }

    /**
     * Game not over while an inbetween exists accross boundary.
     * Check that the game is ont reported as over if
     * there is are two cards inbetween a pair across 
     * the edges of the hand. **/
    @Test
    public void gameover8() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_A, Card.HEART_9, Card.SPADE_A, Card.CLUB_3, Card.CLUB_2};
        Boolean[] sel = {false, false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertFalse(game.gameOver());
    }

    /**
     * Game over check.
     * Make sure the game is reported over when
     * there's some random cards left. **/
    @Test
    public void gameover9() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.DIAMOND_A, Card.SPADE_9, Card.HEART_3, Card.CLUB_2};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertTrue(game.gameOver());
    }
    
    /**
     * Game over check.
     * Make sure the game is reported over when
     * there's some random cards left. **/
    @Test
    public void gameover10() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.HEART_9, Card.SPADE_A, Card.CLUB_9, Card.SPADE_2};
        Boolean[] sel = {false, false, false, false};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertTrue(game.gameOver());
    }

    /**
     * Game over when no cards left.
     * Make sure the game is reported over when
     * there are no cards in either the hand or
     * in the deck. This counts as winning. **/
    @Test
    public void gameover11() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {};
        Boolean[] sel = {};
        Ingeldop game = new Ingeldop(deck, hand, sel);

        // Can discard that pair
        assertTrue(game.gameOver());
    }

    /* An Ingeldop game should convert to
     * a string of a specific form when a
     * call to toString() is made. Additionally
     * a call to parseString() should return a
     * new Ingeldop game from the given string. */
    @Test
    public void stringSerialization0() {
        // Setup game
        Card[] deck = {};
        Card[] hand = {Card.SPADE_A, Card.CLUB_4};
        Boolean[] sel = {false, false};
        Ingeldop game0 = new Ingeldop(deck, hand, sel);
        Ingeldop game1 = Ingeldop.parseString(game0.toString());
        String s0 = "deck=[];hand=[SPADE_A, CLUB_4];sel=[false, false];";

        // Check result
        assertEquals(s0, game0.toString());
        assertEquals(game0, game1);

    }
}

