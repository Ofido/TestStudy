<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ALTERAR COORDENADOR</title>

<script type="text/javascript">
window.onload=function(){
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();
	 if(dd<10){
	        dd='0'+dd
	    } 
	    if(mm<10){
	        mm='0'+mm
	    } 

	today = yyyy+'-'+mm+'-'+dd;
	document.getElementById("datefield").setAttribute("max", today);
	}
</script>

</head>
<body>
	<br /><br />
	ALTERAR DADOS DE ${prof.nome}
	<br /><br /><br />

	<form method="post" action="alterandoProfessor">
		<strong>Nome do Professor:</strong><br />
		<input type="hidden" value="${prof.idProfessor}" name="idProfessor" />
		
		<input type="text" name="nome" maxlength="100" value="${prof.nome}" required/>
		<br /><br />
		
		<strong>Email do Professor:</strong><br />
		<input type="text" name="email" maxlength="255" value="${prof.email}" required/>
		<br /><br />
		
		<strong>Senha do Professor:</strong><br />
		<input type="password" name="senha" maxlength="50" value="${prof.senha}" required/>
		<br /><br />
		
		<strong>CPF do Professor:</strong><br />
		<input type="text" name="cpf" maxlength="14" value="${prof.cpf}" required/>
		<br /><br />
		
		<strong>RG do Professor:</strong><br />
		<input type="text" name="rg" maxlength="12" value="${prof.rg}" required/>
		<br /><br />
		
		<strong>Data de nascimento:</strong><br />
		<input id="datefield" type="date" name="nascimento" min="1930-01-01" value="${prof.nascimento}" required/>
		<br /><br />
		
		<strong>Sexo do Professor:</strong><br />
		<input type="radio" name="sexo" value="masc" ${prof.sexo=="masc"?'checked':''}/>Masculino<br />
		<input type="radio" name="sexo" value="femi" ${prof.sexo=="femi"?'checked':''}/>Feminino
		<br /><br />
						
		<strong>Escola atrelada:</strong>
		<select name="idEmp">
			<option value=0>--SELECIONE UMA ESCOLA--</option>
		<c:forEach items="${LEscola}" var="e">
			<option value="${e.idEmp}" ${e.idEmp==prof.escolaProfessor.idEmp?'selected':''}>${e.nomeEmp}</option>
		</c:forEach>
		</select>
		<br /><br />
	
		<input type="submit" value="Alterar" />
	</form>
	
	<br /><br />
	<a href="backToListProfessores">Cancelar</a>

</body>
</html>