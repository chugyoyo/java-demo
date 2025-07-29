import java.io.Serializable;

public class FileClassLoadDemo implements Serializable {

    private static final long serialVersionUID = 1L;

    static {
        System.out.println("FileClassLoadDemo static block called");
    }

    public FileClassLoadDemo() {
        System.out.println(this + " constructor called");
    }

    public String sayHello() {
        System.out.println(this + " sayHello() method called");
        return "Hello!";
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println(this + " finalize() method called");
        super.finalize();
    }
}