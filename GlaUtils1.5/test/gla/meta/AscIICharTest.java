package gla.meta;

public class AscIICharTest {
	public static void main(String[] args) {
		String table ="  *  ";
		System.out.println((byte)(table.trim().charAt(0)));
		System.out.println((byte)'*');
		System.out.println((char)42);
		
		System.out.println((char)33);
	}
}
