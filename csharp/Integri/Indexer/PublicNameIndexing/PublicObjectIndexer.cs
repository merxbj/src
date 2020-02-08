using Integri.Common;
using Integri.Common.Configuration;
using Integri.Common.Logging;
using Integri.Common.Unipaas;
using Integri.Indexer.PublicNameIndexing.Gathering;
using Integri.Indexer.PublicNameIndexing.Locating;
using System;
using System.Collections.Generic;
using System.Linq;
using log4net;

namespace Integri.Indexer.PublicNameIndexing
{
    public class PublicObjectIndexer : IIndexer
    {
        public PublicObjectIndexer()
        {
            gatherers = Utils.Discover<IGatherer>(typeof(PublicObjectIndexer).Assembly);
        }

        public void Index()
        {
            log.Info("About to index public objects in the following projects...");
            IDictionary<string, Project> projects = ProjectDiscovery.Discover("ProjectConfiguration");
            List<Project> projectList = new List<Project>(projects.Values);
            projectList.ForEach(project => log.InfoFormat("\t{0}", project));

            log.Info("About to gather all declared public objects...");
            List<PublicObject> publics = GatherPublicObjects(projectList);
            log.InfoFormat("Found total of {0} public objects...", publics.Count());

            log.Info("About to find occurrences of all declared public objects...");
            List<Occurrence> occurrences = new List<Occurrence>();
            projectList.ForEach(p => occurrences.AddRange(FindOccurrences(publics, p)));
            log.InfoFormat("Found total of {0} occurrences ...", occurrences.Count);

            log.Info("About to publish the results ...");
            using (DatabasePublisher publisher = new DatabasePublisher())
            {
                publisher.Publish(occurrences);
            }
            log.Info("Results published ...");
            log.Info("Done.");
        }

        private List<PublicObject> GatherPublicObjects(List<Project> projects)
        {
            var publics = new List<PublicObject>();
            projects.ForEach(project => publics.AddRange(GatherProjectPublicObjects(project)));
            return publics;
        }

        private IEnumerable<PublicObject> GatherProjectPublicObjects(Project project)
        {
            Console.WriteLine("\t{0}", project);
            var publics = new List<PublicObject>();
            gatherers.ForEach(g => publics.AddRange(g.GatherPublicObjects(project)));
            return publics;
        }

        private IEnumerable<Occurrence> FindOccurrences(List<PublicObject> publics, Project project)
        {
            ProjectExplorer explorer = new ProjectExplorer(publics);
            return explorer.Explore(project);
        }
        
        private readonly List<IGatherer> gatherers;
        private static readonly ILog log = LoggingFactory.GetLogger();
    }
}
