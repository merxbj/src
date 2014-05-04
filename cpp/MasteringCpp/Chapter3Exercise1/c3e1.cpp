#include <iostream>

static const int FOOT_TO_INCH = 12;

void main()
{
    std::cout << "Enter a distance in inches: ____\b\b\b\b";
    
    int inches = 0;
    std::cin >> inches;

    std::cout << std::endl << inches << " inches is " << inches/FOOT_TO_INCH << " feet and " << inches % FOOT_TO_INCH << " inches" << std::endl;
}