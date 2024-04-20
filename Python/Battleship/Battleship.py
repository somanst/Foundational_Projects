#We start off by importing the libraries we are going to need and defining dicts and characterlist containing all english letters that we are going to use
#through out the code and create the main outputFile
import string
import sys
characterList = list(string.ascii_uppercase)
p1Dict = {}
p2Dict = {}
oF = open("Battleship.out", "w")

#Then we take arguments from the cmd and enter them into this function to return us a list splitted by the needed spliter depending on the file format, and
#if there is an error we return a string with the inserted argument and check if any string was returned we print a string calling out the file
#called wasnt found

def fileListReturner(file, splitter):
    try:
        input = open(file)
    except IOError:
        return file
    inputList = input.read().split(splitter)
    input.close()
    return inputList

iostring = ""
try:
    p1InputList = fileListReturner(sys.argv[1], "\n")
    p2InputList = fileListReturner(sys.argv[2], "\n")
    p1Guess = fileListReturner(sys.argv[3], ";")
    p2Guess = fileListReturner(sys.argv[4], ";")
except:
    print("Less arguments were entered than needed")
    oF.write("Less arguments were entered than needed")
    oF.close()
    exit()

if isinstance(p1InputList, str) == True:
   iostring += p1InputList + ", "
if isinstance(p2InputList, str) == True:
   iostring += p2InputList + ", "
if isinstance(p1Guess, str) == True:
   iostring += p1Guess + ", "
if isinstance(p2Guess, str) == True:
    iostring += p2Guess + ", "

if iostring != "":    
    iostring = iostring[:-2]
    print("IOError: inputfile(s) %s is/are not reachable."%iostring)
    oF.write("IOError: inputfile(s) %s is/are not reachable."%iostring)
    oF.close()
    exit()
    
#This function creates the main boards of each player after the inputs of ships are places in the txt files, after we split the file with the previous function
#we split it once more with the splitter ; to take each column's input and add it to the grid of the player with an index on the matrix according to its 
#location, asserting that there are 10 columns and no random inputs were given, and returning both player's grid and a grid we compare it with and using it
#for the rest of the program

def dictCreate(dictName, characterList, inputList):
    gridList = []    
    placementCout = 0
    for i in range(10):    
        formattedList = inputList[i].split(";")
        assert len(formattedList) == 10
        charIndex = -1
        for j in characterList[:10]:     
            charIndex += 1
            keyName = j + str(i+1)            
            if formattedList[charIndex] == "":
                dictName[keyName] = "-"
            elif formattedList[charIndex] == "C":
                dictName[keyName] = "C"
                placementCout += 1
            elif formattedList[charIndex] == "B":
                dictName[keyName] = "B"
                placementCout += 1
            elif formattedList[charIndex] == "P":
                dictName[keyName] = "P"
                placementCout += 1
            elif formattedList[charIndex] == "D":
                dictName[keyName] = "D"
                placementCout += 1
            elif formattedList[charIndex] == "S":
                dictName[keyName] = "S"
                placementCout += 1
            else:
                raise AssertionError
            gridList.append(keyName)
    assert placementCout == 27
    return dictName, gridList

try:
    p1Dict, gridList = dictCreate(p1Dict, characterList, p1InputList)
    p2Dict, gridList = dictCreate(p2Dict, characterList, p2InputList)
except:
    oF.write("kaBOOM: run for your life!\n")
    print("kaBOOM: run for your life!")

#After that comes defining the ships according to players' grids and putting them in a dict with the ship type and its location range, it calculates how long the ship
#is and its location based on the first occurance of a letter and the length of the ship that belongs to that letter, after looping through out the whole
#grid dthe function returns the player's shipdict and asserts that ships' lengths are right as well as that ship types' number is correct

