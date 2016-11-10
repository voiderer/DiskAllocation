/**
 * Created by yutao on 16-6-29.
 */
public enum UnitType{
    root,counter,branch,leaf,stop,empty,ram,disk;

    @Override
    public String toString() {
        switch (this){
            case root:
                return "memoryRoot";
            case counter:
                return "counter";
            case branch:
                return "branch";
            case leaf:
                return "leaf";
            case stop:
                return "stop";
            case empty:
                return "empty";
            case ram:
                return "ram";
            case disk:
                return "disk";
        }
        return "";
    }

    public static UnitType readString(String s){
        switch (s){
            case "memoryRoot":
                return root;
            case "counter":
                return counter;
            case "branch":
                return branch;
            case "leaf":
                return leaf;
            case "stop":
                return stop;
            case "empty":
                return empty;
            case "ram":
                return ram;
            case "disk":
                return disk;
        }
        return null;
    }
}
