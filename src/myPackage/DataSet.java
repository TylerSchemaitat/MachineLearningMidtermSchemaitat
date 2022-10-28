package myPackage;
import java.util.*;

public class DataSet {
    public List<Double> actual;
    public List<double[]> features;
    public DataSet(){
        actual = new ArrayList<>();
        features = new ArrayList<>();
    }
    public DataSet(List<Double> act, List<double[]> feat){
        actual = act;
        features = feat;
    }
}
