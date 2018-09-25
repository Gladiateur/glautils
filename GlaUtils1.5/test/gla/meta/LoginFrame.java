package gla.meta;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static JButton bt1; // 确定按钮
	private static JButton bt2; // 取消按钮
	private static JLabel jl_1; // 面板
	private static JFrame jf_1; // 登陆窗口
	private static JTextField jtext1; // 用户名
	private static JPasswordField jtext2; // 密码
	private static JLabel j1_admin;
	private static JLabel j1_password;

	public static void main(String[] args) {
		jl_1 = new JLabel();
		Font font = new Font("黑体", Font.PLAIN, 20); // 设置字体
		jf_1 = new JFrame("登陆界面");
		jf_1.setSize(450, 400);

		j1_password = new JLabel("密码");
		j1_password.setBounds(20, 120, 60, 50);
		j1_password.setFont(font);
		jl_1.add(j1_password);

		j1_admin = new JLabel("用户名");
		j1_admin.setBounds(20, 50, 60, 50);
		j1_admin.setFont(font);
		jl_1.add(j1_admin);

		bt1 = new JButton("确定");
		bt1.setBounds(90, 250, 100, 50);
		bt1.setFont(font);
		jl_1.add(bt1);

		bt2 = new JButton("取消");
		bt2.setBounds(250, 250, 100, 50);
		bt2.setFont(font);
		jl_1.add(bt2);

		jtext1 = new JTextField("root");
		jtext1.setBounds(150, 50, 250, 50);
		jtext1.setFont(font);
		jl_1.add(jtext1);

		jtext2 = new JPasswordField("123456");
		jtext2.setBounds(150, 120, 250, 50);
//		jtext2.setFont(font);
		jl_1.add(jtext2);

		jf_1.add(jl_1);
		jf_1.setVisible(true);
		jf_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf_1.setLocation(300, 400);

		ActionListener bt1_ls = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String admin = jtext1.getText();
				char[] password = jtext2.getPassword();
				String str = String.valueOf(password);

				if (admin.equals("root") && str.equals("123456")) {
					JOptionPane.showMessageDialog(jf_1, "连接成功");
				} else
					JOptionPane.showMessageDialog(jf_1, "连接失败");
			}

		};
		bt1.addActionListener(bt1_ls);
	}
}