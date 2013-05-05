using Integri.Common;
using Integri.Common.Configuration;
using Integri.Indexer.PublicNameIndexing.Gathering;
using Integri.Indexer.PublicNameIndexing.Locating;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Indexer.PublicNameIndexing
{
    public class PublicObjectIndexer : IIndexer
    {
        public PublicObjectIndexer()
        {
            this.gatherers = Utils.Discover<IGatherer>();
        }

        public void Index()
        {
            Console.WriteLine("PublicObjectIndexer - About to index public objects in the following projects...");
            List<Project> projects = ProjectDiscovery.Discover("ProjectConfiguration");
            projects.ForEach(project => Console.WriteLine("\t{0}", project));

            Console.WriteLine("PublicObjectIndexer - About to gather all declared public objects...");
            List<PublicObject> publics = GatherPublicObjects(projects);
            Console.WriteLine("PublicObjectIndexer - Found total of {0} public objects...", publics.Count());

            Console.WriteLine("PublicObjectIndexer - About to find occurrences of all declared public objects...");
            List<Occurrence> occurrences = new List<Occurrence>();
            projects.ForEach(p => occurrences.AddRange(FindOccurrences(publics, p)));
            Console.WriteLine("PublicObjectIndexer - Found total of {0} occurrences ...", occurrences.Count);

            Console.WriteLine("PublicObjectIndexer - About to publish the results ...");
            using (DatabasePublisher publisher = new DatabasePublisher())
            {
                publisher.Publish(occurrences);
            }
            Console.WriteLine("PublicObjectIndexer - Results published ...");
            Console.WriteLine("PublicObjectIndexer - Done.");
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
        private List<IGatherer> gatherers;

        private const string projectPath = @"d:\temp\integri\projects\";
    }
}
