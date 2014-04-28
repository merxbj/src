using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using Microsoft.Office.Interop.Outlook;

namespace NcrOrgChartDataGrabber
{
    public interface IOutlookAccess
    {
        Employee GetManagerOf(Employee employee);
    }

    public class OutlookAccess : IOutlookAccess
    {
        private OutlookAccess()
        {
            mapi = GetMapiNamespace();
            cache = new Dictionary<string, Employee>();
        }

        private static OutlookAccess instance;
        public static OutlookAccess Instance
        {
            get { return instance ?? (instance = new OutlookAccess()); }
        }

        public Employee CreateEmployeeFrom(string email)
        {
            Recipient r = mapi.CreateRecipient(email);
            ExchangeUser eu = r.AddressEntry.GetExchangeUser();
            return CreateEmployeeFrom(eu);
        }

        public Employee GetManagerOf(Employee employee)
        {
            Recipient r = mapi.CreateRecipient(employee.Email);
            ExchangeUser eu = r.AddressEntry.GetExchangeUser();
            ExchangeUser eum = eu.GetExchangeUserManager();

            Employee manager;
            if (cache.ContainsKey(eum.Alias))
            {
                manager = cache[eum.Alias];
            }
            else
            {
                manager = CreateEmployeeFrom(eum);
                cache[manager.Qlid] = manager;
            }

            return manager;
        }

        public IEnumerable<Employee> GetDirectReportsOf(Employee manager)
        {
            foreach (AddressEntry dr in manager.Eu.GetDirectReports())
            {
                ExchangeUser eudr = dr.GetExchangeUser();
                if (cache.ContainsKey(eudr.Alias))
                {
                    yield return cache[eudr.Alias];
                }
                else
                {
                    Employee directReport = CreateEmployeeFrom(eudr);
                    cache[directReport.Qlid] = directReport;
                    yield return directReport;
                }
            }
        }

        private static Employee CreateEmployeeFrom(ExchangeUser eu)
        {
            return new Employee(eu, eu.Alias.ToLower(), eu.FirstName, eu.LastName, eu.JobTitle, eu.PrimarySmtpAddress.ToLower());
        }

        private static NameSpace GetMapiNamespace()
        {
            NameSpace nameSpace;

            Application application;

            // Check whether there is an Outlook process running.
            if (Process.GetProcessesByName("OUTLOOK").Count() > 0)
            {

                // If so, use the GetActiveObject method to obtain the process and cast it to an Application object.
                application = (Application) Marshal.GetActiveObject("Outlook.Application");
                nameSpace = application.GetNamespace("MAPI");
            }
            else
            {

                // If not, create a new instance of Outlook and log on to the default profile.
                application = new Application();
                nameSpace = application.GetNamespace("MAPI");
                nameSpace.Logon("", "", Missing.Value, Missing.Value);
            }

            // Return the Outlook Application object.
            return nameSpace;
        }

        private readonly Dictionary<string, Employee> cache;
        private readonly NameSpace mapi;
    }
}
