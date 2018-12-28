using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using NcrOrgChartDataGrabber;

namespace NcrOrgChartDataGrabberTest
{
    [TestClass]
    public class OutlookAccessTests
    {
        [TestMethod]
        public void TestCreateEmployeeFrom()
        {
            Employee emp = OutlookAccess.Instance.CreateEmployeeFrom("jaroslav.merxbauer@ncr.com");
            Assert.AreEqual("jm185267", emp.Qlid);
            Assert.AreEqual("Jarda", emp.FirstName);
            Assert.AreEqual("Merxbauer", emp.LastName);
        }

        [TestMethod]
        public void TestGetManagerOf()
        {
            Employee emp = OutlookAccess.Instance.CreateEmployeeFrom("jaroslav.merxbauer@ncr.com");
            Employee man = OutlookAccess.Instance.GetManagerOf(emp);
            Assert.AreEqual("jan.psenicka@ncr.com", man.Email);
        }

        [TestMethod]
        public void TestGetDirectReportsOf()
        {
            Employee manager = OutlookAccess.Instance.CreateEmployeeFrom("jozef.smizansky@ncr.com");
            List<Employee> directReports = new List<Employee>(OutlookAccess.Instance.GetDirectReportsOf(manager));
            Assert.AreEqual(16, directReports.Count);
        }
    }
}
