package business.util;

public abstract class Argument {

	public static void isNotNull(final Object obj) {
		isTrue( obj != null, " Cannot be null " );
	}
	
	public static void isPositive(final int obj) {
		isTrue( obj > 0, " Debe de ser positivo " );
	}
	
	public static void is012(final int obj) {
		isTrue( obj==0 || obj==1 || obj==2, " Debe de ser un 0, 1 o 2 " );
	}

	public static void isNotNull(final Object obj, String msg) {
		isTrue( obj != null, msg );
	}

	public static void isNull(final Object obj) {
		isTrue( obj == null, " Must be null " );
	}

	public static void isTrue(final boolean test) {
		isTrue(test, "Condition must be true");
	}

	public static void isTrue(final boolean test, final String msg) {
		if (test == true) {
			return;
		}
		throwException(msg);
	}

	public static void isNotEmpty(final String str) {
		isTrue( str != null, 
				"The string cannot be null not empty" );

		isTrue( str.trim().length() > 0, 
				"The string cannot be null not empty" );
	}

	public static void isNotEmpty(final String str, final String msg) {
		isTrue( str != null, msg );
		isTrue( str.trim().length() > 0, msg );
	}
	
	public static void longitudNueve(int telefono) {
		String v = String.valueOf(telefono);
		isTrue(v.length()==9);
	}

	public static void longitudDieciseis(int numeroTarjeta) {
		String v = String.valueOf(numeroTarjeta);
		isTrue(v.length()==16);
	}
	
	public static void longitudNueve(String dNI) {
		isTrue(dNI.length()==9);
	}

	protected static void throwException(final String msg) {
		throw new IllegalArgumentException( msg );
	}

	

	

}