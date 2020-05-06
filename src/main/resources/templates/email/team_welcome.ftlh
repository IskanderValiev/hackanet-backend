<#ftl encoding='UTF-8'>
<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta name="author" content="Iskander Valiev">
</head>
<body>
Welcome to out team of ${teamName}. It is going to be a pleasure to work together for both of us!
Team squad:
<#list members as member>
    <li>${member.name}</li>
</#list>
</body>
</html>