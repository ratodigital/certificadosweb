'use strict';

var app = angular.module('app', [
    'ngRoute',
    'app.controllers'
]);

var controllers = angular.module('app.controllers',[]);
controllers.controller('AppCtrl', function($scope, $http) {
	$scope.appName = 'Certificados PDF';
	
	$scope.radioData = "CSV";

	$scope.wait = false;
	
	$scope.error = "";
		
	$scope.listasMailchimp = function() {
		console.log('function ' + $scope.apikey);
	
		$scope.error = "";
 		$scope.wait = true;
 		
		//http://localhost:8080/mailchimp/lists/6e5dbfb2074d3435fffa69d860e6a9b8-us7
        $http({
            url: '/mailchimp/lists/' + $scope.apikey,
            method: "GET",
            headers: {
                'Content-type': 'application/json'
            }
        }).success(function (json, status, headers, config) {
	 		$scope.wait = false;    
	 		console.log('OK' + json);
	 		if (json.status == "OK") {
	        	$scope.mailchimpLists = json;
	        } else {
	        	$scope.error = "Esta APIKey é inválida. Verifique a API correta na sua conta Mailchimp";
	        }
        }).error(function (data, status, headers, config) {
	 		$scope.wait = false;        
	 		$scope.error = "Falha na obtenção das listas";
            console.log('Fail to load mailchimp lists' + data);
        });
       
	};
});
