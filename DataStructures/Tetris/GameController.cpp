#include <fstream>
#include <iostream>
#include "GameController.h"
using std::cout;

bool GameController::play(BlockFall& game, const string& commands_file){

    // TODO: Implement the gameplay here while reading the commands from the input file given as the 3rd command-line
    //       argument. The return value represents if the gameplay was successful or not: false if game over,
    //       true otherwise.

    std::ifstream file(commands_file);

    std::string line;

    game.active_rotation = game.initial_block;
    int position = 0;
    bool lost = false;

    while (std::getline(file, line)) {
        if(line == "PRINT_GRID"){
            insertBlock(game.active_rotation, game, position, 0);
            printGrid(game, false);
            cout << "\n";
            blockNullifier(game.active_rotation, game, position, 0);
        } else if(line == "ROTATE_RIGHT"){
            if(!collisionChecker(game.active_rotation->right_rotation, game, position, 0)){
                game.active_rotation = game.active_rotation->right_rotation;
            }
        } else if(line == "ROTATE_LEFT"){
            if(!collisionChecker(game.active_rotation->left_rotation, game, position, 0)){
                game.active_rotation = game.active_rotation->left_rotation;
            }
        } else if(line == "MOVE_RIGHT") {
            if (collisionChecker(game.active_rotation, game, ++position, 0)) {
                position--;
            }
        } else if(line == "MOVE_LEFT") {
            if (collisionChecker(game.active_rotation, game, --position, 0)) {
                position++;
            }
        } else if(line == "GRAVITY_SWITCH"){
            game.gravity_mode_on = !game.gravity_mode_on;
            if(game.gravity_mode_on) {
                gravitate(game);
                powerupCompleter(game, false);
                rowColmpleter(game, false);
            }
        } else if(line == "DROP"){
            dropBlock(game.active_rotation, game, position);
            if(game.gravity_mode_on) gravitate(game);
            powerupCompleter(game, true);
            rowColmpleter(game, true);
            if(game.initial_block->next_block == nullptr) break;
            Block* tempPtr = game.initial_block->next_block;
            delete game.initial_block;
            game.initial_block = tempPtr;
            game.active_rotation = tempPtr;

            lost = collisionChecker(game.active_rotation, game, 0, 0);

            position = 0;
        } else{
            cout << "Unknown command: " << line << "\n";
        }

        if(lost) break;


    }

    LeaderboardEntry* newEntry = new LeaderboardEntry(game.current_score ,time(nullptr), game.player_name);
    game.leaderboard.insert_new_entry(newEntry);
    game.leaderboard.write_to_file(game.leaderboard_file_name);

    if(game.initial_block->next_block == nullptr){
       cout << "YOU WIN!\n"
               "No more blocks.\n"
               "Final grid and score:\n\n";

        printGrid(game, false);


        cout << "Leaderboard\n"
                "-----------\n";

        game.leaderboard.print_leaderboard();

        return true;
    }
    if(lost){
        cout << "GAME OVER!\n"
                "Next block that couldn't fit:\n";

        for(int i = 0; i < game.active_rotation->shape.size(); i++){
            for(int j = 0; j < game.active_rotation->shape[0].size(); j++){
                std::string printed;
                game.active_rotation->shape[i][j] == 1 ? printed = occupiedCellChar : printed = unoccupiedCellChar;
                cout << printed;
            }
            cout <<  "\n";
        }

        cout <<  "Final grid and score:\n\n";
        printGrid(game, false);

    }

    if(!lost){
        cout <<  "GAME FINISHED!\n"
                 "No more commands.\n"
                 "Final grid and score:\n\n";

        printGrid(game, false);

    }

    cout <<  "Leaderboard\n"
             "-----------\n";


    game.leaderboard.print_leaderboard();
    game.leaderboard.write_to_file(game.leaderboard_file_name);

    return !lost;
}

bool GameController::insertBlock(Block* blockPtr, BlockFall &game, int positionX, int positionY){
    Block block = *blockPtr;
    if(collisionChecker(blockPtr, game, positionX, 0)) return false;
    for(int i = 0; i < block.shape.size(); i++){
        for(int j = 0; j < block.shape[0].size(); j++){
            if(game.grid[i + positionY][j + positionX] == 1 && block.shape[i][j] == 0) continue;
            game.grid[i + positionY][j + positionX] = block.shape[i][j];
        }
    }
    return true;
}

bool GameController::collisionChecker(Block* blockPtr, BlockFall &game, int positionX, int positionY){
    Block block = *blockPtr;
    if(positionX + block.shape[0].size() > game.cols || positionX < 0) return true;

    for(int i = 0; i < block.shape.size(); i++){
        for(int j = 0; j < block.shape[0].size(); j++){
            if(game.grid[i+ positionY][j + positionX] == 1 && block.shape[i][j] == 1) return true;
        }
    }

    return false;
}

