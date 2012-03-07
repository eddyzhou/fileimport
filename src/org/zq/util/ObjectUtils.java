package org.zq.util;

public class ObjectUtils {

	/**
	 * <p>
	 * Compares two objects for equality, where either one or both objects may
	 * be <code>null</code>.
	 * </p>
	 * 
	 * <pre>
	 * ObjectUtils.equals(null, null) = true
	 * ObjectUtils.equals(null, &quot;&quot;) = false
	 * ObjectUtils.equals(&quot;&quot;, null) = false
	 * ObjectUtils.equals(&quot;&quot;, &quot;&quot;) = true
	 * ObjectUtils.equals(Boolean.TRUE, null) = false
	 * ObjectUtils.equals(Boolean.TRUE, &quot;true&quot;) = false
	 * ObjectUtils.equals(Boolean.TRUE, Boolean.TRUE) = true
	 * ObjectUtils.equals(Boolean.TRUE, Boolean.FALSE) = false
	 * </pre>
	 * 
	 * @param object1
	 *            the first object, may be <code>null</code>
	 * @param object2
	 *            the second object, may be <code>null</code>
	 * @return <code>true</code> if the values of both objects are the same
	 */
	public static boolean equals(Object object1, Object object2) {
		if (object1 == object2) {
			return true;
		}
		if ((object1 == null) || (object2 == null)) {
			return false;
		}
		return object1.equals(object2);
	}

	/**
	 * <p>
	 * Gets the hash code of an object returning zero when the object is
	 * <code>null</code>.
	 * </p>
	 * 
	 * <pre>
	 * ObjectUtils.hashCode(null) = 0
	 * ObjectUtils.hashCode(obj) = obj.hashCode()
	 * </pre>
	 * 
	 * @param obj
	 *            the object to obtain the hash code of, may be
	 *            <code>null</code>
	 * @return the hash code of the object, or zero if null
	 * @since 2.1
	 */
	public static int hashCode(Object obj) {
		return (obj == null) ? 0 : obj.hashCode();
	}

}
