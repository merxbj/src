#include <iostream>

static const short FURLONGS_TO_YARDS = 220;

void threeBlindMice();
void seeHowTheyRun();

void main()
{
    threeBlindMice();
    threeBlindMice();
    seeHowTheyRun();
    seeHowTheyRun();
}

void threeBlindMice()
{
    std::cout << "Three blind mice" << std::endl;
}

void seeHowTheyRun()
{
    std::cout << "See how they run" << std::endl;
}