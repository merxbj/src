using Integri.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace Integri.Indexer.PublicNameIndexing.Gathering
{
    public class ModelsGatherer : IGatherer
    {
        public IList<PublicObject> GatherPublicObjects(Project project)
        {
            XmlDocument doc = new XmlDocument();
            doc.Load(project.SrcPath + "Models.xml");
            XmlNodeList objs = doc.SelectNodes("/Application/ModelsRepository/Models/Object[@Public]");

            var publics = new List<PublicObject>();
            foreach (XmlNode obj in objs)
            {
                publics.Add(new PublicObject(obj.Attributes["Public"].Value, ForType, project.MciFile, Convert.ToInt32(obj.Attributes["id"].Value)));
            }

            return publics;
        }


        public ObjectType ForType
        {
            get { return ObjectType.Model; }
        }
    }
}
