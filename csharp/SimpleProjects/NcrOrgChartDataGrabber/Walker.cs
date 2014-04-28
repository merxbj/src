using System;
using System.Collections.Generic;
using System.IO;

namespace NcrOrgChartDataGrabber
{
    public class Walker
    {
        public Walker(TextWriter log)
        {
            this.log = log ?? TextWriter.Null;
        }

        public Employee WalkOrganizationDownFrom(string email)
        {
            Employee walkRoot = OutlookAccess.Instance.CreateEmployeeFrom(email);
            WalkOrganizationDownFrom(walkRoot, null, 0);
            return walkRoot;
        }

        private void WalkOrganizationDownFrom(Employee employee, Employee manager, int level)
        {
            for (int i = 0; i < level; i++)
            {
                log.Write(" ");
            }

            log.WriteLine(String.Format("{0}:\t{1}", level, employee));

            employee.Manager = manager;
            employee.DirectReports = new List<Employee>(OutlookAccess.Instance.GetDirectReportsOf(employee));
            foreach (Employee directReport in OutlookAccess.Instance.GetDirectReportsOf(employee))
            {
                WalkOrganizationDownFrom(directReport, employee, level + 1);
            }
        }

        private readonly TextWriter log;
    }
}
