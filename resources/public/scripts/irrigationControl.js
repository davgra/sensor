var attachCommand = function(id, command){
  $(id).click(function(){
    console.log("runing...");
    $.ajax({
      method: "GET",
      url: "/irrigation?" + command,
      dataType: "script"
    });
  });
};

attachCommand("#button1", "command=pump");
attachCommand("#button2", "command=stop");

attachCommand("#pump1", "command=1");
attachCommand("#pump2", "command=2");
attachCommand("#pump3", "command=3");
attachCommand("#pump4", "command=4");
attachCommand("#pump5", "command=5");
