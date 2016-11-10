import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.fonts.DefaultMacFontPolicy;
import org.jvnet.substance.skin.FieldOfWheatSkin;
import org.jvnet.substance.title.ClassicTitlePainter;
import org.jvnet.substance.watermark.SubstanceBubblesWatermark;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Random;
import java.util.Vector;

/**
 * Created by yutao on 16-6-26.
 */
public class MainClass extends JFrame {
    private static final int capacity = 7;//定义一个块中最大的数据个数
    private static Random ran;//用于产生随机数随机
    private static Vector<Vector<Integer>> disk;//用于模拟成组链接的二位向量结构
    private static Vector<Integer> memory;//用于表示内存中用于保存超级快信息的模块
    /**
     * 此类的静态初始化函数在这个函数里实现从文件中读取数据
     */
    static {
        String filePath = System.getProperty("user.dir") + "/.data";
        File file = new File(filePath);
        ran = new Random();
        try {
            if (!file.exists()) {
                disk = new Vector<>();
                memory=new Vector<>();
                memory.add(1);
                memory.add(0);
                disk.add(memory);
                file.createNewFile();
            } else {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath));
                disk = (Vector<Vector<Integer>>) inputStream.readObject();
                memory=disk.elementAt(0);
                inputStream.close();
            }
        } catch (IOException e) {
            System.out.println("数据初始化失败");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private JTextField textField;
    private JPanel displayPanel;
    private JTextPane historyList;
    private JTree memoryTree;
    private DefaultMutableTreeNode memoryRoot;

    private MainClass() {
        this.setSize(800, 330);
        this.setTitle("Unix空闲盘块成组链接管理演示程序");
        FlowLayout flowLayout;
        displayPanel =new JPanel(flowLayout=new FlowLayout());
        flowLayout.setAlignment(FlowLayout.LEFT);
        JScrollPane scroll;
        this.add(scroll=new JScrollPane(displayPanel), BorderLayout.CENTER);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JButton button;
        JPanel panel = new JPanel(new FlowLayout());
        button = new JButton("分配");
        button.addActionListener(e -> onAllocate());
        panel.add(button);
        button = new JButton("回收");
        button.addActionListener(e -> onDeallocate());
        panel.add(button);
        panel.add(textField = new JTextField(8));
        textField.setText("" + random());
        this.add(panel, BorderLayout.NORTH);
        historyList=new JTextPane();
        appendLn("此处显示历史信息",Color.BLUE);
        this.add(scroll=new JScrollPane(historyList),BorderLayout.EAST);
        this.add(new JScrollPane(memoryTree =new JTree(memoryRoot =new DefaultMutableTreeNode(new MemoryUnit(UnitType.ram,"  内存     ")))),BorderLayout.WEST);
        memoryTree.setCellRenderer(new MyTreeCellRender());
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                save();
                dispose();
            }
        });
        updateDisplay();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String arg[]) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        SubstanceLookAndFeel.setSkin(new FieldOfWheatSkin());
        SubstanceLookAndFeel.setCurrentTitlePainter(new ClassicTitlePainter());
        SubstanceLookAndFeel.setCurrentWatermark(new SubstanceBubblesWatermark());
        SubstanceLookAndFeel.setFontPolicy(new DefaultMacFontPolicy());
        UIManager.put("Button.font",new Font("Microsoft Yahei UI",Font.BOLD,16));
        UIManager.put("List.font",new Font("Microsoft Yahei UI",0,14));
        UIManager.put("TextPane.font",new Font("Microsoft Yahei UI",Font.BOLD,16));//界面的初始化设置
        new MainClass();
    }

    /**
     * 此函数用于将数据序列化保存在文件中
     */
    private static void save() {
        String filePath = System.getProperty("user.dir") + "/.data";
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath));
            outputStream.writeObject(disk);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于将回收的盘块的编号加入成组链接数据结构中
     * @param tag 表示盘块号
     */
    private static void add(int tag) {
        if (memory.elementAt(0) == capacity) {
            disk.add(0, memory = new Vector<>());
            memory.add(0);
        }
        memory.set(0,memory.elementAt(0)+1);//计数器加一
        memory.add(memory.elementAt(0),tag);//将新之计入块中
    }

    /**
     * 用于从成组链接数据结构中查询空闲的盘块，返回分配的盘块号
     * @return 返回分配的盘块号，如果为0表示没有剩余的盘块。
     */
    private static int remove(){
        int i;
        if(memory.elementAt(0)>1){
            memory.set(0,memory.elementAt(0)-1);
            return memory.remove(memory.elementAt(0)+1);

        }else {
            i=memory.elementAt(1);
            if (i!=0){
                disk.remove(0);
                memory=disk.elementAt(0);
            }
            return i;
        }
    }

    private static int random() {
        return ran.nextInt(1000);
    }

    private void onDeallocate() {
        String string = textField.getText();
        Integer tag;
        try {
            tag = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            appendLn("输入整数格式错误", Color.black);
            return;
        }
        textField.setText("" + random());
        for (Vector<Integer> v : disk) {
            v.stream().filter(tag::equals).forEach(i -> appendLn("盘号重复", Color.black));
        }//用于检测盘块号是否已经存在
        add(tag);
        updateDisplay();
        appendLn("成功回收:   " + string, Color.black);
    }

    private void onAllocate() {
        int i=remove();
        if(i!=0){
            appendLn("成功分配:   "+i,Color.black);
            updateDisplay();
        }else {
            appendLn("磁盘已满",Color.black);
        }

    }

    /**
     * 在成组链接数据结构改变的时候更新界面显示的内容
     */
    private void updateDisplay() {
        int i;
        displayPanel.removeAll();
        Integer last = 0;
        DefaultMutableTreeNode root;
        for (Vector<Integer> v : disk) {
            if (last == 0) {
                root = new DefaultMutableTreeNode(new MemoryUnit(UnitType.disk, "超级块"));
            } else {
                root = new DefaultMutableTreeNode(new MemoryUnit(UnitType.disk, "盘块" + last));
            }
            JTree tree = new JTree(root);
            tree.setCellRenderer(new MyTreeCellRender());
            root.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.counter, "计数器" + v.elementAt(0))));
            last = v.elementAt(1);
            if (v.elementAt(1) > 0) {
                root.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.branch, "地址" + v.elementAt(1))));
            } else {
                root.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.stop, "块号" + v.elementAt(1))));
            }
            for (i = 2; i <= v.elementAt(0); i++) {
                root.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.leaf, "" + v.elementAt(i))));
            }
            for (; i <= capacity; i++) {
                root.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.empty, "空")));
            }

            tree.expandPath(new TreePath(root));
            displayPanel.add(tree);
            displayPanel.updateUI();
        }
        memoryRoot.removeAllChildren();
        memoryRoot.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.counter, "计数器" + memory.elementAt(0))));
        if (memory.elementAt(1) == 0) {
            memoryRoot.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.stop, "" + 0)));
        } else {
            memoryRoot.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.branch, "" + memory.elementAt(1))));
        }
        for (i = 2; i <= memory.elementAt(0); i++) {
            memoryRoot.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.leaf, "" + memory.elementAt(i))));
        }
        for (; i <= capacity; i++) {
            memoryRoot.add(new DefaultMutableTreeNode(new MemoryUnit(UnitType.empty, "空")));
        }
        memoryTree.expandPath(new TreePath(memoryRoot));
        memoryTree.updateUI();

    }

    /**
     * 用于在历史记录中添加新增的操作信息
     * @param string 新增的字符串
     * @param color 显示字符串的颜色
     */
    private void appendLn(String string, Color color) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(attributeSet, 14);
        StyleConstants.setForeground(attributeSet, color);
        Document docs = historyList.getDocument();
        try {
            docs.insertString(docs.getLength(), string + "\n", attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        historyList.setCaretPosition(docs.getLength());
    }

}

