#ifndef _COMMAND_LINE_H
#define _COMMAND_LINE_H

#include <string>
#include <memory>
#include <vector>

typedef std::auto_ptr<class CCommandLine> CCommandLinePtr;

class CArgumentException
{
private:
	std::string m_message;
public:
	CArgumentException(std::string message) : m_message(message) {}
	std::string getMessage() const {return this->m_message;}
};

class CCommandLine
{
private:
	std::string m_cronExpression;
	std::string m_displayMessage;

	CCommandLine() : m_cronExpression(""), m_displayMessage("") {}

public:
	
	static CCommandLinePtr parse(int argc, char* argv[])
	{
		CCommandLinePtr cl = CCommandLinePtr(new CCommandLine());
		
		if (argc < 2 || argc > 4)
		{
			throw CArgumentException("Invalid program parameters!");
		}
		else
		{
			int paramIndex = 1;
			if (strcmp(argv[paramIndex], "--message") == 0)
			{
				if (argc > 2)
				{
					cl->setDisplayMessage(argv[++paramIndex]);
					paramIndex++;
				}
				else
				{
					throw CArgumentException("Invalid program parameters!");
				}
			}
			else if (argc > 2)
			{
				throw CArgumentException("Invalid program parameters!");
			}
			else
			{
				cl->setDisplayMessage("Hello scheduled world!");
			}
			cl->setCronExpression(argv[paramIndex]);
		}

		return cl;
	}
	std::string getCronExpression() const {return this->m_cronExpression;};
	std::string getDisplayMessage() const {return this->m_displayMessage;};
	void setCronExpression(std::string cronExpression) {this->m_cronExpression = cronExpression;};
	void setDisplayMessage(std::string displayMessage) {this->m_displayMessage = displayMessage;};
};

#endif