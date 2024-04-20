import re
#First, we define the lists we are gonna work with, inputFile being the input list splitted by new lines and commas, innerList being a list to reach 
#names of patients that dont have a comma before them, and patientlist being the main list that has the patients added to/removed from
innerList = []
inputFile = []
recommendationBool = False
patientsList = [
    ["patient ", "Diagnoses ", "Disease ","Disease ","Treatment ", "Treatment ", "\n"],
    ["Name ", "Accuracy ", "Name ","Incidence ","Name ", "Risk ", "\n"],
    ["-------------------------------------------------------------------------"]
    ]
#Then, we define the input file reading function that arranges input list and creates the output file 
def fileReading():
    global inputFile
    global outputFile
    f = open('doctors_aid_inputs.txt').read()
    inputFile = re.split('\n|,' , f)     
    outputFile = open('doctors_aid_outputs.txt', "w")

fileReading()
for n in inputFile:
    innerList.append(n.split(" "))

#here we define the create function, we check if the patient is found in the main patient list then if abscent add him/her with his/her data into the main list 
def create(x):
    patientFound = 0
    for patient in patientsList:
        for data in patient:
            if innerList[x][1] == data:
                patientFound = 1
                outputFile.write('Patient ' + ''.join(innerList[x][1]) + " cannot be recorded due to duplication.\n")
    if patientFound == 0:
        l = []
        l.append("\n")
        l.append(innerList[x][1])
        l.append("{:.2%}".format(float(inputFile[x+1])))
        for n in range(x+2,x+5):
            l.append(inputFile[n])
        l.append("{:.0%}".format(float(inputFile[x+5])))
        patientsList.append(l)
        outputFile.write('Patient ' + ''.join(innerList[x][1]) + " is recorded.\n")

#defining the list function to list each patient and adding a new line after each one.
def list():
    for patient in patientsList:
        for data in patient:
            if data != "\n":
                outputFile.write("{:<20}".format(data))
            else:
                outputFile.write(data)
    outputFile.write("\n")

#defining the list of removing a patient, first checking if the patient is in the main list then removing the patient with his data from the main list.
def remove(x):
    patientFound = 0
    for a in patientsList:
            for b in a:
                if innerList[x][1] == b:
                    patientsList.remove(a)
                    string = 'Patient ' + ''.join(innerList[x][1]) + ' is removed.\n'                    
                    patientFound = 1
                elif innerList[1] != b:
                    print()
    if patientFound != 1:
        string = 'Patient ' + ''.join(innerList[x][1]) + " cannot be removed due to abscence.\n"
    outputFile.write(string)    

#defining the probability function, which checks whether this function was called independently first or called from the recommendation function and if the patient
#exists then calculates the probability of having the disease mentioned.
def probability(x):
    global recommendationBool
    patientFound = 0
    newLine = 0
    for patient in patientsList:
        for data in patient:
            if data == "\n":
                newLine = 1
            if innerList[x][1] == data:
                patientFound = 1
                if newLine == 0:
                    calculateList = a[3].split("/")
                    accuracy = float(a[1].strip("%"))                    
                else:
                    calculateList = patient[4].split("/")
                    accuracy = float(patient[2].strip("%"))
                infected = float(calculateList[0])
                healthy = float(calculateList[1]) 
                probability = 100-(100*(1-(accuracy*infected/((100 - accuracy)*healthy+accuracy*infected)))) 
                probabilityString = "{:.2f}".format(probability)
                probabilityString = probabilityString.replace(".00", "")
                string = "Patient " + ''.join(innerList[x][1]+ " has a probability of " + probabilityString + "% of having" + patient[3].lower() + ".\n")
                if recommendationBool == False:
                   outputFile.write(string)
                return probability
    if patientFound == 0:
        string = 'Probability for ' + ''.join(innerList[x][1]) + " cannot be calculated due to absence.\n"
    outputFile.write(string)
         
#defining the recommendation function which calculates the probability of the mentioned patient then compares it with the risk of treatment and gives a suggestion whether to
#take the treatment or not.
def recommendation(x):
    global recommendationBool
    recommendationBool = True
    patientFound = 0
    for a in patientsList:
        for b in a:
            if b == innerList[x][1]:
                patientFound = 1
                if probability(x) > float(a[6].strip("%")):
                    string = "System suggests " + ''.join(innerList[x][1]) + " to have the treatment.\n"                    
                elif probability(x) < float(a[6].strip("%")):
                    string = "System suggests "+ ''.join(innerList[x][1]) + " NOT to have the treatment.\n"                
    if patientFound == 0:
        string = "Recommendation for " + innerList[x][1] + " cannot be calculated due to absence.\n"
    outputFile.write(string)
    recommendationBool = False

#The main function of reading the input file and printing the output according to the commands of the user.
def outputWriting():
    for i in range(len(inputFile)):
        innyList = inputFile[i].split(" ")
        
        if innyList[0] == "create":
            create(i)                
        elif innyList[0] == "remove":                 
            remove(i)
        elif innyList[0] == "list":
             list()
        elif innyList[0] == "probability":
            probability(i)
        elif innyList[0] == "recommendation":
            recommendation(i)
outputWriting()
outputFile.close()

#Made by: Adam Sattout
#StudentID : b2220765061