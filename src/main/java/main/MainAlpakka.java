package main;

public class MainAlpakka {
    public static void main(String[] args) {
        String port = args[0];
        if (port.equals("2550")) {
            StartPublisher.main(new String[]{"2550"});
        } else if (port.equals("2560")) {
            StartSubscriber.main(new String[]{"2560"});
        } else {
            System.out.println("No system was started");
        }
    }


}
