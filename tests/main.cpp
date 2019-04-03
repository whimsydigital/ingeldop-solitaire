#include <vector>
#include <gtest/gtest.h>
#include "Ingeldop.hpp"

using std::out_of_range;
using std::vector;
using std::cout;
using std::endl;


TEST(Ingeldop, dealing) {
    vector<Card> deck = {HEART_5, DIAMOND_5, SPADE_A, HEART_2};
    Ingeldop* game = new Ingeldop(deck);
    ASSERT_EQ(game->handSize(), 0);
    ASSERT_EQ(game->deckSize(), 4);
    ASSERT_THROW(game->cardAt(0), out_of_range);
    
    // Deal cards from deck
    game->deal();
    ASSERT_EQ(game->handSize(), 1);
    ASSERT_EQ(game->deckSize(), 3);
    ASSERT_EQ(game->cardAt(0), HEART_2);
    ASSERT_THROW(game->cardAt(1), out_of_range);
    
    game->deal();
    ASSERT_EQ(game->handSize(), 2);
    ASSERT_EQ(game->deckSize(), 2);
    ASSERT_EQ(game->cardAt(0), SPADE_A);
    ASSERT_THROW(game->cardAt(2), out_of_range);
    
    game->deal();
    ASSERT_EQ(game->handSize(), 3);
    ASSERT_EQ(game->deckSize(), 1);
    ASSERT_EQ(game->cardAt(0), DIAMOND_5);
    ASSERT_THROW(game->cardAt(3), out_of_range);
    
    game->deal();
    ASSERT_EQ(game->handSize(), 4);
    ASSERT_EQ(game->deckSize(), 0);
    ASSERT_EQ(game->cardAt(0), HEART_5);
    ASSERT_THROW(game->cardAt(4), out_of_range);

    // Check all cards at correct index
    ASSERT_EQ(game->cardAt(0), HEART_5);
    ASSERT_EQ(game->cardAt(1), DIAMOND_5);
    ASSERT_EQ(game->cardAt(2), SPADE_A);
    ASSERT_EQ(game->cardAt(3), HEART_2);

    // Deal from hand
    for (int i = 0; i < 1000; i++) {
        ASSERT_EQ(game->handSize(), 4);
        ASSERT_EQ(game->deckSize(), 0);
        ASSERT_EQ(game->cardAt((0+i)%4), HEART_5);
        ASSERT_EQ(game->cardAt((1+i)%4), DIAMOND_5);
        ASSERT_EQ(game->cardAt((2+i)%4), SPADE_A);
        ASSERT_EQ(game->cardAt((3+i)%4), HEART_2);
        game->deal();
    }
}

TEST(Ingeldop, selecting) {
    vector<Card> deck = {HEART_5, DIAMOND_5, SPADE_A, HEART_2};
    Ingeldop* game = new Ingeldop(deck);

    // Test Selection as we deal from deck
    game->deal();
    game->select(0, true);
    ASSERT_TRUE(game->isSelected(0));
    game->select(0, false);
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_THROW(game->select(1, true),  out_of_range);
    ASSERT_THROW(game->select(1, false), out_of_range);
    ASSERT_THROW(game->isSelected(1),    out_of_range);
    
    game->deal();
    game->select(0, true);
    game->select(1, true);
    ASSERT_TRUE(game->isSelected(0));
    ASSERT_TRUE(game->isSelected(1));
    game->select(0, false);
    game->select(1, false);
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_THROW(game->select(2, true),  out_of_range);
    ASSERT_THROW(game->select(2, false), out_of_range);
    ASSERT_THROW(game->isSelected(2),    out_of_range);
   
    // Check that selection follows card
    game->select(0, true);
    game->deal();
    ASSERT_TRUE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(0));
    game->select(1, false);
    game->deal();
    ASSERT_FALSE(game->isSelected(2));
    
    
    // Test Selection if we deal from hand
    game->select(1, true);
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_TRUE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_TRUE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
    game->deal();
    ASSERT_FALSE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_TRUE(game->isSelected(3));
    game->deal();
    ASSERT_TRUE(game->isSelected(0));
    ASSERT_FALSE(game->isSelected(1));
    ASSERT_FALSE(game->isSelected(2));
    ASSERT_FALSE(game->isSelected(3));
}

