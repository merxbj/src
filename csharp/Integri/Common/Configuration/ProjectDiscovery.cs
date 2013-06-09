using System.Collections.Generic;
using System.Configuration;
using Integri.Common.Unipaas;

namespace Integri.Common.Configuration
{
    public class ProjectDiscovery
    {
        public static IDictionary<string, Project> Discover(string configurationSectionName)
        {
            Dictionary<string, Project> projects = new Dictionary<string, Project>();

            ProjectConfigurationSection config = (ProjectConfigurationSection)ConfigurationManager.GetSection(configurationSectionName);
            if (config != null)
            {
                string root = config.ProjectItems.Root;
                foreach (ProjectElement project in config.ProjectItems)
                {
                    projects.Add(project.Name, new Project(project.Name, root + project.Path, project.Mci));
                }
            }

            return projects;
        }
    }
}
