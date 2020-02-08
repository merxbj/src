#include <iostream>
#include <list>
using namespace std;

typedef std::list<std::string> StringList;
int split(const std::string str, const std::string delims, StringList* out);

int main()
{
	string str = "";
	StringList splits;
	int count = split(str, ",", &splits);
	
	for (StringList::iterator it = splits.begin(); it != splits.end(); it++)
		cout << (*it).c_str() << endl;

	system("PAUSE");
	return 0;
}

int split(const std::string str, const std::string delims, StringList* splits)
{
	int count = 0;
	int pos = 0;
	int last = 0;
	
	do
	{
		last = str.find_first_of(delims, pos);
		if (last != std::string::npos)
		{
			splits->push_back(str.substr(pos, last - pos));
			pos = last + 1;
			count++;
		}
	} while (last != std::string::npos);

	if (count == 0)
	{
		splits->push_back(str);
		count++;
	}
	else if (pos < str.size())
	{
		splits->push_back(str.substr(pos, str.size() - pos));
		count++;
	}
	
	return count;
}