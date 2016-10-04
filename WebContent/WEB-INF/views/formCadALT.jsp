<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Cadastro de alternativa</title>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
		<script>
		$(document).ready(function() {
  var max_fields = 6; //maximum input boxes allowed
  var wrapper = $(".input_fields_wrap"); //Fields wrapper
  var add_button = $(".add_field_button"); //Add button ID
  var enviar = $(".enviar");

  var x = 1; //initlal text box count
  $(add_button).click(function(e) { //on add input button click
    e.preventDefault();
    var length = wrapper.find("input:text").length;

    if (x < max_fields) { //max input box allowed
      x++; //text box increment
      $(wrapper).append('<div><input type="text" name="corpoAlternativa" />'); //add input box
      //document.getElementById('Texto1').value = $('#Texto1').val();
    }
    
  });
  
  $(wrapper).on("click", ".salvar_todos", function(e) { //click para salvar todos
	  $( "#target" ).submit()
    // e.preventDefault();
    // $(this).parent('div').remove();
    x--;
  })
  
 // $( "#target" ).submit(function( event ) {
//	 var form_values =  $( this ).serialize();  M�TODO PARA FAZER O SPLIT AUTOMATICO
//	 alert(form_values);
//	 return false;
//	});

});
</script>
</head>
<body>
<form id="target" action="cadastrarAlternativa">
    <div class="input_fields_wrap">
    <button class="add_field_button">Adicionar alternativa</button>
    <div><a href="#" class="salvar_todos">salvar todos</a></div>
    <div><input type="text" id="Texto1" name="corpoAlternativa" placeholder="alternativa correta"></div>
</div>
</form>
</body>
</html>