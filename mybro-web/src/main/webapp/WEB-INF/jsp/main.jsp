<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
	<title>mybro</title>
	<style>
		.username.ng-valid { background-color: lightgreen; }
		.username.ng-dirty.ng-invalid-required { background-color: red; }
		.username.ng-dirty.ng-invalid-minlength { background-color: yellow; }
		div.tooltip-inner {
		    max-width: 350px;
		    /* If max-width does not work, try using width instead */
		    width: 350px;
		    text-align: left;
		}
	</style>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
	<link rel="stylesheet" href="/static/css/app.css"></link>
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
	<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	
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

	    $("#couplingTypeTooltip").tooltip({
	        title: "<div>\
				 - <b>HIGH</b>: 가능한 DB의 컬럼타입과 동일한 타입으로 결정됩니다<br>\
				 - <b>MIDDLE</b>: 숫자(int, long...)과 날짜 타입(Date)만 변경되며, 나머지는 String 타입으로 결정됩니다.<br>\
				 - <b>LOW</b>: 숫자 타입(int, long...)만 변경되며, 나머지는 String 타입으로 결정됩니다.<br>\
				 - <b>NO</b>: 모든 프로퍼티 타입은 String 타입으로 결정됩니다.\
				 </div>",
	        html: true
	    });
	    
	    $("#prefixExceptTooltip").tooltip({
	        title: "<div>\
				 테이블명에서 불필요한 접두어를 제외합니다.<br>\
				 e.g. 테이블명이  <b><i>T_ADMIN</i></b>이고 PREFIX EXCEPT을 <b><i>T_</i></b>로 지정할 경우, 도메인명은 <b><i>admin</i></b>으로 결정됩니다.<br>\
				 PREFIX EXCEPT를 지정하지 않을 경우, 도메인명은 <b><i>tAdmin</i></b>으로 결정됩니다.\
				 </div>",
	        html: true,
	        placement: "bottom"
	    });
	    
	    $("#classNameSuffixTooltip").tooltip({
	        title: "<div>\
				 도메인 클래스의 접미어를 추가합니다.<br>\
				 e.g. 테이블명이  <b><i>T_ADMIN</i></b>이고 DOMAIN NAME SUFFIX을 <b><i>Vo</i></b>로 지정할 경우, 도메인명은 <b><i>tAdminVo</i></b>으로 결정됩니다.\
				 </div>",
	        html: true,
	        placement: "bottom"
	    });
	    
	    $("#domainStyleTooltip").tooltip({
	        title: "<div>\
				 도메인 클래스의 스타일을 지정합니다.<br>\
				 * 현재는 POJO만 제공하며, 추후 LOMBOK 스타일을 지원 예정입니다.\
				 </div>",
	        html: true,
	        placement: "bottom"
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
									<option value="MYBATIS" selected="selected">MYBATIS</option>
									<option value="IBATIS">IBATIS</option>
								</select>
								<div class="has-error" data-ng-show="optionForm.$dirty">
									<span data-ng-show="optionForm.mapperType.$error.required">This is a required field</span>
								</div>
							</div>

							<!-- COUPLING TYPE -->
							<label class="col-md-2 control-lable" for="file">COUPLING TYPE
								<a href="" >
									<span id="couplingTypeTooltip" class="glyphicon glyphicon-question-sign"></span>
						        </a>
							</label>
							<div class="col-md-3">
								<select id="couplingType" name="couplingType" data-ng-model="ctrl.mappingOption.couplingType" class="form-control" required>
									<option value="HIGH">HIGH</option>
									<option value="MIDDLE" selected="selected">MIDDLE</option>
									<option value="LOW" selected="selected">LOW</option>
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
							<label class="col-md-2 control-lable" for="file">PREFIX EXCEPT
								<a href="" >
									<span id="prefixExceptTooltip" class="glyphicon glyphicon-question-sign"></span>
						        </a>
							</label>
							<div class="col-md-3">
								<input type="text" data-ng-model="ctrl.mappingOption.prefixExcept" name="prefixExcept" class="username form-control input-sm" placeholder="Enter prefix except" />
							</div>

							<!-- CLASS NAME SUFFIX -->
							<label class="col-md-2 control-lable" for="file">DOMAIN NAME SUFFIX
								<a href="" >
									<span id="classNameSuffixTooltip" class="glyphicon glyphicon-question-sign"></span>
						        </a>
							</label>
							<div class="col-md-3">
								<input type="text" data-ng-model="ctrl.mappingOption.classNameSuffix" name="classNameSuffix" class="username form-control input-sm" placeholder="Enter class name suffix" />
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="form-group col-md-12">
							<!-- PREFIX EXCEPT -->
							<label class="col-md-2 control-lable" for="file">DOMAIN STYLE
								<a href="" >
									<span id="domainStyleTooltip" class="glyphicon glyphicon-question-sign"></span>
						        </a>
							</label>
							<div class="col-md-3">
								<select id="dtoStyle" name="dtoStyle" data-ng-model="ctrl.mappingOption.dtoStyle" class="form-control" required>
									<option value="POJO" selected="selected">POJO</option>
								</select>
								<div class="has-error" data-ng-show="optionForm.$dirty">
									<span data-ng-show="optionForm.dtoStyle.$error.required">This is a required field</span>
								</div>
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