#ifndef _class_h_
#define _class_h_

#include <iostream>
using namespace std;

class A
{
 public:
    A() { cout << "Entered main!" << endl; }
    ~A() { cout << "Exiting main!" << endl; }
};

class B
{
 public:
    void touch();
};

#endif
