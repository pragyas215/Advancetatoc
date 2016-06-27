package advance;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.sql.Connection;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.JavascriptExecutor;


public class A {
public static void main(String args[]) throws SQLException, InterruptedException, IOException, JSONException{
	

	WebDriver driver=new FirefoxDriver();
	//assignment 1
	driver.get("http://10.0.1.86/");
	driver.get("http://10.0.1.86/tatoc");
	driver.get("http://10.0.1.86/tatoc/advanced/hover/menu");
	
	//JavascriptExecutor js = (JavascriptExecutor)driver;
	WebElement menu = driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/span[1]"));
	//WebElement menu = (WebElement)js.executeScript("document.getElementsByClassName('menutitle')[0].click();");
	
	
	Actions builder = new Actions(driver);
	builder.moveToElement(menu);
	WebElement submenu = driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/span[5]"));
	//WebElement submenu=(WebElement)js.executeScript("document.getElementsByClassName('menuitem')[2].click();");
	
	builder.moveToElement(submenu);
	builder.click().build().perform();
	
	// 2.database
	String symbol= driver.findElement(By.cssSelector("#symboldisplay")).getText();
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String id=null;
		String name=null;
		String passkey= null;   
		
      
		try{  
			
			 // Load Microsoft SQL Server JDBC driver
			
			Class.forName("com.mysql.jdbc.Driver");  
			  
			con=(Connection) DriverManager.getConnection( "jdbc:mysql://10.0.1.86:3306/tatoc","tatocuser","tatoc01"); 
		//prepare statement
		 pstmt= con.prepareStatement("select id from identity where symbol=?;");
		//storing statement
			pstmt.setString(1, symbol);
		//method which returns the requested information
				rs= pstmt.executeQuery();
			if( rs.next()){
				id=  rs.getString("id");
			}
			//System.out.println("hlo");
			int identity= Integer.parseInt(id);
			 rs.close();
			pstmt.close();
			pstmt= con.prepareStatement("select name,passkey from credentials where id=?;");
			pstmt.setInt(1, identity);
			rs= pstmt.executeQuery();
			
			if(((ResultSet) rs).next()){
				name= ((ResultSet) rs).getString("name");
				passkey= ((ResultSet) rs).getString("passkey");
			}
			rs.close();
			pstmt.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if(rs!=null){
				rs.close();
			}
			if(pstmt!=null){
				pstmt.close();
			}
			
			if(con!=null){
				con.close();
			}  
		}
		
		driver.findElement(By.cssSelector("#name")).sendKeys(name);
		driver.findElement(By.cssSelector("#passkey")).sendKeys(passkey);
		driver.findElement(By.cssSelector("#submit")).click();
		//Thread.sleep(2000);

		
		// assignment 3
		 JavascriptExecutor js = (JavascriptExecutor) driver;
			//play video
			 js .executeScript("player.play()");
			 Thread.sleep(40000);
			 driver.findElement(By.linkText("Proceed")).click();

			 String s1=driver.findElement(By.cssSelector("span#session_id")).getText();
			//System.out.println("s1") 
			String s2=s1.substring(12);
			System.out.println(s2);
			String s="http://10.0.1.86/tatoc/advanced/rest/service/token/";
			String s3=s.concat(s2);

			driver.get(s3);

			String s5=driver.findElement(By.cssSelector("html>body>pre")).getText();
			String result = s5.substring(10,42);
			System.out.println(result);
			


			// task 4
			//GET
			
		 driver.get("http://10.0.1.86/tatoc/advanced/rest");
		    String string=driver.findElement(By.id("session_id")).getText();
		    string=string.substring(12, string.length());
		   
		   
		    System.out.println(string);
		    
		    URL url = new URL("http://10.0.1.86/tatoc/advanced/rest/service/token/"+string);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			/*if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}  */


			BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println(response.toString());
			String ssss=new String(response);
			
			JSONObject obj=new JSONObject(ssss);
			ssss=(String) obj.get("token");

			System.out.println(ssss);
			
			//post
			
			URL url1 = new URL("http://10.0.1.86/tatoc/advanced/rest/service/register");
			HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
			

			conn1.setRequestMethod("POST");
			
			conn1.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "id="+string+"&signature="+ssss+"&allow_access=1";
			
			// Send post request
			conn1.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn1.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = conn1.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			
			conn1.disconnect();
			driver.findElement(By.cssSelector(".page a")).click();
			
		
			
			//Part 5
			FirefoxProfile profile = new FirefoxProfile();

			String path = "/home/pragyasingh/Downloads";
		    profile.setPreference("browser.download.folderList", 2);
		    profile.setPreference("browser.download.dir", path);
		    profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
		    profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/Gzip, application/csv, application/ris, text/csv,text/dat, image/png, application/pdf, text/html, text/plain, application/zip, application/x-zip, application/x-zip-compressed, applicaion/Gzip-archive, application/download, application/octet-stream");
		    profile.setPreference("browser.download.manager.showWhenStarting", false);
		    profile.setPreference("browser.download.manager.focusWhenStarting", false);  
		    profile.setPreference("browser.download.useDownloadDir", true);
		    profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		    profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
		    profile.setPreference("browser.download.manager.closeWhenDone", true);
		    profile.setPreference("browser.download.manager.showAlertOnComplete", false);
		    profile.setPreference("browser.download.manager.useWindow", false);
		    profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
		    profile.setPreference("pdfjs.disabled", true);

		    driver = new FirefoxDriver(profile);
		    driver.get("http://10.0.1.86/tatoc/advanced/file/handle");
		    driver.findElement(By.linkText("Download File")).click();
		  
		    
		   
		    BufferedReader br = null;
	        String strng=null, sCurrentLine;
	        try 
	        {
	            int i=0;
	            br = new BufferedReader(new FileReader("/home/pragyasingh/Downloads/file_handle_test.dat"));
	            while ((sCurrentLine = br.readLine()) != null) 
	            {
	                if(i==2)
	                    strng = sCurrentLine;
	                i++;
	            }
	        }
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        } 

		  String strng1 = strng.substring(11,strng.length());
	        driver.findElement(By.id("signature")).sendKeys(strng);
	        driver.findElement(By.className("submit")).click();
}
}
