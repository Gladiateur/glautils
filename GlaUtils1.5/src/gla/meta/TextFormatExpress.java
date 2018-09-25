/*
 * TextappendFormatExpress.java 17/12/19
 */

package gla.meta;

/**
 * 构思：
 * <p>
 * 	TextFormatExpress fe = new TextFormatExpress();
 *		String str1 = "";
 *		String str2 = "";
 *		String str3 = "";
 *		fe.appendFormat("\n\t?\n\n\t?(){\n?\n}",str1,str2,str3);
 *		fe.toStringBuffer();
 * </p>
 * 比如这样：
 * <p>
 * 	public TextFormatExpress appendFormat(String express,String... p1){
 *		System.out.println(p1.length);
 *		for (String string : p1) {
 *			System.out.println(string);
 *		}
 *		return null;
 *	}
 *	
 *	public static void main(String[] args) {
 *		TextappendFormatExpress fe = new  TextappendFormatExpress();
 *		fe.appendFormat("", "a","b");
 *	}
 * </p>
 * 最后结果为：2  a b
 * <p>
 * 或者参考这个例子：
 * 	public int paramsLength;
 *	public String[] params;
 *	
 *	public TextFormatExpress appendFormat(String express,String... p1){
 *		this.paramsLength=p1.length;
 *		this.params=p1;
 *		
 *		return null;
 *	}
 *	
 *	public static void main(String[] args) {
 *		TextFormatExpress fe = new  TextFormatExpress();
 *		fe.appendFormat("?{\n\n\t?}\n", "a","b");
 *		System.out.println(fe.paramsLength+"\n"+fe.params);
 *	}
 * </p>最后结果为：2  [Ljava.lang.String;@2a139a55
 * <p>
 * 一开始是将'$'符号作为占位符，但是由于该符号特殊，在遍历替换时会出错，因此，现在的占位符使用'?'。
 * </p>
 * <p>
 * appendFormat这个方法出现了异常： java.util.regex.PatternSyntaxException:
 * Dangling meta character '?' near index 0 ？
 * 问题原因：String[] strs=express.split("?");
 * temp=temp.replaceFirst("?", params[i]);
 * 这里的符号'?'，需要转义，即修改为以下后执行正常。
 * 解决：String[] strs=express.split("\\?");
 * temp=temp.replaceFirst("\\?", params[i]);
 * 执行后运行正常，比如测试fe.appendFormat("111?222?333?", "a","b","c");
 * 运行结果：111a222b333c。
 * </p>
 * <p>
 * 空格会不会对执行结果造成影响。是否需要用trim去掉空格。
 * 经过测试，空格会对运行结果造成影响。测试语句：
 * System.out.println(fe.appendFormat("111 ? 222 ? 333 ? ", "a","b","c"));
 * 运行后程序会报错：java.lang.RuntimeException: 配置有错：?符号与参数个数不匹配
 * 为什么会报这个错？但是并不是所有场景都是必须去掉空格的。
 * 有一个奇怪的现象，总是appendFormat方法中参数express中占位符的最后一个空格搞的鬼。
 * 如果测试语句是：fe.appendFormat("111?222?333? ", "a","b","c");
 * 运行结果为：111a222b333c 。
 * 如果测试语句是：fe.appendFormat("111?222?333?", "a","b","c");
 * 运行结果为：
 * <span>
 * 	paramsLength=3
 *	subString.length=3
 *	Exception in thread "main" java.lang.RuntimeException: 配置有错：?符号与参数个数不匹配
 *	at gla.test.TextappendFormatExpress.appendFormat(TextappendFormatExpress.java:95)
 *	at gla.test.TextappendFormatExpress.main(TextappendFormatExpress.java:107)
 * </span>
 * 解决办法：原判定语句if(subString.length-1 != paramsLength)改为
 * if(subString.length != paramsLength)，并且原先分隔语句：subString=express.split("\\?");
 * 现在改为subString=express.trim().split("\\?");
 * 去掉首尾的空格，即可统一判定语句，执行通过。这次无论字符串最后是否有空格通能正确分隔子字符串并完成替换。
 * </p>
 * <p>
 * 实例：System.out.println(fe.appendFormat("where id= ? and pwd = ?", "100","123456"));
 * 类似jdbc里通过占位符来完成sql语句的拼接。
 * 运行结果：where id= 100 and pwd = 123456。
 * </p>
 * <p>
 * 发现当子串中有分号时不能正确的分隔。发现分号如果紧挨在占位符前面则分隔正确，若紧挨在占位符后面，则分隔错误。
 * 所有这些都是因为占位符的位置引起的，不同位置可能出现不同个数的子串。所以因为把首尾都加上空格再分，不去空格的分隔。
 * meta1712201613版本解决了上述问题，接下来模拟EntityWriter各种拼接情况进行测试。
 * 实例：fe.appendFormat("public void ?(? ?){\n\t\tthis.?=?;\n}\n","setId","Integer","id","id","id");
 * 测试通过。但是，最后三个参数的值是一样的却要重复写三遍，能否化简写法?
 * </p>
 * <p>
 * appendFormat方法的参数和返回值使用String和StringBuffer哪个更好?
 * </p>
 * <p>
 * 总结一下生成实体类时的某些固定格式。比如生成声明包名的语句：<br>
 * <span>
 * 	StringBuffer sb = new StringBuffer("package " );
 *	return sb.append(packageName.concat(";"));
 * </span>现在通过格式化方法这样来写：new TextFormatExpress().appendFormat("package ?;",packageName);<br>
 * 封装属性部分：
 * <span>
 * final String modify = "private ";
 *		StringBuffer sb = new StringBuffer();	
 *		for (Entry<String, String> string : fieldsMap.entrySet()) {
 *			sb.append("\t");
 *			sb.append(modify.concat(string.getValue().concat(" ")));
 *			sb.append(string.getKey()+";\n");
 *		}
 *		return sb;
 * </span>现在通过格式化方法这样来写：
 * <span>
 * TextFormatExpress fe = new TextFormatExpress();
 * for (Entry<String, String> string : fieldsMap.entrySet()) {
 *			fe.appendFormat("\t? ? ?;\n",modify,string.getValue(),string.getKey());
 *		}
 * </span>
 * 提供getter,setter方法：
 * <span>
 * fe.appendFormat("public ? ?(){\n\treturn ?;\n\t}\n",[TYPE],[METHODNAME],[ATTRIBUTE]);
 * fe.appendFormat("public void ?(? ?){\n\t\tthis.?=?;\n}\n",[METHODNAME],[TYPE],[ATTRIBUTE],[ATTRIBUTE],[ATTRIBUTE]);
 * 其中最后三个[ATTRIBUTE]完全相同。
 * </span>
 * <span>
 * 无参构造器：
 * fe.appendFormat("public ?(){\n\tsuper();\n}\n",[CLASSNAME]);
 * </span>
 * </p>
 * <p>
 * 特别注意一点，以上拼接字符串的手段没用用加号，因为使用加号拼接的效率是最低的，
 * 也尽量不用concat方法，所使用的方法是replaceFirst，该方法是用的StringBuffer做的拼接。
 * 使用StirngBuffer做拼接的效率是最高的。
 * </p>
 * 
 * @author Gladiateur
 * @author Dong Pan
 * @see java.util.regex.Matcher
 */
public class TextFormatExpress {
	public int paramsLength;
	public String[] params;
	public String[] subString;	//提出分隔后的子子串
	public String string;
	
	@Override
	public String toString(){
		return string;
	}
	
	public StringBuffer appendFormat(String express,String... params){
		this.paramsLength=params.length;
		this.params=params;
		express = " ".concat(express).concat(" ");
		subString=express.split("\\?");
		if(subString.length-1 != paramsLength){
			/*
			 * log.debug("真实参数个数："+paramsLength+"\n分隔后子串个数："+subString.length);
			 *	log.debug("占位符个数："+(subString.length-1));
			 */
			throw new RuntimeException("占位符与参数个数不匹配");
		}
		String text=express;	
		for(int i=0;i<paramsLength;i++){
			text=text.replaceFirst("\\?", params[i]);
		}
		StringBuffer sb = new StringBuffer(text);
		this.string = sb.toString();
		return sb;
	}
}
