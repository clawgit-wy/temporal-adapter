//对接系统中表单页面，添加如下JS逻辑

//用于解决跨域问题
document.domain = "yutong.com";

//父窗口接收到消息，如果是异构系统，则刷新页面
window.addEventListener('message', function(event){
    //debugger
    //console.log("收到" , event.origin , "消息：" , event.data);
    try{
        var resultData=JSON.parse(event.data);
        if(event.data&&resultData.refresh=="success"){
            var hostname = location.hostname;
            if(hostname != resultData.hostname){
                window.location.reload();
            }
        }
    }catch(ex){
        console.log("error" , ex);
    }
}, false);
