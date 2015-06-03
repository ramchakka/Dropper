package self.main;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;



import self.util.MetadataReader;
import self.util.WalkFileTreeCopy;
 
public class App
{
 
	
public static void main(String [] args) {
        
		System.out.println("App.main started");        
        //File jpegImageFile = new File("C:\\MyWork\\JPEGTestData\\D1\\IMG_0469.jpg");
        //MetadataReader mde = new MetadataReader();
        //mde.metadataExample(jpegImageFile);
		try {
			WalkFileTreeCopy wst = new WalkFileTreeCopy();
			wst.WalkTheDirTree();
			System.out.println("..Thankyou!");
		} catch (Exception io) {
			io.printStackTrace();
		}
		
        
       }
 
}
