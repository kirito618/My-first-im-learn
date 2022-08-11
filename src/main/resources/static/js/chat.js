//聊天室主人
var username;
// 消息接收者
var toName;
var token = sessionStorage.getItem('token');
var isGroup = false;


//登录后显示用户名和状态
$(function () {
    token = sessionStorage.getItem('token');
    $.ajax({
        //是否异步,此项目此处必须是false
        async: false,
        //请求方式
        type: 'POST',
        headers: {"Content-Type":"text/plain;charset=UTF-8", "token":token},
        //请求url
        url: "/getUserName",
        success : function (res) {
            username = res;
            //$('#chatMeu').html('<p>用户：' + res + "<span style='float: right;color: greenyellow; height: 20px'>在线</span></p>")
        }
    });


    //创建websocket对象
    var ws = new WebSocket("ws://localhost:6542/chat");
    //var ws = new WebSocket("ws://118.178.241.20:6542/chat");


    //建立连接后触发
    ws.onopen = function () {
        $('#chatMeu').html('<p>用户：' + username + "<span style='float: right;color: greenyellow; height: 20px'>在线</span></p>")
    };


    //接收到服务端的推送后触发
    ws.onmessage = function (evt) {
        //获取数据
        var dataStr = evt.data;
        //jsonData 格式举例（需要判断是否是系统消息）：{“isSystem”: false, "fromName": "YYJ", "message": “你在哪里呀？”}
        var jsonData = JSON.parse(dataStr);
        console.log(jsonData);
        //判断是否是系统消息
        if (jsonData.system) {
            //是系统消息，处理
            var friendsList = "";
            var systemMsg = "";
            var allNames = jsonData.message;
            for (var name of allNames) {
                if (username !== name) {
                    friendsList += "<li><a style='text-decoration: underline' onclick='chatWith(\""+ name +"\")'>"+name+"</a></li>";
                    systemMsg += "<li>"+name+" 上线啦！</li>";
                }
            }
            //渲染页面
            $("#friendsList").html(friendsList);
            $("#systemMsg").html(systemMsg);
        }else if(jsonData.groupFlag){
            let groups = jsonData.message;
            let groupsList = "";
            for (let group of groups) {
                groupsList += "<li><a style='text-decoration: underline' onclick='chatRoom(\""+ group +"\")'>"+group+"</a></li>";
            }
            $("#groupsList").html(groupsList);
        }else{
            //不是系统消息，是发送给指定用户的消息,示例值：{“systemMsgFlag”: false, "fromName": "YYJ", "message": “你在哪里呀？”}
            var data = jsonData.message;
            //渲染消息（位于左侧）
            var cnt = "<div class=\"atalk\"><span id=\"asay\">"+data+"</span></div>"
            if (toName === jsonData.fromName) {
                $("#chatCnt").append(cnt);
            }
            var chatData = sessionStorage.getItem(jsonData.fromName);
            if (chatData != null) {
                cnt = chatData + cnt;
            }
            sessionStorage.setItem(jsonData.fromName ,cnt);
        }
    };
    //关闭连接触发
    ws.onclose = function () {
        $('#chatMeu').html('<p>用户' + username + "<span style='float: right;color: #d50a0a; height: 20px'>离线</span></p>")
    };

    //发送按钮点击
    $("#submit").click(function () {
        //获取发送输入框中的内容
        var data = $("#tex_content").val();
        //点击发送后，清空输入内容框架
        $("#tex_content").val("");
        if (isGroup){
            var sendJson = {"toName": toName, "message": data, "group": false};
        }else{
            var sendJson = {"toName": toName, "message": data, "group": true};
        }

        //聊天框显示发送内容（右侧，即我发送的消息）
        var cnt = "<div  class=\"btalk\"><span id=\"bsay\">" + data+ "</span></div>";
        $("#chatCnt").append(cnt);
        var chatData = sessionStorage.getItem(toName);
        if (chatData != null) {
            cnt = chatData + cnt;
        }
        sessionStorage.setItem(toName,cnt);
        //发送数据给服务端
        ws.send(JSON.stringify(sendJson));
    });
});


//点击好友列表后，执行的动作
function chatWith(name){
    toName = name;
    //再次点击好友聊天，需要删除原来的”和xxx"聊天的提示
    $('#p1').remove();
    $('#chatMeu').append('<p id="p1" style="text-align: center">正在和<b style="color: #db41ca ">' + name + '</b>聊天</p>');
    $('#chatMain').css("display", "inline");
    //清空聊天区
    $("#chatCnt").html("");


    //消息入库后  使用数据库中的历史记录，不再使用session存储的暂时数据
    $.ajax({
        async: false,
        //请求方式
        type: 'GET',
        headers: {"Content-Type":"text/plain;charset=UTF-8", "token":token},
        //请求url
        url: "/getOneRecords",
        data: {to:name},
        success : function (res) {
            records = res;
            console.log(records);
            for (record of records){
                let json = record;
                var data = json.message;
                var from = json.fromName;
                let cnt = "";
                if (from === username){
                    //是我发的，那就放在右侧
                    cnt = cnt + "<div  class=\"btalk\"><span id=\"bsay\">" + data+ "</span></div>";
                }else{
                    //渲染消息（位于左侧）
                    cnt = cnt + "<div class=\"atalk\">" + from + " : " + "<span id=\"asay\">"+data+"</span></div>"
                }
                $("#chatCnt").append(cnt);
            }
        }
    });


    // //当点击聊天人列表时，需要获与之对应的聊天记录，聊天记录存放在sessionStorage中
    // var chatData = sessionStorage.getItem(toName);
    // if (chatData != null) {
    //     //渲染聊天数据到聊天区
    //     $("#chatCnt").html(chatData);
    // }
}


//点击某个群组后，执行的动作
function chatRoom(name){
    toName = name;
    //再次点击好友聊天，需要删除原来的”和xxx"聊天的提示
    $('#p1').remove();
    $('#chatMeu').append('<p id="p1" style="text-align: center">聊天群组：<b style="color: #db41ca ">' + name + '</b></p>');
    $('#chatMain').css("display", "inline");
    //清空聊天区
    $("#chatCnt").html("");

    $.ajax({
        async: false,
        //请求方式
        type: 'GET',
        headers: {"Content-Type":"text/plain;charset=UTF-8", "token":token},
        //请求url
        url: "/getRecords",
        data: {groupName:name},
        success : function (res) {
            records = res;
            for (record of records){
                let json = record;
                var data = json.message;
                var from = json.fromName;
                let cnt = "";
                if (from === username){
                    //是我发的，那就放在右侧
                    cnt = cnt + "<div  class=\"btalk\"><span id=\"bsay\">" + data+ "</span></div>";
                }else{
                    //渲染消息（位于左侧）
                    cnt = cnt + "<div class=\"atalk\">" + from + " : " + "<span id=\"asay\">"+data+"</span></div>"
                }
                $("#chatCnt").append(cnt);
            }
        }
    });

}