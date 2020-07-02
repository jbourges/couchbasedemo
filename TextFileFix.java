package coveotest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class can parse an input text file and replace all occurences of a string by another one
 *
 * java -cp coveotest.jar coveotest.TextFileFix <my input file> <string_to_search> <string_replacement>
 * example : java coveotest.TextFileFix "./highcharts/country_device_template.html" "##data##" "{ x: 1,  y: 5.58}"
 *
 */  
public class TextFileFix
{   
    static void modifyFile(String filePath, String oldString, String newString)
    {
        File fileToBeModified = new File(filePath);
         
        String oldContent = "";
         
        BufferedReader reader = null;
         
        FileWriter writer = null;
         
        try
        {
            reader = new BufferedReader(new FileReader(fileToBeModified));
             
            String line = reader.readLine();
             
            while (line != null) 
            {
                oldContent = oldContent + line + System.lineSeparator();
                 
                line = reader.readLine();
            }
             
            String newContent = oldContent.replaceAll(oldString, newString);
             
            writer = new FileWriter(fileToBeModified);
             
            writer.write(newContent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                reader.close();                 
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
}