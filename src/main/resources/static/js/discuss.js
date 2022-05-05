function like(btn, entityType, entityId, entityUserId, postId){

    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId, "entityUserId":entityUserId, "postId":postId},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0){
                //通过当前点赞的a标签对象，获取子标签<i>和<b>
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            } else{
                alert(data.msg);
            }
        }
    );

}