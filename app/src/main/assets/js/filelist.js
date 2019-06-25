  var back_path=null;//上一级目录
  $(function() {
     $("#parentDirLinkBox").hide();
    //初始化文件列表
    initTable();
    //点击文件夹
	 $("body").on("click", "tr", function(event){
	    var isdir=$(this).data('isdir');
	    var path=$(this).data('path');
	    if(isdir){//如果是文件夹
	       back_path=$("#header").html();
	       $("#parentDirLinkBox").show();
	       initTable(path);
	    }else{//如果是文件，执行直接打开操作
	       openFile(path);
	    }
  	});
    //上一级目录
  	 $("body").on("click", "#parentDirLinkBox", function(event){
	    initTable(back_path);
  	});
 })

 //打开文件
 function openFile(path){
   //如果希望电视机也显示相应的文件，则需要另写后台响应代码
        var jsonData = {'path' : path } //路径
        $.ajax({
   			type : 'POST',
   			url : "/sys/showFile",
   			data : jsonData,
   			success : function(dataStr) {
   			  // window.location.href=path;
   			  // window.open(path); //在新窗口打开
   			  showMessage("操作成功");
   			}
   		});
 }

//初始化文件列表
 function initTable(path){
     $("#tbody").html("")
     var jsonData;
     if(path==null){
          jsonData = {
			'path' : '/'  //路径
			}
     }else{
     		jsonData = {
			'path' : path  //路径
			}
     }
     $.ajax({
			type : 'POST',
			url : "/sys/filelist",
			data : jsonData,
			success : function(dataStr) {
			    var data=$.parseJSON(dataStr);
			    $("#header").html(data.message);
				if(data.result){
				  var fileList=data.returnObject;
				  for(var i=0;i<fileList.length;i++){
				     var file=fileList[i]
				     //addRow(name, url, isdir,size, size_string, date_modified, date_modified_string)
				     addRow(file.name,file.path,file.directory,file.size,getFileSize(file.size),file.modifyTime,getLocalTime(file.modifyTime)); //逐行加载文件
				  }
				}else{
				   $("#tbody").html("无数据")
				}
			},
			error:function(data){
			   $("#tbody").html("系统错误")
			}
	});
 }


 function getFileSize(fileByte) {
   var fileSizeByte = fileByte;
   var fileSizeMsg = "";
   if (fileSizeByte < 1048576) fileSizeMsg =Math.ceil(fileSizeByte / 1024) + "KB";
   else if (fileSizeByte == 1048576) fileSizeMsg = "1MB";
   else if (fileSizeByte > 1048576 && fileSizeByte < 1073741824) fileSizeMsg = Math.ceil(fileSizeByte / (1024 * 1024)) + "MB";
   else if (fileSizeByte > 1048576 && fileSizeByte == 1073741824) fileSizeMsg = "1GB";
   else if (fileSizeByte > 1073741824 && fileSizeByte < 1099511627776) fileSizeMsg = Math.ceil(fileSizeByte / (1024 * 1024 * 1024)) + "GB";
   else fileSizeMsg = "1TB+";
   return fileSizeMsg;
 }