#include "ImageMatrix.h"
#include <iostream>


// Default constructor
ImageMatrix::ImageMatrix(){
    height = 1;
    width = 1;
    data = new double*[height];
}


// Parameterized constructor for creating a blank image of given size
ImageMatrix::ImageMatrix(int imgHeight, int imgWidth) : height(imgHeight), width(imgWidth) {
    data = new double*[height];
    for (int i = 0; i < height; ++i) {
        data[i] = new double[width];
    }

    for (int i = 0; i < imgHeight; ++i) {
        for (int j = 0; j < imgWidth; j++) {
            data[i][j] = 0;
        }
    }
}

// Parameterized constructor for loading image from file. PROVIDED FOR YOUR CONVENIENCE
ImageMatrix::ImageMatrix(const std::string &filepath) {
    // Create an ImageLoader object and load the image
    ImageLoader imageLoader(filepath);

    // Get the dimensions of the loaded image
    height = imageLoader.get_height();
    width = imageLoader.get_width();

    // Allocate memory for the matrix
    data = new double*[height];
    for (int i = 0; i < height; ++i) {
        data[i] = new double[width];
    }

    // Copy data from imageLoader to data
    double** imageData = imageLoader.getImageData();
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; j++) {
            data[i][j] = imageData[i][j];
        }
    }
}



// Destructor
ImageMatrix::~ImageMatrix() {
    for (int i = 0; i < height; ++i) {
        delete[] data[i];
    }
    delete[] data;
}

// Parameterized constructor - direct initialization with 2D matrix
ImageMatrix::ImageMatrix(const double** inputMatrix, int imgHeight, int imgWidth){
    height = imgHeight;
    width = imgWidth;

    data = new double*[height];
    for (int i = 0; i < height; ++i) {
        data[i] = new double[width];
    }

    for (int i = 0; i < imgHeight; ++i) {
        for (int j = 0; j < imgWidth; j++) {
            data[i][j] = inputMatrix[i][j];
        }
    }
}

// Copy constructor
ImageMatrix::ImageMatrix(const ImageMatrix &other)  : width(other.width), height(other.height) {
    data = new double*[height];
    for (int i = 0; i < height; ++i) {
        data[i] = new double[width];
        std::copy(other.data[i], other.data[i] + width, data[i]);
    }
}

// Copy assignment operator
ImageMatrix& ImageMatrix::operator=(const ImageMatrix &other) {
    if (this != &other) {
        for (int i = 0; i < height; ++i) {
            delete[] data[i];
        }
        delete[] data;

        width = other.width;
        height = other.height;
        data = new double*[height];
        for (int i = 0; i < height; ++i) {
            data[i] = new double[width];
            for (int j = 0; j < width; ++j) {
                data[i][j] = other.data[i][j];
            }
        }
    }
    return *this;
}



// Overloaded operators

// Overloaded operator + to add two matrices
ImageMatrix ImageMatrix::operator+(const ImageMatrix &other) const {
    ImageMatrix* resultMatrixPtr = new ImageMatrix(height, width);
    double** resultArray = new double*[height];
    for (int i = 0; i < height; ++i) {
        resultArray[i] = new double[width];
    }
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            double sum = this->get_data(i,j) + other.get_data(i,j);
            resultArray[i][j] = sum;
        }
    }
    ImageMatrix resultMatrix = *resultMatrixPtr;
    resultMatrix.applyMatrix(resultArray);
    return resultMatrix;
}

// Overloaded operator - to subtract two matrices
ImageMatrix ImageMatrix::operator-(const ImageMatrix &other) const {
    ImageMatrix* resultMatrixPtr = new ImageMatrix(height, width);

    double** resultArray = new double*[height];
    for (int i = 0; i < height; ++i) {
        resultArray[i] = new double[width];
    }

    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            double sum = this->get_data(i,j) - other.get_data(i,j);
            resultArray[i][j] = sum;
        }
    }
    ImageMatrix resultMatrix = *resultMatrixPtr;
    resultMatrix.applyMatrix(resultArray);
    return resultMatrix;
}

// Overloaded operator * to multiply a matrix with a scalar
ImageMatrix ImageMatrix::operator*(const double &scalar) const {
    ImageMatrix* resultMatrixPtr = new ImageMatrix(height, width);

    double** resultArray = new double*[height];
    for (int i = 0; i < height; ++i) {
        resultArray[i] = new double[width];
    }

    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            double sum = this->get_data(i,j) * scalar;
            resultArray[i][j] = sum;
        }
    }
    ImageMatrix resultMatrix = *resultMatrixPtr;
    resultMatrix.applyMatrix(resultArray);
    return resultMatrix;
}


// Getter function to access the data in the matrix
double** ImageMatrix::get_data() const {
    return data;
}

// Getter function to access the data at the index (i, j)
double ImageMatrix::get_data(int i, int j) const {
    double value = data[i][j];
    return value;
}

int ImageMatrix::get_width() const{
    return width;
};

int ImageMatrix::get_height() const {
    return height;
}

void ImageMatrix::modifyPixel(int y, int x, double value) {
    data[y][x] = value;
}

void ImageMatrix::applyMatrix(double** inputMatrix) const {
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; j++) {
            data[i][j] = inputMatrix[i][j];
        }
    }
}