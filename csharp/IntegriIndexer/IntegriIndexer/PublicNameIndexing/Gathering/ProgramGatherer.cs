using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace IntegriIndexer.PublicNameIndexing.Gathering
{
    public class ProgramGatherer : IGatherer
    {
        public IList<PublicObject> GatherPublicObjects(Project project)
        {
            XmlDocument doc = new XmlDocument();
            doc.Load(project.SrcPath + "ProgramHeaders.xml");
            XmlNodeList objs = doc.SelectNodes("/Application/ProgramsRepositoryHeaders/Program/Header[Public]");

            var publics = new List<PublicObject>();
            foreach (XmlNode obj in objs)
            {
                string name = obj.SelectSingleNode("./Public/@val").Value;
                int localId = Convert.ToInt32(obj.Attributes["id"].Value);
                publics.Add(new PublicObject(name, ForType, project.MciFile, localId));
            }

            return publics;
        }


        public ObjectType ForType
        {
            get { return ObjectType.Program; }
        }
    }
}
