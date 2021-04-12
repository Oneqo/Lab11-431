import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class Utility {
    public static Class getClassFromPath(File selectedFile) {
        //Set variables
        Class c = null;
        boolean isDone = false;
        //Get file name
        StringBuilder name = new StringBuilder();
        String fileName = selectedFile.getName();
        name.append(fileName);
        name.delete(fileName.length()-6, fileName.length());
        //Get URL to the directory
        File parent = selectedFile.getParentFile();
        URL[] classDirectoryURLs = new URL[1];
        do{
            try{
                classDirectoryURLs[0] = parent.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(classDirectoryURLs);
                //Try to get a class object indicated by className
                c = Class.forName(name.toString(), true, loader);
                isDone = true;
            }catch (NoClassDefFoundError err){
                //Happens if URL is not valid, so we update the name, URL and try again
                if(parent.getParentFile()!=null){
                    name.insert(0,'.');
                    name.insert(0,parent.getName());
                    parent = parent.getParentFile();
                }else{
                    isDone = true;
                }
            }catch (Exception exc){
                System.err.println(exc);
            }
        }while(!isDone);
        return c;
    }

    public static String getCoverageFromSkeleton(String result) {
        StringBuilder coverage = new StringBuilder();
        String[] lines = result.split("\n");
        int startIndex = 0;
        //Get index of methods line
        for(int i = 0; i != lines.length; i++){
            coverage.append('\n');
            if(lines[i].equals("\\\\Methods:")){
                startIndex = i+1;
                break;
            }
        }

        for(; startIndex != lines.length; startIndex++){
            if(lines[startIndex].contains("(")){
               coverage.append("0\n");
            }else{
                coverage.append('\n');
            }
        }

        return coverage.toString();
    }
}
