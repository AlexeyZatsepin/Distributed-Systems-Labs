import java.io.Serializable;

public class MyEvent implements Serializable {
    public static final long serialVersionUID = 4211111111L;
    private String name;
    public boolean isHeavyweight() {
        return true;
    }

    public MyEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyEvent{" +
                "name='" + name + '\'' +
                '}';
    }
}
