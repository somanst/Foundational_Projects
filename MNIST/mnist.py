

from PIL import Image
import numpy as np
import matplotlib.pyplot as plt

def Relu(x):
    return np.maximum(0, x)

def Softmax(x):
    y = np.exp(x - np.max(x))
    return y / np.sum(y)

def forward(x, parameters):
    parameters["x"] = x
    w1 = parameters["w1"]
    b1 = parameters["b1"]
    w2 = parameters["w2"]
    b2 = parameters["b2"]
    w3 = parameters["w3"]
    b3 = parameters["b3"]
    

    z1 = np.dot(w1, x) + b1
    a1 = Relu(z1)

    z2 = np.dot(w2, a1) + b2
    a2 = Relu(z2)

    z3 = np.dot(w3, a2) + b3
    a3 = Softmax(z3)
    
    
    cache = { "z1" : z1, 
            "a1" : a1,
            "z2" : z2, 
            "a2" : a2,
            "z3" : z3, 
            "a3" : a3 }
    return cache

dir = "C:/Users/lenovo/Desktop/MNIST/"

b1 = np.loadtxt(dir + "b1.txt")
w1 = np.loadtxt(dir + "w1.txt").reshape((100, 784))

b2 = np.loadtxt(dir + "b2.txt")
w2 = np.loadtxt(dir + "w2.txt").reshape((50, 100))

b3 = np.loadtxt(dir + "b3.txt")
w3 = np.loadtxt(dir + "w3.txt").reshape((10, 50))


params = {"w1": w1, "b1": b1, "w2": w2, "b2": b2, "w3": w3, "b3": b3}
im = Image.open(dir + "num.png", "r")
im = im.convert("L")

def show_image(image, title):
    plt.imshow(image, cmap='gray')
    plt.title(title)
    plt.axis('off')  # Hide the axis
    plt.show(block=False)
    plt.waitforbuttonpress()  # Wait for a button press
    plt.close()

flatMatrix = np.array(im).flatten()

#show_image(flatMatrix.reshape(28, 28), "")

cache = forward(flatMatrix, params)
print("You drew the number: ", end = "")
print(cache["a3"].argmax(), "\nWith a confidence rate of: ", cache["a3"][cache["a3"].argmax()])