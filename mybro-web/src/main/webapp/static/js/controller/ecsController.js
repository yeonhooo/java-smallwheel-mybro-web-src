'use strict';
App.controller('EcsController', [ '$scope', 'EcsService',
		function($scope, EcsService) {
			var self = this;
			self.tables = [];
			self.checkedTables = [];
			
			self.connectionInfo = {
				dbmsType : '',
				host : '',
				port : 0,
				userName : '',
				userPasswd : '',
				dbName : ''
			};
			
			self.mappingOption = {
				mapperType : '',
				couplingType : '',
				prefixExcept : '',
				classNameSuffix : ''
			};

			self.connect = function(connectionInfo) {
				self.tables.length = 0;
				EcsService.connect(connectionInfo).then(
					function() {
						self.getTables(connectionInfo);
					}, function(errResponse) {
						console.error('Error while fetching Currencies');
					}
				);
			};
			
			self.getTables = function(connectionInfo) {
				EcsService.getTables(connectionInfo).then(function(d) {
					self.tables = d;
					alert('총 ' + self.tables.length + '개의 테이블이 조회되었습니다.');
				}, function(errResponse) {
					console.error('Error while fetching tables');
					alert("작업 중 오류가 발생하였습니다.\n예외: " + errResponse.data.errorMessage);
				});			
			};

			self.submit = function() {
				self.connect(self.connectionInfo);
			};

			self.reset = function() {
				self.connectionInfo = {
					dbmsType : '',
					host : '',
					port : 0,
					userName : '',
					userPasswd : '',
					dbName : ''
				};
				$scope.connectionForm.$setPristine(); // reset Form
			};
			
			self.generate = function() {
				
				self.checkedTables.length = 0; // reset array
				$("#table tbody").find("input[type='checkbox']:checked").each(function (i) {
					self.checkedTables.push($(this).closest("tr").find("span").text());
			    });
				
				console.log('Checked table count: ' + self.checkedTables.length);

				if(self.checkedTables.length > 0) {
					var data = JSON.stringify({
						mappingOption : self.mappingOption, 
						tables : self.checkedTables
					});
					
					EcsService.generate(data).then(function(d) {
						console.log(d);
						alert(self.checkedTables.length + '개 테이블에 대한 작업이 완료되었습니다.');
						// EcsService.zip();
					}, function(errResponse) {
						console.error('Error while generate');
					});
				} else {
					alert("테이블을 먼저 선택해주세요.")
				}
			};

		} ]);
