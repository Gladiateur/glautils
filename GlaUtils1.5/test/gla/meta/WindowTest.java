/*
 *  .java 18/07/04
 */
package gla.meta;

import gla.debug.Debug;
import gla.debug.DebugImpl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * EntityWriter.autobeans() Window Support
 * 
 * @author Gladiateur
 */
public class WindowTest extends JFrame {
	private static final long serialVersionUID = 1L;
	private static Debug debugLogger = new DebugImpl();
	private static JButton bt1;
	private static JButton bt2;
	private static JLabel jl_1;
	private static JFrame jf_1; 
	
	private static JLabel dbLabel;
	private static JTextField dbName; 
	
	private static JLabel nameLabel;
	private static JTextField username;
	
	private static JLabel passwordLabel;
	private static JPasswordField password;
	
	private static JLabel pathLabel;
	private static JTextField path;
	
	private static JLabel tablesLabel;
	private static JTextField tables;

	public static void main(String[] args) {
		jl_1 = new JLabel();
		Font font = new Font("黑体", Font.PLAIN, 14);
		jf_1 = new JFrame("GlaUtils-EntityWriter");
		jf_1.setSize(430, 300);

		dbLabel = new JLabel("数据库");
		dbLabel.setBounds(50, 20, 60, 30);
		dbLabel.setFont(font);
		jl_1.add(dbLabel);
		dbName = new JTextField("MYSQL DATABASE NAME");
		dbName.setBounds(150, 20, 250, 30);
		dbName.setFont(font);
		jl_1.add(dbName);
		
		nameLabel = new JLabel("用户名");
		nameLabel.setBounds(50, 50, 60, 30);
		nameLabel.setFont(font);
		jl_1.add(nameLabel);
		username = new JTextField("root");
		username.setBounds(150, 50, 250, 30);
		username.setFont(font);
		jl_1.add(username);
		
		passwordLabel = new JLabel("密  码");
		passwordLabel.setBounds(50, 80, 60, 30);
		passwordLabel.setFont(font);
		jl_1.add(passwordLabel);
		password = new JPasswordField();
		password.setBounds(150, 80, 250, 30);
//		jtext2.setFont(font);
		jl_1.add(password);
		
		pathLabel = new JLabel("目标包路径");
		pathLabel.setBounds(50, 110, 120, 30);
		pathLabel.setFont(font);
		jl_1.add(pathLabel);
		path = new JTextField("com/glautils/domain");
		path.setBounds(150, 110, 250, 30);
		path.setFont(font);
		jl_1.add(path);
		
		tablesLabel = new JLabel("生成表");
		tablesLabel.setBounds(50, 140, 60, 30);
		tablesLabel.setFont(font);
		jl_1.add(tablesLabel);
		tables = new JTextField("[*|admin,user|!(admin,user)]");
		tables.setBounds(150, 140, 250, 30);
		tables.setFont(font);
		jl_1.add(tables);
		
		bt1 = new JButton("执行");
		bt1.setBounds(90, 200, 100, 30);
		bt1.setFont(font);
		jl_1.add(bt1);

		bt2 = new JButton("取消");
		bt2.setBounds(250, 200, 100, 30);
		bt2.setFont(font);
		jl_1.add(bt2);

		
		jf_1.add(jl_1);
		jf_1.setVisible(true);
		jf_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf_1.setLocation(350, 180);//location may be bug
		
		ActionListener bt2_ls = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(1);
				System.exit(0);
			}
		};
		
		ActionListener bt1_ls = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Cautious NullPointEx
				String dbName0 = dbName.getText();
				char[] pwdChar = password.getPassword();
				String pwd0 = String.valueOf(pwdChar);
				String username0 = username.getText();
				String path0 = path.getText();
				String tables0 = tables.getText();
				
				debugLogger.debug("Debug");
				System.out.println(dbName0);
				System.out.println(username0);
				System.out.println(pwd0);
				System.out.println(path0);
				System.out.println(tables0);
				
				//弹出对话框
				JOptionPane.showMessageDialog(null, "message", "title", JOptionPane.INFORMATION_MESSAGE);
				//退出虚拟机
				System.exit(0);
			}

		};
		bt1.addActionListener(bt1_ls);
		bt2.addActionListener(bt2_ls);
	}
}