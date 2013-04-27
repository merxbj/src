using IntegriIndexer.PublicNameIndexing.Gathering;
using IntegriIndexer.PublicNameIndexing.Locating;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IntegriIndexer.PublicNameIndexing
{
    public class PublicNameIndexer : IIndexer
    {
        public PublicNameIndexer()
        {
            this.publisher = new DatabasePublisher();
            this.gatherers = Utils.Discover<IGatherer>();
        }

        public void Index()
        {
            List<Project> projects = DetermineProjects();
            List<PublicObject> publics = GatherPublicObjects(projects);
            List<Occurrence> occurrences = new List<Occurrence>();
            
            projects.ForEach(p => occurrences.AddRange(FindOccurrences(publics, p)));
            publisher.Publish(occurrences);
            
        }

        private List<Project> DetermineProjects()
        {
            // todo: this should be configuration
            return new List<Project>() { 
                new Project("Modely", projectPath + @"modely\modely\Source\", "modely.eci"),
                new Project("System", projectPath + @"system\system\Source\", String.Empty),
                new Project("Evozbd", projectPath + @"Evozbd\Evozbd\Source\", "evozbd.eci"),
                new Project("PDU", projectPath + @"PDU\PDU\Source\", "pdu.eci"),
                new Project("BE", projectPath + @"dom_spr\dom_spr\Source\", "dom_spr.eci"),
            };
                                    
        }

        private List<PublicObject> GatherPublicObjects(List<Project> projects)
        {
            var publics = new List<PublicObject>();
            projects.ForEach(project => publics.AddRange(GatherProjectPublicObjects(project)));
            return publics;
        }

        private IEnumerable<PublicObject> GatherProjectPublicObjects(Project project)
        {
            var publics = new List<PublicObject>();
            gatherers.ForEach(g => publics.AddRange(g.GatherPublicObjects(project)));
            return publics;
        }

        private IEnumerable<Occurrence> FindOccurrences(List<PublicObject> publics, Project project)
        {
            ProjectExplorer explorer = new ProjectExplorer(publics);
            return explorer.Explore(project);
        }

        private IPublisher publisher;
        private List<IGatherer> gatherers;

        private const string projectPath = @"d:\temp\integri\projects\";
    }
}
