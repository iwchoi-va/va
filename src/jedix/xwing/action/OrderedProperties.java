/**
 * Program Name  : CodeUtil.java
 * Description  : 
 * Programmer Name : TypeJ
 * Creation Date : 2009. 7. 8.
 * DATE        : PROGRAMMER : REASON
 * 2009. 7. 8. : TypeJ      : 
 */

package jedix.xwing.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class OrderedProperties
    extends Properties {

    ArrayList orderedKeys = new ArrayList();

    public synchronized Object put(Object key, Object value) {
        Object object = super.put(key, value);
        orderedKeys.add(key);
        return object;
    }

    public synchronized Object remove(Object key) {
        Object object = super.remove(key);
        orderedKeys.remove(key);
        return object;
    }

    public synchronized Iterator getOrderedKeys() {
        return orderedKeys.iterator();
    }
}
