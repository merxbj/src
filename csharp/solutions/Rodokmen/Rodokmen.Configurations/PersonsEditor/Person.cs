using System;
using System.Collections.Generic;
using System.Text;
using Rodokmen.Common;

namespace Rodokmen.Configurations.PersonsEditor
{
    class Person
    {
        #region Private Members
        private int id;
        private string firstName;
        private string lastName;
        private string familyName;
        private Enums.Gender gender;
        private DateTime birthDate;
        private DateTime deathDate;
        private string comment;
        #endregion

        #region Public properties
        public int Id
        {
            get
            {
                return this.id;
            }
            set
            {
                if (value <= 0)
                    throw new Exception("Invalid id");
                this.id = value;
            }
        }

        public string FirstName
        {
            get
            {
                return this.firstName;
            }
            set
            {
                this.firstName = value;
            }
        }

        public string LastName
        {
            get
            {
                return this.lastName;
            }
            set
            {
                this.lastName = value;
            }
        }

        public string FamilyName
        {
            get
            {
                return this.familyName;
            }
            set
            {
                this.familyName = value;
            }
        }

        public Enums.Gender Gender
        {
            get
            {
                return this.gender;
            }
            set
            {
                this.gender = value;
            }
        }

        public DateTime BirthDate
        {
            get
            {
                return this.birthDate;
            }
            set
            {
                if (value > DateTime.Today)
                    throw new Exception("Wrong birthdate");
                this.birthDate = value;
            }
        }

        public DateTime DeathDate
        {
            get
            {
                return this.deathDate;
            }
            set
            {
                if (value > DateTime.Today)
                    throw new Exception("You can't be serious!");
                this.deathDate = value;
            }
        }

        public string Comment
        {
            get
            {
                return this.comment;
            }
            set
            {
                this.comment = value;
            }
        }

        public PersonsDs.PersonsRow DataRow
        {
            set
            {
                if (!AssignDataRow(value))
                    throw new Exception("Wrong data row!");
            }
        }
        #endregion

        #region Helper methods
        private bool AssignDataRow(PersonsDs.PersonsRow row)
        {
            try
            {
                this.Id = row.personId;
                this.FirstName = row.firstName;
                this.LastName = row.lastName;
                this.FamilyName = row.familyName;
                this.Gender = row.gender == 'M' ? Enums.Gender.Male : Enums.Gender.Female;
                this.BirthDate = row.birthDate;
                this.DeathDate = row.deathDate;
                this.Comment = row.comment;
            }
            catch
            {
                return false;
            }
        }
        #endregion

    }
}
