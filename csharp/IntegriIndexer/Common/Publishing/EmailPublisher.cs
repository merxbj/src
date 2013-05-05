using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;

namespace Integri.Common.Publishing
{
    public abstract class EmailPublisher<T> : IPublisher<T>
    {
        public EmailPublisher()
        {
            this.smtp = new SmtpClient
            {
                Host = "smtp.gmail.com",
                Port = 587,
                EnableSsl = true,
                DeliveryMethod = SmtpDeliveryMethod.Network,
                Credentials = new NetworkCredential(new MailAddress("merxbj@gmail.com").Address, "nabu#24ZOR$google"),
                Timeout = 20000
            };
        }

        public virtual void Publish(List<T> publishables)
        {
            SendEmail(new MailAddress("merxbj@gmail.com", "Jarda Merxbauer"),
                        GetTo(),
                        GetCc(),
                        GetSubject(),
                        GetBody());
        }

        protected abstract MailAddressCollection GetTo();
        protected abstract MailAddressCollection GetCc();
        protected abstract string GetSubject();
        protected abstract string GetBody();

        private void SendEmail(MailAddress from, MailAddressCollection to, MailAddressCollection cc, string subject, string body)
        {
            using (var message = new MailMessage()
            {
                From = from,
                Subject = subject,
                Body = body
            })
            {
                foreach (MailAddress singleTo in to)
                {
                    message.To.Add(singleTo);
                }

                foreach (MailAddress singleCc in cc)
                {
                    message.CC.Add(singleCc);
                }

                smtp.Send(message);
            }
        }

        private SmtpClient smtp;
        
    }
}
