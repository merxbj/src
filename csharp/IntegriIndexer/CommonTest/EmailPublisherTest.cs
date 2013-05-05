using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Integri.Common.Publishing;
using System.Collections.Generic;
using System.Net.Mail;
using System.Text;

namespace CommonTest
{
    [TestClass]
    public class EmailPublisherTest
    {
        [TestMethod]
        public void TestEmail()
        {
            StringEmailPublisher publisher = new StringEmailPublisher();
            List<string> lines = new List<string> { "Ahoj", "jak", "se", "mas", "?" };
            publisher.Publish(lines);
        }
    }

    class StringEmailPublisher : EmailPublisher<string>
    {
        public override void Publish(List<string> publishables)
        {
            BuildBody(publishables);
            base.Publish(publishables);
        }

        protected override MailAddressCollection GetTo()
        {
            return new MailAddressCollection { new MailAddress("merxbauer@integri.cz", "Jarda Merxbauer") };
        }

        protected override MailAddressCollection GetCc()
        {
            return new MailAddressCollection { new MailAddress("studnicka@integri.cz", "Tomas Studnicka") };
        }

        protected override string GetSubject()
        {
            return "ACTION REQUIRED - Call By Name - Found Usage Failures";
        }

        protected override string GetBody()
        {
            return body;
        }

        private void BuildBody(List<string> publishables)
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("One or more projects you have been identified as to be a owner of have problems with Call By Name usage:\n\r");
            foreach (string cbn in publishables)
            {
                sb.Append("\t" + cbn.ToString());
            }
            body = sb.ToString();
        }

        private string body;
    }
}
