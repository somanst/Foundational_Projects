// EdgeDetector.cpp

#include "EdgeDetector.h"
#include <cmath>

#include "EdgeDetector.h"
#include <cmath>

// Default constructor
EdgeDetector::EdgeDetector() {
    sobelKernelX = new double*[3];

    sobelKernelX[0]  = new double[3]{-1.0, 0.0, 1.0};
    sobelKernelX[1] = new double[3]{-2.0, 0.0, 2.0};
    sobelKernelX[2] = new double[3]{-1.0, 0.0, 1.0};


    sobelKernelY = new double*[3];

    sobelKernelY[0]  = new double[3]{-1.0, -2.0, -1.0};
    sobelKernelY[1] = new double[3]{0.0, 0.0, 0.0};
    sobelKernelY[2] = new double[3]{1.0, 2.0, 1.0};


}

// Destructor
EdgeDetector::~EdgeDetector() {

}

// Detect Edges using the given algorithm
std::vector<std::pair<int, int>> EdgeDetector::detectEdges(const ImageMatrix& input_image) {
    Convolution* horizontalConvPtr = new Convolution(sobelKernelX, 3, 3, 1, 1);
    Convolution* verticalConvPtr = new Convolution(sobelKernelY, 3, 3, 1, 1);

    Convolution horizontalConv = *horizontalConvPtr;
    Convolution verticalConv = *verticalConvPtr;

    ImageMatrix imageX = horizontalConv.convolve(input_image);
    ImageMatrix imageY = verticalConv.convolve(input_image);
    ImageMatrix* gradiantMatrixPtr = new ImageMatrix(input_image.get_height(), input_image.get_width());
    ImageMatrix gradiantMatrix = *gradiantMatrixPtr;

    double magnitude;
    double magnitudeSums = 0;
    int magnitdueCount = 0;
    for(int i = 0; i < input_image.get_height(); i++){
        for(int j = 0; j < input_image.get_width(); j++){
            magnitude = sqrt(pow(imageX.get_data(i, j), 2.0) + pow(imageY.get_data(i, j), 2.0));
            magnitudeSums += magnitude;
            magnitdueCount++;
            gradiantMatrix.modifyPixel(i, j, magnitude);
        }
    }

    double threshold = magnitudeSums/magnitdueCount;

    std::vector<std::pair<int,int>> edgePixels;

    for(int i = 0; i < input_image.get_height(); i++){
        for(int j = 0; j < input_image.get_width(); j++){
            if(gradiantMatrix.get_data(i, j) > threshold){
                double x = gradiantMatrix.get_data(i,j);
                edgePixels.push_back(std::make_pair(i, j));
            }
        }
    }

    return edgePixels;
}

