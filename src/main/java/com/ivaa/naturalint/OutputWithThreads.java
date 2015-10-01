package com.ivaa.naturalint;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ivaa on 10/1/2015.
 */
public class OutputWithThreads
{
    private String fileName;
    private int threadsNumber;

    public OutputWithThreads(final String _fileName, int _threadsNumber) throws IOException, InterruptedException
    {
        fileName = _fileName;
        threadsNumber = _threadsNumber;
        PrepareFile();

    }

    //shared writer between all threads
    public void RunThreadsOneFileOpen() throws InterruptedException, IOException
    {
        OutputStream stream = new FileOutputStream(fileName, true);
        final PrintWriter writer = new PrintWriter(stream);

        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);
        for (int i=0; i<threadsNumber; i++)
        {
            executorService.submit(
                    new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                PrintValues(true, writer);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        writer.close();
    }

    //each thread opens and closes file every write operation
    public void RunThreadsManyFileOpens()
    {
        for (int i=0; i<threadsNumber; i++)
        {
            Thread newThread = new Thread(
                    new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                PrintValues(false, null);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            newThread.start();
        }
    }


    private void PrintValues(boolean isOneFileStream, PrintWriter writer) throws IOException
    {
        //get date in defined format
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        Date dt = new Date();

        //define strings
        String prefix = String.format("[%s] ", Thread.currentThread().getName());
        String datetime = prefix + dateFormat.format(dt);
        String helloWorld = prefix + "HelloWorld";

        //print to console
        System.out.println(helloWorld);
        System.out.println(datetime);

        if (isOneFileStream)
            WriteOneFileOpen(writer, helloWorld, datetime);
        else
            WriteManyFileOpens(fileName, helloWorld, datetime);
    }

    private synchronized void WriteOneFileOpen(PrintWriter writer, String val1, String val2) throws IOException
    {
        writer.println(val1);
        writer.println(val2);
    }

    private synchronized void WriteManyFileOpens(String fileName, String val1, String val2) throws IOException
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(new FileOutputStream(
                    new File(fileName),
                    true));
            writer.println(val1);
            writer.println(val2);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally
        {
            //close file
            if (writer != null)
                writer.close();
        }
    }

    //check file exist and clean it
    private boolean PrepareFile()
    {
        File file = null;
        OutputStream stream = null;
        try
        {
            file = new File(fileName);
            file.getCanonicalPath();
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
            return true;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return false;
    }
}
