<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
	<title>mybro</title>
	<style>
		.username.ng-valid {
			background-color: lightgreen;
		}
		
		.username.ng-dirty.ng-invalid-required {
			background-color: red;
		}
		
		.username.ng-dirty.ng-invalid-minlength {
			background-color: yellow;
		}
	</style>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
	<link href="/static/css/app.css" rel="stylesheet"></link>
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
	<script src="/static/js/app.js"></script>
	<script src="<c:url value='/static/js/service/ecsService.js' />"></script>
	<script src="<c:url value='/static/js/controller/ecsController.js' />"></script>
	
	<script type="text/javascript">
	$(document).ready(function () {
		$("#dbmsType").change(function(){
			if($(this).val() == 'MYSQL') {
				$("#port").val(3306);
			} else if($(this).val() == 'MSSQL') {
				$("#port").val(1433);
			} else if($(this).val() == 'ORACLE') {
				$("#port").val(1521);
			}
		});
		
		$("#chkAll").click(function(){
			
			if($(this).is(":checked")) {
				$("#table tbody").find("input[type='checkbox']").prop("checked", true);
			} else {
				$("#table tbody").find("input[type='checkbox']").prop("checked", false);
			}
			
		});
	});
	</script>
</head>
<body data-ng-app="myApp" class="ng-cloak">
	<div class="container" data-ng-controller="EcsController as ctrl">
		<h2>MyBro</h2>
		<div class="panel panel-default">
			<div class="panel-heading">
				<span class="lead">Database connection info</span>
			</div>
			<div class="formcontainer">
				<form data-ng-submit="ctrl.submit()" name="connectionForm" class="form-horizontal">
					<div class="row">
						<div class="form-group col-md-12">
							<!-- DBMS -->
							<label class="col-md-2 control-lable" for="dbmsType">DBMS TYPE</label>
							<div class="col-md-3">
								<select id="dbmsType" name="dbmsType" data-ng-model="ctrl.connectionInfo.dbmsType" class="form-control" required>
									<option value="MYSQL" selected="selected">MySQL or MariaDB</option>
									<option value="ORACLE">Oracle</option>
									<option value="MSSQL">SQL Server</option>
								</select>
								<div class="has-error" data-ng-show="connectionForm.$dirty">
									<span data-ng-show="connectionForm.dbmsType.$error.required">This is a required field</span>
								</div>
							</div>

							<!-- HOST -->
							<label class="col-md-2 control-lable" for="file">HOST</label>
							<div class="col-md-3">
								<input type="text" data-ng-model="ctrl.connectionInfo.host" name="host" class="username form-control input-sm" placeholder="Enter hostname or ip address" required data-ng-minlength="8" />
								<div class="has-error" data-ng-show="connectionForm.$dirty">
									<span data-ng-show="connectionForm.host.$error.required">This is a required field</span> 
									<span data-ng-show="connectionForm.host.$error.minlength">Minimum length required is 3</span> 
									<span data-ng-show="connectionForm.host.$invalid">This field is invalid </span>
								</div>
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="form-group col-md-12">
							<!-- USER -->
							<label class="col-md-2 control-lable" for="file">USER</label>
							<div class="col-md-3">
								<input type="text" data-ng-model="ctrl.connectionInfo.userName" name="userName" class="username form-control input-sm" placeholder="Enter username" required data-ng-minlength="3" />
								<div class="has-error" data-ng-show="connectionForm.$dirty">
									<span data-ng-show="connectionForm.userName.$error.required">This is a required field</span> 
									<span data-ng-show="connectionForm.userName.$error.minlength">Minimum length required is 3</span> 
									<span data-ng-show="connectionForm.userName.$invalid">This field is invalid </span>
								</div>
							</div>

							<!-- PASSWORD -->
							<label class="col-md-2 control-lable" for="file">PASSWORD</label>
							<div class="col-md-3">
								<input type="password" data-ng-model="ctrl.connectionInfo.userPasswd" name="userPasswd" class="username form-control input-sm" placeholder="Enter password" required data-ng-minlength="3" />
								<div class="has-error" data-ng-show="connectionForm.$dirty">
									<span data-ng-show="connectionForm.userPasswd.$error.required">This is a required field</span> 
									<span data-ng-show="connectionForm.userPasswd.$error.minlength">Minimum length required is 3</span> 
								</div>
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="form-group col-md-12">
							<!-- PORT -->
							<label class="col-md-2 control-lable" for="file">PORT</label>
							<div class="col-md-3">
								<input type="number" data-ng-model="ctrl.connectionInfo.port" id="port" name="port" class="username form-control input-sm" placeholder="Enter port number" required data-ng-minlength="3" />
								<div class="has-error" data-ng-show="connectionForm.$dirty">
									<span data-ng-show="connectionForm.port.$error.required">This is a required field</span> 
									<span data-ng-show="connectionForm.port.$error.minlength">Minimum length required is 3</span> 
									<span data-ng-show="connectionForm.port.$invalid">This field is invalid </span>
								</div>
							</div>

							<!-- DATABASE -->
							<label class="col-md-2 control-lable" for="file">DATABASE</label>
							<div class="col-md-3">
								<input type="text" data-ng-model="ctrl.connectionInfo.dbName" name="dbName" class="username form-control input-sm" placeholder="Enter database name" required data-ng-minlength="3" />
								<div class="has-error" data-ng-show="connectionForm.$dirty">
									<span data-ng-show="connectionForm.dbName.$error.required">This is a required field</span> 
									<span data-ng-show="connectionForm.dbName.$error.minlength">Minimum length required is 3</span> 
									<span data-ng-show="connectionForm.dbName.$invalid">This field is invalid </span>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="form-actions floatRight">
							<input type="submit" value="Connect" class="btn btn-primary btn-sm" data-ng-disabled="connectionForm.$invalid">
							<button type="button" data-ng-click="ctrl.reset()" class="btn btn-warning btn-sm" data-ng-disabled="connectionForm.$pristine">
          						<span class="glyphicon glyphicon-refresh"></span>
								Reset Form
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		
		<!-- Mapping Option -->
		<div class="panel panel-default">
			<div class="panel-heading">
				<span class="lead">Mapping Option</span>
			</div>
			<div class="mapping-option-container">
				<form data-ng-submit="ctrl.submit()" name="optionForm" class="form-horizontal">
					<div class="row">
						<div class="form-group col-md-12">
							<!-- MAPPER TYPE -->
							<label class="col-md-2 control-lable" for="mapperType">MAPPER TYPE</label>
							<div class="col-md-3">
								<select id="mapperType" name="mapperType" data-ng-model="ctrl.mappingOption.mapperType" class="form-control" required>
									<option value="MYBATIS">MYBATIS</option>
									<option value="IBATIS">IBATIS</option>
									<option value="LOMBOK">LOMBOK</option>
								</select>
								<div class="has-error" data-ng-show="optionForm.$dirty">
									<span data-ng-show="optionForm.mapperType.$error.required">This is a required field</span>
								</div>
							</div>

							<!-- COUPLING TYPE -->
							<label class="col-md-2 control-lable" for="file">COUPLING TYPE</label>
							<div class="col-md-3">
								<select id="couplingType" name="couplingType" data-ng-model="ctrl.mappingOption.couplingType" class="form-control" required>
									<option value="HIGH">HIGH</option>
									<option value="MIDDLE" selected="selected">MIDDLE</option>
									<option value="LOW">LOW</option>
									<option value="NO">NO</option>
								</select>
								<div class="has-error" data-ng-show="optionForm.$dirty">
									<span data-ng-show="optionForm.couplingType.$error.required">This is a required field</span>
								</div>
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="form-group col-md-12">
							<!-- PREFIX EXCEPT -->
							<label class="col-md-2 control-lable" for="file">PREFIX EXCEPT</label>
							<div class="col-md-3">
								<input type="text" data-ng-model="ctrl.mappingOption.prefixExcept" name="prefixExcept" class="username form-control input-sm" placeholder="Enter prefix except" />
							</div>

							<!-- CLASS NAME SUFFIX -->
							<label class="col-md-2 control-lable" for="file">CLASS NAME SUFFIX</label>
							<div class="col-md-3">
								<input type="text" data-ng-model="ctrl.mappingOption.classNameSuffix" name="classNameSuffix" class="username form-control input-sm" placeholder="Enter class name suffix" />
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="form-actions floatRight">
							<button type="button" data-ng-click="ctrl.generate()" class="btn btn-info btn-sm" data-ng-disabled="optionForm.$pristine">Generate</button>
							<a class="btn btn-info btn-sm" href="/api/tables/zip">
								<span class="glyphicon glyphicon-download"></span>
								Download
							</a>
						</div>
					</div>
					
				</form>
			</div>
		</div>
		
		<div class="panel panel-default">
			<!-- Default panel contents -->
			<div class="panel-heading">
				<span class="lead">Table list</span>
				<span> (count: {{ctrl.tables.length}})</span>
			</div>
			
			<div class="tablecontainer">
				<table id="table" class="table table-hover">
					<thead>
						<tr>
							<th><input type="checkbox" id="chkAll"></th>
							<th>No</th>
							<th>Table name</th>
						</tr>
					</thead>
					<tbody>
						<tr data-ng-repeat="tableName in ctrl.tables">
							<td><input type="checkbox"></td>
							<td>{{$index + 1}}</td>
							<td><span data-ng-bind="tableName"></span></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

</body>
</html>