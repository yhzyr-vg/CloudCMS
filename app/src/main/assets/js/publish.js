
		var cur_model;
		function getTime(){
		   var date = new Date();//获取系统当前时间
		   return date.getFullYear()+""+(date.getMonth()+1)+""+date.getDate()+""+date.getHours()+""+date.getMinutes();
		}
		$(document).ready(function(){
	 		$(".leftmodel").click(function(){
			    cur_model=1;//左
	    		$("#fileupload_l").click();
	    	});
	    	$(".rightmodel").click(function(){
			   cur_model=2;//右
	    		$("#fileupload_l").click();
	    	});
			$(".rightmodel").hide();
			$("#progressdiv").hide();
            $("#name").val("模板"+getTime())
			$("input[name=screentype]").on('click',function() {//切换屏幕
				//alert($(this).val())
				$(".leftmodel").html("")
				$(".rightmodel").html("")
				var value=$(this).val();
				var orientation=$("input[name=screen_style]:checked").val();//横竖屏 1 横屏 2 竖屏
				if(orientation==1){//横屏
				 $(".model").addClass('orientation1').removeClass('orientation2');
				}else{//竖屏
				 $(".model").addClass('orientation2').removeClass('orientation1');
				}

				if(value==1){
				  $(".rightmodel").hide();
			      $(".leftmodel").removeClass('size50').removeClass('size30');
				  $(".rightmodel").removeClass('size50').removeClass('size70');
				}else if(value==2){
				  $(".rightmodel").show();
			      $(".leftmodel").addClass('size50').removeClass('size30');
				  $(".rightmodel").addClass('size50').removeClass('size70');
				}else{
				  $(".rightmodel").show();
			      $(".leftmodel").addClass('size30').removeClass('size50');
				  $(".rightmodel").addClass('size70').removeClass('size50');
				}
			});

			$("input[name=screen_style]").on('click',function() {//横竖屏
				var value=$(this).val();
				if(value==1){
				  $(".model").addClass('orientation1').removeClass('orientation2');
				}else if(value==2){
				$(".model").removeClass('orientation1').addClass('orientation2');
				}
			});

			$(".btn_publish").click(function(){//点击发布
			    var name=$("#name").val();//模板名称
			    var screenType=$("input[name=screentype]:checked").val();//选择的屏幕
	    		var imgList=$('.cms_imglist').html();//图片列表
				var startTime=$("#startTime").val();//起始时间
				var endTime=$("#endTime").val();//结束时间
				var orientation=$("input[name=screen_style]:checked").val();//横竖屏 1 横屏 2 竖屏

				if(imgList==null||imgList==""){//没有文件
				  showMessage("请选择需要上传的文件！")
				  return;
				}
				if(screenType==2||screenType==3){//半屏，50%，需要左右屏都要有资源
				  if(imgList.indexOf(",1")<=-1){//左，上半屏没有资源
				  showMessage("上传的资源不完整！errorcode:-1")
				  return;
				  }
				  if(imgList.indexOf(",2")<=-1){//右 下 半屏没有资源
				  showMessage("上传的资源不完整！errorcode:-2")
				  return;
				  }
				}

				var jsonData = {
				    'name' : name, //模板名称
					'screenType' : screenType, //选择的屏幕
					'imgList' : imgList,//图片列表
					'startTime' : startTime, //起始时间
					'endTime' : endTime,//结束时间
					'orientation' : orientation  //横竖屏 1 横屏 2 竖屏
				}

				$.ajax({
			     type : 'POST',
			     url : "/sys/publish",//处理参数
			     data : jsonData,
			     cache : false,
			     success : function(data) {
				   alert(data)
			    }
		      });
	    	});

		});

		function uploadFile(ev,jQueryObj) {
            //var fileObj = document.getElementById("fileupload_0").files[0]; // 获取文件对象
            //var files = document.getElementById("fileupload_0").files;
            var files=jQueryObj.files;
            console.debug(jQueryObj);
            var form = new FormData();
            var flag=true; //默認的flag
            var imgList=$('.cms_imglist').html();//资源列表

    		$.each(files,function(i){
    		  var fileName=files[i].name;//當前要上傳的文件
    		  if((imgList.indexOf("mp4")>-1||imgList.indexOf("mov")>-1)&&(fileName.indexOf("mp4")>-1||fileName.indexOf("mov")>-1)){//已經有視頻並且當前的是視頻
    		    flag=false;//不允許上傳
    		  }
    		  form.append("file"+i,files[i]);
   		 	});

   		 	if(!flag){
   		 	  showMessage("一个模板最多只能上传一个视频！")
    		  return;
   		 	}

			 $.ajax({
                   url:'/uploadifyFile',
						data : form,
						type : "POST",
						dataType : "json",
						cache : false,//上传文件无需缓存
						processData : false,//用于对data参数进行序列化处理 这里必须false
						contentType : false, // 不要设置Content-Type请求头，因为文件数据是以 multipart/form-data 来编码
						beforeSend : function() {

						},
						 xhr: function(){
					        myXhr = $.ajaxSettings.xhr();
					        if(myXhr.upload){
					          myXhr.upload.addEventListener('progress',function(e) {
					             $("#progressdiv").show();
					            if (e.lengthComputable) {
					              var percent = Math.floor(e.loaded/e.total*100);
					              if(percent <= 100) {
					                 $("#percent").val(percent);//图片上传进度
        							 $("#per").html(percent);
					              }
					              if(percent >= 100) {
					                $("#progressdiv").hide();
					              }
					            }
					          }, false);
					        }
					        return myXhr;
					    },
						success : function(result) {
							// alert(result.message+"  "+result.result)
						     if(result!=null&&result.result){
									$(".cms_imglist").append(result.message+","+cur_model+";");	//格式是 图片路径,左右;   表明该图片是在屏幕左边还是右边显示,多张图片逗号区分
									//===========这里处理上传图片或视频的回调  比如显示等
									var img=result.message;
									if(result.returnObject!=null){
									    img=result.returnObject;
									}
									var img='<img src="'+img+'" style="overflow:hidden;width:100%;height:100%" />';
									if(cur_model==1){//左
									$(".leftmodel").html(img)
									}else{//右
									 $(".rightmodel").html(img)
									}
						     }

						},
						error : function() {
							alert("选择文件失败");
						}
					});
		}

		function showMessage(msg){
		alert(msg);
		}