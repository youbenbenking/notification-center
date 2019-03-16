var dataTableApi;
var templateMode={}; 

//页面初始化执行操作
$(function() {
	var $html = "<div id='div-onchat' style='position:absolute;margin-top:0px;'><a ><img src='/Content/images/onchat.jpg' style='width:50px;height:50px;border-radius:25px' /></a></div>";
    $('body').append($html);
//    $("#div-onchat").click(function () {
//        var jspath = getJsPath('high-im-onchat.js');
//        var oid = getParam(jspath, 'oid');
//        window.open("/ChatManage/Youke/Index?oid=" + oid);
//    }).offset({ left: $(window).width() - 50, top: $(window).height() / 2 }).draggable({ containment: 'parent' });

    
	    connect();
	   
	    
	    
	    
})





var stompClient = null;

function setConnected(connected){
     document.getElementById("connect").disabled = connected;
     document.getElementById("disconnect").disabled = !connected;
      $("#response").html();
}

function connect() {
	  var userId = document.getElementById('user').value;
	  var notificationHost = document.getElementById('notificationHost').value;
	  
	  // 建立连接对象（还未发起连接）
      var socket = new SockJS(notificationHost);
      // 获取 STOMP 子协议的客户端对象
      stompClient = Stomp.over(socket);
      // 向服务器发起websocket连接并发送CONNECT帧
      stompClient.connect({}, function(frame) {
    	  			// 连接成功时（服务器响应 CONNECTED 帧）的回调方法
                setConnected(true);
                console.log('Connected: ' + frame);
                
                //STOMP 客户端要想接收来自服务器推送的消息，必须先订阅相应的URL，
                //即发送一个 SUBSCRIBE 帧，然后才能不断接收来自服务器的推送消息；
                stompClient.subscribe('/user/'+ userId +'/message', 
                						function(response){
                									//callback为每次收到服务器推送的消息时的回调方法，
                    								//1.创建新的li元素
                    								var li_new = document.createElement('li');
                    								//2.创建新的文本节点
                    								var textnode=document.createTextNode("新--"+response.body)
                    								//3.将文本节点添加到li元素里
                    								li_new.appendChild(textnode)
                    								//4.想ul中头插法插入元素
                    								var list = document.getElementById("msg_ul");
                    								list.insertBefore(li_new,list.childNodes[0]);
                						});
                
                loadMessages();
                
            });
        }
	    
	    
function disconnect() {
	//从客户端主动断开连接
        if (stompClient != null) {
             stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
 }
        

function sendName() {
        var name = document.getElementById('name').value;
        //客户端可使用 send() 方法向服务器发送信息
        stompClient.send("/queue", {}, 
        					JSON.stringify({ 'name': name}));
}

function clearMsg(){
	 $('.cp_list ul li').each(function(){
		    $(this).remove();
		});
}



//初始加载消息列表
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

