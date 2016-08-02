'use strict';

App.factory('EcsService', ['$http', '$q', function($http, $q){

	return {
	
	    connect: function(connectionInfo){
			return $http.post('/api/connect', connectionInfo)
					.then(
							function(response){
								return;
							}, 
							function(errResponse){
								console.error('Error while connecting');
								return $q.reject(errResponse);
							}
					);
	    },
	    
	    getTables: function(connectionInfo){
			return $http.post('/api/tables', connectionInfo)
					.then(
							function(response){
								return response.data;
							}, 
							function(errResponse){
								console.error('Error while fetch tables');
								return $q.reject(errResponse);
							}
					);
	    },
	    
	    generate: function(data){
			return $http.post('/api/tables/generate', data)
					.then(
							function(response){
								return response.data;
							}, 
							function(errResponse){
								console.error('Error while generating');
								return $q.reject(errResponse);
							}
					);
	    },
	    
	    zip: function(){
	    	var config = {
	    		headers:  {
		            'Accept': 'application/zip;'
		        }
		    };

			return $http.get('/api/tables/zip', config)
					.then(
							function(response){
								return response.data;
							}, 
							function(errResponse){
								console.error('Error while zip');
								return $q.reject(errResponse);
							}
					);
	    }
	    
	};

}]);
