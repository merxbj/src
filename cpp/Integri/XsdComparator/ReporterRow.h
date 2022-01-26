#pragma once
#include "ReporterRowStatus.h"
#include <ostream>

class ReporterRow
{
public:
    ReporterRow(ReporterRowStatus reportRowStatus, int depth, std::string nodeName, std::string nodeValueOfName);
    

private:
    ReporterRowStatus status = ReporterRowStatus::None;
    int depth = 0;
    std::string nodeName;
    std::string nodeValueOfName;

    friend std::ostream& operator<<(std::ostream& os, const ReporterRow& row);
};