using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Data.SqlClient;
using Rodokmen.Common;
using Rodokmen.Report.PersonsReport;

namespace Rodokmen.Report.Ui.PersonsReport
{
    public partial class PersonsReportForm : Form
    {
        public PersonsReportForm()
        {
            FillDataSet();
            InitializeComponent();
            personsReport.SetDataSource(this.personsDs);
        }

        #region DB Access methods
        private void FillDataSet()
        {
            try
            {
                SqlConnection connection = ServerConnector.Connection;
                SqlDataAdapter result = new SqlDataAdapter();
                SqlCommand command = new SqlCommand(@"
                    SELECT person_id as personId,
                           first_name as firstName,
                           last_name as lastName,
                           family_name as familyName,
                           gender as gender,
                           birth_date as birthDate,
                           death_date as deathDate,
                           comment as comment
                    FROM Persons
                    ");

                result.SelectCommand = command;
                result.SelectCommand.Connection = connection;
                SqlCommandBuilder builder = new SqlCommandBuilder(result);
                result.Fill(personsDs, "Persons");
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
        }
        #endregion

        #region Private members
        private PersonsDs personsDs = new PersonsDs();
        #endregion
    }
}
