#include "Sector.h"
#include "cmath"

using namespace std;
// Constructor implementation

Sector::Sector(int x, int y, int z) : x(x), y(y), z(z), left(nullptr), right(nullptr), parent(nullptr), color(RED) {
    // TODO: Calculate the distance to the Earth, and generate the sector code
    distance_from_earth = sqrt(x*x + y*y + z*z);
    string distanceComp = to_string(static_cast<int> (floor(distance_from_earth)));
    string xCoor = (x > 0) ? "R" : "L";
    string yCoor = (y > 0) ? "U" : "D";
    string zCoor = (z > 0) ? "F" : "B";

    if(x == 0) xCoor = "S";
    if(y == 0) yCoor = "S";
    if(z == 0) zCoor = "S";

    string coorComp = xCoor + yCoor + zCoor;
    sector_code = distanceComp + coorComp;
    color = true;
}

Sector::~Sector() {
    // TODO: Free any dynamically allocated memory if necessary
}

void Sector::recalculate(int x_, int y_, int z_){
    x = x_;
    y = y_;
    z = z_;

    distance_from_earth = sqrt(x*x + y*y + z*z);
    string distanceComp = to_string(static_cast<int> (floor(distance_from_earth)));
    string xCoor = (x > 0) ? "R" : "L";
    string yCoor = (y > 0) ? "U" : "D";
    string zCoor = (z > 0) ? "F" : "B";

    if(x == 0) xCoor = "S";
    if(y == 0) yCoor = "S";
    if(z == 0) zCoor = "S";

    string coorComp = xCoor + yCoor + zCoor;
    sector_code = distanceComp + coorComp;
}

Sector& Sector::operator=(const Sector& other) {
    // TODO: Overload the assignment operator
    parent = other.parent;
    left =   other.left;
    right =  other.right;
    distance_from_earth = other.distance_from_earth;
    sector_code = other.sector_code;
    return *this;
}

bool Sector::operator==(const Sector& other) const {
    return (x == other.x && y == other.y && z == other.z);
}

bool Sector::operator!=(const Sector& other) const {
    return !(*this == other);
}

bool Sector::operator<(const Sector &other) const{
    if(x != other.x) return x < other.x;
    else if(y != other.y) return y < other.y;
    else return z < other.z;
}