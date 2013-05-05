using Integri.Common.Configuration;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;

namespace Integri.Common.Configuration
{
    public class ProjectDiscovery
    {
        public static List<Project> Discover(string configurationSectionName)
        {
            List<Project> projects = new List<Project>();

            ProjectConfigurationSection config = (ProjectConfigurationSection)ConfigurationManager.GetSection(configurationSectionName);
            if (config != null)
            {
                string root = config.ProjectItems.Root;
                foreach (ProjectElement project in config.ProjectItems)
                {
                    projects.Add(new Project(project.Name, root + project.Path, project.Mci));
                }
            }

            return projects;
        }
    }
}
