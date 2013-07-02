using System.Configuration;
using System.Xml;

namespace Integri.Common.Configuration
{
    public class XmlNodeSectionHandler : IConfigurationSectionHandler
    {
        /// <summary>
        /// Mandatory Create method that must be implemented
        /// for classes that implemented the IConfigurationSectionHandler
        /// interface.  Used to return configuration data.
        /// </summary>
        /// <param name="parent"></param>
        /// <param name="configContext"></param>
        /// <param name="section"></param>
        /// <returns>The root XmlNode for the config section is returned.</returns>
        public object Create(object parent, object configContext, XmlNode section)
        {
            //just return the XmlNode for now
            return section;
        }
    }
}
