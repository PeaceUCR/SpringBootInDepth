# SpringBoot Demo
**Filter + Static File Configuration+ MultiThreading+ File upload&download+ Socket**

# Heroku Deployment Issue& MVN package error issue
ref

https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception-in-j
https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku
https://springframework.guru/jackson-dependency-issue-spring-boot-maven/

add     

		<!-- jackson dependency issue-->
		<!-- https://springframework.guru/jackson-dependency-issue-spring-boot-maven/-->
		<!-- please ignore version here -->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-core</artifactId>
		</dependency>

		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
		</dependency>
		<!-- jackson dependency issue-->
		<!-- https://springframework.guru/jackson-dependency-issue-spring-boot-maven/-->
		<!-- please ignore version here -->
    
    		<!-- error here with mvn package -->
		<!-- https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception-in-j -->
		<!-- Java 6 = JAX-B Version 2.0   -->
		<!-- Java 7 = JAX-B Version 2.2.3 -->
		<!-- Java 8 = JAX-B Version 2.2.8 -->
		<dependency>
			<groupId>javax.xml.bind</groupId>
			<artifactId>jaxb-api</artifactId>
			<version>2.2.11</version>
		</dependency>
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-core</artifactId>
			<version>2.2.11</version>
		</dependency>
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-impl</artifactId>
			<version>2.2.11</version>
		</dependency>
		<dependency>
			<groupId>javax.activation</groupId>
			<artifactId>activation</artifactId>
			<version>1.1.1</version>
		</dependency>



This Demo is inherit from https://github.com/PeaceUCR/SpringBoot-Rest-Swagger-Log4j

Let's Go

#  /filter/LogFilter  
What's Filter in SpringBoot  
https://stackoverflow.com/questions/4122870/what-is-the-use-of-filter-and-chain-in-servlet  
Similar to the middleware in ExpressJS, work on the request and response between Client
and Server

So in this demo, I build LogFilter to try to log the request and response

add @Component at Class  
The @Component annotation marks a java class as a bean so the component-scanning mechanism of spring can pick it up and pull it into the application context.  
in doFilter() method we can access the request, response, filterChain  

To log request: method, uri, header  
  
        //https://stackoverflow.com/questions/4449096/how-to-read-request-getinputstream-multiple-times  
        //if read inputstream here, then the error will be in controller,
        //request.getInputStream() is allowed to read only one time.
        //we need to print with out affect the origin request
        //catch stream and then get
        //https://stackoverflow.com/questions/35549040/failed-to-read-http-message-org-springframework-http-converter-httpmessagenotre
        //https://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once?noredirect=1&lq=1
**Care the request body is inputstream, it can only be ready once, if read in filter,
then the controller can't read, so we use MultiReadHttpServletRequest, to cache the inputStream**  

we can access valid response when after  filterChain.doFilter();

// https://stackoverflow.com/questions/3242236/capture-and-log-the-response-body
log response
DelegatingServletOutputStream:  
Delegating implementation of ServletOutputStream.Used by MockHttpServletResponse; typically not directly used for testing application controllers.

TeeOutputStream:
Classic splitter of OutputStream. Named after the unix 'tee' command. It allows a stream to be branched off so there are now two streams.

# Static File Configuration

In SpringBoot, static will serve under folder <AppName>/src/main/resources/static/

Put the html file at path
<AppName>/src/main/resources/static/template/blank.html

Then can be  access by
 http://localhost:8080/template/blank.html
 
 
