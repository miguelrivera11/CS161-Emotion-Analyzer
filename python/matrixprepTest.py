# -*- coding: utf-8 -*-
"""
Created on Tue Nov 13 18:53:46 2018

@author: smart
"""

import numpy as np
wordsList = np.load('wordsList.npy')
print('Loaded the word list!')
wordsList = wordsList.tolist() #Originally loaded as numpy array
wordsList = [word.decode('UTF-8') for word in wordsList] #Encode words as UTF-8
wordVectors = np.load('wordVectors.npy')
print ('Loaded the word vectors!')

numTweets = 260 + 291 + 260
maxSeqLength = 35
tweetIndex = 0

ids = np.zeros((numTweets, maxSeqLength), dtype='int32')


angryFile = open("angerTest.txt")
lines = angryFile.readlines()
for line in lines:
    split = line.split()
    wordIndex = 0
    for word in split:
            try:
                ids[tweetIndex][wordIndex] = wordsList.index(word)
            except ValueError:
                ids[tweetIndex][wordIndex] = 399999 #Vector for unkown words
            wordIndex += 1
    tweetIndex += 1
angryFile.close()


happyFile = open("happyTest.txt")
lines = happyFile.readlines()
for line in lines:
    split = line.split()
    wordIndex = 0
    for word in split:
            try:
                ids[tweetIndex][wordIndex] = wordsList.index(word)
            except ValueError:
                ids[tweetIndex][wordIndex] = 399999 #Vector for unkown words
            wordIndex += 1
    tweetIndex += 1
happyFile.close()

sadFile = open("sadTest.txt")
lines = sadFile.readlines()
for line in lines:
    split = line.split()
    wordIndex = 0
    for word in split:
            try:
                ids[tweetIndex][wordIndex] = wordsList.index(word)
            except ValueError:
                ids[tweetIndex][wordIndex] = 399999 #Vector for unkown words
            wordIndex += 1
    tweetIndex += 1
sadFile.close()

for row in ids:
    line = ""
    for column in row:
        line += str(column) + " "
    print(line)

np.save('idsMatrixTest', ids)
