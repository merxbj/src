using Integri.Common.Publishing;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Text;

namespace Integri.CallByNameChecker
{
    class CheckFailurePublisher : EmailPublisher<CallByName>
    {
        public override void Publish(List<CallByName> publishables)
        {
            if (publishables.Count > 0)
            {
                BuildBody(publishables);
                base.Publish(publishables);
            }
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

        private void BuildBody(List<CallByName> publishables)
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendLine("One or more projects you have been identified as to be a owner of have problems with Call By Name usage:");
            foreach (CallByName cbn in publishables)
            {
                sb.AppendLine("\t" + cbn.ToString());
            }
            body = sb.ToString();
        }

        private string body;
    }
}
