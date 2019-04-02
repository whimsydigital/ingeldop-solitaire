/* Ingeldop
 *
 * Author: Bryce Kellogg
 * Copyright: 2019 Bryce Kellogg
 * License: Proprietary
 */
#ifndef LIB_INGELDOP_HPP_
#define LIB_INGELDOP_HPP_

#include <vector>
#include <deque>
#include <ostream>

using std::deque;
using std::vector;


#define rank(x) (x&0x0FF)
#define suit(x) (x&0xF00)


enum Card {
    CLUB_A    = 0x101,
    CLUB_2,      CLUB_3,
    CLUB_4,      CLUB_5,
    CLUB_6,      CLUB_7,
    CLUB_8,      CLUB_9,
    CLUB_10,     CLUB_J,
    CLUB_Q,      CLUB_K,
    DIAMOND_A = 0x201,
    DIAMOND_2,   DIAMOND_3,
    DIAMOND_4,   DIAMOND_5,
    DIAMOND_6,   DIAMOND_7,
    DIAMOND_8,   DIAMOND_9,
    DIAMOND_10,  DIAMOND_J,
    DIAMOND_Q,   DIAMOND_K,
    HEART_A   = 0x301,
    HEART_2,     HEART_3,
    HEART_4,     HEART_5,
    HEART_6,     HEART_7,
    HEART_8,     HEART_9,
    HEART_10,    HEART_J,
    HEART_Q,     HEART_K,
    SPADE_A   = 0x401,
    SPADE_2,     SPADE_3,
    SPADE_4,     SPADE_5,
    SPADE_6,     SPADE_7,
    SPADE_8,     SPADE_9,
    SPADE_10,    SPADE_J,
    SPADE_Q,     SPADE_K,
};

struct discardException : public std::exception {
    const char * what () const throw () {
        return "Error discarding";
    }
};


/**
 *
 **/
class Ingeldop {
 public:

    /* Start a new Ingeldop game
     *
     * The resulting game will have a full
     * shuffled deck and an empty hand.
     *
     * Params:
     *   seed = a seed for the random shuffling
     *          of the deck. If no seed is provided
     *          the shuffle will use a random seed.
     */
    Ingeldop(const vector<Card>&);
    Ingeldop(void);


    /* Deals a Card
     *
     * If there are still Cards in the deck, then
     * move a Card from the top of the deck to the
     * front of the hand. If there are no Cards left
     * in the deck, move a Card from the back of the
     * hand to the front of the hand.
     *
     * Because all Cards are dealt to the front of
     * the hand, the only valid indices to discard
     * are (0,1,2,3).
     */
    void deal(void);


    /* The current size of the hand.
     *
     * Returns:
     *   the number of Cards in the hand
     */
    uint8_t handSize(void);


    /* The current size of the deck.
     *
     * Returns:
     *   the number of Cards left in the deck
     */
    uint8_t deckSize(void);


    /* Discards the selected Cards.
     *
     * Discard Rules:
     *   1) index (0,1,2,3) all four same suit
     *   2) index (0,1) both same rank
     *   3) index (1,2) where (0,3) both same rank
     *   4) index (1,2) where (0,3) both same suit
     *
     * Throws:
     *   Exceptions if we can't discard
     *   TODO
     */
    void discard(void);
   
    
    /* Select the Card at the given index
     *
     * Params:
     *   i   = the desired index to select
     *   sel = true to select or false to deselect.
     *         If already selected, true has no effect.
     *         If not selected, false has no effect.
     *
     * Throws:
     *   std::out_of_range exception if i is larger
     *   than the current size of the hand.
     */
    void select(uint8_t i, bool sel);


    /* See if the Card at the given index is selected.
     *
     * Params:
     *   i = the desired index
     *
     * Returns:
     *   true  if the Card at index i is selected
     *   false if the Card at index i is no selected
     */
    bool isSelected(uint8_t i);
   

    /* Gets the Card at the specified index.
     *
     * Params:
     *   i = the index of the Card to get
     *
     * Returns:
     *   the desired Card
     *
     * Throws:
     *   std::out_of_range exception if i
     *   is larger than the current
     *   size of the hand.
     */
    Card cardAt(uint8_t i);
    
    
    /* Test if there are any possible discards left.
     *
     */
    bool gameOver(void);

 private:
    vector<Card> deck;
    deque<bool>  selected;
    deque<Card>  hand;
};




#endif  // LIB_INGELDOP_HPP_
