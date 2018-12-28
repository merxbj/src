using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using System.Runtime.Serialization.Json;
using NcrOrgChartDataGrabber;

namespace NcrOrgChartDataGrabberApp
{
    class Application
    {
        static void Main(string[] args)
        {
            if (args[0] == "walk")
            {
                Employee root = null;
                try
                {
                    Walker walker = new Walker(Console.Out);
                    root = walker.WalkOrganizationDownFrom(args[1]);
                }
                finally
                {
                    if (root != null)
                    {
                        Serialization.SerializeObject(root, args[1] + ".json");
                    }
                }
            }
            else if (args[0] == "load")
            {
                Employee manager = Serialization.DeSerializeObject<Employee>(args[1] + ".json");
                WalkOrganizationDownFrom(manager, 0);
            }
            else if (args[0] == "resume")
            {
                Employee root = Serialization.DeSerializeObject<Employee>(args[1] + ".json");
                try
                {
                    Walker walker = new Walker(Console.Out);
                    walker.ResumeWalk(root);
                }
                finally
                {
                    if (root != null)
                    {
                        Serialization.SerializeObject(root, args[1] + ".json");
                    }
                }
            }

            Console.WriteLine("Done.");
        }

        private static void WalkOrganizationDownFrom(Employee employee, int level)
        {
            for (int i = 0; i < level; i++)
            {
                Console.Write(" ");
            }

            Console.WriteLine("{0}:\t{1}", level, employee);
            if (employee.DirectReports != null)
            {
                foreach (Employee directReport in employee.DirectReports)
                {
                    WalkOrganizationDownFrom(directReport, level + 1);
                }
            }
        }
    }
}
