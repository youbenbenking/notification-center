var stompClient = null;

//页面初始化执行操作
$(function() {
    
	    connect();
})



function setConnected(connected){
     document.getElementById("connect").disabled = connected;
     document.getElementById("disconnect").disabled = !connected;
      $("#result").html();
}

/**
 * 建立websocket连接(一般为成功登录系统后)
 * @returns
 */
function connect() {
	  var userId = document.getElementById('user').value;
	  var notificationHost = document.getElementById('notificationHost').value;
	  
	  // 建立连接对象（还未发起连接）
      var socket = new SockJS(notificationHost);
      // 获取 STOMP 子协议的客户端对象
      //如果使用原生的Websockets就使用Stomp.client(url)，如果需要使用其他类型的Websocket（例如由SockJS包装的Websocket）就使用Stomp.over(ws)。
      stompClient = Stomp.over(socket);
      // 向服务器发起websocket连接并发送CONNECT帧,连接是异步的,所以需要一个回调函数知道连接的结果
      stompClient.connect({}, function(frame) {
    	  			// 连接成功时（服务器响应 CONNECTED 帧）的回调方法
                setConnected(true);
                console.log('Connected: ' + frame);
                
                //STOMP 客户端要想接收来自服务器推送的消息，必须先订阅相应的URL，
                //即发送一个 SUBSCRIBE 帧，然后才能不断接收来自服务器的推送消息；
                stompClient.subscribe('/user/'+ userId +'/message', 
                						function(message){
                									//callback为每次收到服务器推送的消息时的回调方法，
                    								//1.创建新的li元素
                    								var li_new = document.createElement('li');
                    								//2.创建新的文本节点
                    								var textnode=document.createTextNode("新--"+message.body)
                    								//3.将文本节点添加到li元素里
                    								li_new.appendChild(textnode)
                    								//4.想ul中头插法插入元素
                    								var list = document.getElementById("msg_ul");
                    								list.insertBefore(li_new,list.childNodes[0]);
                    								
                						});
                //创建连接成功后,查询历史通知列表
                loadMessages();
            });
        }
	    
/**
 * 从客户端主动断开连接
 * @returns
 */    
function disconnect() {
        if (stompClient != null) {
             stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
 }
        
/**
 * 从客户端调用send()方法向服务器发送信息
 * @returns
 */ 
function sendName() {
        var name = document.getElementById('name').value;
        stompClient.send("/queue", {}, 
        					JSON.stringify({ 'name': name}));
}

/**
 * 清除消息通知列表
 * @returns
 */
function clearMsg(){
	 $('.cp_list ul li').each(function(){
		    $(this).remove();
		});
}

/**
 * 初始加载消息列表
 * @returns
 */
function loadMessages(){
		var data = {};
		data.srcAppCode = document.getElementById('srcAppCode').value;
		data.userName = document.getElementById('user').value;
		
		 $.ajax({
		        type: "POST",
		        url: "../api/loadMessages",
		        dataType: "json",
		        data: data,
		        success: function (data) {
		            if (data.success) {
		            		  var msgList = data.data;
		            		  if(msgList != null) {
		                          var msgStr = '';
		                          for(var i = 0; i < msgList.length; i++){
		                              msgStr += '<li value="'+ msgList[i].id +'" >' + msgList[i].msgDesc +'</li>';
		                          }
		                          $(".cp_list").html(msgStr);
		                   }
		            } 
		        }
		    });
}

