import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;

public class CoverageToolGUI {
    private JButton openDirectory;
    private JTextArea directoryTextBox;
    private JPanel mainPanel;
    private JTextArea skeletonViewport;
    private JList directoryViewport;
    private JScrollPane skeletonScrollPane;
    private JFrame frame;
    private

    CoverageToolGUI(){
        frame = new JFrame();
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800,600));
        frame.setVisible(true);

        skeletonScrollPane.setRowHeader(new JViewport());

        openDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showDialog(frame, "Choose directory");
                File pathToDirectory = fc.getSelectedFile();
                directoryTextBox.setText(pathToDirectory.toString());
                FileFilter classFilter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(".class");
                    }
                };
                File[] classFiles = pathToDirectory.listFiles(classFilter);
                Object[] displayableFileObjs = new Object[classFiles.length];
                for(int i = 0; i != classFiles.length; i++){
                    displayableFileObjs[i] = new FileWrapper(classFiles[i]);
                }
                directoryViewport.setListData(displayableFileObjs);
            }
        });
        directoryViewport.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    String result = "No Selection";
                    File pathToFile = ((FileWrapper) directoryViewport.getSelectedValue()).getFile();
                    Class classObj = Utility.getClassFromPath(pathToFile);
                    SkeletonBuilder sk = new SkeletonBuilder();
                    try{
                         result = sk.getSkeleton(classObj);
                    }catch (ClassNotFoundException err){
                        //do nothing
                    }
                    skeletonViewport.setText(result);
                }
            }
        });
    }

    public static void main(String[] args){
        CoverageToolGUI app = new CoverageToolGUI();
    }
}
