using Integri.Common;
using Integri.Common.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.CallByNameChecker
{
    class Application
    {
        static void Main(string[] args)
        {
            Console.WriteLine("CallByNameChecker - About to index public objects in the following projects...");
            List<Project> projects = ProjectDiscovery.Discover("ProjectConfiguration");
            projects.ForEach(project => Console.WriteLine("\t{0}", project));

            Console.WriteLine("CallByNameChecker - About to find all Call By Name occurrences...");
            Finder finder = new Finder(projects);
            List<CallByName> occurrences = finder.Find();
            Console.WriteLine("CallByNameChecker - Found total of {0} Call By Names...", occurrences.Count());

            Console.WriteLine("CallByNameChecker - About to check Call By Name occurrences...");
            Checker checker = new Checker(occurrences);
            List<CallByName> failed = checker.Check();
            Console.WriteLine("CallByNameChecker - Found total of {0} Call By Names that do not pass the check...", failed.Count());

            Console.WriteLine("CallByNameChecker - About to publish the results, if any...");
            CheckFailurePublisher publisher = new CheckFailurePublisher();
            publisher.Publish(failed);
            Console.WriteLine("CallByNameChecker - Results publishied...");

            Console.WriteLine("CallByNameChecker - Done.");
        }
    }
}
