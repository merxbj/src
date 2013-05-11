using Integri.Common;
using System;
using System.Collections.Generic;
using System.Xml;
using Integri.Common.Logging;
using Integri.Common.Unipaas;
using log4net;

namespace Integri.CallByNameChecker
{
    // ReSharper disable PossibleNullReferenceException
    class Finder
    {
        public Finder(List<Project> projects)
        {
            this.projects = projects;
        }

        public List<CallByName> Find()
        {
            List<CallByName> occurrences = new List<CallByName>();
            projects.ForEach(project => occurrences.AddRange(FindInProject(project)));
            return occurrences;
        }

        private IEnumerable<CallByName> FindInProject(Project project)
        {
            log.InfoFormat("\t" + project.Name);
            List<CallByName> occurrences = new List<CallByName>();
            ProgramDiscovery pd = new ProgramDiscovery(project);
            foreach (Program program in pd.DiscoverPrograms())
            {
                occurrences.AddRange(FindInProgram(program));
            }
            return occurrences;
        }

        private IEnumerable<CallByName> FindInProgram(Program program)
        {
            log.InfoFormat("\t\t" + program.FileName);
            XmlNodeList invokeNodes = program.Source.SelectNodes("//Invoke[OperationType/@val='B']");
            if (invokeNodes != null)
            {
                foreach (XmlNode invokeNode in invokeNodes)
                {
                    if (invokeNode is XmlElement)
                    {
                        CallByNameBuilder builder = new CallByNameBuilder(program, invokeNode as XmlElement);
                        yield return builder.Build();
                    }
                }
            }
        }

        private readonly List<Project> projects;
        private static readonly ILog log = LoggingFactory.GetLogger();

        #region CallByNameBuilder

        private class CallByNameBuilder
        {
            public CallByNameBuilder(Program program, XmlElement invokeElement)
            {
                this.program = program;
                this.invokeElement = invokeElement;
            }

            public CallByName Build()
            {
                // note that we assume here that expressions will alwyas be found
                XmlElement taskElement = CrawlToTaskElement();
                return new CallByName(BuildTask(taskElement), ResolveCondition(taskElement), ResolveCabinetName(taskElement), ResolvePublicName(taskElement));
            }

            private XmlElement CrawlToTaskElement()
            {
                // i know - this isn't particularly nice, but for the sake of simplicity :-)
                return invokeElement.ParentNode.ParentNode.ParentNode.ParentNode.ParentNode as XmlElement;
            }

            private Task BuildTask(XmlElement taskElement)
            {
                string name = taskElement.SelectSingleNode("Header/@Description").Value;
                Task parent = null;
                if (!taskElement.ParentNode.Name.Equals("Programs"))
                {
                    parent = BuildTask(taskElement.ParentNode as XmlElement);
                }
                return new Task(program, parent, name);
            }

            private string ResolveCondition(XmlElement taskElement)
            {
                XmlElement conditionElement = invokeElement.SelectSingleNode("Condition") as XmlElement;
                if (conditionElement.HasAttribute("val"))
                {
                    return conditionElement.Attributes["val"].Value;
                }
                
                XmlElement expressionsElement = taskElement.SelectSingleNode("Expressions") as XmlElement;
                int expressionPos = int.Parse(conditionElement.SelectSingleNode("@Exp").Value);

                return expressionsElement.SelectSingleNode("Expression[" + expressionPos + "]/ExpSyntax/@val").Value;
            }

            private string ResolvePublicName(XmlElement taskElement)
            {
                XmlElement expressionsElement = taskElement.SelectSingleNode("Expressions") as XmlElement;
                int expressionPos = int.Parse(invokeElement.SelectSingleNode("ProgramName/@Exp").Value);
                string decoratedProgramName = expressionsElement.SelectSingleNode("Expression[" + expressionPos + "]/ExpSyntax/@val").Value;

                return decoratedProgramName.Substring(1, decoratedProgramName.Length - 2); // remove surrounding appostrophes
            }

            private string ResolveCabinetName(XmlElement taskElement)
            {
                // todo: this deserves to be a library func
                XmlElement expressionsElement = taskElement.SelectSingleNode("Expressions") as XmlElement;
                int expressionPos = int.Parse(invokeElement.SelectSingleNode("CabinetName/@Exp").Value);
                string decoratedCabinetName = expressionsElement.SelectSingleNode("Expression[" + expressionPos + "]/ExpSyntax/@val").Value;

                try
                {
                    int endPos = decoratedCabinetName.LastIndexOf(".ecf", StringComparison.Ordinal) + 4;

                    char[] chars = decoratedCabinetName.ToCharArray();
                    int pos = endPos - 5;
                    while (char.IsLetterOrDigit(chars[pos]) || chars[pos] == '_')
                    {
                        pos--;
                    }
                    int startPos = pos + 1;

                    return decoratedCabinetName.Substring(startPos, endPos - startPos);  // cut out the actual cabinet file name
                }
                catch (IndexOutOfRangeException)
                {
                    return "Unknown";
                }
            }

            private readonly Program program;
            private readonly XmlElement invokeElement;
        }

        #endregion
    }
}
