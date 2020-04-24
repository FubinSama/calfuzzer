public class Test1{
	public static void main(String[] args){
		String str1 = "a";
		String str2 = "a";
		String str3 = new String("a");
		System.err.println(str1 == str2);
		System.err.println(str1 == str3);
		str3 = str3.intern();
		System.err.println(str1 == str3);
	}
}
