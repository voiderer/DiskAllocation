import org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class MyTreeCellRender extends DefaultTreeCellRenderer
{
    /**
     * ID 
     */
    private static final long   serialVersionUID    = 1L;

    /**
     * 重写父类DefaultTreeCellRenderer的方法 
     */
    static ImageIcon superIcon, leafIcon, branchIcon, counterIcon,stopIcon,emptyIcon,ramIcon,diskIcon;
    static Font tension;
    static {
        superIcon =new ImageIcon(MainClass.class.getResource("/image/super.png"));
        leafIcon =new ImageIcon(MainClass.class.getResource("/image/leaf.png"));
        branchIcon =new ImageIcon(MainClass.class.getResource("/image/branch.png"));
        counterIcon =new ImageIcon(MainClass.class.getResource("/image/counter.png"));
        stopIcon=new ImageIcon(MainClass.class.getResource("/image/stop.png"));
        emptyIcon=new ImageIcon(MainClass.class.getResource("/image/empty.png"));
        ramIcon=new ImageIcon(MainClass.class.getResource("/image/ram.png"));
        diskIcon=new ImageIcon(MainClass.class.getResource("/image/disk.png"));
        tension =new Font("Microsoft Yahei UI",Font.BOLD,16);
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus)
    {

        //执行父类原型操作  
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);

        String string=value.toString();
        String [] tokens= string.split("\\|");
        setText(tokens[1]);

        if (sel)
        {
            setForeground(getTextSelectionColor());
        }
        else
        {
            setForeground(getTextNonSelectionColor());
        }
        this.setFont(tension);

        //判断是哪个文本的节点设置对应的值（这里如果节点传入的是一个实体,则可以根据实体里面的一个类型属性来显示对应的图标）
        UnitType type=UnitType.readString(tokens[0]);
        if(type!=null) {
            switch (type){
                case root:
                    this.setIcon(superIcon);
                    break;
                case counter:
                    this.setIcon(counterIcon);
                    break;
                case branch:
                    this.setIcon(branchIcon);
                    break;
                case leaf:
                    this.setIcon(leafIcon);
                    break;
                case stop:
                    this.setIcon(stopIcon);
                    break;
                case empty:
                    this.setIcon(emptyIcon);
                    break;
                case ram:
                    this.setIcon(ramIcon);
                    break;
                case disk:
                    this.setIcon(diskIcon);
                    break;
            }

        }
        return this;
    }

}  