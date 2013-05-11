using Integri.Common;
using Integri.Common.Configuration;
using System;
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
            log.Info("CallByNameChecker - About to index public objects in the following projects...");
            List<Project> projects = ProjectDiscovery.Discover("ProjectConfiguration");
            projects.ForEach(project => log.InfoFormat("\t{0}", project));

            log.Info("CallByNameChecker - About to find all Call By Name occurrences...");
            Finder finder = new Finder(projects);
            List<CallByName> occurrences = finder.Find();
            log.InfoFormat("CallByNameChecker - Found total of {0} Call By Names...", occurrences.Count());

            log.Info("CallByNameChecker - About to check Call By Name occurrences...");
            Checker checker = new Checker(occurrences);
            List<CallByName> failed = checker.Check();
            log.InfoFormat("CallByNameChecker - Found total of {0} Call By Names that do not pass the check...", failed.Count());

            log.Info("CallByNameChecker - About to publish the results, if any...");
            CheckFailurePublisher publisher = new CheckFailurePublisher();
            publisher.Publish(failed);
            log.Info("CallByNameChecker - Results publishied...");

            log.Info("CallByNameChecker - Done.");
        }

        private static readonly ILog log = LoggingFactory.GetLogger();
    }
}
