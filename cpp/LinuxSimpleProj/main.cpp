#include "class.h"

int main()
{
	A a;
    B* b = new B();
    b->touch();
    delete b;
    return 0;
}
