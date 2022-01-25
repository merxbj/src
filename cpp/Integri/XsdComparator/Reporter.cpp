#include "Reporter.h"
#include <fstream>

void Reporter::Add(ReporterRow* row)
{
    report.push_back(row);
}

void Reporter::Add(ReporterRowStatus reportRowStatus, int depth, std::string nodeName, std::string nodeValueOfName)
{
    if (IsAcceptableNode(nodeName))
    {
        Add(new ReporterRow(reportRowStatus, depth, nodeName, nodeValueOfName));
    }
}

void Reporter::Export(const char* path)
{
    std::ofstream file;
    file.open(path);
    
    for (auto value : report)
    {
        file << *value << std::endl;
    }

    file.close();
}

bool Reporter::IsAcceptableNode(const std::string& nodeName)
{
    return std::find(acceptableNodes.begin(), acceptableNodes.end(), nodeName) != acceptableNodes.end();
}