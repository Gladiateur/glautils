package gla.meta;

//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
import java.sql.SQLException;

import gla.annotation.AutobeansEntityConfig;
import gla.annotation.AutobeansMySQLConfig;
import gla.meta.EntityWriter;

@AutobeansMySQLConfig(dbName = "p2p", password = "123456", username = "root")
@AutobeansEntityConfig(path = "pp/test/vo", tables = "*")
public class GlaUtils1_4AnnotationTest {
	public static void main(String[] args) throws SQLException {
		EntityWriter.autobeans();
	}
}

//this annotation starts with "Autobeans" that is
//sames as the method "autobeans()" which you will use.
//ElementType is TYPE,do not any else.Cause this annotation
//used just once.

//About fastwrite to file
//I choose "BufferedOutputStream" .
//It is the fast class when the data < 6M
/*class BufferedOutputStreamTest{
	public static void main(String[] args) throws IOException {
		String path = "src\\"+"com/a/domain";
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(new File(path+"/Admin.java")));
		System.out.println(bos);
		bos.write("public class Admin{\n".getBytes());
		bos.write("\t\n}".getBytes());
		bos.close();
	}
}*/





