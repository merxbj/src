using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using Microsoft.Office.Interop.Outlook;

namespace NcrOrgChartDataGrabber
{
    [Serializable]
    [DataContract]
    public class Employee : IEquatable<Employee>
    {
        public Employee(ExchangeUser eu, string qlid, string firstName, string lastName, string title, string email, string city, string department)
        {
            Eu = eu;
            Qlid = qlid;
            Email = email.ToLower();
            FirstName = firstName;
            LastName = lastName;
            Title = title;
            City = city;
            Department = department;
        }

        [DataMember]
        public string Qlid { get; set; }

        [DataMember]
        public string FirstName { get; set; }

        [DataMember]
        public string LastName { get; set; }

        [DataMember]
        public string Title { get; set; }

        [DataMember]
        public string Email { get; set; }

        [DataMember]
        public string City { get; set; }

        [DataMember]
        public string Department { get; set; }

        public Employee Manager { get; set; }

        [DataMember]
        public List<Employee> DirectReports { get; set; }

        public ExchangeUser Eu { get; set; }

        public bool Equals(Employee other)
        {
            if (ReferenceEquals(null, other)) return false;
            if (ReferenceEquals(this, other)) return true;
            return Equals(other.Qlid, Qlid);
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
            // ReSharper disable once NonReadonlyMemberInGetHashCode
            return Qlid?.GetHashCode() ?? 0;
        }

        public static bool operator ==(Employee left, Employee right)
        {
            return Equals(left, right);
        }

        public static bool operator !=(Employee left, Employee right)
        {
            return !Equals(left, right);
        }

        public override string ToString()
        {
            return "[" + Qlid + "] - " + LastName + ", " + FirstName + " (" + Email + ") - " + Title + " at " + City + " in " + Department;
        }
    }
}