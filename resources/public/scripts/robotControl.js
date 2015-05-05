var lastcommand = "";
var command = function(com){
  if(lastcommand != com){
    lastcommand = com;
    $.ajax({
      method: "GET",
      url: "/robot?" + com,
      dataType: "script"
    });
  }
}

var forward = "command=tanksteer&left=160&right=150";
var stop = "command=stop";
var reverse = "command=reverse";
var left = "command=left";
var right = "command=right";

$("body").keydown(function(h){
  switch(h.keyCode){
    case 38:
      command(forward);
      break;
    case 40:
      command(reverse);
      break;
    case 37:
      command(left);
      break;
    case 39:
      command(right);
      break;
    default:
      console.log(h.keyCode);
  }
});
$("body").keyup(function(h){
  switch(h.keyCode){
    case 38:
    case 40:
    case 37:
    case 39:
      command(stop);
      break;
    default:
      console.log(h.keyCode);
  }
});


$("#button1").mousedown(function(){command(forward)});
$("#button1").mouseup(function(){command("command=stop")});

$("#button2").mousedown(function(){command("command=reverse")});
$("#button2").mouseup(function(){command("command=stop")});
