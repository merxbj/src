using System;
using System.Collections.Generic;
using Microsoft.Office.Interop.Outlook;

namespace NcrOrgChartDataGrabber
{
    [Serializable]
    public class Employee : IEquatable<Employee>
    {
        private Employee()
        {
            // for (de)serialization sake
        }

        public Employee(ExchangeUser eu, string qlid, string firstName, string lastName, string title, string email)
        {
            this.eu = eu;
            this.qlid = qlid;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.title = title;
        }

        public string Qlid
        {
            get { return qlid; }
        }

        public string FirstName
        {
            get { return firstName; }
        }

        public string LastName
        {
            get { return lastName; }
        }

        public string Title
        {
            get { return title; }
        }

        public ExchangeUser Eu
        {
            get { return eu; }
        }

        public bool Equals(Employee other)
        {
            if (ReferenceEquals(null, other)) return false;
            if (ReferenceEquals(this, other)) return true;
            return Equals(other.qlid, qlid);
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != typeof (Employee)) return false;
            return Equals((Employee) obj);
        }

        public override int GetHashCode()
        {
            return (qlid != null ? qlid.GetHashCode() : 0);
        }

        public static bool operator ==(Employee left, Employee right)
        {
            return Equals(left, right);
        }

        public static bool operator !=(Employee left, Employee right)
        {
            return !Equals(left, right);
        }

        public Employee Manager { get; set; }
        public List<Employee> DirectReports { get; set; }

        public string Email
        {
            get { return email; }
        }

        public override string ToString()
        {
            return "[" + qlid + "] - " + lastName + ", " + firstName + " (" + email + ") - " + title;
        }

        private readonly string qlid;
        private readonly string firstName;
        private readonly string lastName;
        private readonly string title;
        private readonly string email;

        [NonSerialized]
        private readonly ExchangeUser eu;
    }
}