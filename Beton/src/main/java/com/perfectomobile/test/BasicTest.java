package com.perfectomobile.test;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
	private boolean isFirstRun = true;
	protected ExcelDriver resultSheet;
	private HashMap<String,String> deviceProperties;
	protected DesiredCapabilities caps;
//	private static Init init; 
	
	
	/**
	 * @param caps
	 * @description constructor
	 */
	public BasicTest(DesiredCapabilities caps){
		this.caps = caps;
	}
	
	
	@DataProvider(name="factoryData", parallel=true)
	public static Object[][] factoryData() throws Exception { 		
		 ArrayList<HashMap<String,String>> listMap = new ArrayList<HashMap<String,String>>();
		 listMap = getCapabilitiesListMapFromExcel(Init.getInputDataSheet(), "devices");
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

	
	
	/**@ChangeLog: Changed from variable to context.
	 * @param testCycle
	 * @param context
	 */
	@BeforeClass
	public void beforeClass(ITestContext context){
		this.testCycle = context.getCurrentXmlTest().getParameter("testCycle");
	}
	
	
	@BeforeMethod
	public void beforeMethod(Method method) throws Exception{
		this.testName = method.getName();		
		
		if(!isFirstRun){
			return;
		}
		isFirstRun = false;
		System.out.println("Run started");
		
		if(this.caps.getCapability("deviceName") != null){
			if(this.caps.getCapability("deviceName").toString().toLowerCase().equals("chrome")){
				DesiredCapabilities dc = DesiredCapabilities.chrome();
				this.driver = new RemoteWebDriver(new URL(Init.getRemoteDriverURL()),dc);
				this.deviceDesc = "Chrome";
				return;
			}
		}
		this.driver = PerfectoUtils.getDriver(caps, Init.getDriverRetries(), Init.getRetryIntervalSeconds());
		//PerfectoUtils.initDevicePropertiesList(this.driver);
		if(this.driver != null){
			deviceProperties = PerfectoUtils.getDevicePropertiesList(driver);
			deviceDesc = getDeviceProperty("model");
			deviceDesc += " ";
			deviceDesc += getDeviceProperty("description");
//			this.deviceDesc = driver.getCapabilities().getCapability("model").toString();
//			this.deviceDesc += " ";
//			this.deviceDesc += driver.getCapabilities().getCapability("description").toString();
		}
		resultSheet = new ExcelDriver(Init.getOutputResultSheet(), this.deviceDesc, true);
	 	resultSheet.setResultColumn(this.testCycle, true);
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
	
	public String getDeviceProperty(String key){
		return deviceProperties.get(key);
	}
	
	public HashMap<String, String> getDeviceProperties(){
		return deviceProperties;
	}

	protected String reportFail(String expectedResult, String actualResult){
    	Reporter.log("Value is: " + actualResult + ", Should be: " + expectedResult);
    	String errorFile = PerfectoUtils.takeScreenshot(driver);
		Reporter.log("Error screenshot saved in file: " + errorFile);
		Reporter.log("<br> <img src=" + errorFile + ".png style=\"max-width:50%;max-height:50%\" /> <br>");
		return errorFile;
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
}
