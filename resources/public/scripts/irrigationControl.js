
$("#button1").click(function(){
  console.log("runing...");
  $.ajax({
    method: "GET",
    url: "/irrigation?command=pump",
    dataType: "script"
  });
});

$("#button2").click(function(){
  console.log("stop.");
  $.ajax({
    method: "GET",
    url: "/irrigation?command=stop",
    dataType: "script"
  });
});
