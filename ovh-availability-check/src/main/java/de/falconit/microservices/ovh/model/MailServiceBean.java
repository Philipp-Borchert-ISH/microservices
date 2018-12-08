package de.falconit.microservices.ovh.model;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;

@Singleton
@Startup
public class MailServiceBean
{
    private static final Logger log = Logger.getLogger(MailServiceBean.class);

    private Session session = null;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @PostConstruct
    public void onInit()
    {
        try
        {
            InitialContext ic = new InitialContext();
            String snName = "java:jboss/mail/mailsession";

            session = (Session) ic.lookup(snName);
        } catch (NamingException e)
        {
            log.info("Mail subsystem is disabled");
        }

    }

    public void sendMessage(String subject, String body) throws Exception
    {

        String mailAddresses = "user@localhost";

        Message message = new MimeMessage(session);

        message.setFrom();
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(mailAddresses, false));
        message.setSubject(subject);
        message.setSentDate(new Date());
        message.setContent(body, "text/html; charset=UTF-8");

        log.info("sending mail to: " + mailAddresses);
        try
        {
            Transport.send(message);
        } catch (Exception e)
        {
            log.error("unable to send mail", e);
            throw new RuntimeException(e);
        }
    }

}
