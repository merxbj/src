#include <iostream>
#include <fstream>
#include "parser.h"

using namespace std;

void printXml(Node* root, int level);

int main(int argc, char *argv[])
{
	ifstream xml;
	xml.open(argv[1]);
	Element* root = parseXml(xml);

	printXml(root, 0);

	xml.close();
	return 0;
}

void printXml(Node* root, int level)
{
	for (int i = 0; i < level; i++)
	{
		cout << "\t";
	}

	cout << root->toString() << endl;
	for (list<Node*>::iterator it = root->children.begin(); it != root->children.end(); it++)
	{
		printXml((*it), level++);
	}
}
