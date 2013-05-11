using Integri.Common;
using System;
using System.Collections.Generic;
using System.Xml;
using Integri.Common.Unipaas;

namespace Integri.Indexer.PublicNameIndexing.Gathering
{
    public class ProgramGatherer : IGatherer
    {
        public IList<PublicObject> GatherPublicObjects(Project project)
        {
            var publics = new List<PublicObject>();

            ProgramDiscovery pd = new ProgramDiscovery(project);
            foreach (Program program in pd.DiscoverPrograms())
            {
                XmlDocument source = program.Source;
                XmlNode obj = source.SelectSingleNode("/Application/ProgramsRepository/Programs/Task/Header[Public]");
                if (obj != null)
                {
                    string name = obj.SelectSingleNode("./Public/@val").Value;
                    int localId = Convert.ToInt32(obj.Attributes["id"].Value);
                    publics.Add(new PublicObject(name, ForType, project.MciFile, localId));
                }
            }

            return publics;
        }

        public ObjectType ForType
        {
            get { return ObjectType.Program; }
        }
    }
}
