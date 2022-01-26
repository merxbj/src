#include "XsdComparator.h"
#include <iostream>
#include <stdio.h>
#include <tchar.h>
#include <windows.h>
#include <atlbase.h>
#include <comutil.h>
#include <format>

using namespace std;

XsdComparator::XsdComparator(Reporter& _reporter) : reporter(_reporter)
{
}

void XsdComparator::Compare(const std::string file1, const std::string file2)
{
	try
	{
		MSXML2::IXMLDOMElement* firstRoot = nullptr;
		MSXML2::IXMLDOMElement* secondRoot = nullptr;

		GetRootElement(file1, &firstRoot);
		GetRootElement(file2, &secondRoot);

		if (firstRoot != nullptr && secondRoot != nullptr)
		{
			Compare(firstRoot, secondRoot);
		}
		else
		{
			throw std::exception("One of the roots is null");
		}
	}
	catch (const std::exception& e)
	{
		printf(e.what());
	}
}

void XsdComparator::Compare(MSXML2::IXMLDOMElement* firstRoot, MSXML2::IXMLDOMElement* secondRoot)
{
	auto firstRootNode = static_cast<MSXML2::IXMLDOMNode*>(firstRoot);
	auto secondRootNode = static_cast<MSXML2::IXMLDOMNode*>(secondRoot);

	std::string firstRootElementName = GetElementName(firstRootNode);
	std::string secondRootElementName = GetElementName(secondRootNode);

	std::string firstRootName = GetValueOfNameAttribute(firstRootNode);
	std::string secondRootName = GetValueOfNameAttribute(secondRootNode);

	if (firstRootName.compare(secondRootName) == 0)
	{
		reporter.Add(ReporterRowStatus::None, 0, firstRootElementName, firstRootName);

		MSXML2::IXMLDOMNodeList* firstChildren = nullptr;
		MSXML2::IXMLDOMNodeList* secondChildren = nullptr;

		firstRoot->get_childNodes(&firstChildren);
		if (firstChildren == nullptr)
			throw exception("First children list is null");

		secondRoot->get_childNodes(&secondChildren);
		if (secondChildren == nullptr)
			throw exception("Second children list is null");

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
	MSXML2::IXMLDOMNode* node = nullptr;

	long nodesLength = 0;
	nodes->get_length(&nodesLength);

	for (int i = 0; i < nodesLength; i++)
	{
		nodes->get_item(i, &node);
		if (node == nullptr)
		{ 
			std::string message = std::string("Node #") + static_cast<char>(i) + std::string(" returned null");
			throw std::exception(message.c_str());
		}
		
		map.insert(pair<std::string, MSXML2::IXMLDOMNode*>(GetValueOfNameAttribute(node), node));
	}

	return map;
}

void XsdComparator::CompareChildren(MSXML2::IXMLDOMNodeList* firstChildren, MSXML2::IXMLDOMNodeList* secondChildren, int depth)
{
	auto firstMap = GetNodesMap(firstChildren);
	auto secondMap = GetNodesMap(secondChildren);
	
	for (auto& pair : secondMap)
	{
		if (!firstMap.contains(pair.first))
		{
			reporter.Add(ReporterRowStatus::Added, depth, GetElementName(pair.second), pair.first);
		}
	}

	for (auto& pair : firstMap)
	{
		if (secondMap.contains(pair.first))
		{
			MSXML2::IXMLDOMNodeList* firstNodes = nullptr;
			MSXML2::IXMLDOMNodeList* secondNodes = nullptr;

			pair.second->get_childNodes(&firstNodes);
			if (firstNodes == nullptr)
				throw exception("First children list is null");

			secondMap.find(pair.first)->second->get_childNodes(&secondNodes);
			if (secondNodes == nullptr)
				throw exception("Second children list is null");

			reporter.Add(ReporterRowStatus::None, depth, GetElementName(pair.second), pair.first);
			CompareChildren(firstNodes, secondNodes, depth + 1);
		}
		else
		{
			reporter.Add(ReporterRowStatus::Removed, depth, GetElementName(pair.second), pair.first);
		}
	}
}

void XsdComparator::GetRootElement(const std::string file, MSXML2::IXMLDOMElement** root)
{
	HRESULT hr = CoInitialize(NULL);
	if (SUCCEEDED(hr))
	{
		MSXML2::IXMLDOMDocument2Ptr xmlDoc = nullptr;
		hr = xmlDoc.CreateInstance(__uuidof(MSXML2::DOMDocument60), NULL, CLSCTX_INPROC_SERVER);
		if (xmlDoc == nullptr)
			throw exception("DomDocument is null");

		VARIANT_BOOL success = VARIANT_FALSE;
		xmlDoc->put_resolveExternals(VARIANT_FALSE);
		VARIANT x { VT_EMPTY };
		x.vt = VT_BSTR;
		x.bstrVal = CComBSTR(file.c_str()).Detach();
		hr = xmlDoc->load(x, &success);

		if (SUCCEEDED(hr))
		{
			xmlDoc->get_documentElement(root);
			xmlDoc.Release();
		}
		else
		{
			MSXML2::IXMLDOMParseError* parseError = nullptr;
			xmlDoc->get_parseError(&parseError);
			if (parseError == NULL)
				throw exception("Parse error could not be retrieved.");

			BSTR reason = NULL;
			parseError->get_reason(&reason);

			if (reason == NULL)
				throw exception("Unspecified parse error");
			else
				throw exception(_com_util::ConvertBSTRToString(reason));
		}
	}
}

std::string XsdComparator::GetElementName(MSXML2::IXMLDOMNode* node)
{
	BSTR nodeName = NULL;
	node->get_nodeName(&nodeName);
	if (nodeName == NULL)
		throw std::exception("Node name is null");

	std::string elementName = _com_util::ConvertBSTRToString(nodeName);
	size_t position = elementName.find(':');
	return elementName.substr(position + 1, elementName.size() - position);
}

std::string XsdComparator::GetValueOfNameAttribute(MSXML2::IXMLDOMNode* node)
{
	MSXML2::IXMLDOMNamedNodeMap* attributes = nullptr;
	node->get_attributes(&attributes);
	if (attributes != nullptr)
	{
		MSXML2::IXMLDOMNode* attributeNode = nullptr;
		attributes->getNamedItem(_bstr_t("name"), &attributeNode);
		if (attributeNode != nullptr)
		{
			VARIANT value = { VT_EMPTY };
			attributeNode->get_nodeValue(&value);

			attributeNode->Release();
			attributes->Release();

			if (value.bstrVal == NULL)
				return "Unnamed";
			else
				return _com_util::ConvertBSTRToString(value.bstrVal);
		}
		attributes->Release();
	}

	return "Unnamed";
}