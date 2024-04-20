#First we specify our variables and import the needed libraries, I imported the string library here just to make listing the alphabet to be able
#to detect letters easily later on.
import string
import sys
row, column = 0, 0
inputFile = open(sys.argv[1])
outputFile = open('output.txt', 'w')
alphabet = list(string.ascii_uppercase)
categoryNames = []
categoryInfo = []
dimensions = []

#Then we define the function that will check if a category was created before, if so then return its info, if not then print the suitable response
#this will be used in almost all the coming main functions
def categoryIdentifier(categoryName, caller):
    if categoryName not in categoryNames:
        if caller == "sell":
            print("Warning: Couldn't sell ticket because category was not found")
            outputFile.write("Warning: Couldn't sell ticket because category was not found\n")
        elif caller == "cancel":
            print("Warning: Couldn't cancel ticket because category was not found")
            outputFile.write("Warning: Couldn't cancel ticket because category was not found\n")
        elif caller == "balance":
            print("Warning: Couldn't calculate because category was not found")
            outputFile.write("Warning: Couldn't calculate because category was not found\n")
        elif caller == "show":
            print("Warning: Couldn't show because category was not found")
            outputFile.write("Warning: Couldn't show because category was not found\n")
        innerDimensions = 0
        categoryName = 0
        return categoryName , innerDimensions   
    for i in categoryNames:
        if i == categoryName:
            innerDimensions = dimensions[categoryNames.index(i)]
            categoryName = categoryInfo[categoryNames.index(i)]
            return categoryName , innerDimensions 


#This function is a sub-function that will be used in many of the coming functions. This function prints out the proper response to a false input
#from a user whether it be a seat with more rows, columns, or both.   
def insufficientSpace(i, innerDimensions, categoryName):
    if alphabet.index(i[0]) + 1 > int(innerDimensions[0]) and int(i.strip(i[0])) > int(innerDimensions[1]):
        print("Warning: The category '%s' has less rows and columns than the specified index %s" %(categoryName, i.strip("\n")))
        outputFile.write("Warning: The category '%s' has less rows and columns than the specified index %s\n" %(categoryName, i.strip("\n")))
    elif alphabet.index(i[0]) + 1 > int(innerDimensions[0]):
        print("Warning: The category '%s' has less rows than the specified index %s" %(categoryName, i.strip("\n")))
        outputFile.write("Warning: The category '%s' has less rows than the specified index %s\n" %(categoryName, i.strip("\n")))
    elif int(i.strip(i[0])) > int(innerDimensions[1]):
        print("Warning: The category '%s' has less columns than the specified index %s" %(categoryName, i.strip("\n")))
        outputFile.write("Warning: The category '%s' has less columns than the specified index %s\n" %(categoryName, i.strip("\n")))

#In this function, we create the category by creating a dictionary indicating the seat name as a key and customer's info as value for each seat.
def createCategory(name,size):
    dimensions = size.split("x")
    global row
    global column
    row, column = dimensions[0], dimensions[1]
    if int(row) > 26:
        print("Couldn't create category because row limit has been exceded")
        outputFile.write("Couldn't create category because row limit has been exceded\n")
        return False
    if name in categoryNames:
        return True
    print("The category '{N}' having {S} seats has been created".format(N = name, S = str(int(row)*int(column))))
    outputFile.write("The category '%s' having %d seats has been created\n" % (name, int(row)*int(column)))
    name = {}
    for i in range(int(row)):
        seat = alphabet[i]
        for j in range(int(column)):
            seat = seat + str(j)
            name[seat] = "X"
            seat = alphabet[i]
    outputFile.write("")
    return name

