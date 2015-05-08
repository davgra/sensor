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

var leftWheel=0;
var rightWheel=0;

var updateSpeed = function(){
  if(forward && left){
      leftWheel=100;
      rightWheel=200;
  } else if (forward && right){
      leftWheel=200;
      rightWheel=100;
  } else if (reverse && left){
      leftWheel=-200;
      rightWheel=-100;
  } else if (reverse && right){
      leftWheel=-100;
      rightWheel=-200;
  } else if (left){
      leftWheel=-150;
      rightWheel=150;
  } else if (right){
      leftWheel=150;
      rightWheel=-150;
  } else if (forward){
      leftWheel=150;
      rightWheel=150;
  } else if (reverse){
      leftWheel=-150;
      rightWheel=-150;
  } else {
      leftWheel=0;
      rightWheel=0;
  }
  command("command=tanksteer&left="+leftWheel+"&right="+rightWheel);
};

var forward = false;
var left = false;
var reverse = false;
var right = false;

$("body").keydown(function(h){
  switch(h.keyCode){
    case 38:
      forward= true;
      break;
    case 40:
      reverse=true;
      break;
    case 37:
      left = true;
      break;
    case 39:
      right=true;
      break;
    case 32:
      forward=reverse=left=right=false;
      leftWheel=0;
      rightWheel=0;
      command("command=tanksteer&left="+leftWheel+"&right="+rightWheel);
      break;
    default:
      console.log(h.keyCode);
  }
  updateSpeed();
});
$("body").keyup(function(h){
  switch(h.keyCode){
    case 38:
      forward = false;
      break;
    case 40:
      reverse = false;
      break;
    case 37:
      left = false;
      break;
    case 39:
      right = false;
      break;
    default:
      console.log(h.keyCode);
  }
  updateSpeed();
});


// $("#button1").mousedown(function(){command(forward)});
// $("#button1").mouseup(function(){command("command=stop")});

// $("#button2").mousedown(function(){command("command=reverse")});
// $("#button2").mouseup(function(){command("command=stop")});
