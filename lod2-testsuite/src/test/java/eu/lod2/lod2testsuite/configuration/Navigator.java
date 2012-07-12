package eu.lod2.lod2testsuite.configuration;

import org.openqa.selenium.NoSuchElementException;
import java.util.List;
import junit.framework.Assert;
import org.apache.bcel.generic.TABLESWITCH;
import eu.lod2.lod2testsuite.testcases.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import static org.testng.AssertJUnit.*;


/**
 *
 * @author Stefan Schurischuster
 */
public class Navigator {
    
    /**
     * This methods navigates through the main link menu of lod2-stack.
     * 
     * @param path 
     *          The path of the links that are to be clicked.
     */
    public void navigateTo(String[] path)  {
        int index = 0;
        
        WebElement link = null;
       
        while (index < path.length)  {
            String identifier = "//span[contains(.,'" +path[index]+ "')]"
                    + "[not(contains(@class,'caption'))]";
            if(index > 0)
                identifier = "//div[@class = 'v-menubar-popup'][last()]" +identifier;
            
            try  {
                link = TestCase.driver.findElement(
                                By.xpath(identifier));
            } catch(NoSuchElementException e)  {
                Assert.fail("Element not found: "+e.getMessage());
            }
            
            assertTrue("Could not find link: "+ link,link.isDisplayed());
            
            // This should pop up dropdowns.
            // And then move to the desired link
            
            TestCase.driverActions.moveToElement(link);
            //TestCase.selenium.mouseOver(identifier);
            
            // If it does not clicking does the job.
            //TestCase.driverActions.click();
            link.click();
            //TestCase.bf.bePatient(2000);
            //TestCase.selenium.click(identifier);
            
            index ++;
/*            
            if(index > 1)  {
                // First move to the first avialiable popup item.
                System.out.println("Try moving mouse");
                TestCase.bf.bePatient(1000);
                
                //Need selenium to move the cursor correctly 
                TestCase.selenium.mouseOver("//div[@class = 'v-menubar-popup'][last()]"
                        + "//span[contains(@class,'v-menubar-menuitem')][1]"
                        + "[not(contains(@class,'caption'))]");
                
                System.out.println("Did it move??");
                TestCase.bf.bePatient(5000);
                /*
                TestCase.driverActions.moveToElement(
                        TestCase.driver.findElement(
                        By.xpath("//div[@class = 'v-menubar-popup'][last()]"
                        + "//span[contains(@class,'v-menubar-menuitem')][1]"
                        + "[not(contains(@class,'caption'))]")));
                 * 
                 
                TestCase.bf.bePatient();  
                //TestCase.driverActions.moveToElement(link);
                TestCase.bf.bePatient(10000);
            }
             * 
             */
            
            TestCase.bf.bePatient();  
        } 
    }
    
    /**
     * @TODO
     * @param path 
     */
    public void navigateTo(List<String> path)  {
        navigateTo(path.toArray(new String[0]));
    }
}