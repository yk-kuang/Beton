package com.perfectomobile.test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Factory;
import org.testng.annotations.Parameters;
import org.testng.annotations.DataProvider;

import com.perfectomobile.dataDrivers.excelDriver.ExcelDriver;
import com.perfectomobile.utils.PerfectoUtils;
//import com.perfectomobile.selenium.util.EclipseConnector;

public abstract class BasicTest {
	
	protected RemoteWebDriver driver;
	protected ExcelDriver ed;
	protected String testName;
	protected String testCycle;
	protected String deviceDesc;
	//protected static String capabilitiesFilePath = "testData.xlsx";
	//protected static String capabilitiesFilePath;
	private boolean isFirstRun = true;
  
	protected DesiredCapabilities caps;
  
	protected int retryIntervalSeconds = 30;
	protected int driverRetries = 5;

  
	//@Factory(dataProvider="factoryData")
	public BasicTest(DesiredCapabilities caps){
		this.caps = caps;
	}
	
	@DataProvider(name="factoryData", parallel=true)
	public static Object[][] factoryData() throws Exception {
		
		 //ClassLoader classLoader = PerfectoUtils.class.getClassLoader();
		 //File inputWorkbook = new File(classLoader.getResource(capabilitiesFilePath).getFile());
		 		
		 ArrayList<HashMap<String,String>> listMap = new ArrayList<HashMap<String,String>>();
		 listMap = getCapabilitiesListMapFromExcel("C:\\Users\\AvnerG\\git\\Beton\\data\\testData.xlsx", "devices");
		 Object[][] s = PerfectoUtils.getCapabilitiesArray(listMap);

		 return s;
	}
  
	protected static ArrayList<HashMap<String,String>> getCapabilitiesListMapFromExcel(String filepath, String sheetName) throws Exception{
		
		// Open workbook
		ExcelDriver ed = new ExcelDriver(filepath, sheetName, false);
		
//		String absolutePath = file.getAbsolutePath();
//		ed.setWorkbook(absolutePath);
//		
//		// Open sheet
//		ed.setSheet(sheetName, false);
//					// Read the sheet into 2 dim (String)Object array.
//		// "3" is the number of columns to read.
		return ed.getDataWithHeadersAsHashMap();
	}

	

	@Parameters({"testCycle"})
	@ BeforeClass
	public void beforeClass(String testCycle){
		this.testCycle = testCycle;
	}
	
	
	@BeforeMethod
	public void beforeMethod(Method method) throws MalformedURLException{
		this.testName = method.getName();
		if(!isFirstRun){
			return;
		}
		isFirstRun = false;
		System.out.println("Run started");		
		
		if(this.caps.getCapability("deviceName") != null){
			if(this.caps.getCapability("deviceName").toString().toLowerCase().equals("chrome")){
				DesiredCapabilities dc = DesiredCapabilities.chrome();
				this.driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),dc);
				this.deviceDesc = "Chrome";
				return;
			}
		}
		this.driver = PerfectoUtils.getDriver(caps, this.driverRetries, retryIntervalSeconds);
		this.deviceDesc = driver.getCapabilities().getCapability("model").toString();
	}
	

	@AfterClass
	public void afterClass() {
		try {
			if(this.driver == null){
				return;
			}
			// Get Excel file path
//		  	String filePath = new File("").getAbsolutePath();
//		  	filePath += "testResults.xlsx";
//  	  
//		  	// Open workbook
//	  	  	ExcelDriver ed = new ExcelDriver();
//	  	  	ed.setWorkbook(filePath);
//	  	  	//ed.setSheet(this.deviceDesc, true);
//	    	this.ed.setAutoSize();
	        // Close the browser
	        driver.close();
	         
	        /*
	        // Download a pdf version of the execution report
	        PerfectoUtils.downloadReport(driver, "pdf", "C:\\temp\\report.pdf");
	        */
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
		driver.quit();
	}
	
	
	public void switchToContext(RemoteWebDriver driver, String context) {
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		Map<String,String> params = new HashMap<String,String>();
		params.put("name", context);
		executeMethod.execute(DriverCommand.SWITCH_TO_CONTEXT, params);
	}

	public String getCurrentContextHandle(RemoteWebDriver driver) {		  
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		String context =  (String) executeMethod.execute(DriverCommand.GET_CURRENT_CONTEXT_HANDLE, null);
		return context;
	}

	public List<String> getContextHandles(RemoteWebDriver driver) {		  
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		List<String> contexts =  (List<String>) executeMethod.execute(DriverCommand.GET_CONTEXT_HANDLES, null);
		return contexts;
	}
	
	/* 
	@Parameters({"testCycle"})
	@BeforeClass 
	public void beforeClass(String testCycle) throws Exception{
		if(!isFirstRun){
			return;
		}
		isFirstRun = false;
		System.out.println("Run started");
		this.testCycle = testCycle;
		
		
		if(this.caps.getCapability("deviceName") != null){
			if(this.caps.getCapability("deviceName").toString().toLowerCase().equals("chrome")){
				DesiredCapabilities dc = DesiredCapabilities.chrome();
				this.driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),dc);
				//this.deviceDesc = "Chrome";
				return;
			}
		}
		this.driver = PerfectoUtils.getDriver(caps, this.driverRetries, retryIntervalSeconds);
		if(this.driver == null){
			Assert.fail("Device not found: " + caps);
		}
	 }
 */
//	protected static Object[][] getCapabilitiesArrary(Object[][] s) throws Exception {
//	
//	int sSize = s.length;
//	Object [][] k = new Object[sSize][1];
//	
//	for(int i = 0; i < sSize; i++) {
//		k[i][0] = (Object)PerfectoUtils.getCapabilites((String)s[i][0],(String)s[i][1],(String)s[i][2],(String)s[i][3],(String)s[i][4],
//				(String)s[i][5],(String)s[i][6],(String)s[i][7],(String)s[i][8],(String)s[i][9],(String)s[i][10]);
//	}
//	return k;
//}

//protected static Object[][] generateArraysFromExcel(File file, String sheetName, int numOfCols) throws Exception{
//	
//	// Open workbook
//	ExcelDriver ed = new ExcelDriver();
//	
//	String absolutePath = file.getAbsolutePath();
//	ed.setWorkbook(absolutePath);
//	
//	// Open sheet
//	ed.setSheet(sheetName, false);
//				// Read the sheet into 2 dim (String)Object array.
//	// "3" is the number of columns to read.
//	Object[][] s = ed.getData(numOfCols);
//	return s;
//}
}