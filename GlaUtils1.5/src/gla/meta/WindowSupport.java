/*
 *  WindowSupport.java 18/07/04
 */
package gla.meta;

import gla.debug.Debug;
import gla.debug.DebugImpl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.sat4j.exception.NullConnectionException;
import com.sat4j.user.FrameRoot;

/**
 * EntityWriter.autobeans() Window Support
 * 
 * @author Gladiateur
 * @since 1.5
 */
public class WindowSupport extends JFrame {
	private static final long serialVersionUID = 1L;
	private static Debug debugLogger = new DebugImpl();
	private static ConditionBean conditionBean = EntityWriter
			.getConditionBean();
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

	private static final String WINDOW_CAPTION = "GlaUtils-WindowSupport";

	// load lang.properties
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("gla/meta/lang");
		LANG_DBLABEL = bundle.getString("LANG_DBLABEL");
		LANG_DB_DEFAULT = bundle.getString("LANG_DB_DEFAULT");
		LANG_NAMELABEL = bundle.getString("LANG_NAMELABEL");
		LANG_NAME_DEFAULT = bundle.getString("LANG_NAME_DEFAULT");
		LANG_PASSWORDLABEL = bundle.getString("LANG_PASSWORDLABEL");
		LANG_PATHLABEL = bundle.getString("LANG_PATHLABEL");
		LANG_PATH_DEFAULT = bundle.getString("LANG_PATH_DEFAULT");
		LANG_TABLESLABEL = bundle.getString("LANG_TABLESLABEL");
		LANG_TABLES_DEFAULT = bundle.getString("LANG_TABLES_DEFAULT");
		LANG_BUTTON1 = bundle.getString("LANG_BUTTON1");
		LANG_BUTTON2 = bundle.getString("LANG_BUTTON2");
	}

	private static final String LANG_DBLABEL;
	private static final String LANG_DB_DEFAULT;
	private static final String LANG_NAMELABEL;
	private static final String LANG_NAME_DEFAULT;
	private static final String LANG_PASSWORDLABEL;
	private static final String LANG_PATHLABEL;
	private static final String LANG_PATH_DEFAULT;
	private static final String LANG_TABLESLABEL;
	private static final String LANG_TABLES_DEFAULT;
	private static final String LANG_BUTTON1;
	private static final String LANG_BUTTON2;

	private static void init() {
		jl_1 = new JLabel();
		Font font = new Font("黑体", Font.PLAIN, 14);
		jf_1 = new JFrame(WINDOW_CAPTION);
		jf_1.setSize(420, 280);
		// jf_1.setIconImage(image);
		jf_1.setResizable(false);

		dbLabel = new JLabel(LANG_DBLABEL);
		dbLabel.setBounds(30, 20, 60, 30);
		dbLabel.setFont(font);
		jl_1.add(dbLabel);
		dbName = new JTextField(LANG_DB_DEFAULT);
		dbName.setBounds(130, 20, 250, 30);
		dbName.setFont(font);
		jl_1.add(dbName);

		nameLabel = new JLabel(LANG_NAMELABEL);
		nameLabel.setBounds(30, 50, 60, 30);
		nameLabel.setFont(font);
		jl_1.add(nameLabel);
		username = new JTextField(LANG_NAME_DEFAULT);
		username.setBounds(130, 50, 250, 30);
		username.setFont(font);
		jl_1.add(username);

		passwordLabel = new JLabel(LANG_PASSWORDLABEL);
		passwordLabel.setBounds(30, 80, 60, 30);
		passwordLabel.setFont(font);
		jl_1.add(passwordLabel);
		password = new JPasswordField();
		password.setBounds(130, 80, 250, 30);
		jl_1.add(password);

		pathLabel = new JLabel(LANG_PATHLABEL);
		pathLabel.setBounds(30, 110, 120, 30);
		pathLabel.setFont(font);
		jl_1.add(pathLabel);
		path = new JTextField(LANG_PATH_DEFAULT);
		path.setBounds(130, 110, 250, 30);
		path.setFont(font);
		jl_1.add(path);

		tablesLabel = new JLabel(LANG_TABLESLABEL);
		tablesLabel.setBounds(30, 140, 60, 30);
		tablesLabel.setFont(font);
		jl_1.add(tablesLabel);
		tables = new JTextField(LANG_TABLES_DEFAULT);
		tables.setBounds(130, 140, 250, 30);
		tables.setFont(font);
		jl_1.add(tables);

		bt1 = new JButton(LANG_BUTTON1);
		bt1.setBounds(70, 200, 90, 25);
		bt1.setFont(font);
		jl_1.add(bt1);

		bt2 = new JButton(LANG_BUTTON2);
		bt2.setBounds(230, 200, 90, 25);
		bt2.setFont(font);
		jl_1.add(bt2);

		jf_1.add(jl_1);
		jf_1.setVisible(true);
		jf_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf_1.setLocation(350, 180);// location may be bug
	}

	public static void main(String[] args) {
		init();
		ActionListener bt2_ls = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		ActionListener bt1_ls = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String dbName0 = dbName.getText();
				char[] pwdChar = password.getPassword();
				String pwd0 = String.valueOf(pwdChar);
				String username0 = username.getText();
				String path0 = path.getText();
				String tables0 = tables.getText();
				debugLogger.debug("Debug-WindowSupport [dbName0 = " + dbName0
						+ "]");
				debugLogger.debug("Debug-WindowSupport [username0 = "
						+ username0 + "]");
				debugLogger.debug("Debug-WindowSupport [pwd0 = " + pwd0 + "]");
				debugLogger
						.debug("Debug-WindowSupport [path0 = " + path0 + "]");
				debugLogger.debug("Debug-WindowSupport [tables0 = " + tables0
						+ "]");
				conditionBean.setConstructorStyle(0);
				conditionBean.setTextPackage(EntityWriter.declarePackage(
						EntityWriter.formatToPackage("\\src\\" + path0))
						.toString());
				conditionBean.setTextNotes(EntityWriter.notes().toString());
				conditionBean.setPath("src\\" + path0);
				debugLogger.debug("Debug-actionPerformed path0 = " + path0);
				conditionBean.setTables(tables0);
				String[] allTables = Utils.getAllTables(new FrameRoot(),
						dbName0, username0, pwd0);
				conditionBean.setAllTables(allTables);
				EntityWriter.setConditionBean(conditionBean);
				try {
					String time = EntityWriter.doAutobeans();
					JOptionPane.showMessageDialog(null, "Time consuming:"
							+ time + "\nPlease refresh this project!",
							"GlaUtils-WindowSupport",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (NullConnectionException e) {
					JOptionPane
							.showMessageDialog(null,
									"NullConnectionException!",
									"GlaUtils-WindowSupport",
									JOptionPane.ERROR_MESSAGE);
					debugLogger
							.debug("Debug-actionPerformed [NullConnectionException]");
				} catch (SQLException e) {
					JOptionPane
							.showMessageDialog(null, "SQLException!",
									"GlaUtils-WindowSupport",
									JOptionPane.ERROR_MESSAGE);
					debugLogger.debug("Debug-actionPerformed [SQLException]");
				} finally {
					System.exit(0);
				}
			}
		};
		bt1.addActionListener(bt1_ls);
		bt2.addActionListener(bt2_ls);
	}
}