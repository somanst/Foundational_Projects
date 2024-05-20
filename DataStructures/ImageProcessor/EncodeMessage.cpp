#include "EncodeMessage.h"
#include <cmath>
#include <iostream>
#include <bitset>


// Default Constructor
EncodeMessage::EncodeMessage() {

}

// Destructor
EncodeMessage::~EncodeMessage() {
    
}

int EncodeMessage::checkIfPrime(int n){
    if(n < 2) return 0;
    else if ((n & 1) == 0 && n != 2) return 0;
    for(int i=3; i * i <= n; i+=2){
        if(n % i == 0)
            return 0;
    }
    return 1;
}

int EncodeMessage::applyFibo(int n) {
    if(n == 1 || n == 0) return n;
    else return(applyFibo(n - 1) + applyFibo(n - 2));
}

// Function to encode a message into an image matrix
ImageMatrix EncodeMessage::encodeMessageToImage(const ImageMatrix &img, const std::string &message, const std::vector<std::pair<int, int>>& positions) {
    std::string alteredMessage("");
    for(int i = 0; i < message.size(); i++){
        if(checkIfPrime(i)) {
            int increment = applyFibo(i);
            int dec = message[i];
            dec += increment;
            if(dec <= 32) dec += 33;
            if(dec >= 127) dec = 126;
            char a = dec;
            alteredMessage += a;
        }
        else alteredMessage += message[i];
    }

    for(int i = 0; i < alteredMessage.size() / 2; i++){
        char endingChar = alteredMessage.back();
        alteredMessage.pop_back();
        alteredMessage = endingChar + alteredMessage;
    }

    int decArray[alteredMessage.size()];
    for(int i = 0; i < alteredMessage.size(); i++){
        decArray[i] = alteredMessage[i];
    }

    std::string binaryString("");
    for(int i = 0; i < alteredMessage.size(); i++){
        binaryString += std::bitset<7>(decArray[i]).to_string();
    }

    int overloadedLsbCount = alteredMessage.size() * 7 - positions.size();
    std::string encodedBinaryString = binaryString.substr(0, binaryString.size() - overloadedLsbCount);


    ImageMatrix outputMatrix = img;

    for(int i = 0; i < positions.size(); i++){
        int y = positions[i].first;
        int x = positions[i].second;
        int pixelValue = img.get_data(y, x);
        std::string pixelBinaryForm = std::bitset<16>(pixelValue).to_string();
        pixelBinaryForm.pop_back();
        pixelBinaryForm += encodedBinaryString[i];
        int pixelUpdatedValue = std::stoi(pixelBinaryForm, nullptr, 2);
        outputMatrix.modifyPixel(y, x, pixelUpdatedValue);
    }

    return outputMatrix;


}

