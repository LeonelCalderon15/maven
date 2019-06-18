package utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
	Properties pro;

	public ConfigReader()
	{
		try {
			File source = new File ("./configProperties");

			FileInputStream input = new FileInputStream(source);

			pro = new Properties();

			pro.load(input);

		} catch (Exception exp) {

			System.out.println("Exception is: ---"+exp.getMessage());
		} 
	}
	
	public String getAUT()
	{
		String path = pro.getProperty("AUT");
		return path;
	}
	
	public String getEmail()
	{
		String email = pro.getProperty("Email");
		return email;
	}
	
	public String getPassword()
	{
		String pass = pro.getProperty("Password");
		return pass;
	}
	public String getGridSettings()
	{
		String grid = pro.getProperty("Grid");
		return grid;
	}
	public String getGridHub()
	{
		String grid = pro.getProperty("GridHub");
		return grid;
	}
	
}
