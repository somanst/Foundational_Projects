#ifndef PA2_LEADERBOARD_H
#define PA2_LEADERBOARD_H

#include <ctime>
#include <string>
#include "LeaderboardEntry.h"

#define MAX_LEADERBOARD_SIZE 10

using namespace std;

class Leaderboard {
public:
    LeaderboardEntry* head_leaderboard_entry = nullptr;
    bool timeSwitched = false;
    void read_from_file(const string &filename);
    void write_to_file(const string &filename);
    void print_leaderboard();
    void insert_new_entry(LeaderboardEntry *new_entry);
    void insertEntry(LeaderboardEntry* &entryPtr, LeaderboardEntry *entry);
    void deleteLastEntry(LeaderboardEntry* &headPtr);
    virtual ~Leaderboard();
};


#endif //PA2_LEADERBOARD_H
