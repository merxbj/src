using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using Integri.Common.Unipaas;

namespace Integri.Common
{
    public class Program
    {
        public Program(Project project, string name, string fileName, XmlDocument source)
        {
            this.project = project;
            this.name = name;
            this.fileName = fileName;
            this.source = source;
        }

        public Project Project { get { return project; } }
        public string Name { get { return name; } }
        public string FileName { get { return fileName; } }
        public XmlDocument Source {get { return source; } }

        public override string ToString()
        {
            return project.ToString() + "/" + Name;
        }

        private Project project;
        private string name;
        private string fileName;
        private XmlDocument source;
    }
}
