# -*- coding: utf-8 -*-
"""
Created on Tue Nov 13 09:45:36 2018

@author: smart
"""

import re
strip_special_chars = re.compile("[^A-Za-z0-9 ]+")

def cleanSentences(string):
    string = string.lower().replace("<br />", " ")
    return re.sub(strip_special_chars, "", string.lower())



'''
ANGER DATA PREP
'''

file = open("angerCleanTestSource.txt","r")
output = []
lines = file.readlines()
maxWordCount = 0


for line in lines:
    split = line.split();
    wordCount = len(split)
    count = 0
    for word in split:
        if (count == wordCount-1):
            anger = float(word)
            if (anger > 0.58):
                index = 1
                clean = ""
                while (index != wordCount - 2):
                    currentWord = split[index]
                    if '@' not in currentWord:
                        clean += currentWord + " "
                    index += 1
                clean = cleanSentences(clean)
                output.append(clean)
                if(wordCount-3 > maxWordCount):
                    maxWordCount = wordCount -3
        count += 1
        
file.close()
print("# of tweets", len(output))
outputFile = open("angerTest.txt", "w+")
for tweet in output:
    outputFile.write(tweet + "\n")
print (maxWordCount)
outputFile.close()

'''
HAPPY DATA PREP
'''

file = open("happyCleanTestSource.txt","r")
output = []

lines = file.readlines()
maxWordCount = 0


for line in lines:
    split = line.split();
    wordCount = len(split)
    count = 0
    for word in split:
        if (count == wordCount-1):
            happy = float(word)
            if (happy > 0.58):
                index = 1
                clean = ""
                while (index != wordCount - 2):
                    currentWord = split[index]
                    if '@' not in currentWord:
                        clean += currentWord + " "
                    index += 1
                clean = cleanSentences(clean)
                output.append(clean)
                if(wordCount-3 > maxWordCount):
                    maxWordCount = wordCount -3
        count += 1
file.close()
print("# of tweets", len(output))
outputFile = open("happyTest.txt", "w+")
for tweet in output:
    outputFile.write(tweet + "\n")
print (maxWordCount)
outputFile.close()

'''
SAD DATA PREP
'''
file = open("sadCleanTestSource.txt","r")
output = []

lines = file.readlines()
maxWordCount = 0


for line in lines:
    split = line.split();
    wordCount = len(split)
    count = 0
    for word in split:
        if (count == wordCount-1):
            sad = float(word)
            if (sad > 0.58):
                index = 1
                clean = ""
                while (index != wordCount - 2):
                    currentWord = split[index]
                    if '@' not in currentWord:
                        clean += currentWord + " "
                    index += 1
                clean = cleanSentences(clean)
                output.append(clean)
                if(wordCount-3 > maxWordCount):
                    maxWordCount = wordCount -3
        count += 1
file.close()
print("# of tweets", len(output))
outputFile = open("sadTest.txt", "w+")
for tweet in output:
    outputFile.write(tweet + "\n")
print (maxWordCount)
outputFile.close()
        
