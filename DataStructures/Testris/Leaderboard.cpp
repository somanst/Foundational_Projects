#include <fstream>
#include <sstream>
#include <iostream>
#include "Leaderboard.h"
#include <cstdlib>
#include <ctime>

void Leaderboard::insert_new_entry(LeaderboardEntry * new_entry) {
     //TODO: Insert a new LeaderboardEntry instance into the leaderboard, such that the order of the high-scores
     //      is maintained, and the leaderboard size does not exceed 10 entries at any given time (only the
     //      top 10 all-time high-scores should be kept in descending order by the score).

    insertEntry(head_leaderboard_entry, new_entry);
//
    int entryCount = 1;
    LeaderboardEntry *tempPtr = head_leaderboard_entry;
    while (tempPtr != nullptr) {
        entryCount++;
        tempPtr = tempPtr->next_leaderboard_entry;
    }
//
    if (entryCount > 11)
        deleteLastEntry(head_leaderboard_entry);

}

void Leaderboard::write_to_file(const string& filename) {
    // TODO: Write the latest leaderboard status to the given file in the format specified in the PA instructions

    fstream outFile;
    outFile.open(filename, ios::out);
//
    LeaderboardEntry* tempPtr = head_leaderboard_entry;
    while(tempPtr != nullptr){
        outFile << tempPtr->score  << " " << tempPtr->last_played << " " << tempPtr->player_name << "\n";
        tempPtr = tempPtr->next_leaderboard_entry;
    }
    outFile.close();
}

void Leaderboard::read_from_file(const string& filename) {
    // TODO: Read the stored leaderboard status from the given file such that the "head_leaderboard_entry" member
    //       variable will point to the highest all-times score, and all other scores will be reachable from it
    //       via the "next_leaderboard_entry" member variable pointer.

    std::ifstream file(filename);
    if (!file) {
        return;
    }
    std::string line;
//
    while (std::getline(file, line)) {
        string playerInfo[3];
        int i = 0;
        stringstream subString(line);
        while (subString.good() && i < 3) {
            subString >> playerInfo[i];
            i++;
        }
//
        time_t time1 = static_cast<time_t>(std::stol(playerInfo[1]));
        LeaderboardEntry *entry = new LeaderboardEntry(stoul(playerInfo[0]), time1, playerInfo[2]);
        insert_new_entry(entry);
    }
}



void Leaderboard::print_leaderboard() {
    // TODO: Print the current leaderboard status to the standard output in the format specified in the PA instructions

    LeaderboardEntry* tempPtr = head_leaderboard_entry;
    int rankCount = 0;
    while(tempPtr != nullptr){
        rankCount++;

        std::tm* timeInfo = std::localtime(&tempPtr->last_played);
        char timeString[80];
        strftime(timeString, 80, "%H:%M:%S/%d.%m.%Y", timeInfo);

        cout << rankCount << ". " << tempPtr->player_name << " " << tempPtr->score << " " << timeString << "\n";
        tempPtr = tempPtr->next_leaderboard_entry;

    }
    cout << "\n\n";
}

void Leaderboard::insertEntry(LeaderboardEntry* &headPtr, LeaderboardEntry *entry){
    if(headPtr == nullptr){
        headPtr = entry;
        return;
    }
    if(entry->score <= headPtr->score) insertEntry(headPtr->next_leaderboard_entry, entry);
    else{
        LeaderboardEntry* tempPtr = headPtr;
        headPtr = entry;
        entry->next_leaderboard_entry = tempPtr;
        return;
    }

}

void Leaderboard::deleteLastEntry(LeaderboardEntry* &headPtr){
    if(headPtr->next_leaderboard_entry == nullptr){
        delete headPtr;
        headPtr = nullptr;
        return;
    }
    deleteLastEntry(headPtr->next_leaderboard_entry);
}

Leaderboard::~Leaderboard() {
    // TODO: Free dynamically allocated memory used for storing leaderboard entries

}
