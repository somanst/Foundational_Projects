#include "SpaceSectorBST.h"

using namespace std;

SpaceSectorBST::SpaceSectorBST() : root(nullptr) {}

SpaceSectorBST::~SpaceSectorBST() {
    // Free any dynamically allocated memory in this class.

    treeDeleter(root);
}

void SpaceSectorBST::treeDeleter(Sector* &root_){
    if(!root_) return;
    if(root_->right != nullptr) treeDeleter(root_->right);
    if(root_->left != nullptr) treeDeleter(root_->left);

    delete root_;
}

void SpaceSectorBST::readSectorsFromFile(const std::string& filename) {
    // TODO: read the sectors from the input file and insert them into the BST sector map
    // according to the given comparison critera based on the sector coordinates.

    std::ifstream file(filename);
    std::string line;

    std::getline(file, line);

    while(std::getline(file, line)){
        vector<string> coordinates;
        int coordinatesObserved = 0;

        while (coordinatesObserved != 2) {
            size_t pos = line.find(',');
            string substring = line.substr(0, pos);
            coordinates.push_back(substring);
            line.erase(0, pos + 1);
            coordinatesObserved++;
        }
        coordinates.push_back(line);

        insertSectorByCoordinates(stoi(coordinates[0]), stoi(coordinates[1]), stoi(coordinates[2]));

    }
}

void SpaceSectorBST::insertSectorByCoordinates(int x, int y, int z) {
    // Instantiate and insert a new sector into the space sector BST map according to the 
    // coordinates-based comparison criteria.

    Sector* newSector = new Sector(x, y, z);
    insertSectorNode(root, newSector, nullptr);


}

void SpaceSectorBST::insertSectorNode(Sector* &sector, Sector* newSector, Sector* parent_){
    if(sector == nullptr){
        sector = newSector;
        sector->parent = parent_;
        return;
    }

    if(*newSector < *sector){
        insertSectorNode(sector->left, newSector, sector);
    } else insertSectorNode(sector->right, newSector, sector);
}

void SpaceSectorBST::deleteSector(const std::string& sector_code) {
    // TODO: Delete the sector given by its sector_code from the BST.
    deleteSectorHelper(root, sector_code);
}

void SpaceSectorBST::deleteSectorHelper(Sector* &root_, const string &sectorCode){

    if(!root_) return;

    int case_ = deleteCaseChecker(root_);
    if(root_->sector_code == sectorCode){
        if(case_ == 0){
            delete root_;
            root_ = nullptr;
        } else if(case_ == 1){
            Sector* temp = root_->left;
            Sector* tempParent = root_->parent;
            delete root_;
            root_ = temp;
            root_->parent = tempParent;
        } else if(case_ == 2){
            Sector* temp = root_->right;
            Sector* tempParent = root_->parent;
            delete root_;
            root_ = temp;
            root_->parent = tempParent;
        } else{
            vector<int> coordinates;
            mostLeftSwap(root_->right, coordinates);
            root_->recalculate(coordinates[0], coordinates[1], coordinates[2]);

        }
        return;
    }
    if(case_ == 0) {
        return;
    }
    else if(case_ == 1) deleteSectorHelper(root_->left, sectorCode);
    else if(case_ == 2) deleteSectorHelper(root_->right, sectorCode);
    else{
        deleteSectorHelper(root_->left, sectorCode);
        deleteSectorHelper(root_->right, sectorCode);
    }
}

void SpaceSectorBST::mostLeftSwap(Sector* &root_, vector<int> &coor){

    if(!root_->left){
        coor.push_back(root_->x);
        coor.push_back(root_->y);
        coor.push_back(root_->z);
        Sector* delPtr = root_;
        root_ = root_->right;
        delete delPtr;

    } else {
        mostLeftSwap(root_->left, coor);
    }
}

int SpaceSectorBST::deleteCaseChecker(Sector* sector){

    if(sector->right == nullptr && sector->left == nullptr) return 0;
    else if(!sector->right && sector->left) return 1;
    else if(!sector->left && sector->right) return 2;
    else return 3;
}


