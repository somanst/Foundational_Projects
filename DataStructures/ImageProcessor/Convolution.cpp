#include <iostream>
#include <cstring>
#include <fstream>

#include "Convolution.h"

// Default constructor 
Convolution::Convolution() {
    kernel = new double*[3];
    for (int i = 0; i < 3; ++i) {
        kernel[i] = new double[3];
    }
    kernelHeight = 3;
    kernelWidth = 3;
    stride = 1;
    padding = true;
    padding_value = 1;

}

// Parametrized constructor for custom kernel and other parameters
Convolution::Convolution(double** customKernel, int kh, int kw, int stride_val, bool pad){
    kernel = customKernel;
    kernelHeight = kh;
    kernelWidth = kw;
    stride = stride_val;
    padding = pad;
    if(padding) padding_value = 1;
    else padding_value = 0;
}

// Destructor
Convolution::~Convolution() {

}

// Copy constructor
Convolution::Convolution(const Convolution &other){
    this -> kernel = other.kernel;
    this -> kernelWidth = other.kernelWidth;
    this -> kernelHeight = other.kernelHeight;
    this -> stride = other.stride;
    this -> padding = other.padding;
    this -> padding_value = other.padding_value;
}

// Copy assignment operator
Convolution& Convolution::operator=(const Convolution &other) {
    kernel = other.kernel;
    kernelWidth = other.kernelWidth;
    kernelHeight = other.kernelHeight;
    stride = other.stride;
    padding_value = other.padding_value;
    padding = other.padding;
}


// Convolve Function: Responsible for convolving the input image with a kernel and return the convolved image.
ImageMatrix Convolution::convolve(const ImageMatrix& input_image) const {
    int outputWidth = ((input_image.get_width() - kernelWidth + 2 * padding_value) / stride) + 1;
    int outputHeight = ((input_image.get_height() - kernelHeight + 2 * padding_value) / stride) + 1;
    ImageMatrix proccessedImage = input_image;

    if(padding){
        proccessedImage = applyPadding(input_image);
    }

    ImageMatrix* outputMatrixPtr = new ImageMatrix(outputHeight, outputWidth);
    ImageMatrix outputMatrix = *outputMatrixPtr;

    int modifyPositionY = -1;
    int modifyPositionX = 0;

    for (int y = kernelHeight / 2; y < proccessedImage.get_height() - kernelHeight / 2; y += stride) {
        modifyPositionY++;
        modifyPositionX = 0;
        for (int x = kernelWidth / 2; x < proccessedImage.get_height() - kernelWidth / 2; x += stride) {
            double modifiedValue = applyKernel(kernel, proccessedImage, kernelWidth, kernelHeight, x, y);
            outputMatrix.modifyPixel(modifyPositionY,modifyPositionX,modifiedValue);
            modifyPositionX++;
        }
    }

    return outputMatrix;

}

double Convolution::applyKernel(double** customKarnel, const ImageMatrix imageMatrix, int karnelWidth, int karnelHeight, int positionX, int positionY) const {
    double sum = 0;
    int kernelRadiusX = karnelWidth / 2;
    int kernelRadiusY = karnelHeight / 2;

    for(int i = 0; i < kernelHeight; ++i){
        for(int j = 0; j < kernelWidth; ++j){
            int imageX = positionX + j - kernelRadiusX;
            int imageY = positionY + i - kernelRadiusY;

            if(imageX >= 0 && imageX < imageMatrix.get_width() && imageY >= 0 && imageY < imageMatrix.get_height()){
                double first = imageMatrix.get_data(imageY,imageX);
                double second = customKarnel[i][j];
                sum += first * second;
            }
        }
    }

    return sum;
}

ImageMatrix Convolution::applyPadding(const ImageMatrix imageMatrix) const {
    int inputWidth = imageMatrix.get_width();
    int inputHeight = imageMatrix.get_height();

    int paddedWidth = inputWidth + 2 * padding_value;
    int paddedHeight = inputHeight + 2 * padding_value;

    double** paddedMatrixData = new double*[paddedHeight];
    for (int i = 0; i < paddedHeight; ++i) {
        paddedMatrixData[i] = new double[paddedWidth];
        std::memset(paddedMatrixData[i], 0, paddedWidth * sizeof(double));
    }

    for (int y = 0; y < inputHeight; ++y) {
        for (int x = 0; x < inputWidth; ++x) {
            paddedMatrixData[y + padding_value][x + padding_value] = imageMatrix.get_data(y,x);
        }
    }

    ImageMatrix* paddedMatrixPtr = new ImageMatrix(paddedWidth, paddedHeight);
    ImageMatrix paddedMatrix = *paddedMatrixPtr;
    paddedMatrix.applyMatrix(paddedMatrixData);

    return paddedMatrix;

}
