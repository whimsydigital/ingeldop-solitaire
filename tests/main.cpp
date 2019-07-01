#include <vector>
#include <gtest/gtest.h>
#include "Ingeldop.hpp"

using std::out_of_range;
using std::vector;
using std::cout;
using std::endl;

std::ostream& operator<<(std::ostream& os, Card c) {
    switch (c) {
        case CLUB_A:     os << "CLUB_A";     break;
        case CLUB_2:     os << "CLUB_2";     break;
        case CLUB_3:     os << "CLUB_3";     break;
        case CLUB_4:     os << "CLUB_4";     break;
        case CLUB_5:     os << "CLUB_5";     break;
        case CLUB_6:     os << "CLUB_6";     break;
        case CLUB_7:     os << "CLUB_7";     break;
        case CLUB_8:     os << "CLUB_8";     break;
        case CLUB_9:     os << "CLUB_9";     break;
        case CLUB_10:    os << "CLUB_10";    break;
        case CLUB_J:     os << "CLUB_J";     break;
        case CLUB_Q:     os << "CLUB_Q";     break;
        case CLUB_K:     os << "CLUB_K";     break;
        case DIAMOND_A:  os << "DIAMOND_A";  break;
        case DIAMOND_2:  os << "DIAMOND_2";  break;
        case DIAMOND_3:  os << "DIAMOND_3";  break;
        case DIAMOND_4:  os << "DIAMOND_4";  break;
        case DIAMOND_5:  os << "DIAMOND_5";  break;
        case DIAMOND_6:  os << "DIAMOND_6";  break;
        case DIAMOND_7:  os << "DIAMOND_7";  break;
        case DIAMOND_8:  os << "DIAMOND_8";  break;
        case DIAMOND_9:  os << "DIAMOND_9";  break;
        case DIAMOND_10: os << "DIAMOND_10"; break;
        case DIAMOND_J:  os << "DIAMOND_J";  break;
        case DIAMOND_Q:  os << "DIAMOND_Q";  break;
        case DIAMOND_K:  os << "DIAMOND_K";  break;
        case HEART_A:    os << "HEART_A";    break;
        case HEART_2:    os << "HEART_2";    break;
        case HEART_3:    os << "HEART_3";    break;
        case HEART_4:    os << "HEART_4";    break;
        case HEART_5:    os << "HEART_5";    break;
        case HEART_6:    os << "HEART_6";    break;
        case HEART_7:    os << "HEART_7";    break;
        case HEART_8:    os << "HEART_8";    break;
        case HEART_9:    os << "HEART_9";    break;
        case HEART_10:   os << "HEART_10";   break;
        case HEART_J:    os << "HEART_J";    break;
        case HEART_Q:    os << "HEART_Q";    break;
        case HEART_K:    os << "HEART_K";    break;
        case SPADE_A:    os << "SPADE_A";    break;
        case SPADE_2:    os << "SPADE_2";    break;
        case SPADE_3:    os << "SPADE_3";    break;
        case SPADE_4:    os << "SPADE_4";    break;
        case SPADE_5:    os << "SPADE_5";    break;
        case SPADE_6:    os << "SPADE_6";    break;
        case SPADE_7:    os << "SPADE_7";    break;
        case SPADE_8:    os << "SPADE_8";    break;
        case SPADE_9:    os << "SPADE_9";    break;
        case SPADE_10:   os << "SPADE_10";   break;
        case SPADE_J:    os << "SPADE_J";    break;
        case SPADE_Q:    os << "SPADE_Q";    break;
        case SPADE_K:    os << "SPADE_K";    break;
        case ERR_NOCARD: os << "ERR_NOCARD"; break;
        default: os << "???";
    }
    return os;
}