def shipDefiner(dict, gridList):
    shipDict = {}
    tempDict = {}
    tempDict = dict
    shipCout = 0
    subCout = 0
    for key, value in tempDict.items():        
        indexIdentifier = gridList.index(key)
        if value == "-":
            continue
        if tempDict[gridList[indexIdentifier + 1]] == value:
            if value == "P":
                string = "Patrol" + key
                shipDict[string] = key + "-" + gridList[indexIdentifier + 1]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 1]] == "P"
                tempDict[gridList[indexIdentifier + 1]] = "-"
            elif value == "S":
                string = "Submarine" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 2]
                shipCout += 1
                subCout += 1
                assert  tempDict[gridList[indexIdentifier + 1]] == "S" and  tempDict[gridList[indexIdentifier + 2]] == "S"
                tempDict[gridList[indexIdentifier + 1]] = "-"
                tempDict[gridList[indexIdentifier + 2]] = "-"
            elif value == "D":
                string = "Destroyer" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 2]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 1]] == "D" and  tempDict[gridList[indexIdentifier + 2]] == "D"
                tempDict[gridList[indexIdentifier + 1]] = "-"
                tempDict[gridList[indexIdentifier + 2]] = "-"
            elif value == "B":
                string = "Battleship" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 3]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 1]] == "B" and  tempDict[gridList[indexIdentifier + 2]] == "B" and tempDict[gridList[indexIdentifier + 3]] == "B"
                tempDict[gridList[indexIdentifier + 1]] = "-"
                tempDict[gridList[indexIdentifier + 2]] = "-"
                tempDict[gridList[indexIdentifier + 3]] = "-"
            elif value == "C":
                string = "Carrier" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 4]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 1]] == "C" and  tempDict[gridList[indexIdentifier + 2]] == "C"
                assert  tempDict[gridList[indexIdentifier + 3]] == "C" and  tempDict[gridList[indexIdentifier + 4]] == "C"
                tempDict[gridList[indexIdentifier + 1]] = "-"
                tempDict[gridList[indexIdentifier + 2]] = "-"
                tempDict[gridList[indexIdentifier + 3]] = "-"
                tempDict[gridList[indexIdentifier + 4]] = "-"
        else:
            if value == "P":
                string = "Patrol" + key
                shipDict[string] = key + "-" + gridList[indexIdentifier + 10]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 10]] == "P"
                tempDict[gridList[indexIdentifier + 10]] = "-"
            elif value == "S":
                string = "Submarine" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 20]
                shipCout += 1
                subCout += 1
                assert  tempDict[gridList[indexIdentifier + 10]] == "S" and  tempDict[gridList[indexIdentifier + 20]] == "S"
                tempDict[gridList[indexIdentifier + 10]] = "-"
                tempDict[gridList[indexIdentifier + 20]] = "-"
            elif value == "D":
                string = "Destroyer" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 20]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 10]] == "D" and  tempDict[gridList[indexIdentifier + 20]] == "D"
                tempDict[gridList[indexIdentifier + 10]] = "-"
                tempDict[gridList[indexIdentifier + 20]] = "-"
            elif value == "B":
                string = "Battleship" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 30]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 10]] == "B" and  tempDict[gridList[indexIdentifier + 20]] == "B" and tempDict[gridList[indexIdentifier + 30]] == "B"
                tempDict[gridList[indexIdentifier + 10]] = "-"
                tempDict[gridList[indexIdentifier + 20]] = "-"
                tempDict[gridList[indexIdentifier + 30]] = "-"
            elif value == "C":
                string = "Carrier" + key
                shipDict[string] = key + "-" + gridList[gridList.index(key) + 40]
                shipCout += 1
                assert  tempDict[gridList[indexIdentifier + 10]] == "C" and  tempDict[gridList[indexIdentifier + 20]] == "C"
                assert  tempDict[gridList[indexIdentifier + 30]] == "C" and  tempDict[gridList[indexIdentifier + 40]] == "C"
                tempDict[gridList[indexIdentifier + 10]] = "-"
                tempDict[gridList[indexIdentifier + 20]] = "-"
                tempDict[gridList[indexIdentifier + 30]] = "-"
                tempDict[gridList[indexIdentifier + 40]] = "-"
    assert shipCout == 9
    assert subCout == 1
    return shipDict

#This is a small function that creates the visible grids for the players
def hiddenTablesCreate(gridList):
    table = {}
    for i in gridList:
        table[i] = "-"
    return table

try:
    p1Ships = shipDefiner(p1Dict, gridList)
    p2Ships = shipDefiner(p2Dict, gridList)
except:    
    oF.write("kaBOOM: run for your life!\n")
    print("kaBOOM: run for your life!")



p1Table = hiddenTablesCreate(gridList)
p2Table = hiddenTablesCreate(gridList)

p1Dict, gridList = dictCreate(p1Dict, characterList, p1InputList)
p2Dict, gridList = dictCreate(p2Dict, characterList, p2InputList)

#last input is empty so we remove it

p1Guess.remove(p1Guess[-1])
p2Guess.remove(p2Guess[-1])

