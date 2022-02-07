// XsdComparator.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include "XsdComparator.h"

bool FileExists(std::string fileName) {
    struct stat buffer;
    return (stat(fileName.c_str(), &buffer) == 0);
}

int main(int argc, char* argv[])
{
    if (argc < 3)
    {
        std::cout << "You must provide two xml files";
        std::exit(1);
    }

    if (FileExists(argv[1]) && FileExists(argv[2]))
    {
        std::cout << argv[1] << " " << argv[2] << std::endl;
        Reporter reporter;
        std::unique_ptr<XsdComparator> comparator = std::make_unique<XsdComparator>(reporter);
        comparator->Compare(argv[1], argv[2]);
        reporter.Export("d:\\_temp\\test\\report.txt");
    }
    else
    {
        std::cout << "One of the files doesnt exists";
    }
}

