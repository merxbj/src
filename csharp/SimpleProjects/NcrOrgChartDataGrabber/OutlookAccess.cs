using System;
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

        private static OutlookAccess _instance;
        public static OutlookAccess Instance => _instance ?? (_instance = new OutlookAccess());

        public Employee CreateEmployeeFrom(string email)
        {
            Recipient r = mapi.CreateRecipient(email);
            ExchangeUser eu = r.AddressEntry.GetExchangeUser();
            return CreateEmployeeFrom(eu.Alias, eu);
        }

        public Employee GetManagerOf(Employee employee)
        {
            Recipient r = mapi.CreateRecipient(employee.Email);
            ExchangeUser eu = r.AddressEntry.GetExchangeUser();
            ExchangeUser eum = eu.GetExchangeUserManager();

            Employee manager;
            string alias = GetAlias(eum);
            if (cache.ContainsKey(alias))
            {
                manager = cache[alias];
            }
            else
            {
                manager = CreateEmployeeFrom(alias, eum);
                cache[alias] = manager;
            }

            return manager;
        }

        public IEnumerable<Employee> GetDirectReportsOf(Employee manager)
        {
            if (manager.Eu == null)
            {
                // Eu is only a cached (non-persistent) property - reload it if missing (restored from persistence?)
                Recipient r = mapi.CreateRecipient(manager.Email);
                ExchangeUser eu = r.AddressEntry.GetExchangeUser();
                manager.Eu = eu;
            }

            foreach (AddressEntry dr in manager.Eu.GetDirectReports())
            {
                ExchangeUser eudr = dr.GetExchangeUser();
                string alias = GetAlias(eudr);
                if (cache.ContainsKey(alias))
                {
                    yield return cache[alias];
                }
                else
                {
                    Employee directReport = CreateEmployeeFrom(alias, eudr);
                    cache[alias] = directReport;
                    yield return directReport;
                }
            }
        }

        private string GetAlias(ExchangeUser eudr)
        {
            if (!string.IsNullOrEmpty(eudr.Alias))
            {
                return eudr.Alias.ToLower();
            }

            string first = eudr.FirstName ?? "a";
            string last = eudr.LastName ?? "b";
            string prefix = first.Substring(0, 1).ToLower() + last.Substring(0, 1).ToLower();
            int suffix = 0;
            string alias;
            do
            {
                suffix++;
                alias = string.Format("-{0}{1:D6}", prefix, suffix); // '-' stands for virtual (to avoid clashes with future aliasees)
            } while (cache.ContainsKey(alias));

            return alias;
        }

        private static Employee CreateEmployeeFrom(string alias, ExchangeUser eu)
        {
            return new Employee(eu, alias, eu.FirstName, eu.LastName, eu.JobTitle, eu.PrimarySmtpAddress ?? String.Empty, eu.City, eu.Department);
        }

        private static NameSpace GetMapiNamespace()
        {
            NameSpace nameSpace;

            Application application;

            // Check whether there is an Outlook process running.
            if (Process.GetProcessesByName("OUTLOOK").Any())
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
