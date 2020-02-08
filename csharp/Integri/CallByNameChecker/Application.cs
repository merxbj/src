using Integri.Common.Configuration;
using System.Collections.Generic;
using System.Linq;
using Integri.Common.Logging;
using Integri.Common.Unipaas;
using log4net;

namespace Integri.CallByNameChecker
{
    class Application
    {
        static void Main()
        {
            log.Info("CallByNameChecker - About to check Call By Name usage in the following projects...");
            IDictionary<string, Project> projects = ProjectDiscovery.Discover("ProjectConfiguration");
            foreach (Project project in projects.Values)
            {
                log.InfoFormat("\t{0}", project);
            }

            log.Info("CallByNameChecker - About to find all Call By Name occurrences...");
            Finder finder = new Finder(projects.Values);
            List<CallByName> occurrences = finder.Find();
            log.InfoFormat("CallByNameChecker - Found total of {0} Call By Names...", occurrences.Count());

            CheckDangerousUsage(occurrences);
            CheckDuplicateEvents(projects, occurrences);

            log.Info("CallByNameChecker - Done.");
        }

        // todo: finish
        private static void CheckDuplicateEvents(IDictionary<string, Project> projects, List<CallByName> occurrences)
        {
            if (!projects.ContainsKey(ProjectNames.Modely))
            {
                log.Warn("Unable to find project Modely. Cannot continue with duplicate checking.");
                return;
            }

            if (!projects.ContainsKey(ProjectNames.System))
            {
                log.Warn("Unable to find project System. Cannot continue with duplicate checking.");
                return;
            }

            Project modely = projects["Modely"];
            //List<Event> publishedEvents = FindPublishedEvents(modely);

            Project system = projects["System"];
            // for each event find handle in system/main program
            // for each handle filter for external program calls
            // for each external program compare event, handle and program parameters
            // for each external program find Call By Name and compare parameters
        }

        private static void CheckDangerousUsage(List<CallByName> occurrences)
        {   
            log.Info("CallByNameChecker - About to check Call By Name occurrences...");
            DangerousUsageChecker dangerousUsageChecker = new DangerousUsageChecker(occurrences);
            List<CallByName> failed = dangerousUsageChecker.Check();
            log.InfoFormat("CallByNameChecker - Found total of {0} Call By Names that do not pass the check...", failed.Count());

            log.Info("CallByNameChecker - About to publish the results, if any...");
            CheckFailurePublisher publisher = new CheckFailurePublisher();
            publisher.Publish(failed);
            log.Info("CallByNameChecker - Results publishied...");
        }

        private static readonly ILog log = LoggingFactory.GetLogger();
    }
}
