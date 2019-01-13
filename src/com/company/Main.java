package com.company;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

    public static void main(String[] args) {
        try {
            Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

            String username, password;

            try{

                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("credentials.json"));
                username = (String) jsonObject.get("username");
                password = (String) jsonObject.get("password");

            }catch(Exception e){

                System.out.print("Username: ");
                username = new Scanner(System.in).nextLine();
                System.out.print("Password: ");
                password = new Scanner(System.in).nextLine();

                JSONObject obj = new JSONObject();
                obj.put("username", username);
                obj.put("password", password);

                try (FileWriter file = new FileWriter("credentials.json")){
                    file.write(obj.toJSONString());}

            }

            System.out.println("To change credentials, delete " + new File("credentials.json").getCanonicalPath());

            final WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setUseInsecureSSL( true );
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            System.out.println("Signing in...");


            HtmlPage page = webClient.getPage("https://192.168.64.1:10443/auth1.html");
            page.executeJavaScript("javascript:" +
                    "document.getElementById('userName').value = '" + username + "';"+
                    "document.getElementsByName('pwd')[0].value = '" + password + "';"+
                    "document.getElementsByName('Submit')[0].click();");
            webClient.waitForBackgroundJavaScript(1000);

            page = webClient.getPage("http://192.168.64.1/dynUserLogin.html?loginDone=1");
            System.out.println(page.asText());
            webClient.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
