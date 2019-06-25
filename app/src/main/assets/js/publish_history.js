$(function() {
    //初始化模板列表
    initTable();
    //点击模板
	 $("body").on("click", "tr", function(event){
	    var isdir=$(this).data('isdir');
	    var tid=$(this).data('tid');
	    showTemplate(tid);
  	});

 })

 //电视机上展示对应的模板
 function showTemplate(id){
    var jsonData = {'id' : id  }
    $.ajax({
			type : 'POST',
			data : jsonData,
			url : "/sys/publishTemplateById",
			success : function(dataStr) {
			    var data=$.parseJSON(dataStr);
				if(data.result){
				showMessage("发布成功");
				}else{
				showMessage("发布失败");
				}
			},
			error:function(data){
			   showMessage("系统错误");
			}
	});
 }

//初始化文件列表
 function initTable(path){
     $("#tbody").html("");
     $.ajax({
			type : 'POST',
			url : "/sys/publishHistory",
			success : function(dataStr) {
			    var data=$.parseJSON(dataStr);
				if(data.result){
				  var list=data.returnObject;
				  for(var i=0;i<list.length;i++){
				     var model=list[i];
				     //addTemplateRow(name, id, date_modified_string)
				     addTemplateRow(model.name,i,model.startTime+"-"+model.endTime); //逐行加载模板
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