void SpaceSectorBST::displaySectorsInOrder() {
    // TODO: Traverse the space sector BST map in-order and print the sectors 
    // to STDOUT in the given format.

    vector<Sector*> inorderVector = inorderTraverse(root);
    cout << "Space sectors inorder traversal:\n";
    for(Sector* ptr : inorderVector){
        cout << ptr->sector_code  << endl;
    }
    cout << "\n";
}

vector<Sector*> SpaceSectorBST::inorderTraverse(Sector* root_){
    vector<Sector*> inorder;
    if(root_->left){
        vector<Sector*> miniorder = inorderTraverse(root_->left);
        inorder.insert(inorder.end(), miniorder.begin(), miniorder.end());
    }

    inorder.push_back(root_);

    if(root_->right){
        vector<Sector*> miniorder_ = inorderTraverse(root_->right);
        inorder.insert(inorder.end(), miniorder_.begin(), miniorder_.end());
    }

    return inorder;
}

void SpaceSectorBST::displaySectorsPreOrder() {
    // TODO: Traverse the space sector BST map in pre-order traversal and print 
    // the sectors to STDOUT in the given format.

    vector<Sector*> preorderVector = preorderTraverse(root);
    cout << "Space sectors preorder traversal:\n";
    for(Sector* ptr : preorderVector){
        cout << ptr->sector_code << endl;
    }
    cout << "\n";
}

vector<Sector*> SpaceSectorBST::preorderTraverse(Sector* root_){
    vector<Sector*> preorder;

    preorder.push_back(root_);

    if(root_->left){
        vector<Sector*> miniorder = preorderTraverse(root_->left);
        preorder.insert(preorder.end(), miniorder.begin(), miniorder.end());
    }

    if(root_->right){
        vector<Sector*> miniorder_ = preorderTraverse(root_->right);
        preorder.insert(preorder.end(), miniorder_.begin(), miniorder_.end());
    }

    return preorder;
}

void SpaceSectorBST::displaySectorsPostOrder() {
    // TODO: Traverse the space sector BST map in post-order traversal and print 
    // the sectors to STDOUT in the given format.

    vector<Sector*> postorderVector = postorderTraverse(root);
    cout << "Space sectors postorder traversal:\n";
    for(Sector* ptr : postorderVector){
        cout << ptr->sector_code << endl;
    }
    cout << "\n";
}

vector<Sector*> SpaceSectorBST::postorderTraverse(Sector* root_){
    vector<Sector*> postorder;

    if(root_->left){
        vector<Sector*> miniorder = postorderTraverse(root_->left);
        postorder.insert(postorder.end(), miniorder.begin(), miniorder.end());
    }

    if(root_->right){
        vector<Sector*> miniorder_ = postorderTraverse(root_->right);
        postorder.insert(postorder.end(), miniorder_.begin(), miniorder_.end());
    }

    postorder.push_back(root_);

    return postorder;
}

std::vector<Sector*> SpaceSectorBST::getStellarPath(const std::string& sector_code) {
    std::vector<Sector*> path;
    // TODO: Find the path from the Earth to the destination sector given by its
    // sector_code, and return a vector of pointers to the Sector nodes that are on
    // the path. Make sure that there are no duplicate Sector nodes in the path!

    vector<Sector*> postorder = postorderTraverse(root);
    int i;
    for(i = 0; i < postorder.size(); i++){
        if(postorder[i]->sector_code == sector_code) break;
    }

    if(i == postorder.size()) return path;

    path.push_back(postorder[i]);
    Sector* parentLink = postorder[i]->parent;
    while(parentLink != nullptr){
        path.push_back(parentLink);
        parentLink = parentLink->parent;
    }

    int size = path.size();

    for (int i = 0; i < size / 2; ++i) {
        std::swap(path[i], path[size - i - 1]);
    }

    return path;
}

void SpaceSectorBST::printStellarPath(const std::vector<Sector*>& path) {
    // TODO: Print the stellar path obtained from the getStellarPath() function 
    // to STDOUT in the given format.

    if(path.empty()){
        cout << "A path to Dr. Elara could not be found."<< endl;
        return;
    }
    cout << "The stellar path to Dr. Elara: ";
    for(int i = 0; i < path.size(); i++){
        cout << path[i]->sector_code;
        if(i != path.size() - 1) cout << "->";
    }
    cout << "\n\n";
}