#For the sell ticket we first check if the seat specified was a range or a single seat, if single then try to find the seat and put the customer's info
#into it, if not then print out that there is no seat found with the insufficientSpace function. If it was a range then take the start and end of the
#and do the same thing, and at the end inform the user if the seat was purchased.
def sellTicket(info):
    customer, type, categoryName, seats = info[0], info[1], info[2], info[3:]
    category, innerDimensions = categoryIdentifier(categoryName, "sell")
    if category == 0:
        return()
    for i in seats:
        keyfound = 0   
        rangeApplicable = 1
        purchaseDone = 0
        if i.split("-")[0] == i:
            for key, value in category.items():
                if key == i.strip("\n") and category[key] == "X":               
                    category[key] = [customer, type]
                    keyfound = 1
                    purchaseDone = 1
                elif key == i.strip("\n") and category[key] != "X":
                    print("Warning: The seat %s cannot be sold to %s since it was already sold!"%(i.strip("\n"), customer))
                    outputFile.write("Warning: The seat %s cannot be sold to %s since it was already sold!\n"%(i.strip("\n"), customer))
                    purchaseDone = 0
                    keyfound = 1
            if keyfound == 0:
                insufficientSpace(i, innerDimensions, categoryName)
                purchaseDone = 0
                    
        elif i.split("-")[0] != i:
            rangeApplicable = 1
            for key, value in category.items():
                keyfound = 0
                if alphabet.index(i[0]) + 1 > int(innerDimensions[0]) and int(i.split("-")[1]) > int(innerDimensions[1]):
                    print("Warning: The category '%s' has less rows and columns than the specified index %s" %(categoryName, i.strip("\n")))
                    outputFile.write("Warning: The category '%s' has less rows and columns than the specified index %s\n" %(categoryName, i.strip("\n")))
                    purchaseDone = 0
                    break
                elif alphabet.index(i[0]) + 1 > int(innerDimensions[0]):
                    print("Warning: The category '%s' has less rows than the specified index %s" %(categoryName, i.strip("\n")))
                    outputFile.write("Warning: The category '%s' has less rows than the specified index %s\n" %(categoryName, i.strip("\n")))
                    purchaseDone = 0
                    break
                elif int(i.split("-")[1]) > int(innerDimensions[1]):
                    print("Warning: The category '%s' has less columns than the specified index %s" %(categoryName, i.strip("\n")))
                    outputFile.write("Warning: The category '%s' has less columns than the specified index %s\n" %(categoryName, i.strip("\n")))
                    purchaseDone = 0
                    break
                for n in range(int(i.split("-")[0].strip(i.split("-")[0][0])), int(i.split("-")[1]) + 1):
                    checkingKey = i.split("-")[0][0] + str(n)                   
                    if key == checkingKey and category[checkingKey] != "X":
                        print("Warning: The seats %s cannot be sold to %s due some of them have already been sold!"%(i.strip("\n"), customer))
                        outputFile.write("Warning: The seats %s cannot be sold to %s due some of them have already been sold!\n"%(i.strip("\n"), customer))
                        rangeApplicable = 0
                        purchaseDone = 0
                        break
                if rangeApplicable == 0:
                    break
                if rangeApplicable == 1:                    
                    for n in range(int(i.split("-")[0].strip(i.split("-")[0][0])), int(i.split("-")[1]) + 1):
                        checkingKey = i.split("-")[0][0] + str(n)
                        if key == checkingKey:
                            category[key] = [customer, type]
                        purchaseDone = 1
        if purchaseDone == 1:
            print("Success: %s has bought %s at %s"%(customer, i.strip("\n"), info[2]))
            outputFile.write("Success: %s has bought %s at %s\n"%(customer, i.strip("\n"), info[2]))

#To cancel a ticket, after checking if the category is available, we check for the info of the selected seat, and inform the user if it was already
#free or if the seat was successfully resetted  
def cancelTicket(info):
    categoryName, seats = info[0], info[1:]
    category, innerDimensions = categoryIdentifier(categoryName, "cancel")
    if category == 0:
        return()
    for i in seats:
        for key, value in category.items():
            keyFound = CancellingOutput(i, key, categoryName, category, innerDimensions)
        if keyFound == True:
            continue
        insufficientSpace(i, innerDimensions, categoryName)
 
#This function is a sub-function for the cancelTicket function the prints out if the seat was canceled or has been already free.
def CancellingOutput(i, key, categoryName, category, innerDimensions):
    if i.strip("\n") == key and category[key] != "X":
        category[key] = "X"
        print("Success: The seat %s at ’%s’ has been canceled and now ready to sell again"%(i.strip("\n"), categoryName))
        outputFile.write("Success: The seat %s at ’%s’ has been canceled and now ready to sell again\n"%(i.strip("\n"), categoryName))
        return(True)
    elif i.strip("\n") == key and category[key] == "X":
        print("Error: The seat %s at ’%s’ has already been free! Nothing to cancel"%(i.strip("\n"),categoryName))
        outputFile.write("Error: The seat %s at ’%s’ has already been free! Nothing to cancel\n"%(i.strip("\n"),categoryName))
        return(True)

