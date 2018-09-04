$(function(){
	sendForSimulation = function(){
		$(".loader").css("visibility","visible");
		$.ajax({
		    url: "addstation",
		    context: document.body,
		    success: function(){
		      $(".simulator-result").css("visibility","visible");
		      $(".default-setup").css("visibility","hidden");
		      $(".do-simulation").css("visibility","hidden");
		      $(".loader").css("visibility","hidden");
		      window.setTimeout(timeout, 6000);
		      window.open("http://localhost:8080/graph.html");
		      $(".instruction").css("display","block");
		    }
		});
		
	}
	
	sendForWeightSimulation = function(){
		$(".loader").css("visibility","visible");
		$.ajax({
		    url: "addstationweight",
		    context: document.body,
		    success: function(){
		      $(".simulator-result").css("visibility","visible");
		      $(".default-setup").css("visibility","hidden");
		      $(".do-simulation").css("visibility","hidden");
		      $(".loader").css("visibility","hidden");
		      window.setTimeout(timeout, 6000);
		      window.open("http://localhost:8080/graph.html");
		      $(".instruction").css("display","block");
		    }
		});
	}

	resetdb = function(){
		$(".loader").css("visibility","visible");
		$.ajax({
		    url: "resetdb",
		    context: document.body,
		    success: function(){
		      $(".simulator-result").css("visibility","visible");
		      $(".default-setup").css("visibility","hidden");
		      $(".redo-setup").css("visibility","visible");
		      $(".do-simulation").css("visibility","hidden");
		      $(".simulator-result").css("visibility","hidden");
		    
		      $(".loader").css("visibility","hidden");
		      $(".instruction").css("display","none");
		    }
		});
	}
	setup = function(){
		$(".loader").css("visibility","visible");
		$.ajax({
		    url: "setup",
		    context: document.body,
		    success: function(){
		      $(".simulator-result").css("visibility","hidden");
		      $(".default-setup").css("visibility","visible");
		      $(".redo-setup").css("visibility","hidden");
		      $(".do-simulation").css("visibility","visible");
		    
		      $(".loader").css("visibility","hidden");
		      $(".instruction").css("visibility","visible");
		    }
		});
	}
	displayinst = function(){
		$(".instruction").css("display","block");
	}
	
	timeout = function(){
		$(".loader").css("visibility","hidden");
	}
});