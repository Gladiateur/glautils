package gla.meta;

import java.util.ResourceBundle;

public class LangPropertiesTest {
	public static void main(String[] args) {
		ResourceBundle bundle = ResourceBundle.getBundle("gla/meta/lang");
		String string = bundle.getString("LANG_NAMELABEL");
		System.out.println(string);
		//LANG_BUTTON2
		String string2 = bundle.getString("LANG_BUTTON2");
		System.out.println(string2);
		
	}
}
