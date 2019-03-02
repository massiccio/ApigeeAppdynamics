package org.massiccio.apigee.appdynamics.filters;

import java.util.function.Predicate;

import javax.management.ObjectName;

/**
 * Filter for the <i>type</i> property.
 */
public class TypeFilter implements Predicate<ObjectName> {
	
	public static final String INBOUND_TRAFFIC = "inboundtraffic";
	
	public static final String FAULTS = "faults";
	
	public static final String TYPE = "type";

	/**
	 * Return <code>true</code> if the object name has a property called "type" whose value
	 * is either "faults" or "inboundtraffic".
	 * Return <code>false</code> otherwise.
	 */
    @Override
    public boolean test(ObjectName t) {
    	if (t == null) {
    		return false;
    	}
        String type = t.getKeyProperty(TYPE);
        
        return (FAULTS.equals(type) || INBOUND_TRAFFIC.equals(type));
    }

}
