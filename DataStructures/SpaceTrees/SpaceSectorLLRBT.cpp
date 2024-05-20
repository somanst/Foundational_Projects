#include "SpaceSectorLLRBT.h"

using namespace std;

SpaceSectorLLRBT::SpaceSectorLLRBT() : root(nullptr) {}

void SpaceSectorLLRBT::readSectorsFromFile(const std::string& filename) {
    // TODO: read the sectors from the input file and insert them into the LLRBT sector map
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

// Remember to handle memory deallocation properly in the destructor.
SpaceSectorLLRBT::~SpaceSectorLLRBT() {
    // TODO: Free any dynamically allocated memory in this class.

    treeDeleter(root);
}

void SpaceSectorLLRBT::treeDeleter(Sector* &root_){
    if(root_->right) treeDeleter(root_->right);
    if(root_->left) treeDeleter(root_->left);

    delete root_;
}

void SpaceSectorLLRBT::insertSectorByCoordinates(int x, int y, int z) {
    // TODO: Instantiate and insert a new sector into the space sector LLRBT map 
    // according to the coordinates-based comparison criteria.

    Sector* newSector = new Sector(x, y, z);
    if(!root){
        root = newSector;
        root->color = false;
    }
    else{

        insertSectorNode(root, newSector, nullptr);
        bool ss = checkBalance(root);

        while(ss){
            ss = checkBalance(root);
            root->color = false;
        }

        cout << "";
    }
}

void SpaceSectorLLRBT::insertSectorNode(Sector* &sector, Sector* newSector, Sector* parent_){
    if(sector == nullptr){
        sector = newSector;
        sector->parent = parent_;
        return;
    }

    if(*newSector < *sector){
        insertSectorNode(sector->left, newSector, sector);
    } else insertSectorNode(sector->right, newSector, sector);
}

bool SpaceSectorLLRBT::checkBalance(Sector* &root_){

    if(!root_->right && !root_->left) return false;
    if(root_->right && root_->left){
        if(root_->right->color && root_->left->color){
            flipColors(root_);
            return true;
        }
    }
    if(root_->right){
        if(root_->right->color){

            rotateLeft(root_);
            return true;
        }
    } if(root_->left) {
        if(root_->left->left){
            if (root_->left->color && root_->left->left->color) {

                rotateRight(root_);
                return true;
            }
        }
    }

    bool x = false ,y = false;
    if(root_->right){
        x = checkBalance(root_->right);
    }
    if(root_->left){
        y = checkBalance(root_->left);
    }
    return x || y;

}


void SpaceSectorLLRBT::flipColors(Sector* &root_){
    root_->right->color = false, root_->left->color = false;
    root_->color = true;
}

void SpaceSectorLLRBT::rotateLeft(Sector* &root_){

    Sector* temp =  root_;
     Sector* leftyLeftovers = nullptr;

     if(root_->right->left){
         leftyLeftovers = root_->right->left;
     }
     root_ = root_->right;
     root_->parent = temp->parent;
     root_->left = temp;

     temp->right = leftyLeftovers;
     if(leftyLeftovers) temp->right->parent = temp;
     temp->parent = root_;
     bool tempBool = root_->color;
     root_->color = temp->color;
     temp->color = tempBool;

}

void SpaceSectorLLRBT::rotateRight(Sector* &root_){
    Sector* temp =  root_;
    Sector* rightyLeftovers = nullptr;
    if(root_->left->right){
        rightyLeftovers = root_->left->right;
    }

    root_ = root_->left;
    root_->right = temp;
    root_->parent = temp->parent;
    temp->parent = root_;
    temp->left = rightyLeftovers;


    if(rightyLeftovers) temp->left->parent = temp;

    bool tempBool = root_->color;
    root_->color = temp->color;
    temp->color = tempBool;
}

void SpaceSectorLLRBT::displaySectorsInOrder() {
    // TODO: Traverse the space sector LLRBT map in-order and print the sectors 
    // to STDOUT in the given format.

    vector<Sector*> inorderVector = inorderTraverse(root);
    cout << "Space sectors inorder traversal:\n";
    for(Sector* ptr : inorderVector){
        string color = (ptr->color) ? "RED sector: " : "BLACK sector: ";
        cout << color << ptr->sector_code << endl;
    }
    cout << "\n";
}

vector<Sector*> SpaceSectorLLRBT::inorderTraverse(Sector* root_){
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

void SpaceSectorLLRBT::displaySectorsPreOrder() {
    // TODO: Traverse the space sector LLRBT map in pre-order traversal and print 
    // the sectors to STDOUT in the given format.

    vector<Sector*> preorderVector = preorderTraverse(root);
    cout << "Space sectors preorder traversal:\n";
    for(Sector* ptr : preorderVector){
        string color = (ptr->color) ? "RED sector: " : "BLACK sector: ";
        cout << color << ptr->sector_code  << endl;
    }
}

vector<Sector*> SpaceSectorLLRBT::preorderTraverse(Sector* root_){
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

void SpaceSectorLLRBT::displaySectorsPostOrder() {
    // TODO: Traverse the space sector LLRBT map in post-order traversal and print 
    // the sectors to STDOUT in the given format.

    vector<Sector*> postorderVector = postorderTraverse(root);
    cout << "Space sectors postorder traversal:\n";
    for(Sector* ptr : postorderVector){
        string color = (ptr->color) ? "RED sector: " : "BLACK sector: ";
        cout << color << ptr->sector_code  << endl;
    }
    cout << "\n";
}

vector<Sector*> SpaceSectorLLRBT::postorderTraverse(Sector* root_){
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

std::vector<Sector*> SpaceSectorLLRBT::getStellarPath(const std::string& sector_code) {
    std::vector<Sector*> path;
    // TODO: Find the path from the Earth to the destination sector given by its
    // sector_code, and return a vector of pointers to the Sector nodes that are on
    // the path. Make sure that there are no duplicate Sector nodes in the path!

    vector<Sector*> postorder = postorderTraverse(root);
    int targetIndex;
    int earthIndex;
    for(targetIndex = 0; targetIndex < postorder.size(); targetIndex++){
        if(postorder[targetIndex]->sector_code == sector_code) break;
    }

    for(earthIndex = 0; earthIndex < postorder.size(); earthIndex++){
        if(postorder[earthIndex]->sector_code == "0SSS") break;
    }

    if(targetIndex == postorder.size()) return path;

    if(root->sector_code == "0SSS"){
        path.push_back(postorder[targetIndex]);
        Sector* parentLink = postorder[targetIndex]->parent;
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

    Sector* parentLink;
    Sector* earthParentLink = postorder[earthIndex];
    Sector* sharedParent;
    int found;

    while(earthParentLink != nullptr){
        found = 0;
        parentLink = postorder[targetIndex]->parent;
        while(parentLink != nullptr){
            if(parentLink == earthParentLink){
                sharedParent = parentLink;
                found = 1;
                break;
            }
            parentLink = parentLink->parent;
        }
        if(found != 0) break;
        else if(earthParentLink == postorder[targetIndex]){
            found = 2;
            break;
        }
        earthParentLink = earthParentLink->parent;
    }

    parentLink = postorder[targetIndex]->parent;
    earthParentLink = postorder[earthIndex];
    if(found == 1) {
        path.push_back(postorder[targetIndex]);
        while (parentLink != sharedParent) {
            path.push_back(parentLink);
            parentLink = parentLink->parent;
        }

        if(sharedParent != earthParentLink){
            path.push_back(sharedParent);
        }

        vector<Sector*> earthSubpath;

        while (earthParentLink != sharedParent) {
            earthSubpath.push_back(earthParentLink);
            earthParentLink = earthParentLink->parent;
        }

        if(postorder[earthIndex] == parentLink){
            earthSubpath.push_back(parentLink);
        }

        int earthSize = earthSubpath.size();

        for (int i = 0; i < earthSize / 2; ++i) {
            std::swap(earthSubpath[i], earthSubpath[earthSize - i - 1]);
        }

        path.insert(path.end(), earthSubpath.begin(), earthSubpath.end());
    }

    else if(found == 2){
        earthParentLink = postorder[earthIndex];
        while(earthParentLink != parentLink){
            path.push_back(earthParentLink);
            earthParentLink = earthParentLink->parent;
        }

        return path;
    }


    int size = path.size();

    for (int i = 0; i < size / 2; ++i) {
        std::swap(path[i], path[size - i - 1]);
    }

    return path;
}

void SpaceSectorLLRBT::printStellarPath(const std::vector<Sector*>& path) {
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