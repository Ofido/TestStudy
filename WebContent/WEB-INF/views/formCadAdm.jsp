<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Cadastro de Administrador</title>
</head>
<body>
	<form action="adicionaAdm" enctype="multipart/form-data" method="post">
	<p>
		<strong>Nome: </strong> <input type="text" name="nome"
			placeholder="Insira nome completo" required /><br />
	</p>
	<p>
		<strong>Email: </strong> <input type="email" name="email"
			placeholder="exemplo@exemplo.com" required /><br />
	</p>
	<p>
		<strong>CPF: </strong> <input type="cpf" name="cpf"
			placeholder="Sem pontua��o" required /><br />
	</p>
	
	<p>
		<strong>RG: </strong> <input type="rg" name="rg"
			placeholder="Sem pontua��o" required /><br />
	</p>
	<p>
		<strong>Data de nascimento: </strong> <input type="date" name="nascimento"
			placeholder="Sem pontua��o" required /><br />
	</p>
	<p>
		<strong>Senha: </strong> <input type="password" name="senha"
			placeholder="" required /><br />
	</p>
	<p>
	<input type="file" name="arquivo" accept="image/jpeg" required>
	</p>
	<p>
		<strong>Sexo: </strong><br /> <input type="radio" name="sexo"
			value="Masc" required />Masculino<br /> <input type="radio"
			name="sexo" value="Fem" required />Feminino<br />
	</p>
	<input type="submit" value="enviar">
	</form>
	
</body>
</html>