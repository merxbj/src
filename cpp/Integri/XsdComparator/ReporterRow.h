#pragma once
#include "ReporterRowStatus.h"
#include <ostream>

class ReporterRow
{
public:
    ReporterRow(ReporterRowStatus reportRowStatus, int depth, std::string nodeName, std::string nodeValueOfName);
    

private:
    ReporterRowStatus status;
    int depth;
    std::string nodeName;
    std::string nodeValueOfName;

    char ConvertStatus(ReporterRowStatus status);
    friend std::ostream& operator<<(std::ostream& os, const ReporterRow& row);
};