TEST(Ingeldop, dealing) {
    Card deck[] = {HEART_5, DIAMOND_5, SPADE_A, HEART_2};  // deal from end
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));

    // No deals yet
    ASSERT_EQ(game->handSize, 0);
    ASSERT_EQ(game->deckSize, 4);
    ASSERT_EQ(game->cardAt(0), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(1), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(2), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(3), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);

    // Deal first card
    game->deal();
    ASSERT_EQ(game->handSize, 1);
    ASSERT_EQ(game->deckSize, 3);
    ASSERT_EQ(game->cardAt(0), HEART_2);
    ASSERT_EQ(game->cardAt(1), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(2), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(3), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);
    
    // Deal second card
    game->deal();
    ASSERT_EQ(game->handSize, 2);
    ASSERT_EQ(game->deckSize, 2);
    ASSERT_EQ(game->cardAt(0), HEART_2);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
    ASSERT_EQ(game->cardAt(2), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(3), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);
    
    // Deal third card
    game->deal();
    ASSERT_EQ(game->handSize, 3);
    ASSERT_EQ(game->deckSize, 1);
    ASSERT_EQ(game->cardAt(0), HEART_2);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
    ASSERT_EQ(game->cardAt(2), DIAMOND_5);
    ASSERT_EQ(game->cardAt(3), ERR_NOCARD);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);
    
    // Deal fourth card
    game->deal();
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->deckSize, 0);
    ASSERT_EQ(game->cardAt(0), HEART_2);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
    ASSERT_EQ(game->cardAt(2), DIAMOND_5);
    ASSERT_EQ(game->cardAt(3), HEART_5);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);

    // Deal from hand
    game->deal();
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->deckSize, 0);
    ASSERT_EQ(game->cardAt(0), SPADE_A);
    ASSERT_EQ(game->cardAt(1), DIAMOND_5);
    ASSERT_EQ(game->cardAt(2), HEART_5);
    ASSERT_EQ(game->cardAt(3), HEART_2);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);

    // Deal from hand
    game->deal();
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->deckSize, 0);
    ASSERT_EQ(game->cardAt(0), DIAMOND_5);
    ASSERT_EQ(game->cardAt(1), HEART_5);
    ASSERT_EQ(game->cardAt(2), HEART_2);
    ASSERT_EQ(game->cardAt(3), SPADE_A);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);
    
    // Deal from hand
    game->deal();
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->deckSize, 0);
    ASSERT_EQ(game->cardAt(0), HEART_5);
    ASSERT_EQ(game->cardAt(1), HEART_2);
    ASSERT_EQ(game->cardAt(2), SPADE_A);
    ASSERT_EQ(game->cardAt(3), DIAMOND_5);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);
    
    // Deal from hand
    game->deal();
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->deckSize, 0);
    ASSERT_EQ(game->cardAt(0), HEART_2);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
    ASSERT_EQ(game->cardAt(2), DIAMOND_5);
    ASSERT_EQ(game->cardAt(3), HEART_5);
    ASSERT_EQ(game->cardAt(4), ERR_NOCARD);
}

TEST(Ingeldop, selecting) {
    Card deck[] = {HEART_5, DIAMOND_5, SPADE_A, HEART_2};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
   
    // No deal yet
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));

    // Deal first card
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    
    // Select first card
    game->select(0, true);
    ASSERT_TRUE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    
    // Deselect first card
    game->select(0, false);
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
   
    // Deal second card
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    
    // Select first and second card
    game->select(0, true);
    game->select(1, true);
    ASSERT_TRUE(game->isSelected(0));
    ASSERT_TRUE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    
    // Deselect first card
    game->select(0, false);
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_TRUE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
   
    // Deal third and fourth cards
    game->deal();
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_TRUE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    
    // Deal from hand
    game->deal();
    ASSERT_TRUE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_TRUE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_TRUE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_TRUE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    ASSERT_FALSE(game->isSelected(4));
}

// Test discarding with nothing dealt
TEST(Ingeldop, discardingEmpty) {
    Ingeldop* game = new Ingeldop();
    ASSERT_FALSE(game->discard());  // Can't discard if no selection
}

// Test discarding matching pair
TEST(Ingeldop, discardingPair0) {
    Card deck[] = {HEART_5, DIAMOND_5};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> DIAMOND_5
    game->deal();  // 1 -> HEART_5
    game->select(0,true);
    game->select(1,true);
    ASSERT_TRUE(game->discard());
    ASSERT_EQ(game->handSize, 0);
}

// Test discarding matching pair (after couple deals)
TEST(Ingeldop, discardingPair1) {
    Card deck[] = {HEART_5, DIAMOND_5, SPADE_A, HEART_2};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> HEART_2
    game->deal();  // 1 -> SPADE_A
    game->deal();  // 2 -> DIAMOND_5
    game->deal();  // 3 -> HEART_5
    game->select(2,true);
    game->select(3,true);
    ASSERT_TRUE(game->discard());
    ASSERT_EQ(game->handSize, 2);
    ASSERT_EQ(game->cardAt(0), HEART_2);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
}

