// DecodeMessage.cpp

#include "DecodeMessage.h"
#include <iostream>
#include <cstdint>
#include <cstring>
#include <bitset>

// Default constructor
DecodeMessage::DecodeMessage() {
    // Nothing specific to initialize here
}

// Destructor
DecodeMessage::~DecodeMessage() {
    // Nothing specific to clean up
}

std::string DecodeMessage::decodeFromImage(const ImageMatrix& image, const std::vector<std::pair<int, int>>& edgePixels){
    std::string binaryString("");

    for(int i = 0; i < edgePixels.size(); i++){
        int y = edgePixels[i].first;
        int x = edgePixels[i].second;
        int pixelValue = image.get_data(y, x);
        int LSB = pixelValue & 1;
        std::string LSBchar(std::to_string(LSB));
        binaryString.append(LSBchar);
    }

    if(binaryString.size() % 7 != 0){
        int zeroCount = 0;
        if(binaryString.size() < 7) {
            zeroCount = 7 - binaryString.size();
        }
        else {
            zeroCount = 7 - binaryString.size() % 7;
        }
        for(int i = 0; i < zeroCount; i++){
            binaryString.insert(0, "0");
        }

    }

    int byteCount = binaryString.size() / 7;

    std::string byteArray[byteCount];
    std::string message("");
    for(int i = 0; i < byteCount; i++){
        byteArray[i] = binaryString.substr(i * 7, 7);
        int dec = std::stoi(byteArray[i], nullptr, 2);
        if(dec <= 32) dec += 33;
        if(dec >= 127) dec = 126;
        char a = dec;
        message.append(1, a);
    }


    return message;

}

