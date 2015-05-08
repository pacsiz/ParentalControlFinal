package hu.uniobuda.nik.parentalcontrol.backend;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class ValueComparatorTest extends TestCase {

    Map map;
    TreeMap<String, String> mapDec;
    TreeMap<String, String> mapInc;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        map = new HashMap();
        map.put("2","B");
        map.put("3","C");
        map.put("1","A");
    }

    public void testValueComparatorInc()
    {
        mapInc = new TreeMap(new ValueComparatorDec(map));
        mapInc.putAll(map);
        assertEquals("1",mapInc.lafirstKey());
    }
}