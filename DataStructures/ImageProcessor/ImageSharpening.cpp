#include <iostream>
#include "ImageSharpening.h"

// Default constructor
ImageSharpening::ImageSharpening() {
    kernel_height = 3;
    kernel_width = 3;
    blurring_kernel = new double*[kernel_height];
    for (int i = 0; i < 3; ++i) {
        blurring_kernel[i] = new double[kernel_width];
    }

    for (int i = 0; i < 3; ++i) {
        for (int j = 0; j < 3; j++) {
            blurring_kernel[i][j] = 1.0/9.0;
        }
    }
}

ImageSharpening::~ImageSharpening(){

}

ImageMatrix ImageSharpening::sharpen(const ImageMatrix& input_image, double k) {
    Convolution* blurConvPtr = new Convolution(blurring_kernel, 3, 3, 1, 1);
    Convolution blurConv = *blurConvPtr;
    ImageMatrix blurredImg = blurConv.convolve(input_image);

    ImageMatrix sharpImg = input_image + ((input_image - blurredImg) * k);
    for(int i = 0; i < sharpImg.get_height(); i++) {
        for (int j = 0; j < sharpImg.get_width(); j++) {
            if(sharpImg.get_data(i, j) < 0) sharpImg.modifyPixel(i, j, 0);
            else if(sharpImg.get_data(i, j) > 255) sharpImg.modifyPixel(i, j, 255);
        }
    }

    return sharpImg;
}
