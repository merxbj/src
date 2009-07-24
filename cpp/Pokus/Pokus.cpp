#include <iostream>
using namespace std;

bool test();

int main()
{
	if (test())
		cout << "Ano" << endl;
	else
		cout << "Ne" << endl;

	system("PAUSE");
	return 0;
}

bool test()
{
	return false || (false && true); // should return true
}