package com.ivaa.naturalint;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Created by ivaa on 9/30/2015.
 */
public class Main {
    private static Properties properties;

    public static void main(String[] args) throws InterruptedException, IOException
    {
        //read properties file
        ReadProperties();

        int threadsNumber = 0;
        final String fileName = "output.txt";

        //get threadsNumber from properties
        try
        {
            threadsNumber = Integer.parseInt(properties.getProperty("threadsNumber"));
            if (threadsNumber > 10)
            {
                System.out.println("Threads number is too big.");
                System.exit(0);
            }
        }
        catch (Exception e)
        {
            System.out.println("Cannot parse property threadsNumber");
        }

        OutputWithThreads output = new OutputWithThreads(fileName, threadsNumber);

        //run threads with shared file writer between all of them
        output.RunThreadsOneFileOpen();

        //if you want to run threads with open/close opreations for each one, please use this one:
        //output.RunThreadsManyFileOpens();
    }

    private static void ReadProperties()
    {
        properties = new Properties();
        InputStream inputStream = null;

        try
        {
            URL url = Main.class.getClassLoader().getResource("config.properties");
            if (url != null)
            {
                String filePath = URLDecoder.decode(url.getPath(), "UTF-8");
                File file = new File(filePath);
                inputStream = new FileInputStream(filePath);
                properties.load(inputStream);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
