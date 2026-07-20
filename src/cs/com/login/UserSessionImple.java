/*
 * This software was developed and owned by Locus
 * Illegal use of this software will violate the Copy Right Law
 * ******************************************************
 * Program Name : @(#)CallSessionManager.java
 * Function description : Web Application Framework
 * Programmer Name : Cho HyunSun(karroo@locus.com)
 * Creation Date : 2004.04.26
 * ******************************************************
 *                    P R O G R A M H I S T O R Y
 * ******************************************************
 * DATE			:	PRGMMER		: REASON
 *				:				:
 */

package cs.com.login;

import java.util.*;
import com.locus.jedi.waf.*;
/**
 *	HttpSession대신에 사용하는 객체
 *
 * @version		WAF 1.0		26 APR 2003
 * @author		Cho HyunSun[karroo@locus.com]
 */
public class UserSessionImple implements UserSession
{
	private Map data = Collections.synchronizedMap(new HashMap());

	private BaseEntity entity;

	public UserSessionImple(BaseEntity entity){
		this.entity = entity;
	}

	public Object getAttribute(Object key){
		return data.get(key);
	}
	public void setAttribute(String key, Object value){
		data.put(key, value);
	}	
	public boolean contains(Object value){
		return data.containsValue(value);
	}
	public boolean containsKey(Object key){
		return data.containsKey(key);
	}

	public boolean containsValue(Object value){
		return data.containsValue(value);
	}
	public void clear(){
		data.clear();
	}
	public Set keySet(){
		return data.keySet();
	}
	public Set entrySet(){
		return data.entrySet();
	}
	public boolean isEmpty(){
		return data.isEmpty();
	}
	public Object remove(Object key){
		return data.remove(key);
	}
	public int size(){
		return data.size();
	}
	public void putAll(Map t){
		data.putAll(t);
	}

	public BaseEntity getBaseEntity(){
		return entity;
	}

	public Collection values(){
		return data.values();
	}
	public Collection keys(){
		return data.keySet();
	}
	/**
	 * key에 대한 value를 String으로 리턴한다.
	 * @param key
	 * @return value Object의 toString() 값.
	 */
	public String getString(String key){
		Object v = data.get(key);
		if(v == null) return null;
		return v.toString();
	}
	/**
	 * key에 대한 값을 String으로 리턴한다. key가 없거나 value가 null이면
	 * defaultValue를 리턴한다.
	 * @param key
	 * @param defaultValue
	 * @return value Object의 toString() 값. 또는, defaultValue
	 */
	public String getString(String key,String defaultValue){
		String result = defaultValue;
		Object v = data.get(key);
		if(v != null){
			result =  v.toString();
		}
		return result;
	}

}