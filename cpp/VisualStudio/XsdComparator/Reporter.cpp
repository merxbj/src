#include "Reporter.h"
#include <fstream>

void Reporter::Add(std::unique_ptr<ReporterRow> row)
{
    report.push_back(std::move(row));
}

void Reporter::Add(ReporterRowStatus reportRowStatus, int depth, std::string nodeName, std::string nodeValueOfName)
{
    if (IsAcceptableNode(nodeName))
    {
        Add(std::make_unique<ReporterRow>(reportRowStatus, depth, nodeName, nodeValueOfName));
    }
}

void Reporter::Export(const std::string path)
{
    std::ofstream file;
    file.open(path);
    
    for (auto& value : report)
    {
        file << *value << std::endl;
    }

    file.close();
}

bool Reporter::IsAcceptableNode(const std::string& nodeName)
{
    return std::find(acceptableNodes.begin(), acceptableNodes.end(), nodeName) != acceptableNodes.end();
}