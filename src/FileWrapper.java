import java.io.File;

public class FileWrapper {
    File source;
    public FileWrapper(File source){
        this.source = source;
    }
    public File getFile(){
        return source;
    }

    @Override
    public String toString() {
        return source.getName();
    }
}