// Test discarding with nothing dealt
TEST(Ingeldop, discardingEmpty) {
    Ingeldop* game = new Ingeldop();
    ASSERT_THROW(game->discard(), discardException);  // Can't discard if no selection
}

// Test discarding matching pair
TEST(Ingeldop, discardingPair0) {
    vector<Card> deck = {HEART_5, DIAMOND_5};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->discard();
    ASSERT_EQ(game->handSize(), 0);
}

// Test discarding matching pair (after couple deals)
TEST(Ingeldop, discardingPair1) {
    vector<Card> deck = {HEART_5, DIAMOND_5, SPADE_A, HEART_2};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->discard();
    ASSERT_EQ(game->handSize(), 2);
    ASSERT_EQ(game->cardAt(0), SPADE_A);         // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_2);         // Check Card value
}

// Test discarding matching suit (not allowed)
TEST(Ingeldop, discardingPair2) {
    vector<Card> deck = {HEART_5, HEART_2, SPADE_A, HEART_3};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 4);
    ASSERT_EQ(game->cardAt(0), HEART_5);  // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_2);  // Check Card value
    ASSERT_EQ(game->cardAt(2), SPADE_A);  // Check Card value
    ASSERT_EQ(game->cardAt(3), HEART_3);  // Check Card value
}

// Test discarding matching pair in middle (not allowed)
TEST(Ingeldop, discardingPair3) {
    vector<Card> deck = {HEART_5, HEART_A, SPADE_A, SPADE_3};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 4);
    ASSERT_EQ(game->cardAt(0), HEART_5);  // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_A);  // Check Card value
    ASSERT_EQ(game->cardAt(2), SPADE_A);  // Check Card value
    ASSERT_EQ(game->cardAt(3), SPADE_3);  // Check Card value
}

// Test discarding non-adjacent matching pair (not allowed)
TEST(Ingeldop, discardingPair5) {
    vector<Card> deck = {HEART_A, HEART_5, SPADE_A, SPADE_3};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(2,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 4);
    ASSERT_EQ(game->cardAt(0), HEART_A);  // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_5);  // Check Card value
    ASSERT_EQ(game->cardAt(2), SPADE_A);  // Check Card value
    ASSERT_EQ(game->cardAt(3), SPADE_3);  // Check Card value
}

// Test discarding when non (0,1,2,3) selected (correct pair)
TEST(Ingeldop, discardingpair6) {
    vector<Card> deck = {HEART_A, HEART_5, SPADE_A, SPADE_2, CLUB_2, DIAMOND_9};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(3,true);
    game->select(4,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 6);
    ASSERT_EQ(game->cardAt(0), HEART_A);    // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_5);    // Check Card value
    ASSERT_EQ(game->cardAt(2), SPADE_A);    // Check Card value
    ASSERT_EQ(game->cardAt(3), SPADE_2);    // Check Card value
    ASSERT_EQ(game->cardAt(4), CLUB_2);     // Check Card value
    ASSERT_EQ(game->cardAt(5), DIAMOND_9);  // Check Card value
}

// Test discarding 4 same suit
TEST(Ingeldop, discardingFour0) {
    vector<Card> deck = {HEART_A, HEART_5, HEART_7, HEART_3};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    game->discard();
    ASSERT_EQ(game->handSize(), 0);
}

// Test discarding 4 same suit  (after couple deals)
TEST(Ingeldop, discardingFour1) {
    vector<Card> deck = {HEART_A, HEART_5, HEART_7, HEART_3, SPADE_A, CLUB_4};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    game->discard();
    ASSERT_EQ(game->handSize(), 2);
    ASSERT_EQ(game->cardAt(0), SPADE_A);    // Check Card value
    ASSERT_EQ(game->cardAt(1), CLUB_4);     // Check Card value
}

// Test discarding 4 different suit
TEST(Ingeldop, discardingFour2) {
    vector<Card> deck = {HEART_A, SPADE_5, HEART_7, HEART_3};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 4);
    ASSERT_EQ(game->cardAt(0), HEART_A);    // Check Card value
    ASSERT_EQ(game->cardAt(1), SPADE_5);    // Check Card value
    ASSERT_EQ(game->cardAt(2), HEART_7);    // Check Card value
    ASSERT_EQ(game->cardAt(3), HEART_3);    // Check Card value
}