// Test discarding matching suit (not allowed)
TEST(Ingeldop, discardingPair2) {
    Card deck[] = {HEART_5, HEART_2, SPADE_A, HEART_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> HEART_3
    game->deal();  // 1 -> SPADE_A
    game->deal();  // 2 -> HEART_2
    game->deal();  // 3 -> HEART_5
    game->select(2,true);
    game->select(3,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->cardAt(0), HEART_3);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
    ASSERT_EQ(game->cardAt(2), HEART_2);
    ASSERT_EQ(game->cardAt(3), HEART_5);
}

// Test discarding matching pair in middle (not allowed)
TEST(Ingeldop, discardingPair3) {
    Card deck[] = {HEART_5, HEART_A, SPADE_A, SPADE_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> SPADE_3
    game->deal();  // 1 -> SPADE_A
    game->deal();  // 2 -> HEART_A
    game->deal();  // 3 -> HEART_5
    game->select(1,true);
    game->select(2,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->cardAt(0), SPADE_3);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
    ASSERT_EQ(game->cardAt(2), HEART_A);
    ASSERT_EQ(game->cardAt(3), HEART_5);
}

// Test discarding non-adjacent matching pair (not allowed)
TEST(Ingeldop, discardingPair5) {
    Card deck[] = {HEART_A, HEART_5, SPADE_A, SPADE_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> SPADE_3
    game->deal();  // 1 -> SPADE_A
    game->deal();  // 2 -> MEART_5
    game->deal();  // 3 -> HEART_A
    game->select(1,true);
    game->select(3,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->cardAt(0), SPADE_3);
    ASSERT_EQ(game->cardAt(1), SPADE_A);
    ASSERT_EQ(game->cardAt(2), HEART_5);
    ASSERT_EQ(game->cardAt(3), HEART_A);
}

// Test discarding when non top 4 selected (correct pair)
TEST(Ingeldop, discardingpair6) {
    Card deck[] = {HEART_A, HEART_5, SPADE_A, SPADE_2, CLUB_2, DIAMOND_9};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> DIAMOND_9
    game->deal();  // 1 -> CLUB_2
    game->deal();  // 2 -> SPADE_2
    game->deal();  // 3 -> SPADE_A
    game->deal();  // 4 -> HEART_5
    game->deal();  // 5 -> HEART_A
    game->select(1,true);
    game->select(2,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 6);
    ASSERT_EQ(game->cardAt(0), DIAMOND_9);
    ASSERT_EQ(game->cardAt(1), CLUB_2);
    ASSERT_EQ(game->cardAt(2), SPADE_2);
    ASSERT_EQ(game->cardAt(3), SPADE_A);
    ASSERT_EQ(game->cardAt(4), HEART_5);
    ASSERT_EQ(game->cardAt(5), HEART_A);
}

// Test discarding 4 same suit
TEST(Ingeldop, discardingFour0) {
    Card deck[] = {HEART_A, HEART_5, HEART_7, HEART_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    ASSERT_TRUE(game->discard());
    ASSERT_EQ(game->handSize, 0);
}

// Test discarding 4 same suit  (after couple deals)
TEST(Ingeldop, discardingFour1) {
    Card deck[] = {HEART_A, HEART_5, HEART_7, HEART_3, SPADE_A, CLUB_4};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(2,true);
    game->select(3,true);
    game->select(4,true);
    game->select(5,true);
    ASSERT_TRUE(game->discard());
    ASSERT_EQ(game->handSize, 2);
    ASSERT_EQ(game->cardAt(0), CLUB_4);     // Check Card value
    ASSERT_EQ(game->cardAt(1), SPADE_A);    // Check Card value
}

// Test discarding 4 different suit
TEST(Ingeldop, discardingFour2) {
    Card deck[] = {HEART_A, SPADE_5, HEART_7, HEART_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> HEART_3
    game->deal();  // 1 -> HEART_7
    game->deal();  // 2 -> SPADE_5
    game->deal();  // 3 -> HEART_A
    game->select(0,true);
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->cardAt(0), HEART_3);    // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_7);    // Check Card value
    ASSERT_EQ(game->cardAt(2), SPADE_5);    // Check Card value
    ASSERT_EQ(game->cardAt(3), HEART_A);    // Check Card value
}

// Test discarding four when non (0,1,2,3) selected (correct four)
TEST(Ingeldop, discardingFour3) {
    Card deck[] = {CLUB_4, HEART_A, HEART_5, HEART_7, HEART_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();  // 0 -> HEART_3
    game->deal();  // 1 -> HEART_7
    game->deal();  // 2 -> HEART_5
    game->deal();  // 3 -> HEART_A
    game->deal();  // 4 -> CLUB_4
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    game->select(4,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 5);
    ASSERT_EQ(game->cardAt(0), HEART_3);    // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_7);    // Check Card value
    ASSERT_EQ(game->cardAt(2), HEART_5);    // Check Card value
    ASSERT_EQ(game->cardAt(3), HEART_A);    // Check Card value
    ASSERT_EQ(game->cardAt(4), CLUB_4);     // Check Card value
}

// Test discarding 4 with hidden pair
TEST(Ingeldop, discardingFour4) {
    Card deck[] = {HEART_A, SPADE_5, HEART_5, HEART_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->cardAt(0), HEART_3);    // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_5);    // Check Card value
    ASSERT_EQ(game->cardAt(2), SPADE_5);    // Check Card value
    ASSERT_EQ(game->cardAt(3), HEART_A);    // Check Card value
}

// Test discarding to make pair
TEST(Ingeldop, discardingInbetween0) {
    Card deck[] = {HEART_A, SPADE_5, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    ASSERT_TRUE(game->discard());
    ASSERT_EQ(game->handSize, 2);
    ASSERT_EQ(game->cardAt(0), SPADE_A);    // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_A);    // Check Card value
}

// Test discarding between suit
TEST(Ingeldop, discardingInbetween1) {
    Card deck[] = {SPADE_3, CLUB_10, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    ASSERT_TRUE(game->discard());
    ASSERT_EQ(game->handSize, 2);
    ASSERT_EQ(game->cardAt(0), SPADE_A);    // Check Card value
    ASSERT_EQ(game->cardAt(1), SPADE_3);    // Check Card value
}

// Test discarding to make incorrect groups (not allowed)
TEST(Ingeldop, discardingInbetween2) {
    Card deck[] = {DIAMOND_3, CLUB_10, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    ASSERT_FALSE(game->discard());  // Can't discard
    ASSERT_EQ(game->handSize, 4);
    ASSERT_EQ(game->cardAt(0), DIAMOND_3);    // Check Card value
    ASSERT_EQ(game->cardAt(1), CLUB_10);    // Check Card value
    ASSERT_EQ(game->cardAt(2), HEART_5);    // Check Card value
    ASSERT_EQ(game->cardAt(3), SPADE_A);    // Check Card value
}

// Test no moves after full deal
TEST(Ingeldop, gameover0) {
    Card deck[] = {DIAMOND_3, CLUB_10, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));

    // Deal all the cards in the deck
    while (game->deckSize != 0) {
        ASSERT_FALSE(game->gameOver());
        game->deal();
    }

    // No possible discards
    ASSERT_TRUE(game->gameOver());
}

// Test pairs left
TEST(Ingeldop, gameover1) {
    Card deck[] = {DIAMOND_3, CLUB_5, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));

    // Deal all the cards in the deck
    while (game->deckSize != 0) {
        ASSERT_FALSE(game->gameOver());
        game->deal();
    }

    // Can discard that pair
    ASSERT_FALSE(game->gameOver());

    // Check for pairs that span boundaries
    game->deal();
    game->deal();
    ASSERT_FALSE(game->gameOver());

    // Discard them, then check
    game->deal();
    game->select(0, true);
    game->select(1, true);
    game->discard();
    ASSERT_TRUE(game->gameOver());
}

// Test between pairs
TEST(Ingeldop, gameover2) {
    Card deck[] = {DIAMOND_3, CLUB_5, CLUB_4, SPADE_A, HEART_5};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));

    // Deal all the cards in the deck
    while (game->deckSize != 0) {
        ASSERT_FALSE(game->gameOver());
        game->deal();
    }

    // Can discard between pair
    ASSERT_FALSE(game->gameOver());

    // Check between pairs that span boundaries
    game->deal();
    game->deal();
    ASSERT_FALSE(game->gameOver());

    // Discard them, then check
    game->deal();
    game->deal();
    game->select(1, true);
    game->select(2, true);
    game->discard();
    ASSERT_FALSE(game->gameOver());

    // Discard pair, game over
    game->select(0, true);
    game->select(1, true);
    game->discard();
    ASSERT_TRUE(game->gameOver());
}

// Test between suits
TEST(Ingeldop, gameover3) {
    Card deck[] = {CLUB_5, DIAMOND_4, SPADE_A, HEART_8, DIAMOND_3};
    Ingeldop* game = new Ingeldop(deck, sizeof(deck)/sizeof(deck[0]));

    // Deal all the cards in the deck
    while (game->deckSize != 0) {
        ASSERT_FALSE(game->gameOver());
        game->deal();
    }

    // Can discard between suit
    ASSERT_FALSE(game->gameOver());

    // Check between pairs that span boundaries
    game->deal();
    game->deal();
    ASSERT_FALSE(game->gameOver());

    // Discard them, then check
    game->deal();
    game->deal();
    game->select(1, true);
    game->select(2, true);
    game->discard();
    ASSERT_TRUE(game->gameOver());
}


int main(int argc, char **argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
