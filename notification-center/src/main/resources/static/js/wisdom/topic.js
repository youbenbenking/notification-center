var dataTableApi;
var templateMode={}; 

//页面初始化执行操作
$(function() {
	   

//	    connect()
})





var stompClient = null;

function setConnected(connected){
     document.getElementById("connect").disabled = connected;
     document.getElementById("disconnect").disabled = !connected;
      $("#result").html();
}

function connect() {
	  var userId = document.getElementById('user').value;
      var socket = new SockJS("http://127.0.0.1:10089/webServer");
      stompClient = Stomp.over(socket);
      stompClient.connect({}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                
                stompClient.subscribe('/topic/getResponse', function(response){
                    var response1 = document.getElementById('result');
                    var p = document.createElement('p');
                    p.style.wordWrap = 'break-word';
                    p.appendChild(document.createTextNode(response.body));
                    response1.appendChild(p);
                });
            });
        }
	    
	    
function disconnect() {
        if (stompClient != null) {
             stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
 }
        
function sendName() {
        var name = document.getElementById('name').value;
        console.info(1111111111);
        stompClient.send("/subscribe", {}, 
        					JSON.stringify({ 'name': name}));
}

