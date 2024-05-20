// Convolution.h

#ifndef CONVOLUTION_H
#define CONVOLUTION_H

#include "ImageMatrix.h"

// Class `Convolution`: Provides the functionality to convolve an image with
// a kernel. Padding is a bool variable, indicating whether to use zero padding or not.
class Convolution {
public:
    // Constructors and destructors
    Convolution(); // Default constructor
    Convolution(double** customKernel, int kernelHeight, int kernelWidth, int stride, bool padding); // Parametrized constructor for custom kernel and other parameters
    ~Convolution(); // Destructor

    Convolution(const Convolution &other); // Copy constructor
    Convolution& operator=(const Convolution &other); // Copy assignment operator

    ImageMatrix convolve(const ImageMatrix& input_image) const; // Convolve Function: Responsible for convolving the input image with a kernel and return the convolved image.
    double applyKernel(double** customKarnel, const ImageMatrix imageMatrix, int karnelWidth, int karnelHeight, int positionX, int positionY) const;
    ImageMatrix applyPadding(const ImageMatrix imageMatrix) const;


private:
    // Add any private member variables and functions .
    int kernelHeight;
    int kernelWidth;
    int stride;
    bool padding;
    double** kernel;
    int padding_value;
};

#endif // CONVOLUTION_H
