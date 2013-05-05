using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Integri.Common
{
    public class Project
    {
        public Project(string name, string srcPath, string mciFile)
        {
            this.Name = name;
            this.SrcPath = srcPath;
            this.MciFile = mciFile;
        }

        public override string ToString()
        {
            return Name;
        }

        public string Name { get; set; }
        public string SrcPath { get; set; }
        public string MciFile { get; set; }
    }
}
