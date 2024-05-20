#ifndef SPACESECTORLLRBT_H
#define SPACESECTORLLRBT_H

#include "Sector.h"
#include <iostream>
#include <fstream>  
#include <sstream>
#include <vector>

class SpaceSectorLLRBT {
public:
    Sector* root;
    SpaceSectorLLRBT();
    ~SpaceSectorLLRBT();
    void readSectorsFromFile(const std::string& filename);
    void insertSectorByCoordinates(int x, int y, int z);
    void displaySectorsInOrder();
    void displaySectorsPreOrder();
    void displaySectorsPostOrder();
    std::vector<Sector*> getStellarPath(const std::string& sector_code);
    void printStellarPath(const std::vector<Sector*>& path);
    void insertSectorNode(Sector* &sector, Sector* newSector, Sector* parent_);
    void rotateLeft(Sector* &root_);
    void rotateRight(Sector* &root_);
    bool checkBalance(Sector* &root_);
    void flipColors(Sector* &root_);
    std::vector<Sector*> inorderTraverse(Sector* root_);
    std::vector<Sector*> preorderTraverse(Sector* root_);
    std::vector<Sector*> postorderTraverse(Sector* root_);
    void treeDeleter(Sector* &root_);
};

#endif // SPACESECTORLLRBT_H
