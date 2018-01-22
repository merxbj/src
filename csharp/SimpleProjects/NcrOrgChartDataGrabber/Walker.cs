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
            try
            {
                root = OutlookAccess.Instance.CreateEmployeeFrom(email);
                WalkOrganizationDownFrom(root, null, 0);
            }
            catch (Exception ex)
            {
                log.WriteLine(ex.ToString());
            }

            return root;
        }

        public void ResumeWalk(Employee rootToResume)
        {
            root = rootToResume;

            try
            {
                WalkOrganizationDownFrom(root, null, 0);
            }
            catch (Exception ex)
            {
                log.WriteLine(ex.ToString());
            }
        }

        private void WalkOrganizationDownFrom(Employee employee, Employee manager, int level)
        {
            if (employee == manager)
            {
                return; // cyrcular reference - Bill Nuti? :)
            }

            for (int i = 0; i < level; i++)
            {
                log.Write(" ");
            }

            log.WriteLine("{0}:\t{1}", level, employee);

            employee.Manager = manager;

            if (employee.DirectReports == null)
            {
                // if the employee was restored from persistence, we could already have the DirectReports filled in
                employee.DirectReports = new List<Employee>(OutlookAccess.Instance.GetDirectReportsOf(employee));
            }
            
            if (employee.DirectReports.Count > 0)
            {
                foreach (var directReport in employee.DirectReports)
                {
                    WalkOrganizationDownFrom(directReport, employee, level + 1);
                }

                Serialization.SerializeObject(root, root.Email + ".json");
            }
        }

        private Employee root;
        private readonly TextWriter log;
    }
}
