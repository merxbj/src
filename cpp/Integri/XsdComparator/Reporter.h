#pragma once
#include "ReporterRowStatus.h"
#include "ReporterRow.h"
#include <list>
#include <string>
#include <vector>

class Reporter
{
public:
	void Add(std::unique_ptr<ReporterRow> row);
	void Add(ReporterRowStatus reportRowStatus, int depth, std::string nodeName, std::string nodeValueOfName);
	void Export(std::string path);
private:
	std::list<std::unique_ptr<ReporterRow>> report;
	std::vector<std::string> acceptableNodes{ "schema", "element", "attribute", "xs:schema", "xs:element", "xs:attribute"};

	bool IsAcceptableNode(const std::string& value);
};