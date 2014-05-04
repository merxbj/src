#include <iostream>

static const short FURLONGS_TO_YARDS = 220;

void main()
{
    std::cout << "Enter furlongs: ";
    double furlongs;
    std::cin >> furlongs;

    std::cout << std::endl << "Entered " << furlongs << " furlongs, which is ";

    double yards = furlongs * FURLONGS_TO_YARDS;

    std::cout << yards << " yards." << std::endl;
}