#To calculate the balance of a specific category after checking if it has already been created, we set the number of student, season tickets, and
#full tickets to 0 and add 1 at each encounter in the category, then add the price of the ticket to the balance, and finally print out each value
#in a formatted way.                            
def balance(name):
    balance = 0
    student = 0
    full = 0
    season = 0
    name = name.strip("\n")
    category, innerDimensions = categoryIdentifier(name, "balance")
    if category == 0:
        return()
    for key, value in category.items():
        if value == "X":
            continue
        type = value[1]
        if type == "student":
            ammount = 10
            student += 1
        elif type == "full":
            ammount = 20
            full += 1
        elif type == "season":
            ammount = 250
            season += 1
        balance += ammount
    string = "Category report of ’%s'"%(name)
    print(string + "\n" + len(string) * "-")
    outputFile.write(string + "\n" + len(string) * "-")
    print("\nSum of students = %d, Sum of full pay = %d, Sum of season ticket = %d, and Revenues = %d Dollars"%(student, full, season, balance))
    outputFile.write("\nSum of students = %d, Sum of full pay = %d, Sum of season ticket = %d, and Revenues = %d Dollars\n"%(student, full, season, balance))

#This function prints out the category seats in a gridded way by type first the letter, then the symbol refrencing the type of customer setting
#at that seat in a loop, ending up in a gridded top-view of the category in stadium.
def showCategory(name):
    name = name.strip("\n")
    category, innerDimensions = categoryIdentifier(name, "show")
    if category == 0:
        return()
    print("Printing category layout of %s\n"%name)
    outputFile.write("Printing category layout of %s\n\n"%name)
    i = int(innerDimensions[0])
    while i >= 1:
        print(alphabet[i-1], end = " ")
        outputFile.write(alphabet[i-1] + " ")
        for key, value in category.items():                       
                typePrinter(value, key, i)
        print("\n")
        outputFile.write("\n")
        i -= 1
    for j in range(int(innerDimensions[1])):
        print("%3d"%j, end = "")
        outputFile.write("%3d"%j)
    print("\n")
    outputFile.write("\n")

#The typePrinter function is a sub-function for the showCategory function to type the symbol refrencing the type of customer setting at the specified seat
def typePrinter(value, key, i):
    if key[0] == alphabet[i-1]:
        if value == "X":
            symbol = "X"
        else: 
            type = value[1]
            if type == "student":
                symbol = "S"
            elif type == "full":
                symbol = "F"
            elif type == "season":
                symbol = "T"
        print("%-3s"%symbol, end = "")
        outputFile.write("%-3s"%symbol)

#The main code is here, it cycles through a loop reading each line of the file, taking the first command and returning the rest of the argumants to the.
#propper functions, with the create function having a specification to be able to diffrentiate between the states of duplication, normally creating and
#inserting too many rows
for i in inputFile:
    currentLine = i.split(" ")
    if currentLine[0] == "CREATECATEGORY":
        nameCategory = currentLine[1]
        category = (createCategory(currentLine[1], currentLine[2]))
        if nameCategory not in categoryNames and category != False and category != True:
            categoryNames.append(nameCategory)        
            dimensions.append([row, column.strip("\n")])
            categoryInfo.append(category)        
        elif category == True:
            print("Warning: Cannot create the category for the second time. The stadium has already %s."%(nameCategory))
            outputFile.write("Warning: Cannot create the category for the second time. The stadium has already %s.\n"%(nameCategory))
    elif currentLine[0] == "SELLTICKET":
        sellTicket(currentLine[1:])
    elif currentLine[0] == "CANCELTICKET":
        cancelTicket(currentLine[1:])
    elif currentLine[0] == "BALANCE":
        balance(currentLine[1])
    elif currentLine[0] == "SHOWCATEGORY":
        showCategory(currentLine[1])
outputFile.close()
