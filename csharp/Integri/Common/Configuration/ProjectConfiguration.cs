using System.Configuration;

namespace Integri.Common.Configuration
{
    public class ProjectConfigurationSection : ConfigurationSection
    {
        [ConfigurationProperty("projects")]
        public ProjectsCollection ProjectItems
        {
            get { return ((ProjectsCollection)(base["projects"])); }
        }
    }

    public class ProjectsCollection : ConfigurationElementCollection
    {
        [ConfigurationProperty("root", DefaultValue = "", IsKey = true, IsRequired = false)]
        public string Root
        {
            get
            {
                return ((string)(base["root"]));
            }
            set
            {
                base["root"] = value;
            }
        }

        protected override ConfigurationElement CreateNewElement()
        {
            return new ProjectElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((ProjectElement)element).Name;
        }

        public ProjectElement this[int idx]
        {
            get
            {
                return (ProjectElement)BaseGet(idx);
            }
        }
    }

    public class ProjectElement : ConfigurationElement
    {
        [ConfigurationProperty("name", DefaultValue = "", IsKey = true, IsRequired = true)]
        public string Name
        {
            get
            {
                return ((string)(base["name"]));
            }
            set
            {
                base["name"] = value;
            }
        }

        [ConfigurationProperty("path", DefaultValue = "", IsKey = false, IsRequired = true)]
        public string Path
        {
            get
            {
                return ((string)(base["path"]));
            }
            set
            {
                base["path"] = value;
            }
        }

        [ConfigurationProperty("mci", DefaultValue = "", IsKey = false, IsRequired = false)]
        public string Mci
        {
            get
            {
                return ((string)(base["mci"]));
            }
            set
            {
                base["mci"] = value;
            }
        }
    }
}
