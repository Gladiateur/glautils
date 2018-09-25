package gla.meta;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class Frame1 {
    public static void main(String[] args) {
        new FirstFrame().setVisible(true);
    }
}
 
class  FirstFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	JTextField name;
    public FirstFrame() {
        super("窗体之间数据传递");
        this.setSize(330, 200);
        this.setLayout(null);
        this.setLocation(100, 50);
        JLabel a=new JLabel("姓名:");
        name=new JTextField("姓   名",10);
        //按钮
        JButton b=new JButton("传递");
        //添加按钮事件
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                new SecondFrame(FirstFrame.this).setVisible(true);
                FirstFrame.this.setVisible(false);
            }
        });
        JPanel pane=new JPanel();
        pane.add(a);
        pane.add(name);
        pane.add(b);
        setContentPane(pane);
    }
}
class  SecondFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	public SecondFrame(FirstFrame frm) {
        super("显示数据");
        this.setSize(330, 200);
        this.setLayout(null);
        this.setLocation(100, 50);
        JLabel a=new JLabel(frm.name.getText(),10);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel pane=new JPanel();
        pane.add(a);
        setContentPane(pane);
    }
}