#for game checking we first check players' ships locations and take the range ships are into account, then compare them with the visible tables being shot at that
#location. which += 1 the shot count, if shot count meets the length of the ship type taken from the ships dict the function will return that a ship of that
#type was destroyed (horizontal and vertical ships are taken into account in different ways which is why we use if twice)
def gameoverChecker(p1Ships, p1Table, characterList, gridList):
    patrolsDead = 0
    submarinesDead = 0
    destroyersDead = 0
    battleshipsDead = 0
    carriersDead = 0
    for key, value in p1Ships.items():
        checkingRange = value.split("-")
        if gridList.index(checkingRange[1]) - gridList.index(checkingRange[0]) < 10:            
            shot = 0
            fromm = gridList.index(checkingRange[0])
            to = gridList.index(checkingRange[1])
            while fromm <= to:
                if p1Table[gridList[fromm]] == "X":
                    shot += 1
                fromm += 1            
            if "Patrol" in key and shot == 2:
                patrolsDead += 1
            elif "Submarine" in key and shot == 3:
                submarinesDead += 1
            elif "Destroyer" in key and shot == 3:
                destroyersDead += 1
            elif "Battleship" in key and shot == 4:
                battleshipsDead += 1
            elif "Carrier" in key and shot == 5:
                carriersDead += 1
        else:
            shot = 0
            for i in range(int(checkingRange[0][1:]), int(checkingRange[1][1:]) + 1):      
                checkingKey = value[0] + str(i)
                if p1Table[checkingKey] == "X":
                    shot += 1
            if "Patrol" in key and shot == 2:
                patrolsDead += 1
            elif "Submarine" in key and shot == 3:
                submarinesDead += 1
            elif "Destroyer" in key and shot == 3:
                destroyersDead += 1
            elif "Battleship" in key and shot == 4:
                battleshipsDead += 1
            elif "Carrier" in key and shot == 5:
                carriersDead += 1
                
    return patrolsDead, submarinesDead, destroyersDead, battleshipsDead, carriersDead

#This function has 2 ways of using, it can check if a game is over and print tables into the terminal and the oF, it first equalizes the number of destryoed 
#ships with the returned values of the function gameoverChecker() then sees if it was called to check or print, with taking care of spaces and tabs...

