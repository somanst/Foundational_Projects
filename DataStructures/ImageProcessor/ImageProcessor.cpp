#include "ImageProcessor.h"

ImageProcessor::ImageProcessor() {

}

ImageProcessor::~ImageProcessor() {

}


std::string ImageProcessor::decodeHiddenMessage(const ImageMatrix &img) {
    ImageSharpening* imgSharperPtr = new ImageSharpening();
    ImageSharpening imgSharper = *imgSharperPtr;
    ImageMatrix sharpImg = imgSharper.sharpen(img, 2.0);

    EdgeDetector* edgeDetectorPtr = new EdgeDetector();
    EdgeDetector edgeDetector = *edgeDetectorPtr;
    std::vector<std::pair<int,int>> edgeVector = edgeDetector.detectEdges(sharpImg);

    DecodeMessage* decodeMessagePtr = new DecodeMessage();
    DecodeMessage decodeMessage = *decodeMessagePtr;
    std::string message = decodeMessage.decodeFromImage(sharpImg, edgeVector);
    return message;
}

ImageMatrix ImageProcessor::encodeHiddenMessage(const ImageMatrix &img, const std::string &message) {
    ImageSharpening* imgSharperPtr = new ImageSharpening();
    ImageSharpening imgSharper = *imgSharperPtr;
    ImageMatrix sharpImg = imgSharper.sharpen(img, 2.0);

    EdgeDetector* edgeDetectorPtr = new EdgeDetector();
    EdgeDetector edgeDetector = *edgeDetectorPtr;
    std::vector<std::pair<int,int>> edgeVector = edgeDetector.detectEdges(sharpImg);

    EncodeMessage* messageEncoder = new EncodeMessage();
    ImageMatrix outputMatrix = messageEncoder->encodeMessageToImage(img, message, edgeVector);
    return outputMatrix;
}
