/**
 * Created by yutao on 16-6-29.
 */
public class MemoryUnit {
    UnitType type;
    String content;
    MemoryUnit(UnitType type,String content){
        this.type=type;
        this.content=content;
    }
    @Override
    public String toString() {
        return type.toString()+"|"+content;
    }
}
