#include "XsdComparator.h"
#include <iostream>
#include <stdio.h>
#include <tchar.h>
#include <windows.h>
#include <atlbase.h>
#include <comutil.h>

using namespace std;

XsdComparator::XsdComparator(Reporter* reporter)
{
	this->reporter = reporter;
}

void XsdComparator::Compare(const std::string file1, const std::string file2)
{
	try
	{
		MSXML2::IXMLDOMElement* firstRoot;
		MSXML2::IXMLDOMElement* secondRoot;

		GetRootElement(file1, &firstRoot);
		GetRootElement(file2, &secondRoot);

		Compare(firstRoot, secondRoot);
	}
	catch (const std::exception& e)
	{
		printf(e.what());
	}
}

void XsdComparator::Compare(MSXML2::IXMLDOMElement* firstRoot, MSXML2::IXMLDOMElement* secondRoot)
{
	std::string firstRootElementName = GetElementName((MSXML2::IXMLDOMNode*)firstRoot);
	std::string secondRootElementName = GetElementName((MSXML2::IXMLDOMNode*)secondRoot);

	std::string firstRootName = GetValueOfNameAttribute((MSXML2::IXMLDOMNode*)firstRoot);
	std::string secondRootName = GetValueOfNameAttribute((MSXML2::IXMLDOMNode*)secondRoot);

	if (firstRootName.compare(secondRootName) == 0)
	{
		reporter->Add(None, 0, firstRootElementName, firstRootName);

		MSXML2::IXMLDOMNodeList* firstChildren;
		MSXML2::IXMLDOMNodeList* secondChildren;

		firstRoot->get_childNodes(&firstChildren);
		secondRoot->get_childNodes(&secondChildren);

		CompareChildren(firstChildren, secondChildren, 1);

		firstChildren->Release();
		secondChildren->Release();
	}
	else
	{
		MessageBox(0, _T("Root element names does not match"), _T("Error"), MB_OK);
	}
}

std::map<std::string, MSXML2::IXMLDOMNode*> XsdComparator::GetNodesMap(MSXML2::IXMLDOMNodeList* nodes)
{
	std::map<std::string, MSXML2::IXMLDOMNode*> map;
	MSXML2::IXMLDOMNode* node;

	long nodesLength;
	nodes->get_length(&nodesLength);

	for (int i = 0; i < nodesLength; i++)
	{
		nodes->get_item(i, &node);
		map.insert(pair<std::string, MSXML2::IXMLDOMNode*>(GetValueOfNameAttribute(node), node));

	}

	return map;
}

void XsdComparator::CompareChildren(MSXML2::IXMLDOMNodeList* firstChildren, MSXML2::IXMLDOMNodeList* secondChildren, int depth)
{
	auto firstMap = GetNodesMap(firstChildren);
	auto secondMap = GetNodesMap(secondChildren);

	for (auto it2 = secondMap.begin(); it2 != secondMap.end(); ++it2)
	{
		if (!firstMap.contains(it2->first))
		{
			reporter->Add(Added, depth, GetElementName(it2->second), it2->first);
		}
	}

	for (auto it1 = firstMap.begin(); it1 != firstMap.end(); ++it1)
	{
		if (secondMap.contains(it1->first))
		{
			MSXML2::IXMLDOMNodeList* firstNodes;
			MSXML2::IXMLDOMNodeList* secondNodes;

			it1->second->get_childNodes(&firstNodes);
			secondMap.find(it1->first)->second->get_childNodes(&secondNodes);

			reporter->Add(None, depth, GetElementName(it1->second), it1->first);
			CompareChildren(firstNodes, secondNodes, depth + 1);
		}
		else
		{
			reporter->Add(Removed, depth, GetElementName(it1->second), it1->first);
		}
	}
}

void XsdComparator::GetRootElement(const std::string file, MSXML2::IXMLDOMElement** root)
{
	HRESULT hr = CoInitialize(NULL);
	if (SUCCEEDED(hr))
	{
		MSXML2::IXMLDOMDocument2Ptr xmlDoc;
		hr = xmlDoc.CreateInstance(__uuidof(MSXML2::DOMDocument60), NULL, CLSCTX_INPROC_SERVER);

		VARIANT_BOOL success = VARIANT_FALSE;
		xmlDoc->put_resolveExternals(VARIANT_FALSE);
		VARIANT x;
		x.vt = VT_BSTR;
		x.bstrVal = CComBSTR(file.c_str()).Detach();
		hr = xmlDoc->load(x, &success);


		MSXML2::IXMLDOMParseError* parseError;
		xmlDoc->get_parseError(&parseError);

		if (SUCCEEDED(hr))
		{
			xmlDoc->get_documentElement(root);
			xmlDoc.Release();
		}
		else
		{
			BSTR reason;
			parseError->get_reason(&reason);
			throw exception(_com_util::ConvertBSTRToString(reason));
		}
	}
}

void XsdComparator::ReadXml(const std::string file)
{
	HRESULT hr = CoInitialize(NULL);
	if (SUCCEEDED(hr))
	{
		MSXML2::IXMLDOMDocument2Ptr xmlDoc;
		hr = xmlDoc.CreateInstance(__uuidof(MSXML2::DOMDocument60), NULL, CLSCTX_INPROC_SERVER);

		VARIANT_BOOL success = VARIANT_FALSE;
		xmlDoc->put_resolveExternals(VARIANT_FALSE);
		VARIANT x;
		x.vt = VT_BSTR;
		x.bstrVal = CComBSTR(file.c_str()).Detach();
		hr = xmlDoc->load(x, &success);

		MSXML2::IXMLDOMParseError* parseError;
		xmlDoc->get_parseError(&parseError);
		
		if (SUCCEEDED(hr)) {
			MSXML2::IXMLDOMElement* root;
			xmlDoc->get_documentElement(&root);
			BSTR nodeName;
			root->get_nodeName(&nodeName);
			printf("%S\n", nodeName);

			MSXML2::IXMLDOMNodeList* nodes;
			root->get_childNodes(&nodes);
			long length;
			nodes->get_length(&length);

			MSXML2::IXMLDOMNode* node;
			for (int i = 0; i < length; i++)
			{
				nodes->get_item(i, &node);
				node->get_nodeName(&nodeName);
				printf("%S\n", nodeName);
				node->Release();
			}
		}
	}
}

std::string XsdComparator::GetElementName(MSXML2::IXMLDOMNode* node)
{
	BSTR nodeName;
	node->get_nodeName(&nodeName);
	std::string elementName = _com_util::ConvertBSTRToString(nodeName);
	int position = elementName.find(':');
	return elementName.substr(position + 1, elementName.size() - position);
}

std::string XsdComparator::GetValueOfNameAttribute(MSXML2::IXMLDOMNode* node)
{
	MSXML2::IXMLDOMNamedNodeMap* attributes;
	node->get_attributes(&attributes);
	if (attributes != NULL)
	{
		MSXML2::IXMLDOMNode* attributeNode;
		attributes->getNamedItem(_bstr_t("name"), &attributeNode);
		if (attributeNode != NULL)
		{
			VARIANT value;
			attributeNode->get_nodeValue(&value);

			attributeNode->Release();
			attributes->Release();
			return _com_util::ConvertBSTRToString(value.bstrVal);
		}
		attributes->Release();
	}

	return "Unnamed";
}