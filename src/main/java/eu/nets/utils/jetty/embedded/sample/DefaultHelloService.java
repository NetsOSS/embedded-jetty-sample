package eu.nets.utils.jetty.embedded.sample;

/**
 * @author Kristian Rosenvold
 */
public class DefaultHelloService implements HelloService {

    @Override
    public String sayHello(String name) {
        if (name == null) {
            return "Hello, Stranger!";
        }
        return "Hello, " + name;
    }
}
