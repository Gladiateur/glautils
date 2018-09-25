/*
 * DebugImpl.java 18/06/28
 */
package gla.debug;

import gla.meta.Utils;

/**
 * Develop Mode.
 * 
 * @author Gladiateur
 * @since 1.4
 */
public class DebugImpl implements Debug{
	/*off this function , set this false*/
	public static boolean FLAG = false ; 
	@Override
	public void debug(Object obj) {
		if(FLAG){
			Utils.out(obj.toString());
		}
	}
}
