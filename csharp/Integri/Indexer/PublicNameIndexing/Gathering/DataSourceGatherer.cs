using System;
using System.Collections.Generic;
using System.Xml;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing.Gathering
{
    public class DataSourceGatherer : IGatherer
    {
        public IList<PublicObject> GatherPublicObjects(Project project)
        {
            XmlDocument doc = new XmlDocument();
            doc.Load(project.SrcPath + "DataSources.xml");
            XmlNodeList dataObjects = doc.SelectNodes("/Application/DataSourceRepository/DataObjects/DataObject[@Public]");

            var publics = new List<PublicObject>();
            foreach (XmlNode dataObject in dataObjects)
            {
                publics.Add(new PublicObject(dataObject.Attributes["Public"].Value, ForType, project.MciFile, Convert.ToInt32(dataObject.Attributes["id"].Value)));
            }

            return publics;
        }


        public ObjectType ForType
        {
            get { return ObjectType.DataSource; }
        }
    }
}