# SpringBoot MultiThreading
 For Spring Boot Controller
 
 By default Spring Boot web applications are multi-threaded and will handle multiple requests concurrently.  
     
       //Demo(Controller method run in separate thread)
       //wait 10 seconds to get resonse
       //if send two request at same time,
       // the controller method will be excute in separate thread
       //https://stackoverflow.com/questions/46223363/spring-boot-handle-multiple-requests-concurrently?rq=1
       @RequestMapping(value = "/handler", method = RequestMethod.POST, consumes = "application/json; charset=utf-8")
       public String handler(@RequestBody String jsonData){
           logger.info("Handler STARTED:"+new Date());
           try {
               Thread.sleep(10000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           logger.info("Handler COMPLETED:"+new Date());
           return new Date().toString();
       }
       
       Output:(see [thread name] is different)
       [INFO ] 2018-07-18 15:54:49.423 [http-nio-8080-exec-2] FileController - Handler STARTED:Wed Jul 18 15:54:49 PDT 2018
       [INFO ] 2018-07-18 15:54:50.498 [http-nio-8080-exec-4] FileController - Handler STARTED:Wed Jul 18 15:54:50 PDT 2018
       [INFO ] 2018-07-18 15:54:59.427 [http-nio-8080-exec-2] FileController - Handler COMPLETED:Wed Jul 18 15:54:59 PDT 2018
       [INFO ] 2018-07-18 15:55:00.503 [http-nio-8080-exec-4] FileController - Handler COMPLETED:Wed Jul 18 15:55:00 PDT 2018

So we will use multithread in Service, then 
in /service/FileService, use thread pool to execute the files store,
Task specify a file destination then save it.

# File Operation at upload&download
Java io vs nio  
https://dzone.com/articles/java-nio-vs-io


Upload  
getByte from file, then write by Files.write(path, bytes);

Download  
write bytes into response outputstream
  
           //https://stackoverflow.com/questions/48622208/convert-object-from-java-nio-file-path-to-java-io-file
           //http://tutorials.jenkov.com/java-nio/channels.html#channel-implementations
            //https://stackoverflow.com/questions/858980/file-to-byte-in-java
            public void downloadFile(String name, HttpServletResponse response) throws IOException{
                    Path path = Paths.get(name);
                    if(Files.exists(path)){
           FileChannel inChannel = FileChannel.open(path);

            int size = (int) inChannel.size();

            MappedByteBuffer buf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);

            byte[] bytes = new byte[size];
            buf.get(bytes);
            //outputstream can't write buffer
           OutputStream ros = response.getOutputStream();
           ros.write(bytes);
        }
    }

**Test**
upload
![alt text](https://raw.githubusercontent.com/PeaceUCR/SpringBootInDepth/master/Screen%20Shot%202018-07-19%20at%205.00.56%20PM.png)

download . 
http://localhost:8080/files/511531243880_.pic.jpg
file . 
http://localhost:8080/upload/511531243880_.pic.jpg

# Socket 
structre of socket
![alt text](https://github.com/PeaceUCR/SpringBootInDepth/blob/master/drawIO.png?raw=true)
open webpage at http://localhost:8080/template/index.html  

Reference
https://spring.io/guides/gs/messaging-stomp-websocket/
http://www.baeldung.com/websockets-spring
https://www.callicoder.com/spring-boot-websocket-chat-example/

pom.xml  

        <!--Socket -->
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-websocket</artifactId>
        		</dependency>
        		<!--work with socket-->
        		<dependency>
        			<groupId>com.fasterxml.jackson.core</groupId>
        			<artifactId>jackson-core</artifactId>
        			<version>2.7.3</version>
        		</dependency>
            <dependency>
                <groupId>com.fasterxml.jackson.core</groupId>
                <artifactId>jackson-databind</artifactId>
                <version>2.7.3</version>
            </dependency>
            
            
**/config/WebSocketConfig** 

//The @EnableWebSocketMessageBroker is used to enable our WebSocket server.
//enables WebSocket message handling, backed by a message broker.

    // STOMP stands for Simple Text Oriented Messaging Protocol. It is a messaging protocol that defines the format and rules for data exchange.
    //we’re configuring a message broker that will be used to route messages from one client to another

    //add end point here to let client to connect
    //in main.js
    //     var socket = new SockJS('/ws');
    //     stompClient = Stomp.over(socket);
    //     stompClient.connect({}, onConnected, onError);
    //name 'ws' is the end point here

    //Notice the use of withSockJS() with the endpoint configuration. SockJS is used to enable fallback options for browsers that don’t support websocket.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        registry.addEndpoint("/ws").withSockJS();
    }


    //we’re configuring a message broker that will be used to route messages from one client to another.

    //the first line defines that the messages whose destination starts with “/topic” should be routed to the message broker. Message broker broadcasts messages to all the connected clients who are subscribed to a particular topic.
    //The second line defines that the messages whose destination starts with “/app” should be routed to message-handling methods
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //to enable a simple memory-based message broker to carry the messages back to the client on destinations prefixed with "/topic".
        config.enableSimpleBroker("/topic");

        // It also designates the "/app" prefix for messages that are bound for @MessageMapping-annotated methods. This prefix will be used to define all the message mappings;
        config.setApplicationDestinationPrefixes("/app");
    }
      


**/controller/chatcontroller** 
    
    //The @MessageMapping annotation ensures that if a message is sent to destination "/chat.sendMessage", then the sendMessage() method is called.
    //The return value is broadcast to all subscribers to "/topic/public" as specified in the @SendTo annotation.

    //at client
    /*
     stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
     client send msg will me handled by this method
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    //at client
    /*
        stompClient.send("/app/chat.addUser",
            {},
            JSON.stringify({sender: username, type: 'JOIN'})
        )
        will be handled by this method

        The return value is broadcast to all subscribers to "/topic/public" as specified in the @SendTo annotation.
    */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }


**/controller/WebSocketEventListener**  
    
    /*
        We’ll use event listeners to listen for socket connect and disconnect events
        so that we can log these events and also broadcast them when a user joins or leaves the chat room -
        
    convertAndSend
    https://docs.spring.io/spring/docs/4.0.0.M1_to_4.2.0.M2/Spring%20Framework%204.2.0.M2/org/springframework/messaging/simp/SimpMessageSendingOperations.html
    * */
    @Component
    public class WebSocketEventListener {
    
        private static final Logger logger = LogManager.getLogger(WebSocketEventListener.class);
    
        @Autowired
        private SimpMessageSendingOperations messagingTemplate;
    
        @EventListener
        public void handleWebSocketConnectListener(SessionConnectedEvent event) {
            logger.info("Received a new web socket connection");
        }
    
        //In the SessionDisconnect event, we’ve written code to extract the user’s name from the websocket session
        // and broadcast a user leave event to all the connected clients.
        @EventListener
        public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            if(username != null) {
                logger.info("User Disconnected : " + username);
    
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessage.MessageType.LEAVE);
                chatMessage.setSender(username);
    
                messagingTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }
    }
