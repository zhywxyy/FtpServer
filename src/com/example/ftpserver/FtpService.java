package com.example.ftpserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;

public class FtpService extends Service
{
	static FtpServer server = null;
	static String port;
	
	@SuppressLint("SdCardPath")
	private static String dirname;  
	// ftp�����������ļ�·��  
	private static String filename;
	
	@Override
	public void onCreate()
	{
		SharedPreferences sharedPreferences= getSharedPreferences("ftpserver", Activity.MODE_PRIVATE); 
		
		// ʹ��getString�������value��ע���2��������value��Ĭ��ֵ 
		port    = sharedPreferences.getString("port", "2221"); 
		dirname = sharedPreferences.getString("dirname", Environment.getExternalStorageDirectory().getPath() + "/ftp"); 
		System.out.println("dirname = " + dirname);
		filename = dirname + "/myusers.properties";
				
		/* Create Ftp Server */
    	FtpServerFactory serverFactory = new FtpServerFactory();
    	ListenerFactory factory = new ListenerFactory();

    	 // set the port of the listener
        factory.setPort(Integer.parseInt(port));

        // without SSL configuration
        /*// define SSL configuration
        SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setKeystoreFile(new File("src/test/resources/ftpserver.jks"));
        ssl.setKeystorePassword("password");

        // set the SSL configuration for the listener
        factory.setSslConfiguration(ssl.createSslConfiguration());
        factory.setImplicitSsl(true);*/

        // replace the default listener 
        serverFactory.addListener("default", factory.createListener());
        
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
                 
        InputStream is = getResources().openRawResource(R.raw.myusers);
        
        File file = new File(filename);
        
        File destfile = new File(dirname);
        if (!destfile.exists()) {
        	destfile.mkdirs();
        }
        
        inputstreamtofile(is, file);
        userManagerFactory.setFile(file);
        
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        
        // start the server
        server = serverFactory.createServer(); 
        
        try {
			server.start();
		} catch (FtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		} 
        
	}
	
	public void inputstreamtofile(InputStream ins, File file){
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		try {
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ins.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}