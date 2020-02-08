using System;
using System.Collections.Generic;
using System.Xml;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing.Gathering
{
    public class EventGatherer : IGatherer
    {
        public IList<PublicObject> GatherPublicObjects(Project project)
        {
            XmlDocument doc = new XmlDocument();
            doc.Load(project.SrcPath + "Prg_1.xml");
            XmlNodeList objs = doc.SelectNodes("/Application/ProgramsRepository/Programs/Task[@MainProgram='Y']/EVNT[@EXPOSE=1]");

            var publics = new List<PublicObject>();
            foreach (XmlNode obj in objs)
            {
                publics.Add(new PublicObject(obj.Attributes["Public"].Value, ForType, project.MciFile, Convert.ToInt32(obj.Attributes["id"].Value)));
            }

            return publics;
        }


        public ObjectType ForType
        {
            get { return ObjectType.Event; }
        }
    }
}
