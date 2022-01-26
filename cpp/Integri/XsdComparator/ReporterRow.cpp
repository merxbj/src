#include "ReporterRow.h"

ReporterRow::ReporterRow(ReporterRowStatus status, int depth, std::string nodeName, std::string nodeValueOfName)
{
    this->status = status;
    this->depth = depth;
    this->nodeName = nodeName;
    this->nodeValueOfName = nodeValueOfName;
}

char ConvertStatus(ReporterRowStatus status)
{
    switch (status)
    {
        case ReporterRowStatus::Added:
            return '+';
        case ReporterRowStatus::Removed:
            return '-';
        default:
            return ' ';
    }
}

std::ostream& operator<<(std::ostream& os, const ReporterRow& row)
{
    return (os << ConvertStatus(row.status) << std::string(row.depth, ' ') << row.nodeName << " " << row.nodeValueOfName);
}