package IpChanger.Runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import IpChanger.service.CloudflareIpChanger;

import java.util.Scanner;

@Component
public class MainRunner implements CommandLineRunner {
    @Autowired
    CloudflareIpChanger ipChangerService;

    @Override
    public void run(String...args) throws Exception{
        System.out.println("Enter the Ip: ");
        Scanner scanner = new Scanner(System.in);
        String ip = scanner.nextLine();
        ipChangerService.geAllDnsNames();
      ipChangerService.updateIp(ip);
      scanner.close();
      System.exit(0);
    }
}
