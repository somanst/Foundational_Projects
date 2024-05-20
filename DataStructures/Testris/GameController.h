#ifndef PA2_GAMECONTROLLER_H
#define PA2_GAMECONTROLLER_H

#include "BlockFall.h"

using namespace std;

class GameController {
public:
    bool play(BlockFall &game, const string &commands_file); // Function that implements the gameplay
    bool insertBlock(Block* block, BlockFall &game, int positionX, int positionY);
    bool collisionChecker(Block* block, BlockFall &game, int positionX, int positionY);
    void blockNullifier(Block* blockPtr, BlockFall &game, int positionX, int positionY);
    void gravitate(BlockFall &game);
    void dropBlock(Block* blockPtr, BlockFall &game, int positionX);
    void rowColmpleter(BlockFall &game, bool beforePrinter);
    void powerupCompleter(BlockFall &game, bool beforePrinter);
    void printGrid(BlockFall &game, bool clearance);
};


#endif //PA2_GAMECONTROLLER_H
