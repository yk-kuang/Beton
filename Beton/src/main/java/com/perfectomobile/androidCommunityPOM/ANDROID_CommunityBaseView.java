package com.perfectomobile.androidCommunityPOM;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.perfectomobile.utils.PerfectoUtils;
import com.perfectomobile.utils.PerfectoUtils.*;
import com.perfectomobile.utils.VisualDriverControl;


public class ANDROID_CommunityBaseView {
	
	RemoteWebDriver driver;
	
		
	// Page elements
	By actionBarTitle = By.id("android:id/action_bar_title");
	By dashboardTitle = By.xpath("//*[@text='Perfecto Mobile Community']");
	By txtSignIn = By.xpath("//*[@text='Sign In']");
	By closeSearch = By.id("android:id/up");
	
	
		
			
	By inputEmailAddress = By.id("com.bloomfire.android.perfecto:id/email_address");
	By inputPassword = By.id("com.bloomfire.android.perfecto:id/password");
	By btnDone = By.xpath("//*[@text='Done']");
	By btnSearch = By.id("com.bloomfire.android.perfecto:id/menu_search");
	By inputSearch = By.id("android:id/search_src_text");
	
	// Class constructor
	public ANDROID_CommunityBaseView(RemoteWebDriver driver){
		this.driver = driver;
					
	}
	// init to check for BasePage load and log-in if necessary	
	public ANDROID_CommunityBaseView login(String appUser, String appUserPwd){		
		//Check if login view is open
		try{				
			this.driver.findElement(txtSignIn);			
		}catch (Exception e){	//already signed in -- logout		
			openMenuDrawer().gotoSettings().logout();			
		}
		//enter credentials and submit
		try {			
			this.driver.findElement(inputEmailAddress).sendKeys(appUser);
			this.driver.findElement(inputPassword).sendKeys(appUserPwd);
			this.driver.findElement(btnDone).click();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Check for dashboard page load
		try{
			PerfectoUtils.fluentWait(actionBarTitle, driver, 10);
        	this.driver.findElements(dashboardTitle);
        	return new ANDROID_CommunityBaseView(this.driver);
        }
        catch(Exception e){
        	/*
        	String errorFile = PerfectoUtils.takeScreenshot(driver);
			Reporter.log("Error screenshot saved in file: " + errorFile);
			*/
        	throw new IllegalStateException();
        }
		
	}
		
	//Open menu drawer	
	public ANDROID_MenuPageView openMenuDrawer(){
		this.driver.findElement(actionBarTitle).click();
		return new ANDROID_MenuPageView(this.driver);
	}
	
	// click top menu search button 
	public ANDROID_MenuPageView clickSearchButton(){
		this.driver.findElement(btnSearch).click();
		return new ANDROID_MenuPageView(this.driver);
	}
	
	// close the search field 
	public ANDROID_MenuPageView closeSearch(){
		this.driver.findElement(closeSearch).click();
//		if(this.driver.findElement(closeSearch).isDisplayed()){ //click again if search bar has only cleared
//			this.driver.findElement(closeSearch).click();
//		}
		return new ANDROID_MenuPageView(this.driver);
	}
	
	//input text into exposed search field after clicking on the search button
	public ANDROID_SearchResultsView inputSearchText(String q){
		this.driver.findElement(btnSearch).sendKeys(q);
		// click keyboard enter key
		VisualDriverControl.clickByTextOffset(this.driver, "DEL", LabelPosition.BELOW, "10%");

		return new ANDROID_SearchResultsView(this.driver);
	}

	
}
