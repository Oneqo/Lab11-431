import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
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
    private JTextArea coverageStatistics;

    CoverageToolGUI(){
        frame = new JFrame();
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700,450));
        frame.setVisible(true);

        //create rowHeader
        String coverage = new String();
        coverageStatistics = new JTextArea();
        coverageStatistics.setBackground(Color.LIGHT_GRAY);
        coverageStatistics.setEditable(false);
        coverageStatistics.setText(coverage);

        JViewport vw = new JViewport();
        vw.setView(coverageStatistics);
        skeletonScrollPane.setRowHeader(vw);

        openDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fc.showDialog(frame, "Choose directory");
                if(result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION)
                    return;
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
                    String result = "No Selection", parsedCoverage;
                    FileWrapper fr = (FileWrapper) directoryViewport.getSelectedValue();
                    if(fr == null)
                        return;
                    File pathToFile = fr.getFile();
                    Class classObj = Utility.getClassFromPath(pathToFile);
                    SkeletonBuilder sk = new SkeletonBuilder();
                    try{
                         result = sk.getSkeleton(classObj);
                    }catch (ClassNotFoundException err){
                        //do nothing
                    }
                    parsedCoverage = Utility.getCoverageFromSkeleton(result);
                    skeletonViewport.setText(result);
                    coverageStatistics.setText(parsedCoverage);
                    skeletonScrollPane.getViewport().updateUI();
                }
            }
        });
    }

    public static void main(String[] args){
        CoverageToolGUI app = new CoverageToolGUI();
    }
}
