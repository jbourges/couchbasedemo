package coveotest;

import java.io.BufferedReader;  
import java.io.BufferedWriter;
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.File;
import java.io.IOException;  
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.time.Duration;

/**
 * This class can parse an input text file an split the csv from the json data
 * All the input can be injected into the couchbase server
 *
 * java -cp coveotest.jar coveotest.ExtractJSONFile <my input file> <my output file> <my output file json>
 * example : java coveotest.ExtractJSONFile "./data/searches1.csv" "./data/result1.csv" "./data/result1.json"
 *
 * Takes ~1 second/Mega
 *  
 */
public final class ExtractJSONFile
{

	private static final double MEG = (Math.pow(1024, 2));
	
	public enum FieldType {
		All,
		Json,
		Csv
	}
	
    public static void main(final String[] args) {
		long start = System.currentTimeMillis();
		int indexLine=0;
		String line = "";  
		String splitBy = ",";  	
		boolean initHeader= true;
		String[] headerInput=null;
		String[] headerOutput=null;
		String[] headerOutputJSON=null;		
		boolean[] isJSON=null;
		if (args.length!=3)
			System.exit(0);
		try   
		{  
			BufferedReader br = new BufferedReader(new FileReader(args[0])); 
			File fileToDelete = new File(args[1]);
			fileToDelete.delete();
			fileToDelete = new File(args[2]);
			fileToDelete.delete();
			File fileToWrite = new File(args[1]);
			FileWriter writer = new FileWriter(fileToWrite);
			File fileToWriteJSON = new File(args[2]);
			FileWriter writerJSON = new FileWriter(fileToWriteJSON);
			while ((line = br.readLine()) != null) 
			{  
				String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				if (initHeader)
				{
					if (headerInput==null)
					{
						headerInput = splitted;
						isJSON = new boolean[headerInput.length];
					}
					else
					{
						ArrayList<String> selectedColumns = new ArrayList<>();
						ArrayList<String> selectedColumnsJSON = new ArrayList<>();
						
						for (int i=0;i<splitted.length;i++)
						{	
							isJSON[i]=splitted[i].startsWith("\"{");
							if (isJSON[i])
								selectedColumnsJSON.add(headerInput[i]);
							else
								selectedColumns.add(headerInput[i]);
						}
						
						headerOutput=selectedColumns.toArray(new String[selectedColumns.size()]);
						headerOutputJSON=selectedColumnsJSON.toArray(new String[selectedColumnsJSON.size()]);	
						
						outputLine(writer,headerOutput,isJSON,headerOutputJSON,FieldType.All);
						outputLine(writer,splitted,isJSON,headerOutputJSON,FieldType.Csv);
						outputLine(writerJSON,splitted,isJSON,headerOutputJSON,FieldType.Json);
						initHeader = false;
					}
				}
				else
				{
					outputLine(writer,splitted,isJSON,headerOutputJSON,FieldType.Csv);
					outputLine(writerJSON,splitted,isJSON,headerOutputJSON,FieldType.Json);
				}
				indexLine++;
			}  
			writer.close();
			writerJSON.close();
		}   
		catch (IOException e)   
		{  
		    System.err.println("indexLine="+indexLine);
			e.printStackTrace();  
		} 
		long end = System.currentTimeMillis();
		System.err.println((end - start) / 1000f + " seconds");
        System.exit(0);
    }
	
	static public void outputLine(FileWriter writer, String[] fields, boolean[] isJson, String[] headerJSON, FieldType fieldType)
	{
		try
		{
			BufferedWriter bufferedWriter = new BufferedWriter(writer, (int)MEG);
			int countJSON = 0;
			for (int i=0;i<fields.length;i++)
			{
				if (isJson[i] && fieldType==FieldType.Json)
				{
					if (countJSON==0)
					{
						writer.write("{");
						writer.write("\"ids\":"+fields[0]+",");
						writer.write("\"datetime\":"+fields[1]+",");
					}
					String f = fields[i].replaceAll("\"\"","\"");		
					writer.write("\""+headerJSON[countJSON]+"\":");
					if (f.length()>1)
						writer.write(f.substring(1,f.length()-1) );
					else
						writer.write("\"\"");
					if (countJSON!=headerJSON.length-1)
						writer.write(",");	
					else
						writer.write("}");
					countJSON++;
				} else if (!isJson[i] && fieldType==FieldType.Csv)
				{
					writer.write(fields[i]);
					if (i!=fields.length-1)
						writer.write(',');	
				} else if (fieldType==FieldType.All)
				{
					if(i==0)
					{
						writer.write("ids");
					}
					else
					{
						writer.write(fields[i]);	
					}
				
					if (i!=fields.length-1)
						writer.write(',');
				}
			}
			writer.write(System.lineSeparator());
			writer.flush();			
		}
		catch(IOException e)   
		{
			e.printStackTrace();  
		}
	}
}