def tablePrinter(characterList,gridList,table, table2, caller):
    p1Win = False
    p2Win = False
    gameOver = False
    pat2, sub2, dest2, batt2, car2 = gameoverChecker(p2Ships, p2Table, characterList, gridList)
    pat1, sub1, dest1, batt1, car1 = gameoverChecker(p1Ships, p1Table, characterList, gridList)

    if caller == "checker":
        if pat1 == 4 and sub1 == 1 and dest1 == 1 and batt1 == 2 and car1 == 1:
            gameOver = True  
            p2Win = True
            if pat2 == 4 and sub2 == 1 and dest2 == 1 and batt2 == 2 and car2 == 1:
                p1Win = True
        elif pat2 == 4 and sub2 == 1 and dest2 == 1 and batt2 == 2 and car2 == 1:
            gameOver = True
            p1Win = True
        return gameOver, p1Win, p2Win

    if caller == "printer":
        oF.write("%s\t\t%s"%("Player1's Hidden Board", "Player2's Hidden Board\n  "))
        print("%s\t\t%s"%("Player1's Hidden Board", "Player2's Hidden Board\n  "), end = "")
    elif caller == "printer2":
        oF.write("%s\t\t\t\t%s"%("Player1's Board", "Player2's Board\n  "))
        print("%s\t\t\t%s"%("Player1's Board", "Player2's Board\n  "), end = "")

    for i in characterList[:characterList.index(gridList[-1][0]) + 1]:
        oF.write("%-2s"%i)
        print("%-2s"%i, end = "")
    oF.write("\t\t  ")
    print("\t\t  ", end = "")

    for i in characterList[:characterList.index(gridList[-1][0]) + 1]:
        if i != "J":
            oF.write("%-2s"%i)
            print("%-2s"%i, end = "")
        else:
            oF.write(("%-2s"%i)[:-1])
            print(("%-2s"%i)[:-1], end = "")
    print()
    oF.write("\n")

    for i in range(1, int(gridList[-1][1:]) + 1):
        oF.write("%-2d"%i)
        print("%-2d"%i, end = "")

        for j in characterList[:characterList.index(gridList[-1][0]) + 1]:
            piece = j + str(i)
            oF.write("%-2s"%table[piece])
            print("%-2s"%table[piece], end = "") 
        oF.write("\t\t%-2d"%i)
        print("\t\t%-2d"%i, end = "")

        for j in characterList[:characterList.index(gridList[-1][0]) + 1]:
            piece = j + str(i)
            if j != "J":
                oF.write("%-2s"%table2[piece])
                print("%-2s"%table2[piece], end = "")  
            else:
                oF.write(("%-2s"%table2[piece])[:-1])
                print(("%-2s"%table2[piece])[:-1], end = "")  
        oF.write("\n")
        print()   

    print("\nCarrier\t\t%s\t\tCarrier\t\t%s"%(("X "*car1 + "- "*(1-car1))[:-1], ("X "*car2 + "- "*(1-car2))[:-1]), end = "")
    print("\nBattleship\t%s\t\tBattleship\t%s"%(("X "*batt1 + "- "*(2-batt1))[:-1], ("X "*batt2 + "- "*(2-batt2))[:-1]), end = "")
    print("\nDestroyer\t%s\t\tDestroyer\t%s"%(("X "*dest1 + "- "*(1-dest1))[:-1], ("X "*dest2 + "- "*(1-dest2))[:-1]), end = "")
    print("\nSubmarine\t%s\t\tSubmarine\t%s"%((("X "*sub1 + "- "*(1-sub1)))[:-1], ("X "*sub2 + "- "*(1-sub2))[:-1]), end = "")
    print("\nPatrol Boat\t%s\t\tPatrol Boat\t%s"%(("X "*pat1 + "- "*(4-pat1))[:-1], ("X "*pat2 + "- "*(4-pat2))[:-1]))
    oF.write("\nCarrier\t\t%s\t\t\t\tCarrier\t\t%s"%((("X "*car1 + "- "*(1-car1))[:-1], ("X "*car2 + "- "*(1-car2))[:-1])))
    oF.write("\nBattleship\t%s\t\t\t\tBattleship\t%s"%(("X "*batt1 + "- "*(2-batt1))[:-1], ("X "*batt2 + "- "*(2-batt2))[:-1]))
    oF.write("\nDestroyer\t%s\t\t\t\tDestroyer\t%s"%(("X "*dest1 + "- "*(1-dest1))[:-1], ("X "*dest2 + "- "*(1-dest2))[:-1]))
    oF.write("\nSubmarine\t%s\t\t\t\tSubmarine\t%s"%(("X "*sub1 + "- "*(1-sub1))[:-1], ("X "*sub2 + "- "*(1-sub2))[:-1]))
    oF.write("\nPatrol Boat\t%s\t\t\tPatrol Boat\t%s\n"%(("X "*pat1 + "- "*(4-pat1))[:-1], ("X "*pat2 + "- "*(4-pat2))[:-1]))
    if caller != "printer2":
        oF.write("\n")
    

#Then comes the function the checks if there are any errors in players' outputs are available and returns the appropriate message of it
#for the user and if an error is available or not with the keyname of the move that was played.
def errorCheck(moveIndex, characterList, gridList, guessList):
    if moveIndex == len(guessList):
        return "needGuess", "NG", False
    try:
        sit = 0
        errorAv = False
        errorString = "\n"
        move = guessList[moveIndex].split(",")     
        y = move[0]
        x = move[1]        
        if x == "" or y == "":
            sit = 1
            raise IndexError
        str(int(y))
        keyName = x + y
        if len(move) > 2 or x not in characterList:
            raise ValueError             
        assert keyName in gridList        
    except ValueError:
        errorString = "ValueError: Couldnt process the value entered into the grid"
        errorAv = True
        
    except IndexError:
        if sit == 0:
            errorString = "IndexError: Entered less values than needed"
        elif sit == 1:
            errorString = "IndexError: Entered an empty value"
        errorAv = True
    except AssertionError:
        errorString = "AssertionError: Invalid Operation."
        errorAv = True
    except:        
        errorString = "kaBOOM: run for your life!"
        errorAv = True
    else:        
        return errorString, keyName, errorAv
    return errorString, "", errorAv
    

round = 0    
moveIndex1 = 0
moveIndex2 = 0
p1Win = False
p2Win = False
gameOver = False
p1Error = False
p2Error = False

oF.write("Battle of Ships Game\n")
print("Battle of Ships Game")