void GameController::blockNullifier(Block* blockPtr, BlockFall &game, int positionX, int positionY){
    Block block = *blockPtr;
    for(int i = 0; i < block.shape.size(); i++){
        for(int j = 0; j < block.shape[0].size(); j++){
            if(block.shape[i][j] == 0)
                continue;
            game.grid[i + positionY][j + positionX] = 0;
        }
    }
}

void GameController::gravitate(BlockFall &game){
    for(int i = game.rows - 1; i > 0; i--){
        for(int j = 0; j < game.cols; j++){
            if(game.grid[i][j] == 0){
                int checkedRow = i - 1;
                while(checkedRow >= 1 && game.grid[checkedRow][j] == 0){
                    checkedRow--;
                }
                if(checkedRow != 0 || game.grid[0][j] == 1){
                    game.grid[checkedRow][j] = 0;
                    game.grid[i][j] = 1;
                }
            }
        }
    }
}

void GameController::dropBlock(Block* blockPtr, BlockFall &game, int positionX) {

    int positionY = 0;
    while (positionY + blockPtr->shape.size() - 1 <= game.rows - 1) {
        bool state = collisionChecker(blockPtr, game, positionX, positionY);
        if (state) {
            insertBlock(blockPtr, game, positionX, positionY - 1);
            break;
        }

        positionY++;
    }

    if (positionY + blockPtr->shape.size() - 1 > game.rows - 1) insertBlock(blockPtr, game, positionX, positionY - 1);

    int blockScore = 0;
    for (int i = 0; i < blockPtr->shape.size(); i++) {
        for (int j = 0; j < blockPtr->shape[0].size(); j++) {
            if (blockPtr->shape[i][j] == 1) {
                blockScore++;
            }
        }
    }
    game.current_score += blockScore * (positionY - 1);
}

void GameController::rowColmpleter(BlockFall &game, bool beforePrinter){
    vector<int> zeroRow;
    for(int j = 0; j < game.cols; j++){
        zeroRow.push_back(0);
    }
    bool firstRowFound = false;

    for(int i = 0; i < game.rows; i++){
        bool rowDone = true;
        for(int j = 0; j < game.cols; j++){
            if(game.grid[i][j] == 0){
                rowDone = false;
                break;
            }
        }
        if(rowDone && !firstRowFound){
            firstRowFound = true;
            if(beforePrinter) {
                printGrid(game, true);
                cout << "\n";
            }
            game.current_score += game.cols;
            for(int k = i; k > 0; k--){
                game.grid[k] = game.grid[k - 1];
                game.grid[0] = zeroRow;
            }
        } else if(rowDone){
            game.current_score += game.cols;
            for(int k = i; k > 0; k--){
                game.grid[k] = game.grid[k - 1];
                game.grid[0] = zeroRow;
            }
        }
    }
}

void GameController::powerupCompleter(BlockFall &game, bool beforePrinter){
    bool powerupFound;
    for(int i = 0; i <= game.rows - game.power_up.size(); i++){
        for(int j = 0; j <= game.cols - game.power_up[0].size(); j++){
            powerupFound = true;
            for(int k = 0; k < game.power_up.size(); k++){
                for(int m = 0; m < game.power_up[0].size(); m++){
                    if(game.grid[i + k][j + m] != game.power_up[k][m]){
                        powerupFound = false;
                        break;
                    }
                    if(!powerupFound) break;
                }
            }
            if(powerupFound) break;
        }
        if(powerupFound) break;
    }

    if(powerupFound){
        if(beforePrinter) {
            printGrid(game, true);
            cout << "\n";
        }
        game.current_score += 1000;
        for(int i = 0; i < game.rows; i++){
            for(int j = 0; j < game.cols; j++){
                if(game.grid[i][j] == 1){
                    game.current_score += 1;
                    game.grid[i][j] = 0;
                }
            }
        }
    }
}

void GameController::printGrid(BlockFall &game, bool clearance){
    if(game.firstEntry){
        game.highScore = game.current_score;
    }
    if(!clearance){
        cout << "Score: " << game.current_score << "\n";
        cout << "High Score: " << game.highScore << "\n";
    } else {
        cout << "Before clearing:\n";
    }

    for(int i = 0; i < game.rows; i++){
        for(int j = 0; j < game.cols; j++){
            std::string printed;
            game.grid[i][j] == 1 ? printed = occupiedCellChar : printed = unoccupiedCellChar;
            cout << printed;
        }
        cout << "\n";
    }
    cout << "\n";
}
