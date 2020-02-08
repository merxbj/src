#include "parser.h"

using namespace std;

int level = 0;

Element* parseXml(ifstream& xml)
{
	vector<XmlToken*> tokens;

	parseTokens(&tokens, xml);

	Element* root = parseElement(tokens);

	return root;
}

void parseTokens(vector<XmlToken*>* tokens, ifstream& xml)
{
	char buffer[2048];
	int buffPos = 0;
	XmlTokenType currentTokenType = XmlTokenType::UNKNOWN;
	char ch;
	while (!xml.eof())
	{
		xml >> ch;
		switch (ch)
		{
		case '<':
			if (xml.peek() != '/')
			{
				buffPos = 0;
				buffer[0] = '\0';
				currentTokenType = STARTING_TAG;
			}
			else
			{
				if (currentTokenType != XmlTokenType::UNKNOWN)
				{
					XmlToken* token = new XmlToken(currentTokenType);
					token->name = new char[buffPos++];
					strncpy(token->name, buffer, buffPos);
					token->name[buffPos] = '\0';
					tokens->push_back(token);
				}
				buffPos = 0;
				buffer[0] = '\0';
				currentTokenType = XmlTokenType::ENDING_TAG;
			}
			break;
		case '>':
			{
				if (currentTokenType != XmlTokenType::UNKNOWN)
				{
					XmlToken* token = new XmlToken(currentTokenType);
					token->name = new char[buffPos++];
					strncpy(token->name, buffer, buffPos);
					token->name[buffPos] = '\0';
					tokens->push_back(token);
				}
				buffPos = 0;
				buffer[0] = '\0';
				if (currentTokenType == XmlTokenType::STARTING_TAG) 
				{
					currentTokenType = XmlTokenType::DATA;
				} 
				else
				{
					currentTokenType = XmlTokenType::UNKNOWN;
				}
			}
			break;
		case '/':
			break;
		case ' ':
		case '\t':
			if (currentTokenType != XmlTokenType::DATA)
			{
				throw 5; // unexpected token
			}
			else
			{
				buffer[buffPos++] = ch;
			}
		default:
			buffer[buffPos++] = ch;
			break;
		}
	}
}

Element* parseElement(vector<XmlToken*>& tokens)
{
	Element* currentElement = NULL;
	for (vector<XmlToken*>::iterator it = tokens.begin(); it != tokens.end(); it++)
	{
		switch ((*it)->type)
		{
		case STARTING_TAG:
			{
				if (currentElement == NULL)
				{
					currentElement = new Element();
					currentElement->parent = NULL;
				}
				else
				{
					Element* tmp = new Element();
					tmp->parent = currentElement;
					currentElement->children.push_back(tmp);
					currentElement = tmp;
				}
				int len = strlen((*it)->name);
				currentElement->name = new char[len + 1];
				strncpy(currentElement->name, (*it)->name, len);
				currentElement->name[len] = '\0';
			}
			break;
		case ENDING_TAG:
			if (currentElement->parent != NULL)
			{
				currentElement = static_cast<Element*>(currentElement->parent);
			}
			break;
		case DATA:
			StringData* tmp = new StringData();
			tmp->parent = currentElement;
			currentElement->children.push_back(tmp);
			break;
		}
	}
	return currentElement;
}