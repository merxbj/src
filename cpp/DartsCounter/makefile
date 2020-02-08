# Program DartsCounter
# Program se snazi dojit co nejefektivnejsim zpusobem k co nejmensimu poctu 
# hodu.
#

DartsCounter: main.o darts.o
	g++ -g -Wall -o $@ main.o darts.o

main.o: main.cpp
darts.o: darts.cpp darts.h
clean:
	rm -rf *o DartsCounter
