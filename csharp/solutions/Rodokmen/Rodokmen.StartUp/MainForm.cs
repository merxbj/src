using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Rodokmen.Report.Ui;

namespace Rodokmen.StartUp
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Rodokmen.Report.Ui.PersonsReport.PersonsReportForm reportPersonsForm = new Rodokmen.Report.Ui.PersonsReport.PersonsReportForm();
            reportPersonsForm.Show();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Rodokmen.Report.Ui.RelationsDefinitionReport.RelationsDefinitionReportForm reportRelDefForm = new Rodokmen.Report.Ui.RelationsDefinitionReport.RelationsDefinitionReportForm();
            reportRelDefForm.Show();
        }

        private void button3_Click(object sender, EventArgs e)
        {

        }
    }
}