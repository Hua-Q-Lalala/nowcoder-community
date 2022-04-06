$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	//获取接收方 和 内容
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();

	//发送Ajax请求
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName, "content":content},
		function(data){
			data = $.parseJSON(data);
			if(data.code == 0){	//code为0 发送成功
				$("#hintBody").text("发送成功");
			}else{
				$("#hintBody").text(data.msg);
			}

			//显示提示信息 并刷新页面
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 2000);
		}
	);

}

function delete_msg() {

	var btn = this;
	var id = $(btn).prev().val();

	//发送Ajax请求
	$.post(
		CONTEXT_PATH + "/letter/delete",
		{"id":id},
		function(data){
			data = $.parseJSON(data);
			console.log(data)
			if(data.code == 0){	//code为0 发送成功
				// TODO 删除数据
				$(btn).parents(".media").remove();
			} else {
				alert(data.msg);
			}
		}
	);

}