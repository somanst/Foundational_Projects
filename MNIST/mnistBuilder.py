import tensorflow as tf
import math
import numpy as np
import matplotlib.pyplot as plt

(trainImages, trainLabels), (testImages, testLabels) = tf.keras.datasets.mnist.load_data()
trainImages2 = trainImages.reshape(60000, 784)
testImages2 = testImages.reshape(10000, 784)
plt.imshow(trainImages2[2].reshape(28, 28), cmap = 'gray')

def Relu(x):
    return np.maximum(0, x)

def reluDerivative(x):
    return np.array(x > 0).astype(float)
    
def Softmax(x):
    y = np.exp(x - np.max(x))
    return y / np.sum(y)

def calculateLoss(label, softmax):
    epsilon = 1e-12
    value = np.clip(softmax, epsilon, 1.0)[label]
    return -(np.log(value))

def initializeParams(inputSize, h1Size, h2Size, outputSize):
    w1 = np.random.randn(h1Size, inputSize) *0.01
    b1 = np.zeros(h1Size)
    w2 = np.random.randn(h2Size, h1Size) *0.01
    b2 = np.zeros(h2Size)
    w3 = np.random.randn(outputSize, h2Size) *0.01
    b3 = np.zeros(outputSize)
    
    parameters = {"w1" : w1, 
                  "b1" : b1,
                  "w2" : w2, 
                  "b2" : b2,
                  "w3" : w3, 
                  "b3" : b3}
    return parameters

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

    
def backward(parameters, cache, label):

    w1 = parameters["w1"]
    b1 = parameters["b1"]
    w2 = parameters["w2"]
    b2 = parameters["b2"]
    w3 = parameters["w3"]
    b3 = parameters["b3"]
    x = parameters["x"]
    
    z1 = cache["z1"]
    a1 = cache["a1"]
    z2 = cache["z2"]
    a2 = cache["a2"]
    z3 = cache["z3"]
    a3 = cache["a3"]
    
    m = 10
    y = np.zeros(10)
    y[label] = 1
    
    dz3 = (a3 - y)
    dw3 = np.outer(dz3, a2)
    db3 = dz3
    
    dz2 = np.dot(w3.T, dz3)*reluDerivative(a2)
    dw2 = np.outer(dz2, a1)
    db2 = dz2
    
    dz1 = np.dot(w2.T, dz2)*reluDerivative(a1)
    dw1 = np.outer(dz1, x)
    db1 = dz1
    
    lr = 0.00025

    parameters["w1"] = w1 - dw1*lr
    parameters["w2"] = w2 - dw2*lr
    parameters["w3"] = w3 - dw3*lr
    parameters["b1"] = b1 - db1*lr
    parameters["b2"] = b2 - db2*lr
    parameters["b3"] = b3 - db3*lr
    
    return parameters

params = initializeParams(784, 100, 50, 10)
loss = []
x = 1
for i in range(trainImages2.shape[0] - 2):
    cache = forward(trainImages2[i], params)
    params = backward(params, cache, trainLabels[i])
    loss.append(calculateLoss(trainLabels[i], cache["a3"]))
    x += 1

print("x = ", x)
sum = 0

def show_image(image, title):
    plt.imshow(image, cmap='gray')
    plt.title(title)
    plt.axis('off')  # Hide the axis
    plt.show(block=False)
    plt.waitforbuttonpress()  # Wait for a button press
    plt.close()

for i in range(len(testImages2)):
    cache = forward(testImages2[i], params)
    if cache["a3"].argmax() == testLabels[i]: 
        sum = sum + 1
        #show_image(testImages[i].reshape(28, 28), "True")
    else:
        print(cache["a3"].argmax())
        #show_image(testImages[i].reshape(28, 28), "False")


print(sum / 100)

np.savetxt("C:/Users/lenovo/Desktop/MNIST/w1.txt", params["w1"])
np.savetxt("C:/Users/lenovo/Desktop/MNIST/b1.txt", params["b1"])
np.savetxt("C:/Users/lenovo/Desktop/MNIST/w2.txt", params["w2"])
np.savetxt("C:/Users/lenovo/Desktop/MNIST/b2.txt", params["b2"])
np.savetxt("C:/Users/lenovo/Desktop/MNIST/w3.txt", params["w3"])
np.savetxt("C:/Users/lenovo/Desktop/MNIST/b3.txt", params["b3"])
