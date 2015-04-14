package hu.uniobuda.nik.parentalcontrol;

import java.util.Comparator;
import java.util.Map;

public class ValueComparatorDec implements Comparator {

    Map map;

    public ValueComparatorDec(Map map){
        this.map = map;

    }
    public int compare(Object keyA, Object keyB){

        Comparable valueA = (Comparable) map.get(keyA);
        Comparable valueB = (Comparable) map.get(keyB);

        return (valueA.compareTo(valueB));
    }
}

