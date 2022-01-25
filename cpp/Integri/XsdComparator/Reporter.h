#pragma once
#include "ReporterRowStatus.h"
#include "ReporterRow.h"
#include <list>
#include <string>
#include <vector>

class Reporter
{
public:
	void Add(ReporterRow* row);
	void Add(ReporterRowStatus reportRowStatus, int depth, std::string nodeName, std::string nodeValueOfName);
	void Export(const char* path);
private:
	std::list<ReporterRow*> report;
	std::vector<std::string> acceptableNodes{ "schema", "element", "attribute", "xs:schema", "xs:element", "xs:attribute"};

	bool IsAcceptableNode(const std::string& value);
};