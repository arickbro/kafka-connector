package ch.yax.connect.quickstart.source;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class test implements Runnable {

    static long lastMessageTime;
    static ArrayList<String> records = new ArrayList<String>();
    static ArrayList<String> bucket = new ArrayList<String>();

    long numberOfRetries = 0 ;
    long timeout = 5000;
    String uri = "ws://192.168.1.10:8181";
    boolean reconnectIfNoActivity = false;
    long inactivityMs = 600000;


    public void setConfig(RandomSourceConfig configs) {
        uri = configs.getString("ws.uri");
        reconnectIfNoActivity = configs.getBoolean("reconnect.no.record");
        inactivityMs = configs.getLong("inactivity.ms");
        timeout = configs.getLong("reconnect.delay");
    }

    public void run(){

        WebsocketClientEndpoint clientEndPoint;
        
        while(true){
            log.info("connecting to "+uri);
            // open websocket
            try {
                numberOfRetries = numberOfRetries+1;
                clientEndPoint = new WebsocketClientEndpoint(new URI(uri));

                // add listener
                clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                    public void handleMessage(String message) {
                        capture(message);
                    }
                });
                log.info("connected");
            }
            catch(Exception e) {
                log.info(e.getMessage());
                continue;
            }

            /* daemon to  check the connection */
            while(true){

                try {
                    Thread.sleep(timeout);
                    clientEndPoint.userSession.isOpen();
                    long currentEpoch = System.currentTimeMillis() - lastMessageTime;
                    if(reconnectIfNoActivity && currentEpoch >= inactivityMs ){
                        lastMessageTime =0;
                        log.error(" no activity for more than 10s, close the connection" );
                        clientEndPoint.userSession.close();
                        break;
                    }
                }
                catch (InterruptedException ex) {
                    System.out.println("InterruptedException exception: " + ex.getMessage());
                }catch(Exception e) {
                    log.error(e.getMessage());
                    break;
                }
                
            }
            

        }

        
    }

    static void capture(String data){
        records.add(data);
        lastMessageTime = System.currentTimeMillis();
    }
    
    public synchronized List<String>  getData(){
        bucket = (ArrayList<String>)records.clone();   
        records.clear();
        return bucket;
    }
}
