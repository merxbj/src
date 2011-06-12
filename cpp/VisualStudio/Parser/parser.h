#ifndef _PARSER_H_
#define _PARSER_H_

#include <list>
#include <fstream>
#include <vector>

using namespace std;

class Node
{
public:
	list<class Node*> children;
	Node* parent;
	virtual ~Node()
	{
		for (list<Node*>::iterator it = children.begin(); it != children.end(); it++)
		{
			delete *it;
		}
	}
	virtual const char* toString() const
	{
		return "Plain Node";
	}
};

class Element : public Node
{
public:
	char* name;
	virtual ~Element()
	{
		delete name;
	}

	virtual const char* toString() const
	{
		return name;
	}
};

class StringData : public Node
{
public:
	char* data;
	virtual ~StringData()
	{
		delete data;
	}

	virtual const char* toString() const
	{
		return data;
	}
};

enum XmlTokenType
{
	UNKNOWN,
	STARTING_TAG,
	ENDING_TAG,
	DATA
};

class XmlToken
{
public:
	char* name;
	XmlTokenType type;
	XmlToken(XmlTokenType _type) : type(_type) {}
	~XmlToken() 
	{
		delete name;
	}
};

Element* parseXml(ifstream& xml);
Element* parseElement(vector<XmlToken*>& tokens);
void parseTokens(vector<XmlToken*>* tokens, ifstream& xml);

#endif