// Test discarding four when non (0,1,2,3) selected (correct four)
TEST(Ingeldop, discardingFour3) {
    vector<Card> deck = {CLUB_4, HEART_A, HEART_5, HEART_7, HEART_3};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    game->select(4,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 5);
    ASSERT_EQ(game->cardAt(0), CLUB_4);     // Check Card value
    ASSERT_EQ(game->cardAt(1), HEART_A);    // Check Card value
    ASSERT_EQ(game->cardAt(2), HEART_5);    // Check Card value
    ASSERT_EQ(game->cardAt(3), HEART_7);    // Check Card value
    ASSERT_EQ(game->cardAt(4), HEART_3);    // Check Card value
}

// Test discarding 4 with hidden pair
TEST(Ingeldop, discardingFour4) {
    vector<Card> deck = {HEART_A, SPADE_5, HEART_5, HEART_3};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(0,true);
    game->select(1,true);
    game->select(2,true);
    game->select(3,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 4);
    ASSERT_EQ(game->cardAt(0), HEART_A);    // Check Card value
    ASSERT_EQ(game->cardAt(1), SPADE_5);    // Check Card value
    ASSERT_EQ(game->cardAt(2), HEART_5);    // Check Card value
    ASSERT_EQ(game->cardAt(3), HEART_3);    // Check Card value
}

// Test discarding to make pair
TEST(Ingeldop, discardingInbetween0) {
    vector<Card> deck = {HEART_A, SPADE_5, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    game->discard();
    ASSERT_EQ(game->handSize(), 2);
    ASSERT_EQ(game->cardAt(0), HEART_A);    // Check Card value
    ASSERT_EQ(game->cardAt(1), SPADE_A);    // Check Card value
}

// Test discarding between suit
TEST(Ingeldop, discardingInbetween1) {
    vector<Card> deck = {SPADE_3, CLUB_10, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    game->discard();
    ASSERT_EQ(game->handSize(), 2);
    ASSERT_EQ(game->cardAt(0), SPADE_3);    // Check Card value
    ASSERT_EQ(game->cardAt(1), SPADE_A);    // Check Card value
}

// Test discarding to make incorrect groups (not allowed)
TEST(Ingeldop, discardingInbetween2) {
    vector<Card> deck = {DIAMOND_3, CLUB_10, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck);
    game->deal();
    game->deal();
    game->deal();
    game->deal();
    game->select(1,true);
    game->select(2,true);
    ASSERT_THROW(game->discard(), discardException);  // Can't discard
    ASSERT_EQ(game->handSize(), 4);
    ASSERT_EQ(game->cardAt(0), DIAMOND_3);    // Check Card value
    ASSERT_EQ(game->cardAt(1), CLUB_10);    // Check Card value
    ASSERT_EQ(game->cardAt(2), HEART_5);    // Check Card value
    ASSERT_EQ(game->cardAt(3), SPADE_A);    // Check Card value
}

// Test no moves after full deal
TEST(Ingeldop, gameover0) {
    vector<Card> deck = {DIAMOND_3, CLUB_10, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck);

    // Deal all the cards in the deck
    while (game->deckSize() != 0) {
        ASSERT_FALSE(game->gameOver());
        game->deal();
    }

    // No possible discards
    ASSERT_TRUE(game->gameOver());
}

// Test pairs left
TEST(Ingeldop, gameover1) {
    vector<Card> deck = {DIAMOND_3, CLUB_5, HEART_5, SPADE_A};
    Ingeldop* game = new Ingeldop(deck);

    // Deal all the cards in the deck
    while (game->deckSize() != 0) {
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
    vector<Card> deck = {DIAMOND_3, CLUB_5, CLUB_4, SPADE_A, HEART_5};
    Ingeldop* game = new Ingeldop(deck);

    // Deal all the cards in the deck
    while (game->deckSize() != 0) {
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
    vector<Card> deck = {CLUB_5, DIAMOND_4, SPADE_A, HEART_8, DIAMOND_3};
    Ingeldop* game = new Ingeldop(deck);

    // Deal all the cards in the deck
    while (game->deckSize() != 0) {
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
