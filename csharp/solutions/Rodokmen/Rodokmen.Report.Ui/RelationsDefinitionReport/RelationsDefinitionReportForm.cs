using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Data.SqlClient;
using Rodokmen.Common;
using Rodokmen.Report.RelationsDefinitionReport;

namespace Rodokmen.Report.Ui.RelationsDefinitionReport
{
    public partial class RelationsDefinitionReportForm : Form
    {
        public RelationsDefinitionReportForm()
        {
            FillDataSet();
            InitializeComponent();
            relationsDefinitionReport.SetDataSource(relationsDefinitionDs);
        }

        #region DB Access methods
        private void FillDataSet()
        {
            try
            {
                SqlConnection connection = ServerConnector.Connection;
                SqlDataAdapter result = new SqlDataAdapter();
                SqlCommand command = new SqlCommand(@"
                    SELECT relation_id as relationId,
                           name as name,
                           male_name as maleName,
                           female_name as femaleName,
                           max_rel_count as maxRelCount
                    FROM d_relations AS RelationsDefinition
                    ");

                result.SelectCommand = command;
                result.SelectCommand.Connection = connection;
                SqlCommandBuilder builder = new SqlCommandBuilder(result);
                result.Fill(relationsDefinitionDs, "RelationsDefinition");
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
        }
        #endregion

        #region Private members
        private RelationsDefinitionDs relationsDefinitionDs = new RelationsDefinitionDs();
        #endregion
    }
}
