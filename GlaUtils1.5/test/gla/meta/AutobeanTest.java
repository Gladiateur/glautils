package gla.meta;

import java.sql.SQLException;

public class AutobeanTest {
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		EntityWriter.autobeans();
//		new EntityWriter(null);
	}
}
