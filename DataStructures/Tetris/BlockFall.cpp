#include <fstream>
#include <sstream>
#include <iostream>
#include "BlockFall.h"


BlockFall::BlockFall(string grid_file_name, string blocks_file_name, bool gravity_mode_on, const string &leaderboard_file_name, const string &player_name) : gravity_mode_on(
        gravity_mode_on), leaderboard_file_name(leaderboard_file_name), player_name(player_name) {
    initialize_grid(grid_file_name);
    read_blocks(blocks_file_name);
    leaderboard.read_from_file(leaderboard_file_name);

    if(leaderboard.head_leaderboard_entry != nullptr){
        this->highScore = leaderboard.head_leaderboard_entry->score;
        this->firstEntry = false;
    } else this->firstEntry = true;
}

void BlockFall::read_blocks(const string &input_file) {
    // TODO: Read the blocks from the input file and initialize "initial_block" and "active_rotation" member variables
    // TODO: For every block, generate its rotations and properly implement the multilevel linked list structure
    //       that represents the game blocks, as explained in the PA instructions.
    // TODO: Initialize the "power_up" member variable as the last block from the input file (do not add it to the linked list!)

    std::ifstream file(input_file);

    std::string line;

    int blockCount = 0;

    while (std::getline(file, line)) {
        if(line.find('[') != std::string::npos) blockCount++;
    }

    file.clear();
    file.seekg(0);

    vector<vector<bool>> curShape;
    bool firstBlock = true;

    while (std::getline(file, line)) {

        bool shapeFormed = false;
        std::istringstream iss(line);
        vector<bool> row;
        char unit;
        while (iss >> unit && !line.empty()){
            if(unit == '[') {
                curShape.clear();
                continue;
            }
            else if(unit == ']') {
                curShape.push_back(row);
                shapeFormed = true;
                break;
            }
            row.push_back(unit - '0');
        }
        if(shapeFormed && !line.empty() && firstBlock) {
            firstBlock = false;
            initial_block = new Block();
            initial_block->shape = curShape;
            createRotations(*initial_block);
            active_rotation = initial_block;
            blockCount--;
        } else if(shapeFormed && !line.empty() && blockCount > 1) {
            Block* block= new Block();
            block->shape = curShape;
            createRotations(*block);
            addBlock(initial_block, block);
            blockCount--;
        } else if(shapeFormed && blockCount == 1){
            power_up = curShape;
        } else if(!shapeFormed && !line.empty()) curShape.push_back(row);

    }

    file.close();
    rotationNext();

}

void BlockFall::initialize_grid(const string &input_file) {
    // TODO: Initialize "rows" and "cols" member variables
    // TODO: Initialize "grid" member variable using the command-line argument 1 in main

    rows = 0;
    cols = 0;

    std::ifstream file(input_file);

    std::string line;

    while (std::getline(file, line)) {
        cols = 0;
        std::istringstream iss(line);
        vector<int> row;
        int pixel;
        while (iss >> pixel) {
            row.push_back(pixel);
            cols++;
        }
        grid.push_back(row);
        rows++;
    }

    file.close();
}

Block* BlockFall::addBlock(Block* headBlock, Block* addedBlock){
    if(headBlock->next_block == nullptr){
        headBlock->next_block = addedBlock;
        return headBlock;
    }
    headBlock->next_block = addBlock(headBlock->next_block, addedBlock);
    return headBlock;
}


void BlockFall::createRotations(Block &block){
    vector<vector<bool>> rightRotationShape;
    vector<vector<bool>> leftRotationShape;
    vector<vector<bool>> doubleRotationShape;
    vector<vector<bool>> originalShape = block.shape;

    int shapeRows = originalShape.size();
    int shapeCols = originalShape[0].size();

    for(int i = 0; i < shapeCols; i++){
        vector<bool> rightRotationRow;
        vector<bool> leftRotationRow;
        for(int j = 0; j < shapeRows; j++){
            rightRotationRow.push_back(originalShape[shapeRows - 1 - j][i]);
            leftRotationRow.push_back(originalShape[j][shapeCols - 1 - i]);
        }
        rightRotationShape.push_back(rightRotationRow);
        leftRotationShape.push_back(leftRotationRow);
    }

    for(int i = 0; i < shapeRows; i++){
        vector<bool> doubleRotationRow;
        for(int j = 0; j < shapeCols; j++){
            doubleRotationRow.push_back(originalShape[shapeRows - 1 - i][shapeCols - 1 - j]);
        }
        doubleRotationShape.push_back(doubleRotationRow);
    }

    Block* rightBlockPtr = new Block();
    Block* leftBlockPtr = new Block();
    Block* doubleBlockPtr = new Block();

    rightBlockPtr->shape = rightRotationShape;
    leftBlockPtr->shape = leftRotationShape;
    doubleBlockPtr->shape = doubleRotationShape;

    block.right_rotation = rightBlockPtr;
    block.left_rotation = leftBlockPtr;

    rightBlockPtr->right_rotation = doubleBlockPtr;
    rightBlockPtr->left_rotation = &block;

    leftBlockPtr->right_rotation = &block;
    leftBlockPtr->left_rotation = doubleBlockPtr;

    doubleBlockPtr->right_rotation = leftBlockPtr;
    doubleBlockPtr->left_rotation = rightBlockPtr;
}

void BlockFall::rotationNext(){
    Block* blockPtr = initial_block;
    while(blockPtr->next_block != nullptr){
        blockPtr->left_rotation->next_block = blockPtr->next_block;
        blockPtr->right_rotation->next_block = blockPtr->next_block;
        blockPtr->right_rotation->right_rotation->next_block = blockPtr->next_block;
        blockPtr = blockPtr->next_block;
    }
}

BlockFall::~BlockFall() {
    // TODO: Free dynamically allocated memory used for storing game blocks

}