#This is the main block of the program, it keeps repeating as long as the game isnt over, prints tables based on move index for each player, prints an error 
#message if there is any, and modifies both the original and visible dicts of players
while gameOver == False:   
    round += 1
    if moveIndex1 == len(p1Guess) or moveIndex2 == len(p2Guess):        
        break
    
    errorString1, keyName1, errorAv1 = errorCheck(moveIndex1, characterList, gridList, p1Guess)
    print("\nPlayer1's Move\n\nRound : %d\t\t\t\t\tGrid Size: %dx%d\n\n"%(round, characterList.index(gridList[-1][0]) + 1, int(gridList[-1][1:])), end = "")
    oF.write("\nPlayer1's Move\n\nRound : %d\t\t\t\t\tGrid Size: %dx%d\n\n"%(round, characterList.index(gridList[-1][0]) + 1, int(gridList[-1][1:])))
    tablePrinter(characterList, gridList, p1Table, p2Table, "printer")
    oF.write("Enter your move: %s\n"%p1Guess[moveIndex1])
    print("\nEnter your move: %s"%p1Guess[moveIndex1])

    while errorAv1 == True:
        oF.write(errorString1)
        print(errorString1, end = "")
        moveIndex1 += 1
        if moveIndex1 < len(p1Guess):
            moveIn = p1Guess[moveIndex1]
        else:
            moveIn = ""
        oF.write("\nEnter your move: %s\n"%moveIn)
        print("\nEnter your move: %s\n"%moveIn)
        errorString1, keyName1, errorAv1 = errorCheck(moveIndex1, characterList, gridList, p1Guess)
        

    if keyName1 == "NG":
        break
    
    if p2Dict[keyName1] != "-":
        p2Table[keyName1] = "X"
        p2Dict[keyName1] = "X"
    else:
        p2Table[keyName1] = "O"
        p2Dict[keyName1] = "O"
    moveIndex1 += 1

    errorString2, keyName2, errorAv2 = errorCheck(moveIndex2, characterList, gridList, p2Guess)
    print("\nPlayer2's Move\n\nRound : %d\t\t\t\t\tGrid Size: %dx%d\n\n"%(round, characterList.index(gridList[-1][0]) + 1, int(gridList[-1][1:])), end = "")
    oF.write("\nPlayer2's Move\n\nRound : %d\t\t\t\t\tGrid Size: %dx%d\n\n"%(round, characterList.index(gridList[-1][0]) + 1, int(gridList[-1][1:])))
    tablePrinter(characterList, gridList, p1Table, p2Table, "printer")
    oF.write("Enter your move: %s\n"%p2Guess[moveIndex2])
    print("\nEnter your move: %s"%p2Guess[moveIndex2])

    while errorAv2 == True:
        oF.write(errorString2)
        print(errorString2, end = "")
        moveIndex2 += 1
        if moveIndex2 <= len(p1Guess) - 1:
            moveIn = p2Guess[moveIndex2]
        else:
            moveIn = ""
        oF.write("\nEnter your move: %s\n"%moveIn)
        print("\nEnter your move: %s"%moveIn)
        errorString2, keyName2, errorAv2 = errorCheck(moveIndex2, characterList, gridList, p2Guess)

    if keyName2 == "NG":
        break

    if p1Dict[keyName2] != "-":
        p1Table[keyName2] = "X"
        p1Dict[keyName2] = "X"
    else:
        p1Table[keyName2] = "O"
        p1Dict[keyName2] = "O"
    moveIndex2 += 1
    
    gameOver, p1Win, p2Win = tablePrinter(characterList, gridList, p1Table, p2Table, "checker")
    
    
#Here comes winning conditions, if the game was won by one or both of the players it prints the following strings with the table of final information at the end
if gameOver == True and p1Win == True and p2Win == False:
    oF.write("\nPlayer1 Wins!\n\n")
    print("\nPlayer1 Wins!\n")
    p1Table = p1Dict
elif gameOver == True and p2Win == True and p1Win == False:
    oF.write("\nPlayer2 Wins!\n\n")
    print("\nPlayer2 Wins!\n")
    p2Table = p2Dict
elif gameOver == True and p2Win == True and p1Win == True:
    print('\nIt is a Draw!\n')
    oF.write("\nIt is a Draw!\n\n")
if gameOver == True:
    oF.write("Final Information\n\n")
    print("Final Information\n")
    tablePrinter(characterList,gridList,p1Table, p2Table, "printer2")

#Name: Adam Sattout
#ID: b2220765061

oF.close()

