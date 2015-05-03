
$("#button1").mousedown(function(){
  console.log("runing...");
  $.ajax({
    method: "GET",
    url: "/robot?command=forward",
    dataType: "script"
  });
});

$("#button1").mouseup(function(){
  console.log("stop.");
  $.ajax({
    method: "GET",
    url: "/robot?command=stop",
    dataType: "script"
  });
});
