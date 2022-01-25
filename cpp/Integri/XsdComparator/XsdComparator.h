#pragma once
#include <string>
#include "Reporter.h"
#include <map>
#import <msxml6.dll> raw_interfaces_only

class XsdComparator
{
public:
	XsdComparator(Reporter* reporter);
	void Compare(const std::string file1, const std::string file2);
	void Compare(MSXML2::IXMLDOMElement* firstRoot, MSXML2::IXMLDOMElement* secondRoot);
private:
	Reporter* reporter;

	void ReadXml(const std::string file);
	std::string GetElementName(MSXML2::IXMLDOMNode* node);
	void GetRootElement(const std::string file, MSXML2::IXMLDOMElement** root);
	std::string GetValueOfNameAttribute(MSXML2::IXMLDOMNode* node);
	void CompareChildren(MSXML2::IXMLDOMNodeList* firstChildren, MSXML2::IXMLDOMNodeList* secondChildren, int depth);
	std::map<std::string, MSXML2::IXMLDOMNode*> GetNodesMap(MSXML2::IXMLDOMNodeList* nodes);
};
