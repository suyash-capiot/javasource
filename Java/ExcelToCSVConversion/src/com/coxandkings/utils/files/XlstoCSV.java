package com.coxandkings.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.commons.lang3.StringEscapeUtils;


public class XlstoCSV 
{
	static void xls(File inputFile, /*File outputFile */ String outputFilePath) 
	{
        // For storing data into CSV files
        try 
        {
      

			// Get the workbook object for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(inputFile));
			
			// Get all sheets from the workbook
				for(int i=0;i<workbook.getNumberOfSheets();i++)
				{
					StringBuffer data = new StringBuffer();
					XSSFSheet sheet = workbook.getSheetAt(i);
					String csvFilename = outputFilePath + workbook.getSheetName(i) + ".csv";
					System.out.println("csv file name is " + csvFilename);
					Cell cell;
					Row row;
        
					File outputFile = new File(csvFilename);
					FileOutputStream fos = new FileOutputStream(outputFile);

					// Iterate through each rows from first sheet
					//Iterator<Row> rowIterator = sheet.iterator();
					int rowStart = Math.min(15, sheet.getFirstRowNum());
				    int rowEnd = Math.max(1400, sheet.getLastRowNum());
				    //System.out.println("Number of rows is from : " + rowStart + " to " + rowEnd);
						//while (rowIterator.hasNext()) 
					for(int rowNum = rowStart; rowNum < rowEnd; rowNum++) 	
				    {   //System.out.println("Iteration change");
							//row = rowIterator.next();
				            row = sheet.getRow(rowNum);
							// For each row, iterate through each columns
				            int lastColumn = Math.max(row.getLastCellNum(), 0);
				            //System.out.println("columns number is : " + lastColumn);
							//Iterator<Cell> cellIterator = row.cellIterator();
							//while (cellIterator.hasNext())
				            for (int cn = 0; cn < lastColumn; cn++) {
							   
								cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
								if (cell == null) {
									data.append(",");
									//System.out.println("Appended column");
						             // The spreadsheet is empty in this cell
						          } else {

							
								//cell = cellIterator.next();
                        
								switch (cell.getCellType()) 
								{    
									case Cell.CELL_TYPE_BOOLEAN:
									data.append(cell.getBooleanCellValue() + ",");
									//System.out.println("Cell type is boolean");
									break;
                                
									case Cell.CELL_TYPE_STRING:
									try
									{
										Date s;
										DateFormat df = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss");
										s=cell.getDateCellValue();
										
										data.append( df.format(s).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");    
										//System.out.println("csv is string ka date is " + i);
										break;
									}
									catch(Exception ex)
									{
										data.append(StringEscapeUtils.escapeCsv(cell.getStringCellValue()).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");                        	
										//System.out.println("Cell type is string" + StringEscapeUtils.escapeCsv(cell.getStringCellValue()).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
										break;
									}
									
									case Cell.CELL_TYPE_NUMERIC:
									try
									{
										Date s;
										DateFormat df = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss");
										if(DateUtil.isCellDateFormatted(cell))
										{ s=cell.getDateCellValue(); 
										//System.out.println("Cell type is numeric mein date");
										//System.out.println("csv is numeric " + df.format(s).trim());
										data.append(df.format(s).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
										}
										else
										{
											Double ts = cell.getNumericCellValue();
									    String tss = ts.toString();
										data.append( tss.trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
										//System.out.println("Cell type is numeric");
										}
										
										                        	
										break;
									}
									catch(Exception x)
									{   Double ts = cell.getNumericCellValue();
									    String tss = ts.toString();
										data.append( tss.trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
										//System.out.println("Cell type is string mein numeric");
										break;
									}

									case Cell.CELL_TYPE_BLANK:
									data.append(""+ ",");
									//System.out.println("Cell type is blank");
									break;
                        
									default:
									data.append(cell + ",");
									//System.out.println("Cell type is default");
								}
				            }
                         
							}
							data.append('\n');
						}

					fos.write(data.toString().getBytes());
					fos.close();
				}
        }
        catch (FileNotFoundException e) 
        {
                e.printStackTrace();
        }
        catch (IOException e) 
        {
                e.printStackTrace();
        }
        
    }

       
	static void xlshssf(File inputFile, /*File outputFile */ String outputFilePath) 
	{
        // For storing data into CSV files
        
        try 
        {
      
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(inputFile));
			
			// Get all sheets from the workbook
			for(int i=0;i<workbook.getNumberOfSheets();i++)
			{
				StringBuffer data = new StringBuffer();
				HSSFSheet sheet = workbook.getSheetAt(i);
				String csvFilename = outputFilePath + workbook.getSheetName(i) + ".csv";
				//System.out.println("csv file name is " + csvFilename);
				Cell cell;
				Row row;
        
				File outputFile = new File(csvFilename);
				FileOutputStream fos = new FileOutputStream(outputFile);

				// Iterate through each rows from first sheet
				//Iterator<Row> rowIterator = sheet.iterator();
				int rowStart = Math.min(15, sheet.getFirstRowNum());
			    int rowEnd = Math.max(1400, sheet.getLastRowNum());
			    //System.out.println("Number of rows is from : " + rowStart + " to " + rowEnd);
					//while (rowIterator.hasNext()) 
				for(int rowNum = rowStart; rowNum < rowEnd; rowNum++) 	
			    {   //System.out.println("Iteration change");
						//row = rowIterator.next();
			            row = sheet.getRow(rowNum);
						// For each row, iterate through each columns
			            int lastColumn = Math.max(row.getLastCellNum(), 0);
			            //System.out.println("columns number is : " + lastColumn);
						//Iterator<Cell> cellIterator = row.cellIterator();
						//while (cellIterator.hasNext())
			            for (int cn = 0; cn < lastColumn; cn++) {
						   
							cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);
							if (cell == null) {
								data.append(",");
								//System.out.println("Appended column");
					             // The spreadsheet is empty in this cell
					          } else {

						
							//cell = cellIterator.next();
                    
							switch (cell.getCellType()) 
							{    
								case Cell.CELL_TYPE_BOOLEAN:
								data.append(cell.getBooleanCellValue() + ",");
								//System.out.println("Cell type is boolean");
								break;
                            
								case Cell.CELL_TYPE_STRING:
								try
								{
									Date s;
									DateFormat df = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss");
									s=cell.getDateCellValue();
									
									data.append( df.format(s).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");    
									//System.out.println("csv is string ka date is " + i);
									break;
								}
								catch(Exception ex)
								{
									data.append(StringEscapeUtils.escapeCsv(cell.getStringCellValue()).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");                        	
									//System.out.println("Cell type is string" + StringEscapeUtils.escapeCsv(cell.getStringCellValue()).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
									break;
								}
								
								case Cell.CELL_TYPE_NUMERIC:
								try
								{
									Date s;
									DateFormat df = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss");
									if(DateUtil.isCellDateFormatted(cell))
									{ s=cell.getDateCellValue(); 
									//System.out.println("Cell type is numeric mein date");
									//System.out.println("csv is numeric " + df.format(s).trim());
									data.append(df.format(s).trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
									}
									else
									{
										Double ts = cell.getNumericCellValue();
								    String tss = ts.toString();
									data.append( tss.trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
									//System.out.println("Cell type is numeric");
									}
									
									                        	
									break;
								}
								catch(Exception x)
								{   Double ts = cell.getNumericCellValue();
								    String tss = ts.toString();
									data.append( tss.trim().replaceAll("\\r?\\n", "").replaceAll("\\s", " ") + ",");
									//System.out.println("Cell type is string mein numeric");
									break;
								}

								case Cell.CELL_TYPE_BLANK:
								data.append(""+ ",");
								//System.out.println("Cell type is blank");
								break;
                    
								default:
								data.append(cell + ",");
								//System.out.println("Cell type is default");
							}
			            }
                     
						}
						data.append('\n');
					}

				fos.write(data.toString().getBytes());
				fos.close();
			}
        }
        catch (FileNotFoundException e) 
        {
                e.printStackTrace();
        }
        catch (IOException e) 
        {
                e.printStackTrace();
        }
        
      }
	
	public static void convertXLSheetToCSV(String inputfile, String outputFilePath)
	{
		File inputFilePath = new File(inputfile);
		String ipFilename = inputFilePath.getName();
		System.out.println("Input file is " + ipFilename);
		try
		{
		if(ipFilename.toLowerCase().contains("xlsx"))
		{   System.out.println("In xls call ");
			xls(inputFilePath,outputFilePath);
		}
		else
			xlshssf(inputFilePath, outputFilePath);
		}
		catch(Exception e)
		{
			if(e.toString().startsWith("org.apache.poi.hssf.OldExcelFormatException"))
			{
				System.out.println("Call the excel old");
				JavaExcelRead old = new JavaExcelRead();
				old.convertOLDExcelToCSV(inputfile, outputFilePath);
			}
			else
			{   
				System.out.println("Didnt work yaar....exception is : " + e.toString());
			}
		}
	}

       
public static void main(String[] args) 
        {
                //String inputFile = "D://ToCSVConvert//Temp//160511_160426_G_Inventory_Rts.xlsx";
	String inputFile = args[0];
                //File outputFile = new File("D://ToCSVConvert//Temp//csv//output.csv");
                //String outputFilePath = "D://ToCSVConvert//Temp//csv//";
	String outputFilePath = args[1];
                convertXLSheetToCSV(inputFile, outputFilePath);
        }
}
