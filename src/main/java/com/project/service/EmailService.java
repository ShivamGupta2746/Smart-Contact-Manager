package com.project.service;

import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@Service
public class EmailService {

	    public boolean sendEmail(String subject, String message, String to) {
	    	
	    		boolean f = false;
	    		
	    		String from ="codecheatwithshivam@gmail.com";

	        // Set the host of gmail
	        String host ="smtp.gmail.com";

	        // Set the properties
	        Properties properties = System.getProperties();
	        System.out.println("Properties :" + properties);

	        // set the important information to properties
	        properties.put("mail.smtp.host",host);
	        properties.put("mail.smtp.port","465");
	        properties.put("mail.smtp.ssl.enable","true");
	        properties.put("mail.smtp.auth","true");

	        // Step 1 get the session

	      Session session =  Session.getInstance(properties, new Authenticator(){
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication("codecheatwithshivam@gmail.com","agzanmibvfaydjbv");
	            }
	        });
	        session.setDebug(true);

	                // Compose the simple text in gmail
	        try {
	        MimeMessage m = new MimeMessage(session);

	            // from email
	           // m.setFrom(from);
	            m.setFrom(new InternetAddress(from));

	            // Set the reciepients to the message
	            m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));

	            // set the subject to message
	            m.setSubject(subject);

	            // set the message
	            m.setText(message);

	                    // Step 3  send the message by the transport class

	            Transport.send(m);

	            System.out.println("Send Successfully ..........");

	            f= true;
	            	
	        }catch (Exception e){
	            e.printStackTrace();
	        }


	        return f;
	    }
	    
	}



