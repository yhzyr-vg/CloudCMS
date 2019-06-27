$(function(){
     init();
	 $("body").on("click", ".list-row", function(event){
	  var $this=$(this);
	  var path=$this.data('resource');
	  var jsonData = {'path' : path } //路径
      $.ajax({
   			type : 'POST',
   			url : "/sys/showFile",
   			data : jsonData,
   			success : function(dataStr) {
               $this.addClass("red").siblings().removeClass('red');
   			}
   	   });
  	});
})

function init(){
    $.ajax({
			type : 'POST',
			url : "/sys/productList",
			success : function(dataStr) {
			var data=$.parseJSON(dataStr);
				if(data.result){
				  var list=data.returnObject;
				  createList(list);
				}else{
				   $(".productlist").html("没有商品信息");
				}
			},
			error:function(data){
			    $(".productlist").html("没有商品信息");
			}
	});
}
function createList(list){
    $(".productlist").html("");
    var strlist="";
    for(var i=0;i<list.length;i++){
				     var product=list[i]
				      var row=createRow(product.productResource,product.thumbnail,product.name);
				      strlist=strlist+row;
				  }
	$(".productlist").html(strlist);
}
/**加载商品行*/
function createRow(value,imgurl,name){
    var row='<div class="list-row" data-resource="'+value+'">'+
            '<img src="'+imgurl+'" width="260" height="350" alt="" style="opacity: 1;" >'+
            '<div class="product-name"><h2>'+name+'</h2></div></div>';
     return row;
}