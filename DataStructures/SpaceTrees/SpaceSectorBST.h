#ifndef SPACESECTORBST_H
#define SPACESECTORBST_H

#include <iostream>
#include <fstream>  
#include <sstream>
#include <vector>

#include "Sector.h"

class SpaceSectorBST {
  
public:
    Sector *root;
    SpaceSectorBST();
    ~SpaceSectorBST();
    void readSectorsFromFile(const std::string& filename); 
    void insertSectorByCoordinates(int x, int y, int z);
    void deleteSector(const std::string& sector_code);
    void displaySectorsInOrder();
    void displaySectorsPreOrder();
    void displaySectorsPostOrder();
    std::vector<Sector*> getStellarPath(const std::string& sector_code);
    void printStellarPath(const std::vector<Sector*>& path);
    void insertSectorNode(Sector* &scetor, Sector* newSector, Sector* parent);
    std::vector<Sector*> inorderTraverse(Sector* root_);
    std::vector<Sector*> preorderTraverse(Sector* root_);
    std::vector<Sector*> postorderTraverse(Sector* root_);
    void deleteSectorHelper(Sector* &root_,const std::string &sectorCode);
    int deleteCaseChecker(Sector* sector);
    void mostLeftSwap(Sector* &root_, std::vector<int> &coordiantes);
    void treeDeleter(Sector* &root_);
};

#endif // SPACESECTORBST_H
